//jQuery(document).ready(function() {
//	
//});

var editPopupStatus = 0;

jQuery(document).on('click', ".edit-group", function() {
	var groupId = jQuery(this).attr('data-id');
	var groupName = jQuery(this).attr('data-name');
	jQuery("#edit-group-id").val(groupId);
	jQuery("#edit-group-name").val(groupName);
});

jQuery(document).on('click', ".edit-domain", function() {
	var domainId = jQuery(this).attr('data-id');
	var domainName = jQuery(this).attr('data-name');
	jQuery("#edit-domain-id").val(domainId);
	jQuery("#edit-domain-name").val(domainName);
});

//jQuery("#popupEditGroupClose").live('click', function(){
//	editGroupPopupStatus = disablePopup("#popupEditGroup", editGroupPopupStatus);
//});

//jQuery("#popupEditGroupClose").live('click', function(){
//	editPopupStatus = disablePopup("#popupEditGroup", editPopupStatus);
//});

/*jQuery(document).on('click', ".popup-close", function(){
	editPopupStatus = disablePopup(".popup-edit", editPopupStatus);
});

jQuery(document).keypress(function(e) {
	if(e.keyCode==27 && editPopupStatus==1) {
		editPopupStatus = disablePopup(".popup-edit", editPopupStatus);
	}
});

jQuery(document).on('click', "#backgroundPopup", function() {
	if (editPopupStatus == 1) {
		editPopupStatus = disablePopup(".popup-edit", editPopupStatus);
	}
});*/
