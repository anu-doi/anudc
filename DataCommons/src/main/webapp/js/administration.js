//jQuery(document).ready(function() {
//	
//});

var editPopupStatus = 0;

jQuery(".edit-group").live('click', function() {
	var groupId = jQuery(this).attr('data-id');
	var groupName = jQuery(this).attr('data-name');
	centrePopup("#popupEditGroup");
	editPopupStatus = loadPopup("#popupEditGroup", editPopupStatus);
	jQuery("#edit-group-id").val(groupId);
	jQuery("#edit-group-name").val(groupName);
});

jQuery(".edit-domain").live('click', function() {
	var domainId = jQuery(this).attr('data-id');
	var domainName = jQuery(this).attr('data-name');
	centrePopup(".popup-edit");
	editPopupStatus = loadPopup(".popup-edit", editPopupStatus);
	jQuery("#edit-domain-id").val(domainId);
	jQuery("#edit-domain-name").val(domainName);
});

//jQuery("#popupEditGroupClose").live('click', function(){
//	editGroupPopupStatus = disablePopup("#popupEditGroup", editGroupPopupStatus);
//});

//jQuery("#popupEditGroupClose").live('click', function(){
//	editPopupStatus = disablePopup("#popupEditGroup", editPopupStatus);
//});

jQuery(".popup-close").live('click', function(){
	editPopupStatus = disablePopup(".popup-edit", editPopupStatus);
});

jQuery(document).keypress(function(e) {
	if(e.keyCode==27 && editPopupStatus==1) {
		editPopupStatus = disablePopup(".popup-edit", editPopupStatus);
	}
});

jQuery("#backgroundPopup").live('click', function() {
	if (editPopupStatus == 1) {
		editPopupStatus = disablePopup(".popup-edit", editPopupStatus);
	}
});
