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
		jQuery('img#loading').show();
		jQuery.ajax(
		{
			url : url,
			type : "DELETE",
		}).fail(function()
		{
			alert('Unable to delete file.');
		}).always(function()
		{
			window.location = window.location.href.split("?")[0];
		});
	}
}

function deleteExtRef(pid, extRefUrl)
{
	if (confirm("Are you sure you want to delete " + extRefUrl))
	{
		jQuery('img#loading').show();
		jQuery.ajax(
		{
			url : "/DataCommons/rest/upload/bag/" + encodeURI(pid) + "/extrefs/",
			type : "POST",
			data :
			{
				"deleteUrl" : extRefUrl
			}
		}).fail(function()
		{
			alert('Unable to delete file.');
		}).always(function()
		{
			window.location = window.location.href.split("?")[0];
		});
	}
}

function addExtRef(pid)
{
	var url = prompt("Please enter a URL:");
	if (url != null && url != "")
	{
		jQuery('img#loading').show();
		jQuery.ajax(
		{
			url : "/DataCommons/rest/upload/bag/" + encodeURI(pid) + "/extrefs/",
			type : "POST",
			data :
			{
				"addUrl" : url
			}
		}).fail(function()
		{
			alert('Unable to add url.');
		}).always(function()
		{
			window.location = window.location.href.split("?")[0];
		});
	}
}

function toggleIsFilesPublic(pid, curFlag) {
	var newFlag;
	if (curFlag == "false")
		newFlag = "true";
	else
		newFlag = "false";
	
	jQuery.ajax({
			url: "/DataCommons/rest/upload/bag/" + encodeURI(pid) + "/ispublic",
			type: "PUT",
			contentType: "text/plain",
			data: newFlag
	}).done(function(msg, status) {
		isFilesPublic = msg;
	}).fail(function(msg, status) {
		alert("Unable to retrieve Files Public status");
	});
	return isFilesPublic;
}
