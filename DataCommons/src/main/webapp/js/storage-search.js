jQuery(document).ready(function()
{
	jQuery(".show-section").on('click', function() {
		console.log('aaa');
		var dataSection = jQuery(this).attr('data-section');
		console.log(dataSection);
		if (jQuery(this).prop("checked")) {
			console.log('Is checked');
			jQuery("#" + dataSection).removeClass('hidden');
		}
		else {
			console.log('is not checked');
			jQuery("#" + dataSection).addClass('hidden');
		}
	});
});