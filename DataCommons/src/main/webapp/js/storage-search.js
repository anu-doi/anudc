jQuery(document).ready(function()
{
	jQuery(document).on('click',".show-section", function() {
		var dataSection = jQuery(this).attr('data-section');
		if (jQuery(this).prop("checked")) {
			jQuery("#" + dataSection).removeClass('hidden');
		}
		else {
			jQuery("#" + dataSection).addClass('hidden');
		}
	});
});