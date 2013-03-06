
// jQuery(document).ready(function() {
//	    var hashVal = window.location.hash.split("#")[1];
//	    if(hashVal == 'live') {
//	        $("#live").show();
//	    }
// });

function documentReady() {
	jQuery('div.pagetabs-nav > ul').each(function(){
	    // For each set of tabs, we want to keep track of
	    // which tab is active and it's associated content
	    var $active, $content, $links = jQuery(this).find('a');

	    // If the location.hash matches one of the links, use that as the active tab.
	    // If no match is found, use the first link as the initial active tab.
	    $active = jQuery($links.filter('[href="'+location.hash+'"]')[0] || $links[0]);
	    $active.addClass('pagetabs-select');
	    $content = jQuery($active.attr('href'));

	    // Hide the remaining content
	    $links.not($active).each(function () {
	        jQuery(jQuery(this).attr('href')).hide();
	    });

	    // Bind the click event handler
	    jQuery(this).on('click', 'a', function(e){
	        // Make the old tab inactive.
	        $active.removeClass('pagetabs-select');
	        $content.hide();

	        // Update the variables with the new link and content
	        $active = jQuery(this);
	        $content = jQuery(jQuery(this).attr('href'));

	        // Make the tab active.
	        $active.addClass('pagetabs-select');
	        $content.show();

	        // Prevent the anchor's default click action
	        e.preventDefault();
	    });
	});
	if (window.location.hash == "") {
		jQuery('div.pagetabs-nav > ul > li > a')[0].click();
	} else {
		jQuery('div.pagetabs-nav > ul > li > a[href=' + window.location.hash + ']').click();
	}
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
