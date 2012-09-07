function tabSelect(el, containerId)
{
	jQuery('#tabs').find('*').removeClass('pagetabs-select');
	jQuery(el).addClass('pagetabs-select');

	// Hide containers for tabs.
	jQuery('#info').hide();
	jQuery('#files').hide();

	// Show the container corresponding to the tab selected.
	jQuery(containerId).show();

	return false;
}
