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
 */

/**
 * Document ready functions for the link item objects.  This includes adding a selection list
 * to the itemId text field
 * 
 * Version	Date		Developer			Description
 * 0.1		07/05/2012	Genevieve Turner (GT)	Initial
 * 0.2		29/05/2012	Genevieve Turner (GT)	Removed console.log which was causing issues in browsers other than firefox
 */
jQuery(document).ready(function() {
	jQuery("#itemId").autocomplete({
		source: function (request, response) {
			jQuery.ajax({
				url: "/DataCommons/rest/list/items",
				dataType: "json",
				data: {
					title: request.term,
					type: jQuery("#itemType").val()
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
		}
	});
});

var linkPopupStatus = 0;

/**
 * Centre and open the popup
 * 
 * Version	Date		Developer				Description
 * 0.1		07/05/2012	Genevieve Turner (GT)	Initial
 * 0.5		24/07/2012	Genevieve Turner (GT)	Moved loadPopup,centrePopup and disablePopup functions to popup.js
 */
jQuery("#itemLinkButton").live('click', function(){
	centrePopup("#popupLink");
	linkPopupStatus = loadPopup("#popupLink", linkPopupStatus);
});

/**
 * Close the popup when the close button as been clicked
 * 
 * Version	Date		Developer				Description
 * 0.1		07/05/2012	Genevieve Turner (GT)	Initial
 * 0.5		24/07/2012	Genevieve Turner (GT)	Moved loadPopup,centrePopup and disablePopup functions to popup.js
 */
jQuery("#popupLinkClose").live('click', function(){
	linkPopupStatus = disablePopup("#popupLink", linkPopupStatus);
});

/**
 * Close the popup when the background has been clicked
 * 
 * Version	Date		Developer				Description
 * 0.1		07/05/2012	Genevieve Turner (GT)	Initial
 * 0.5		24/07/2012	Genevieve Turner (GT)	Moved loadPopup,centrePopup and disablePopup functions to popup.js
 */
jQuery("#backgroundPopup").live('click', function() {
	linkPopupStatus = disablePopup("#popupLink", linkPopupStatus);
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
});

/**
 * Perform an ajax call when the add link form is submitted
 * 
 * Version	Date		Developer				Description
 * 0.1		07/05/2012	Genevieve Turner (GT)	Initial
 * 0.3		29/05/2012	Genevieve Turner (GT)	Added error notification
 * 0.4		02/07/2012	Genevieve Turner (GT)	Updated to have the pid in the path
 * 0.5		24/07/2012	Genevieve Turner (GT)	Moved loadPopup,centrePopup and disablePopup functions to popup.js
 */
jQuery("#formAddLink").live('submit', function() {
	var pathArray = window.location.pathname.split('/');
	var pid = pathArray[pathArray.length - 1];
	var urlStr = "/DataCommons/rest/display/addLink/" + pid;
	var typeStr = jQuery("#linkType").val();
	var itemStr = jQuery("#itemId").val();
	var dataString = 'linkType=' + typeStr + '&itemId=' + itemStr;
	jQuery.ajax({
		type: "POST",
		url: urlStr,
		data: dataString,
		success: function() {
			linkPopupStatus = disablePopup("#popupLink", linkPopupStatus);
		},
		error: function() {
			alert('Error Adding Link');
		}
	});
});
