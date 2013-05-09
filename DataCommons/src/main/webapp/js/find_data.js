var findDataPopupStatus = 0;

jQuery("#findInfo").live('click', function() {
	centrePopup("#popupFindData");
	findDataPopupStatus = loadPopup("#popupFindData", findDataPopupStatus);
});

jQuery("#popupFindDataClose").live('click', function() {
	findDataPopupStatus = disablePopup("#popupFindData", findDataPopupStatus);
});

jQuery("#backgroundPopup").live('click', function() {
	findDataPopupStatus = disablePopup("#popupFindData", findDataPopupStatus);
});

jQuery(document).keypress(function(e) {
	if(e.keyCode==27 && findDataPopupStatus==1) {
		findDataPopupStatus = disablePopup("#popupFindData", findDataPopupStatus);
	}
});

jQuery("#searchData").live('click', function() {
	//Ensure that it works for internet explorer.  Though it does appear to bring up a security dialog for it...
	jQuery.support.cors = true;
	jQuery.ajax({
		type: "GET",
		url: "http://localhost:8180/services/rest/person/search",
		dataType: "json",
		data: {
			email: jQuery("#findEmail").val(),
			"given-name": jQuery("#findGiven").val(),
			surname: jQuery("#findSurname").val(),
			jsoncallback: "people"
		},
		success: function(data) {
			jQuery("#findDataContent").html('');
			var table = jQuery("<table></table>");
			if (data.entity.length == 0) {
				jQuery('#findDataContent').text('No Results Found');
			}
			else {
				jQuery.each(data.entity, function(index, item) {
					var row = jQuery('<tr></tr>');
	
					var radioButton = jQuery('<input type="radio" name="selectPerson" />').val(index);
					
					row.append(jQuery('<td></td>').html(radioButton));
					row.append(jQuery('<td></td>').text(item.email));
					row.append(jQuery('<td></td>').text(item['given-name']));
					row.append(jQuery('<td></td>').text(item.surname));
					row.append(jQuery('<td class="hidden"></td>').text(item['nla-id']));
					row.append(jQuery('<td class="hidden"></td>').text(item['phone']));
					row.append(jQuery('<td class="hidden"></td>').text(item['fax']));
					row.append(jQuery('<td class="hidden"></td>').text(item['description']));
					var subjects = '';
					jQuery.each(item['for-subject'], function(index, item) {
						if (subjects != '') {
							subjects = subjects + '|' + item.code;
						}
						else {
							subjects = item.code;
						}
					});
					row.append(jQuery('<td class="hidden"></td>').text(subjects));
					table.append(row);
					
				});
				jQuery('#findDataContent').html(table);
			}
		},
		error: function(jqXHR, textStatus) {
			alert('Error retrieving data');
		}
	});
});

jQuery("#selectData").live('click', function() {
	var selectedRow = jQuery("input[name='selectPerson']:checked").closest('tr');
	var email = selectedRow.find('td').eq('1').text();
	var givenName = selectedRow.find('td').eq('2').text();
	var surname = selectedRow.find('td').eq('3').text();
	var nlaId = selectedRow.find('td').eq('4').text();
	var phoneNumbers = selectedRow.find('td').eq('5').text();
	var faxNumbers = selectedRow.find('td').eq('6').text();
	var description = selectedRow.find('td').eq('7').text();
	var forSubjects = selectedRow.find('td').eq('8').text();
	
	jQuery("input[name='email']").val(email);
	jQuery("input[name='givenName']").val(givenName);
	jQuery("input[name='lastName']").val(surname);
	jQuery("input[name='nlaIdentifier']").val(nlaId);
	jQuery("input[name='phone']").val(phoneNumbers);
	jQuery("input[name='fax']").val(faxNumbers);
	setMultiTextRowData('phone', phoneNumbers);
	setMultiTextRowData('fax', faxNumbers);
	setSubjectRowData(forSubjects);
	var descriptionLength = jQuery("textarea[name='fullDesc']").val().length;
	if (descriptionLength > 0 && description.length > 0) {
		if (confirm("Override the current description?\n\nPlease note that pressing cancel will still\npopulate the other fields but it will not\nreplace the current description")) {
			jQuery("textarea[name='fullDesc']").val(description);
		}
	}
	else if (description.length > 0) {
		jQuery("textarea[name='fullDesc']").val(description);
	}
	
	findDataPopupStatus = disablePopup("#popupFindData", findDataPopupStatus);
});

function setMultiTextRowData(fieldname, strlist) {
	jQuery("input[name='" + fieldname + "']").val('');
	values = strlist.split(',');
	
	var rowCount = jQuery("#" + fieldname + " tr").length;
	
	//Ensure there are enough rows
	if (rowCount < values.length) {
		for (var i = rowCount; i < values.length; i++) {
			addTableRow(fieldname);
		}
	}
	
	//Set the values
	for (var i = 0; i < values.length; i++) {
		if (values[i].length == 5) {
			values[i] = '612' + values[i];
		}
		jQuery("#" + fieldname + " tr:eq(" + i + ") input[name='"+ fieldname +"']").val(values[i]);
	}
}

function setSubjectRowData(strlist) {
	values = strlist.split('|');
	jQuery("#anzforSubject").empty();
	for (var i = 0; i < values.length; i++) {
		var value = values[i];
		
		var str = value.match(/(.*)0{4}$/);
		
		var codeVal = '0'
		
		if (str) {
			codeVal = str[1];
		}
		else {
			str = value.match(/(.*)0{2}$/);
			if (str) {
				codeVal = str[1];
			}
			else {
				codeVal = value;
			}
		}
		
		var text = jQuery("#anzforSubject2 option[value='" + codeVal + "']").text();
		jQuery("#anzforSubject").append(jQuery('<option>', { value: codeVal, text : text }));
	}
}
