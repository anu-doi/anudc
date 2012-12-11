/**
 * publish.js
 * 
 * Australian National University Data Commons
 * 
 * Javascript file that includes some functions for the mass publication/verification screens
 * 
 * Version	Date		Developer			Description
 * 0.1		10/12/2012	Genevieve Turner	Initial
 */

/**
 * Performs functions when the html document has completed and is ready.
 * Notably ensuring validation occurs on submit.
 * 
 * Version	Date		Developer			Description
 * 0.1		10/12/2012	Genevieve Turner	Initial
 */
jQuery(document).ready(function()
{
	jQuery("#validateForm").submit(function() {
		return jQuery('#validateForm').validate().form();
	});
	
	jQuery("#publishForm").submit(function() {
		return jQuery('#publishForm').validate().form();
	});
});

/**
 * selectAll
 * 
 * Select all the checkboxes with the  given name
 * 
 * Version	Date		Developer			Description
 * 0.1		10/12/2012	Genevieve Turner	Initial
 * 
 * @param value The name of the field to select all checkboxes for
 */
function selectAll(value) {
	console.log('Select All: ' + value);
	jQuery("input[name='" + value + "']").attr('checked', true);
};

/**
 * deselectAll
 * 
 * De-select all the checkboxes with the given name
 * 
 * Version	Date		Developer			Description
 * 0.1		10/12/2012	Genevieve Turner	Initial
 * 
 * @param value The name of the field to de-select all checkboxes for
 */
function deselectAll(value) {
	console.log('De-select All: ' + value);
	jQuery("input[name='" + value + "']").attr('checked', false);
};