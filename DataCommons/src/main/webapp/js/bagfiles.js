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

function deleteFile(url)
{
	if (confirm("Are you sure you want to delete this file?"))
	{
		jQuery.ajax(
		{
			url : url,
			type : "DELETE",
		}).done(function()
		{
			alert('Deleted successfully.');
		}).fail(function()
		{
			alert('Unable to delete file.');
		}).always(function()
		{
			window.location.reload();
		});
	}
}

function deleteExtRef(pid, extRefUrl)
{
	if (confirm("Are you sure you want to delete " + extRefUrl))
	{
		jQuery.ajax(
		{
			url : "/DataCommons/rest/upload/bag/" + encodeURI(pid) + "/extrefs/",
			type : "POST",
			data :
			{
				"deleteUrl" : extRefUrl
			}
		}).done(function()
		{
			alert('Deleted successfully.');
		}).fail(function()
		{
			alert('Unable to delete file.');
		}).always(function()
		{
			window.location.reload();
		});
	}
}

function addExtRef(pid)
{
	var url = prompt("Please enter a URL:");
	if (url != null && url != "")
	{
		jQuery.ajax(
		{
			url : "/DataCommons/rest/upload/bag/" + encodeURI(pid) + "/extrefs/",
			type : "POST",
			data :
			{
				"addUrl" : url
			}
		}).done(function()
		{
			alert('Added successfully.');
		}).fail(function()
		{
			alert('Unable to add url.');
		}).always(function()
		{
			window.location.reload();
		});
	}
}
