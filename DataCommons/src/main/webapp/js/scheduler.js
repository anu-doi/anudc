jQuery(document).ready(function() {
	jQuery("#report").trigger('change');
	
	jQuery("#form").validate({
		rules: {
			hour: {
				required: true,
				range: [0, 23]
			},
			minute: {
				required: true,
				range: [0, 59]
			},
			dayOfWeek: {
				required: true
			},
			report: {
				required: true
			},
			email: {
				required: true
			}
		}
		/*submitHandler: function(form) {
			form.submit();
		}*/
	});
});

jQuery("#report").live('change', function() {
	console.log('report is changed');
	var reportId = jQuery("#report").val();
	if (reportId == '') {
		var reportParams = jQuery("#reportparams");
		reportParams.html('No Report Selected');
	}
	else {
		console.log(reportId);
		var url = "/DataCommons/rest/report/schedule/report-param/" + reportId;
		console.log(url);
		jQuery.ajax({
			url: url,
			type: "GET",
			contentType: "application/json",
			success: function(data) {
				console.log('Successful');
				processData(data);
			}
		});
	}
});

function processData(data) {
	console.log('In process data');
	console.log(data);
	var reportParams = jQuery("#reportparams");
	reportParams.html('');
	
	var table = jQuery("<table/>");

	jQuery.map(data, function(item, i) {
		if (item.paramName == 'pid') {
			processPid(item, reportParams);
		}
		else if (item.requestParam == 'rid') {
			processRid(item, reportParams);
		}
		else if (item.requestParam == 'groupId') {
			processGroupId(item, reportParams);
		}
		else if (item.paramName == 'name' || item.defaultValue != '') {
			//Do nothing
		}
		else {
			var tr = jQuery("<tr/>");
			tr.append(jQuery("<td/>").text(item.id.seqNum));
			tr.append(jQuery("<td/>").text(item.paramName));
			tr.append(jQuery("<td/>").text(item.requestParam));
			tr.append(jQuery("<td/>").text(item.defaultValue));
			table.append(tr);
		}
	});
	reportParams.append(table);
}

function processPid(item, reportParams) {
	reportParams.append('<label for="pid"/>').text('Identifier');
	reportParams.append(' ');
	reportParams.append('<input id="pid" name="pid"/>');
}

function processRid(item, reportParams) {
	var label = jQuery('<label/>').attr('for', item.paramName).text('Web Service Id');
	reportParams.append(label);
	reportParams.append(' ');
	var input = jQuery('<input/>').attr('id', item.paramName).attr('name', item.paramName);
	reportParams.append(input);
	reportParams.append("<br/>");
}

function processGroupId(item, reportParams) {
	var label = jQuery('<label/>').attr('for', item.paramName).text("Group");
	reportParams.append(label);
	reportParams.append(' ');
	
	var select = jQuery('<select/>').attr('id', item.paramName).attr('name', item.paramName);
	var baseOption = jQuery('<option/>').attr('value', "").text("-- No Value Selected --");
	select.append(baseOption);
	
	jQuery.ajax({
		url: "/DataCommons/rest/report/schedule/groups",
		type: "GET",
		contentType: "application/json",
		success: function(data) {
			console.log('Successful');
			jQuery.map(data, function(option, i) {
				console.log(option);
				var option = jQuery('<option/>').attr('value', option.id).text(option.name);
				select.append(option);
			});
		}
	});
	reportParams.append(select);
	//reportParams.append('<label for="groupId"/>').text();
}

function deleteReportAuto(delUrl) {
	if (confirm("Are you sure you want to unschedule the running of this report?")) {
		jQuery.ajax( {
			url: delUrl,
			type: "DELETE"
		}).fail(function() {
			alert("Unable to delete automated report.");
		}).always(function() {
			window.location = window.location.href.split("?")[0];
		});
	}
}