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
	
	condEnableSelTasks();
	history.pushState(null, null, window.location.href.split("?")[0]);
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
			url: window.location.href + "/ispublic",
			type: "PUT",
			contentType: "text/plain",
			data: newFlag
	}).done(function(msg, status) {
		window.location = window.location.href + "?smsg=Files' public status changed successfully.";
	}).fail(function(msg, status) {
		alert("Unable to change Files Public status");
	});
}

function deleteSelected(pid) {
	var activeAjax = 0;
	jQuery("#tblFiles input:checkbox:checked").each(function(index) {
		if (this.value != "") {
			console.log("Deleting: " + this.value);
			var url = "/DataCommons/rest/upload/bag/" + encodeURI(pid) + "/" + this.value;
			activeAjax++;
			jQuery.ajax({
				url : url,
				type : "DELETE",
			}).fail(function() {
				alert('Unable to delete file ' + this.value);
			}).always(function() {
				if (--activeAjax == 0) {
					window.location = window.location.href;
				}
			});
		}
	});
}


function toggleCheckboxes(element) {
	jQuery('#tblFiles input:checkbox').not(element).prop('checked', element.checked);
	condEnableSelTasks();
}

function downloadAsZip(zipUrl) {
	jQuery("#tblFiles input:checkbox:checked").each(function(index) {
		if (index == 0) {
			zipUrl += "?";
		} else {
			zipUrl += "&";
		}
		zipUrl += "file=" + encodeURI(this.value);
	});
	window.location = zipUrl;
}

function condEnableSelTasks() {
	if (jQuery('#tblFiles input:checkbox:checked').length > 0) {
		jQuery("#idDelSelected").removeAttr("disabled");
		jQuery("#idDownloadZipSelected").removeAttr("disabled", "disabled");
	} else {
		jQuery("#idDelSelected").attr("disabled", "disabled");
		jQuery("#idDownloadZipSelected").attr("disabled", "disabled");
	}
}