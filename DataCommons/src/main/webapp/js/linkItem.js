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
 * Centre and open the popup
 */
jQuery("#itemLinkButton").live('click', function(){
	centrePopup();
	loadPopup();
});

/**
 * Close the popup when the close button as been clicked
 */
jQuery("#popupLinkClose").live('click', function(){
	disablePopup();
});

/**
 * Close the popup when the background has been clicked
 */
jQuery("#backgroundPopup").click(function() {
	disablePopup();
});

/**
 * Close the popup when the escape button has been pressed
 */
jQuery(document).keypress(function(e) {
	if(e.keyCode==27 && popupStatus==1) {
		disablePopup();
	}
});

/**
 * Perform an ajax call when the add link form is submitted
 */
jQuery("#formAddLink").live('submit', function() {
	var hash = getURLVars();
	var urlStr = "/DataCommons/rest/display/addLink?item=" + hash['item'];
	var typeStr = jQuery("#txtType").val();
	var itemStr = jQuery("#txtItem").val();
	var dataString = 'txtType=' + typeStr + '&txtItem=' + itemStr;
	jQuery.ajax({
		type: "POST",
		url: urlStr,
		data: dataString,
		success: function() {
			disablePopup();
		}
	});
});

// Status of the popup
var popupStatus = 0;

/**
 * loadPopup
 * 
 * Display the popup on the screen
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
 */
function disablePopup() {
	if (popupStatus == 1) {
		jQuery("#backgroundPopup").fadeOut("slow");
		jQuery("#popupLink").fadeOut("slow");
	}
	popupStatus = 0;
}