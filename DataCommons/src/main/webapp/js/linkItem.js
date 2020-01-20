/**
 * linkItem.js
 * 
 * Australian National University Data Commons
 * 
 * Javascript file that performs actions for the item link popup
 * 
 * Version	Date		Developer				Description
 * 0.1		07/05/2012	Genevieve Turner (GT)	Initial
 * 0.3		29/05/2012	Genevieve Turner (GT)	Added error notification
 * 0.4		02/07/2012	Genevieve Turner (GT)	Updated to have the pid in the path
 * 0.5		24/07/2012	Genevieve Turner (GT)	Moved loadPopup,centrePopup and disablePopup functions to popup.js
 * 0.6		14/08/2012	Genevieve Turner (GT)	Updated link functionality such that it does not display the qualified id
 * 0.7		28/08/2012	Genevieve Turner (GT)	Added amendments for enabling the addition of nla identifiers
 * 0.8		19/09/2012	Genevieve Turner (GT)	Updated to retrieve link types from the database
 * 0.9		22/10/2012	Genevieve Turner (GT)	Added code for removal and editing of links
 */

/**
 * Document ready functions for the link item objects.  This includes adding a selection list
 * to the itemId text field
 * 
 * Version	Date		Developer			Description
 * 0.1		07/05/2012	Genevieve Turner (GT)	Initial
 * 0.2		29/05/2012	Genevieve Turner (GT)	Removed console.log which was causing issues in browsers other than firefox
 * 0.6		14/08/2012	Genevieve Turner (GT)	Updated link functionality such that it does not display the qualified id
 * 0.8		19/09/2012	Genevieve Turner (GT)	Updated to retrieve link types from the database
 */
jQuery(document).ready(function() {
	jQuery("#itemSearch").autocomplete({
		source: function (request, response) {
			jQuery.ajax({
				url: "/DataCommons/rest/list/items",
				dataType: "json",
				data: {
					title: request.term,
					type: jQuery("#linkItemType").val()
				},
				success: function(data) {
					response ( jQuery.map(data.results, function(item, i) {
						return {
							label: item.title,
							value: item.item
						};
					}));
				}
			});
		},
		minLength: 2,
		open: function() {
			jQuery(this).removeClass("ui-corner-all").addClass("ui-corner-top");
		},
		close: function() {
			jQuery(this).removeClass("ui-corner-top").addClass("ui-corner-all");
		},
		select: function(event, ui) {
			var value = ui.item.value;
			var result = value.match(/info:fedora\/(.*)/)[1];
			jQuery("#itemName").text(ui.item.label);
			jQuery("#itemIdentifier").text(result);
			jQuery("#itemId").text(ui.item.value);
			jQuery("#itemSearch").val(ui.item.label);
			return false;
		},
	    focus: function(event, ui) {
	    	jQuery("#itemSearch").val(ui.item.label);
	        return false; // Prevent the widget from inserting the value.
	    }
	});

	jQuery("#linkItemType").change(function() {
		var cat1 = jQuery("#itemType").val();
		var cat2 = jQuery("#linkItemType").val();
		
		jQuery.ajax({
			type: "GET",
			url: "/DataCommons/rest/list/relation_types",
			data: {
				cat1: cat1,
				cat2: cat2
			},
			dataType: "json",
			success: function(data) {
				jQuery("#linkType option").remove();
				jQuery.map(data, function(item, i) {
					jQuery("#linkType").append("<option value='" + i + "'>" + item + "</option>");
				});
			},
			error: function(jqXHR, textStatus, errorThrown) {
				var test = jQuery.parseJSON(jqXHR.responseText);
				alert('Error retrieving categories for links:\n' + test[0]);
			}
		});
	});
	
	jQuery("#linkItemType").trigger('change');
});

var linkPopupStatus = 0;
var editLinkPopupStatus = 0;
var editMode = 0;

/**
 * Centre and open the popup
 * 
 * Version	Date		Developer				Description
 * 0.1		07/05/2012	Genevieve Turner (GT)	Initial
 * 0.5		24/07/2012	Genevieve Turner (GT)	Moved loadPopup,centrePopup and disablePopup functions to popup.js
 * 0.7		28/08/2012	Genevieve Turner (GT)	Added amendments for enabling the addition of nla identifiers
 */
jQuery("#itemLinkButton").live('click', function(){
	jQuery("#itemIdentifier").text('None Selected');
	jQuery("#itemName").text('None Selected');
	jQuery("#itemId").val('');
	jQuery("#linkExternal").val('');
	jQuery("#previousLinkType").val('');
	editMode = 0;
	centrePopup("#popupLink");
	linkPopupStatus = loadPopup("#popupLink", linkPopupStatus);
});

/**
 * Retrieve the relationships to this object and open a popup with them
 * 
 * Version	Date		Developer				Description
 * 0.9		22/10/2012	Genevieve Turner (GT)	Initial
 */
jQuery("#editLinkButton").live('click', function(){
	getLinks();
	centrePopup("#popupEditLink");
	editLinkPopupStatus = loadPopup("#popupEditLink", editLinkPopupStatus);
});

/**
 * getLinks
 * 
 * Retrieve the links associated with the current object
 * 
 * Version	Date		Developer				Description
 * 0.9		22/10/2012	Genevieve Turner (GT)	Initial
 */
function getLinks() {
	var pathArray = window.location.pathname.split('/');
	var pid = pathArray[pathArray.length - 1];
	var urlStr = "/DataCommons/rest/display/getLinks/" + pid;
	
	jQuery.ajax({
		type: "GET",
		url: urlStr,
		success: function(data) {
			var table = jQuery('<table></table>');
			
			jQuery.map(data.results, function(item, i) {
				var row = jQuery('<tr></tr>');
				var title = getItemTitle(item);
				row.append(jQuery('<td></td>').text(title));
				var relation = getRelationshipType(item.predicate);
				row.append(jQuery('<td></td>').text(relation));
				var deleteEditCell = jQuery("<td></td>").attr("class","icon-col-width");
				var editImg = jQuery('<img/>', {
					src: '//style.anu.edu.au/_anu/images/icons/silk/pencil.png',
					title: 'Edit relationship ' + title,
					click: function(e) {
						editLink(item, this);
					}
				});
				deleteEditCell.append(editImg);
				
				var deleteImg = jQuery('<img/>', {
					src: '//style.anu.edu.au/_anu/images/icons/silk/cross.png',
					title: 'Delete relationship ' + item.title,
					click: function(e) {
						deleteLink(item, this);
					}
				});
				deleteEditCell.append(deleteImg);
				row.append(deleteEditCell);
				table.append(row);
			});
			jQuery('#editLinkContent').html(table);
		}
	});
}

/**
 * deleteLink
 * 
 * Version	Date		Developer				Description
 * 0.9		22/10/2012	Genevieve Turner (GT)	Initial
 * 
 * @param item The item for which you are deleting the link
 * @param row The row in the table for which the link is contained
 */
function deleteLink(item, row) {
	var pid = getPid();
	var relation = getRelationshipType(item.predicate);
	var urlStr = '/DataCommons/rest/display/removeLink/' + pid;
	jQuery.ajax({
		type: "POST",
		url: urlStr,
		data: {
			itemId: item.item
			,linkType: relation
		},
		success: function(data) {
			if (row) {
				jQuery(row).closest('tr').remove();
			}
		},
		error: function() {
			alert('Error removing link');
		}
	});
}

/**
 * editLink
 * 
 * Opens closes the edit link dialog and opens the link item dialog and populates
 * the dialog with the selected records information
 * 
 * Version	Date		Developer				Description
 * 0.9		22/10/2012	Genevieve Turner (GT)	Initial
 * 
 * @param item The item for which you are editing the link
 * @param row The row in the table for which the link is contained
 */
function editLink(item, row) {
	var title = getItemTitle(item);
	var relation = getRelationshipType(item.predicate);
	
	editLinkPopupStatus = disablePopup("#popupEditLink", editLinkPopupStatus);
	jQuery("#linkItemType").val(item.type);
	jQuery("#linkItemType").trigger('change');
	var pid = getPidFromInfo(item.item);
	
	editMode = 1;
	centrePopup("#popupLink");
	linkPopupStatus = loadPopup("#popupLink", linkPopupStatus);
	
	if (pid) {
		jQuery("#itemIdentifier").text(pid);
		jQuery("#linkExternal").val('');
		jQuery("#itemName").text(title);
		jQuery("#itemId").text(item.item);
	}
	else {
		jQuery("#itemIdentifier").text('None Selected');
		jQuery("#itemName").text('None Selected');
		jQuery("#linkExternal").val(item.item);
		jQuery("#itemId").val('');
	}
	jQuery("#previousLinkType").val(relation);
	//Because the possible relationship types are returned after this is set this does not work
	jQuery("#linkType").val(relation);
}

/**
 * getPid
 * 
 * Retrieves the pid from the path
 * 
 * Version	Date		Developer				Description
 * 0.9		22/10/2012	Genevieve Turner (GT)	Initial
 * 
 * @returns The pid
 */
function getPid() {
	var pathArray = window.location.pathname.split('/');
	var pid = pathArray[pathArray.length - 1];
	return pid;
}

/**
 * getRelationshipType
 * 
 * Removes the namespace from the relationship type
 * 
 * Version	Date		Developer				Description
 * 0.9		22/10/2012	Genevieve Turner (GT)	Initial
 * 
 * @param relationship
 * @returns
 */
function getRelationshipType(relationship) {
	var relation = relationship.substring(26);
	return relation;
}

/**
 * getPidFromInfo
 * 
 * Removes the info:fedora from in front of a pid
 * 
 * Version	Date		Developer				Description
 * 0.9		22/10/2012	Genevieve Turner (GT)	Initial
 * 
 * @param pid The pid to retrieve
 * @returns The pid without the info:fedora
 */
function getPidFromInfo(pid) {
	var results = pid.match(/info:fedora\/(.*)/);
	if (results) {
		return results[1];
	}
}

/**
 * getItemTitle
 * 
 * Gets the items title.
 * 
 * Version	Date		Developer				Description
 * 0.9		22/10/2012	Genevieve Turner (GT)	Initial
 * 
 * @param item The item to get the title for
 * @returns {String} The title
 */
function getItemTitle(item) {
	var title = '';
	if (item.title) {
		title = item.title;
	}
	else {
		title = item.item;
	}
	return title;
}

/**
 * Close the popup when the close button has been clicked
 * 
 * Version	Date		Developer				Description
 * 0.1		07/05/2012	Genevieve Turner (GT)	Initial
 * 0.5		24/07/2012	Genevieve Turner (GT)	Moved loadPopup,centrePopup and disablePopup functions to popup.js
 */
jQuery("#popupLinkClose").live('click', function(){
	linkPopupStatus = disablePopup("#popupLink", linkPopupStatus);
});

/**
 * Close the edit link popup when the close button has been clicked
 * 
 * Version	Date		Developer				Description
 * 0.9		22/10/2012	Genevieve Turner (GT)	Initial
 */
jQuery("#popupEditLinkClose").live('click', function(){
	linkPopupStatus = disablePopup("#popupEditLink", editLinkPopupStatus);
});

/**
 * Close the popup when the background has been clicked
 * 
 * Version	Date		Developer				Description
 * 0.1		07/05/2012	Genevieve Turner (GT)	Initial
 * 0.5		24/07/2012	Genevieve Turner (GT)	Moved loadPopup,centrePopup and disablePopup functions to popup.js
 */
jQuery("#backgroundPopup").live('click', function() {
	if (linkPopupStatus == 1) {
		linkPopupStatus = disablePopup("#popupLink", linkPopupStatus);
	}
	if (editLinkPopupStatus == 1) {
		editLinkPopupStatus = disablePopup("#popupEditLink", editLinkPopupStatus);
	}
});

/**
 * Close the popup when the escape button has been pressed
 * 
 * Version	Date		Developer				Description
 * 0.1		07/05/2012	Genevieve Turner (GT)	Initial
 * 0.5		24/07/2012	Genevieve Turner (GT)	Moved loadPopup,centrePopup and disablePopup functions to popup.js
 */
jQuery(document).keypress(function(e) {
	if(e.keyCode==27 && linkPopupStatus==1) {
		linkPopupStatus = disablePopup("#popupLink", linkPopupStatus);
	}
	if(e.keyCode==27 && editLinkPopupStatus==1) {
		editLinkPopupStatus = disablePopup("#popupEditLink", editLinkPopupStatus);
	}
});

/**
 * Perform an ajax call when the add link form is submitted
 * 
 * Version	Date		Developer				Description
 * 0.1		07/05/2012	Genevieve Turner (GT)	Initial
 * 0.3		29/05/2012	Genevieve Turner (GT)	Added error notification
 * 0.4		02/07/2012	Genevieve Turner (GT)	Updated to have the pid in the path
 * 0.5		24/07/2012	Genevieve Turner (GT)	Moved loadPopup,centrePopup and disablePopup functions to popup.js
 * 0.7		28/08/2012	Genevieve Turner (GT)	Added amendments for enabling the addition of nla identifiers
 */
jQuery("#formAddLink").live('submit', function() {
	var pid = getPid();
	var urlStr = '';
	if (editMode == 1) {
		urlStr = "/DataCommons/rest/display/editLink/" + pid;
	}
	else {
		urlStr = "/DataCommons/rest/display/addLink/" + pid;
	}
	var typeStr = jQuery("#linkType").val();
	var itemStr = jQuery("#itemId").text();
	var previousTypeStr = jQuery("#previousLinkType").val();
	
	if (itemStr == '' || itemStr == 'None Selected') {
		itemStr = jQuery("#linkExternal").val();
	}
//	var dataString = 'linkType=' + typeStr + '&itemId=' + itemStr;
	jQuery.ajax({
		type: "POST",
		url: urlStr,
		data: {
			linkType: typeStr,
			itemId: itemStr,
			removeLinkType: previousTypeStr
		},
		success: function() {
			linkPopupStatus = disablePopup("#popupLink", linkPopupStatus);
		},
		error: function() {
			alert('Error Adding Link');
		}
	});
});
