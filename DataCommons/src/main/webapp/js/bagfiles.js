function tabSelect(el, containerId)
{
	jQuery('.pagetabs-nav > ul > li > a[href]').removeClass('pagetabs-select');
	jQuery(el).addClass('pagetabs-select');

	// Hide containers for tabs.
	jQuery('#info').hide();
	jQuery('#files').hide();
	jQuery('#extRefs').hide();

	// Show the container corresponding to the tab selected.
	jQuery(containerId).show();

	return;
}
