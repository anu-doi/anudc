
jQuery(document).ready(function() {
	var options = {
		url: function(phrase) {
			return "/DataCommons/rest/list/items";
		},
		getValue: function(element) {
			console.log("In getValue");
			console.log(element);
			return element.title;
		},
//		getValue: "title",
		ajaxSettings: {
			dataType: "json",
			method: "GET",
			data: {
			}
		},
		preparePostData: function(data) {
			data.type = $("#linkItemType").val();
			data.title = $("#itemSearch").val();
			return data;
		},
		list: {
			match: {
				enabled: true
			}
		},
		requestDelay: 400
	}
	
	$("#itemSearch").easyAutocomplete(options);
	/*$("#itemSearch").searchableOptionList({
		data: function() {
			console.log('In data section');
			console.log($(this));
			var values = [
				{"type": "option", "value": "aa", "label": "AA"}
				,{"type": "option", "value": "bb", "label": "BB"}
				];
			return values;
		}
	});*/
	
	/*$("#itemSearch").autocomplete({
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
	});*/

	$("#linkItemType").change(function() {
		var cat1 = jQuery("#itemType").val();
		var cat2 = jQuery("#linkItemType").val();
		
		$.ajax({
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
	
	$("#linkItemType").trigger('change');
	
	$("#editLinkButton").on('click', function(){
		console.log("In editlink button")
		getLinks();
		
	});
	
	$("#itemLinkButton").on('click', function(){
		console.log('In item link button');
		jQuery("#itemIdentifier").text('None Selected');
		jQuery("#itemName").text('None Selected');
		jQuery("#itemId").val('');
		jQuery("#linkExternal").val('');
		jQuery("#previousLinkType").val('');
//		editMode = 0;
//		centrePopup("#popupLink");
//		linkPopupStatus = loadPopup("#popupLink", linkPopupStatus);
	});
});


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
				var editImg = jQuery('<img/>', {
					src: '//style.anu.edu.au/_anu/images/icons/silk/pencil.png',
					title: 'Edit relationship ' + title,
					click: function(e) {
						editLink(item, this);
					}
				});
				row.append(jQuery('<td></td>').html(editImg));
				
				var deleteImg = jQuery('<img/>', {
					src: '//style.anu.edu.au/_anu/images/icons/silk/cross.png',
					title: 'Delete relationship ' + item.title,
					click: function(e) {
						deleteLink(item, this);
					}
				});
				row.append(jQuery('<td></td>').html(deleteImg));
				table.append(row);
			});
			jQuery('#editLinkContent').html(table);
		}
	});
}

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

function getRelationshipType(relationship) {
	var relation = relationship.substring(26);
	return relation;
}

function editLink(item, row) {
	console.log("In edit link");
	var title = getItemTitle(item);
	console.log(title);
	var relation = getRelationshipType(item.predicate);
	console.log(relation);
	
//	editLinkPopupStatus = disablePopup("#popupEditLink", editLinkPopupStatus);
//	jQuery("#linkItemType").val(item.type);
//	jQuery("#linkItemType").trigger('change');
//	var pid = getPidFromInfo(item.item);
//	
//	editMode = 1;
//	centrePopup("#popupLink");
//	linkPopupStatus = loadPopup("#popupLink", linkPopupStatus);
//	
//	if (pid) {
//		jQuery("#itemIdentifier").text(pid);
//		jQuery("#linkExternal").val('');
//		jQuery("#itemName").text(title);
//		jQuery("#itemId").text(item.item);
//	}
//	else {
//		jQuery("#itemIdentifier").text('None Selected');
//		jQuery("#itemName").text('None Selected');
//		jQuery("#linkExternal").val(item.item);
//		jQuery("#itemId").val('');
//	}
//	jQuery("#previousLinkType").val(relation);
//	//Because the possible relationship types are returned after this is set this does not work
//	jQuery("#linkType").val(relation);
}

function deleteLink(item, row) {
	console.log('In delete link');
	var pid = getPid();
	console.log(pid);
	var relation = getRelationshipType(item.predicate);
	console.log(relation);
	var urlStr = '/DataCommons/rest/display/removeLink/' + pid;
	console.log(urlStr);
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

function getPid() {
	var pathArray = window.location.pathname.split('/');
	var pid = pathArray[pathArray.length - 1];
	return pid;
}