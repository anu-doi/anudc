jQuery(document).ready(function()
{
	jQuery(".show-section").on('click', function() {
		var dataSection = jQuery(this).attr('data-section');
		if (jQuery(this).prop("checked")) {
			jQuery("#" + dataSection).removeClass('hidden');
		}
		else {
			jQuery("#" + dataSection).addClass('hidden');
		}
	});
});