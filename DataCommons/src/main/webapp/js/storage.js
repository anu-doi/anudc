function documentReady() {
	initTabs();
	initPing();
	initDragDrop();
	condEnableSelTasks();

	// jQuery("tr.file-row").on("click", selTblRow);
}

function initTabs() {
	jQuery("div.pagetabs-nav > ul").each(
			function() {
				// For each set of tabs, we want to keep track of
				// which tab is active and it's associated content
				var $active, $content, $links = jQuery(this).find("a");

				// If the location.hash matches one of the links, use that as
				// the active tab.
				// If no match is found, use the first link as the initial
				// active tab.
				$active = jQuery($links
						.filter('[href="' + location.hash + '"]')[0]
						|| $links[0]);
				$active.addClass("pagetabs-select");
				$content = jQuery($active.attr("href"));

				// Hide the remaining content
				$links.not($active).each(function() {
					jQuery(jQuery(this).attr("href")).hide();
				});

				// Bind the click event handler
				jQuery(this).on("click", "a", function(e) {
					// Make the old tab inactive.
					$active.removeClass("pagetabs-select");
					$content.hide();

					// Update the variables with the new link and content
					$active = jQuery(this);
					$content = jQuery(jQuery(this).attr("href"));

					// Make the tab active.
					$active.addClass("pagetabs-select");
					$content.show();

					// Prevent the anchor's default click action
					e.preventDefault();
				});
			});
	if (window.location.hash == "") {
		jQuery("div.pagetabs-nav > ul > li > a")[0].click();
	} else {
		jQuery(
				"div.pagetabs-nav > ul > li > a[href=" + window.location.hash
						+ "]").click();
	}
	history.replaceState(null, null, window.location.href.split("?")[0]);
}

function initPing() {
	if (jQuery("#uploadFiles").length > 0) {
		var intervalMillis = 25 * 60 * 1000;		// 25 Mins
		window.setInterval(pingServer, intervalMillis);
	}
}

function pingServer() {
	jQuery.ajax({
		url: window.location.href,
		type: "HEAD",
		timeout: 5000
	});
}


// Code from http://hayageek.com/drag-and-drop-file-upload-jquery/
function initDragDrop() {
	var obj = jQuery("#dragandrophandler");
	obj.on("dragenter", function(e) {
		e.stopPropagation();
		e.preventDefault();
		jQuery(this).css("border", "2px solid #0B85A1");
	});
	obj.on("dragover", function(e) {
		e.stopPropagation();
		e.preventDefault();
	});
	obj.on("drop", function(e) {

		jQuery(this).css("border", "2px dotted #0B85A1");
		e.preventDefault();
		var files = e.originalEvent.dataTransfer.files;
		handleFileUpload(files, obj);
	});

	jQuery(document).on("dragenter", function(e) {
		e.stopPropagation();
		e.preventDefault();
	});
	jQuery(document).on("dragover", function(e) {
		e.stopPropagation();
		e.preventDefault();
		obj.css("border", "2px dotted #0B85A1");
	});
	jQuery(document).on("drop", function(e) {
		e.stopPropagation();
		e.preventDefault();
	});
}

activeUploads = 0;
function sendFileToServer(file, status, hash) {
	var uploadURL = file.name; // Upload URL
	var extraData = {}; // Extra Data.
	var jqXHR = jQuery.ajax({
		xhr : function() {
			var xhrobj = jQuery.ajaxSettings.xhr();
			if (xhrobj.upload) {
				xhrobj.upload.addEventListener("progress", function(event) {
					var percent = 0;
					var position = event.loaded || event.position;
					var total = event.total;
					if (event.lengthComputable) {
						percent = Math.ceil(position / total * 100 / 2 + 50);
					}
					// Set progress
					status.setProgress(percent);
				}, false);
			}
			return xhrobj;
		},
		url : uploadURL,
		type : "POST",
		contentType : "application/octet-stream",
		headers: {"Content-MD5": hash},
		processData : false,
		cache : false,
		data : file,
		success : function(data) {
			status.setProgress(100);
			jQuery("#status1").append("File upload Done<br>");
			activeUploads--;
			if (activeUploads == 0) {
				window.location = window.location.href;
			}
		}
	});

	status.setAbort(jqXHR);
}


function handleFileUpload(files, obj) {
	for (var i = 0; i < files.length; i++) {
		var fd = new FormData();
		fd.append("file", files[i]);
		var status = new createStatusbar(obj);
		status.setFileNameSize(files[i].name, files[i].size);
		// sendFileToServer(files[i], status);
		activeUploads++;
		calcMd5(files[i], status, sendFileToServer);
	}
}

rowCount = 0;
function createStatusbar(obj) {
	rowCount++;
	var row = "odd";
	if (rowCount % 2 == 0) {
		row = "even";
	}
	this.statusbar = jQuery("<div class='statusbar " + row + "'></div>");
	this.filename = jQuery("<div class='filename'></div>").appendTo(
			this.statusbar);
	this.size = jQuery("<div class='filesize'></div>").appendTo(this.statusbar);
	this.progressBar = jQuery("<div class='progressBar'><div></div></div>")
			.appendTo(this.statusbar);
	this.abort = jQuery("<div class='abort'>Abort</div>").appendTo(
			this.statusbar);
	obj.after(this.statusbar);

	this.setFileNameSize = function(name, size) {
		var sizeStr = "";
		var sizeKB = size / 1024;
		if (parseInt(sizeKB) > 1024) {
			var sizeMB = sizeKB / 1024;
			sizeStr = sizeMB.toFixed(2) + " MB";
		} else {
			sizeStr = sizeKB.toFixed(2) + " KB";
		}

		this.filename.html(name);
		this.size.html(sizeStr);
	};
	this.setProgress = function(progress) {
		var progressBarWidth = progress * this.progressBar.width() / 100;
		this.progressBar.find("div").animate({
			width : progressBarWidth
		}, 10).html(progress + "% ");
		if (parseInt(progress) >= 100) {
			this.abort.hide();
		}
	};
	this.setAbort = function(jqxhr) {
		console.log(jqxhr);
		var sb = this.statusbar;
		this.abort.click(function() {
			jqxhr.abort();
			sb.hide();
		});
	};
}

function selTblRow(el) {
	if (jQuery(el.target).is("a,input,img,span")) {
		el.stopPropagation();
	} else {
		if (el.ctrlKey) {
			jQuery(this).toggleClass("selected-file");
			el.preventDefault();
		} else {
			console.log(el);
		}
	}

	updateActions();
}

function updateActions() {
	var selectedRows = jQuery("table tr.file-row.selected-file");

}

function deleteFile(url) {
	if (confirm("Are you sure you want to delete this file?")) {
		// jQuery("img#loading").show();
		jQuery.ajax({
			url : url,
			type : "DELETE",
		}).fail(function() {
			alert("Unable to delete file.");
		}).always(function() {
			window.location = window.location.href.split("?")[0];
		});
	}
}

function createDir() {
	var dirName = prompt("Folder Name: ");
	if (dirName != null && dirName != "") {
		var dirUri = dirName;
		jQuery.ajax({
			url : dirUri,
			type : "POST"
		}).fail(function() {
			alert("Unable to create folder " + dirName);
		}).always(function() {
			window.location = window.location.href;
		});
	}
}

function toggleCheckboxes(element) {
	jQuery(".tbl-files td input:checkbox").prop("checked", element.checked);
	condEnableSelTasks();
}

function condEnableSelTasks() {
	if (jQuery(".tbl-files td input:checkbox:checked").length > 0) {
		jQuery("#action-del-selected").removeClass("disabled");
		// jQuery("#idDownloadZipSelected").removeAttr("disabled", "disabled");
	} else {
		jQuery("#action-del-selected").addClass("disabled");
		// jQuery("#idDownloadZipSelected").attr("disabled", "disabled");
	}
}

function deleteSelected(pid) {
	if (jQuery(".tbl-files td input:checkbox:checked").length > 0) {
		jQuery("img#loading").show();
		var activeAjax = 0;
		jQuery(".tbl-files td input:checkbox:checked").each(function(index) {
			if (this.value != "") {
				console.log("Deleting: " + this.value);
				var url = this.value;
				activeAjax++;
				jQuery.ajax({
					url : url,
					type : "DELETE",
				}).fail(function() {
					alert("Unable to delete file " + this.value);
				}).always(function() {
					if (--activeAjax == 0) {
						window.location = window.location.href;
					}
				});
			}
		});
	}
}

function calcMd5(file, status, callback) {
	var md5 = CryptoJS.algo.MD5.create();
	var chunkSize = 2 * 1024;
	var nChunks = Math.ceil(file.size / chunkSize);
	var currentChunk = 0;
	var lastPercent = 0;
	var abort = false;
	
	var frOnload = function(e) {
		var percent = Math.ceil(currentChunk * 100 / nChunks / 2);
		if (lastPercent != percent && percent < 100) {
			lastPercent = percent;
			status.setProgress(percent);
		}
		if (!abort) {
			md5.update(CryptoJS.lib.WordArray.create(e.target.result));
			currentChunk++;
			
			if (currentChunk < nChunks) {
				loadNext();
			} else {
				var hash = md5.finalize();
				var hashHex = hash.toString(CryptoJS.enc.Hex);
				if (callback) {
					if (typeof(callback) === "function") {
						callback(file, status, hashHex);
					}
				} 
			}
		} 
	};
	
	function loadNext() {
		var reader = new FileReader();
		reader.onload = frOnload;
		reader.onerror = null;
		reader.onabort = function(e) {
			abort = true;
		};
		
		var start = currentChunk * chunkSize;
		var end = ((start + chunkSize) >= file.size) ? file.size : start + chunkSize;
		reader.readAsArrayBuffer(file.slice(start, end));
		// status.setAbort(reader);
	}
	
	loadNext();
}