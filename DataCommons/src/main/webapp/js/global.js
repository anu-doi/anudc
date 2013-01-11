/**
 * global.js
 * 
 * Australian National University Data Comons
 * 
 * Global javascript file to be included in all HTML documents. Contains global constants and utility methods. 
 * 
 * Version	Date		Developer			Description
 * 0.1		08/03/2012	Rahul Khanna		Initial
 * 0.2		20/03/2012	Genevieve Turner	Added functions for adding/removing rows
 * 0.3		26/03/2012	Genevieve Turner	Removed existing functions for adding/removing rows and changed to use jQuery
 * 0.4		07/05/2012	Genevieve Turner	Removed add link functionality from global.js
 * 0.5		20/06/2012	Genevieve Turner	Added remove selected function
 * 0.6		14/09/2012	Genevieve Turner	Updated tab selection code
 */

/**
 * Performs functions when the html document has completed and is ready. Currently this disables the submit button on the edit page.
 * 
 * Version	Date		Developer			Description
 * 0.3		26/03/2012	Genevieve Turner	Created function
 */
jQuery(document).ready(function()
{
	jQuery("#editSubmit").attr("disabled", "disabled");
	
	jQuery("#tabs li a:first").addClass('pagetabs-select');
	
	// Load the selected tab when it has been clicked.
	jQuery("#tabs li").click(function() {
		jQuery("#tabs a").removeClass('pagetabs-select');
		jQuery(this).find("a").addClass('pagetabs-select');
		jQuery(".tab-content").hide();
		var selected_tab = jQuery(this).find("a").attr("href");
		jQuery(selected_tab).fadeIn();
		return false;
	});
	jQuery("#tabs a.pagetabs-select").each(function(key,value) {
		var selected_tab = jQuery(value).attr("href");
		jQuery(selected_tab).fadeIn();
	});
});

/**
 * When the edit drop down lists selected value changes this event fires. It performs an ajax request that returns html to add to the screen.
 * 
 * Version	Date		Developer			Description
 * 0.3		26/03/2012	Genevieve Turner	Created function
 */
jQuery("#editSelect").live('change', function()
{
	var aURL = window.location.href.slice(0, window.location.href.indexOf('?'));
	var paramhash = getURLVars();
	//TODO could potentially move this so its not a hard coded value?
	paramhash['layout'] = 'def:edit';
	params = jQuery.param(paramhash);
	var requestURL = aURL + "/" + jQuery(this).val() + "?" + params;
	jQuery.ajax(
	{
		type : "GET",
		url : requestURL,
		cache : false,
		success : processReturn
	});
});

/**
 * getURLVars
 * 
 * Returns a map of parameters
 * 
 * Version	Date		Developer			Description
 * 0.3		26/03/2012	Genevieve Turner	Created function
 * 
 */
function getURLVars()
{
	var vars = {}, hash;
	var hashes = window.location.href.slice(window.location.href.indexOf('?') + 1).split('&');
	for ( var i = 0; i < hashes.length; i++)
	{
		hash = hashes[i].split('=');
		vars[hash[0]] = hash[1];
	}
	return vars;
}

/**
 * processReturn Adds the given values to the html document.
 * 
 * Version	Date		Developer			Description
 * 0.3		26/03/2012	Genevieve Turner	Created function
 * 
 * @param xml
 *            The fields to add to the document.
 */
function processReturn(xml)
{
	jQuery("#extraFields").empty();
	jQuery("#extraFields").append(xml);
	jQuery("#editSubmit").removeAttr("disabled");
	jQuery('#anzforSubject2').combobox();
	jQuery('#anzseoSubject2').combobox();
}

/**
 * Takes an integer as a parameter and formats the integer with commas as thousand separators. E.g. 123456789 returns 123,456,789.
 * 
 * @param nStr
 *            A number as string. E.g. "123456"
 * @returns A formatted integer as string. E.g. "123,456".
 */
function groupDigits(nStr)
{
	nStr += '';
	x = nStr.split('.');
	x1 = x[0];
	x2 = x.length > 1 ? '.' + x[1] : '';
	var rgx = /(\d+)(\d{3})/;
	while (rgx.test(x1))
	{
		x1 = x1.replace(rgx, '$1' + ',' + '$2');
	}
	return x1 + x2;
}

/**
 * Adds a new row to the specified table
 * 
 * Version	Date		Developer			Description
 * 0.3		26/03/2012	Genevieve Turner	Created function
 * 
 * @param tableName
 *            The name of the table to add a row to
 */
function addTableRow(tableName)
{
	var newRow = jQuery('#' + tableName + ' tbody>tr:last').clone(true);
	newRow.find("input:text").val('');
	newRow.find("option:selected").removeAttr("selected");
	newRow.find("textarea").val('');
	newRow.insertAfter('#' + tableName + ' tbody>tr:last');
}

/**
 * Removes the table row of the clicked button. If it is the last row in the table then the values are cleared.
 * 
 * Version	Date		Developer			Description
 * 0.3		26/03/2012	Genevieve Turner	Created function
 * 
 * @param buttonField
 *            The button that was clicked
 */
function removeTableRow(buttonField)
{
	if (jQuery(buttonField).closest('table').find('tbody > tr').length > 1)
	{
		jQuery(buttonField).parent().parent().remove();
	}
	else
	{
		var tableRow = jQuery(buttonField).parent().parent();
		// Clear the values of the fields in the table
		tableRow.find("input:text").val('');
		tableRow.find("select option:first-child").attr("selected", "selected");
		tableRow.find("textarea").val('');
	}
}

/**
 * Removes the selected options from the given list
 * 
 * Version	Date		Developer			Description
 * 0.5		21/06/2012	Genevieve Turner	Created function
 * @param fieldName The name of the field to remove values from
 */
function removeSelected(fieldName) {
	jQuery("#" + fieldName + " option:selected").remove();
}