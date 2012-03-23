/**
 * Takes an integer as a parameter and formats the integer with commas as thousand separators. E.g. 123456789 returns 123,456,789.
 * 
 * @param nStr
 *            The number to be formatted as string. For example, "123456789.12345"
 * @returns Formatted number as string. For example, "123,456,789.12345"
 */
function addCommas(nStr)
{
	// Make nStr a string if it isn't already.
	nStr += '';
	// Extract the numbers after the decimal as they don't need formatting. - "yyy" in "xxxx.yyy"
	x = nStr.split('.');
	var preDec = x[0];
	var postDec = x.length > 1 ? '.' + x[1] : '';
	var rgx = /(\d+)(\d{3})/;
	while (rgx.test(preDec))
	{
		preDec = preDec.replace(rgx, '$1' + ',' + '$2');
	}
	return preDec + postDec;
}

function getProgressStatusAjax()
{
	// TODO Fix hard coding.
	var servletUrl = "/DataCommons" + "/upload/upload.do?rand=" + (Math.random() * 100000000000000000);
	var reqFreqInMs = 200;
	var ajaxRequest;

	if (window.XMLHttpRequest)
	{
		ajaxRequest = new XMLHttpRequest();
	}
	else if (window.ActiveXObject)
	{
		ajaxRequest = new ActiveXObject("Microsoft.XMLHTTP");
	}

	ajaxRequest.onreadystatechange = function()
	{
		// readyState 4 = request finished and response is ready
		// status 200 = HTTP "OK"
		if (ajaxRequest.readyState == 4 && ajaxRequest.status == 200)
		{
			var xml = ajaxRequest.responseXML;
			var isNotFinished = xml.getElementsByTagName("finished")[0];
			var myBytesRead = xml.getElementsByTagName("bytes_read")[0];
			var myContentLength = xml.getElementsByTagName("content_length")[0];
			var myPercent = xml.getElementsByTagName("percent_complete")[0];

			if ((isNotFinished == null) && (myPercent == null))
			{
				document.getElementById("idStatusText").innerHTML = "Initialising...";
				window.setTimeout("getProgressStatusAjax();", reqFreqInMs);
			}
			else
			{
				myBytesRead = myBytesRead.firstChild.data;
				myContentLength = myContentLength.firstChild.data;

				if (myPercent == null)
					myPercent = "100";
				else
					myPercent = myPercent.firstChild.data;

				document.getElementById("idProgressBarFill").style.width = myPercent + "%";
				document.getElementById("idStatusText").innerHTML = myPercent + "%. " + addCommas(Math.round(myBytesRead / 1024)) + " KB of "
						+ addCommas(Math.round(myContentLength / 1024)) + "KB.";

				if (myPercent < 100)
					window.setTimeout("getProgressStatusAjax();", reqFreqInMs);
			}
		}
	};

	// Send the AJAX request.
	ajaxRequest.open("GET", servletUrl, true);
	ajaxRequest.send();
}
