/**
 * linkItem.js
 * 
 * Australian National University Data Commons
 * 
 * Javascript file that performs actions for the item link popup
 * 
 * Version	Date		Developer			Description
 * 0.1		07/05/2012	Genevieve Turner	Initial
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

/**
 * Centre and open the popup
 * 
 * Version	Date		Developer				Description
 * 0.1		07/05/2012	Genevieve Turner (GT)	Initial
 */
jQuery("#itemLinkButton").live('click', function(){
	centrePopup();
	loadPopup();
});

/**
 * Close the popup when the close button as been clicked
 * 
 * Version	Date		Developer				Description
 * 0.1		07/05/2012	Genevieve Turner (GT)	Initial
 */
jQuery("#popupLinkClose").live('click', function(){
	disablePopup();
});

/**
 * Close the popup when the background has been clicked
 * 
 * Version	Date		Developer				Description
 * 0.1		07/05/2012	Genevieve Turner (GT)	Initial
 */
jQuery("#backgroundPopup").live('click', function() {
	disablePopup();
});

/**
 * Close the popup when the escape button has been pressed
 * 
 * Version	Date		Developer				Description
 * 0.1		07/05/2012	Genevieve Turner (GT)	Initial
 */
jQuery(document).keypress(function(e) {
	if(e.keyCode==27 && popupStatus==1) {
		disablePopup();
	}
});

/**
 * Perform an ajax call when the add link form is submitted
 * 
 * Version	Date		Developer				Description
 * 0.1		07/05/2012	Genevieve Turner (GT)	Initial
 * 0.3		29/05/2012	Genevieve Turner (GT)	Added error notification
 */
jQuery("#formAddLink").live('submit', function() {
	var hash = getURLVars();
	var urlStr = "/DataCommons/rest/display/addLink?item=" + hash['item'];
	var typeStr = jQuery("#linkType").val();
	var itemStr = jQuery("#itemId").val();
	var dataString = 'linkType=' + typeStr + '&itemId=' + itemStr;
	jQuery.ajax({
		type: "POST",
		url: urlStr,
		data: dataString,
		success: function() {
			disablePopup();
		},
		error: function() {
			alert('Error Adding Link');
		}
	});
});

// Status of the popup
var popupStatus = 0;

/**
 * loadPopup
 * 
 * Display the popup on the screen
 * 
 * Version	Date		Developer				Description
 * 0.1		07/05/2012	Genevieve Turner (GT)	Initial
 */
function loadPopup() {
	if (popupStatus == 0) {
		// Clear the values when the popup is opened
		jQuery("#popupLink").find("input:text").val('');
		
		jQuery("#backgroundPopup").css ({
			"opacity": "0.7"
		});
		jQuery("#backgroundPopup").fadeIn("slow");
		jQuery("#popupLink").fadeIn("slow");
		popupStatus = 1;
	}
}

/**
 * centrePopup
 * 
 * Centre the popup in the screen
 * 
 * Version	Date		Developer				Description
 * 0.1		07/05/2012	Genevieve Turner (GT)	Initial
 */
function centrePopup() {
	var windowWidth = document.documentElement.clientWidth;
	var windowHeight = document.documentElement.clientHeight;
	var popupHeight = jQuery("#popupLink").height();
	var popupWidth = jQuery("#popupLink").width();
	
	jQuery("#popupLink").css({
		"position": "absolute",
		"top": windowHeight/2-popupHeight/2,
		"left": windowWidth/2-popupWidth/2
	});
	
	jQuery("#backgroundPopup").css({
		"height": windowHeight
	});
}

/**
 * disablePopup
 * 
 * Close the popup
 * 
 * Version	Date		Developer				Description
 * 0.1		07/05/2012	Genevieve Turner (GT)	Initial
 */
function disablePopup() {
	if (popupStatus == 1) {
		jQuery("#backgroundPopup").fadeOut("slow");
		jQuery("#popupLink").fadeOut("slow");
	}
	popupStatus = 0;
}
