/**
 * popup.js
 * 
 * Australian National University Data Commons
 * 
 * Contains methods for the reject changes popup
 * 
 * Version	Date		Developer				Description
 * 0.1		25/07/2012	Genevieve Turner (GT)	Initial
 */

var rejectPopupStatus = 0;

/**
 * Opens popup when button is clicked
 * 
 * Version	Date		Developer				Description
 * 0.1		25/07/2012	Genevieve Turner (GT)	Initial
 */
jQuery("#rejectReasonButton").live('click', function() {
	centrePopup("#rejectPopup");
	rejectPopupStatus = loadPopup("#rejectPopup", rejectPopupStatus);
});

/**
 * Closes popup when link is clicked
 * 
 * Version	Date		Developer				Description
 * 0.1		25/07/2012	Genevieve Turner (GT)	Initial
 */
jQuery("#rejectPopupClose").live('click', function() {
	rejectPopupStatus = disablePopup("#rejectPopup", rejectPopupStatus);
});

/**
 * Closes popup when background is clicked
 * 
 * Version	Date		Developer				Description
 * 0.1		25/07/2012	Genevieve Turner (GT)	Initial
 */
jQuery("#backgroundPopup").live('click', function() {
	rejectPopupStatus = disablePopup("#rejectPopup", rejectPopupStatus);
});

/**
 * Closes popup when escape is pressed
 * 
 * Version	Date		Developer				Description
 * 0.1		25/07/2012	Genevieve Turner (GT)	Initial
 */
jQuery(document).keypress(function(e) {
	if(e.keyCode==27 && rejectPopupStatus==1) {
		rejectPopupStatus = disablePopup("#rejectPopup", rejectPopupStatus);
	}
});