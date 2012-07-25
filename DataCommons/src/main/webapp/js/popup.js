/**
 * popup.js
 * 
 * Australian National University Data Commons
 * 
 * Contains methods for loading and closing popups
 * 
 * Version	Date		Developer				Description
 * 0.1		25/07/2012	Genevieve Turner (GT)	Initial
 */

/**
 * loadPopup
 * 
 * Display the popup on the screen
 * 
 * Version	Date		Developer				Description
 * 0.1		24/07/2012	Genevieve Turner (GT)	Moved to popup.js and modified to be more general
 * 
 * @param id The id of the popup to load
 * @param popupStatus The status of the popup
 * @returns The status of the popup
 */
function loadPopup(id, popupStatus) {
	if (popupStatus == 0) {
		// Clear the values when the popup is opened
		jQuery(id).find("input:text").val('');
		jQuery(id).find("textarea").val('');
		
		jQuery("#backgroundPopup").css ({
			"opacity": "0.7"
		});
		jQuery("#backgroundPopup").fadeIn("slow");
		jQuery(id).fadeIn("slow");
		popupStatus = 1;
	}
	return popupStatus;
}

/**
 * centrePopup
 * 
 * Centres the popup in the screen
 * 
 * Version	Date		Developer				Description
 * 0.1		24/07/2012	Genevieve Turner (GT)	Moved to popup.js and modified to be more general
 * 
 * @param id The id of the popup to load
 * @returns The status of the popup
 */
function centrePopup(id) {
	var windowWidth = document.documentElement.clientWidth;
	var windowHeight = document.documentElement.clientHeight;
	var popupHeight = jQuery(id).height();
	var popupWidth = jQuery(id).width();
	
	jQuery(id).css({
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
 * 0.1		24/07/2012	Genevieve Turner (GT)	Moved to popup.js and modified to be more general
 * 
 * @param id The id of the popup to load
 * @param popupStatus The status of the popup
 * @returns The status of the popup
 */
function disablePopup(id, popupStatus) {
	if (popupStatus == 1) {
		jQuery("#backgroundPopup").fadeOut("slow");
		jQuery(id).fadeOut("slow");
	}
	popupStatus = 0;
	return popupStatus;
}
