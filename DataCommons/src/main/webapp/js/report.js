/**
 * report.js
 * 
 * Australian National University Data Commons
 * 
 * Contains methods for the report page
 * 
 * Version	Date		Developer				Description
 * 0.1		25/07/2012	Genevieve Turner (GT)	Initial
 */

jQuery(document).ready(function() {
	jQuery("#itemSearch").autocomplete({
		source: function (request, response) {
			jQuery.ajax({
				url: "/DataCommons/rest/list/items",
				dataType: "json",
				data: {
					title: request.term,
					type: jQuery("#linkItemType").val()
				},
				success: function(data) {
					response ( jQuery.map(data.results, function(item, i) {
						return {
							label: item.title,
							value: item.item
						};
					}));
				}
			});
		},
		minLength: 2,
		open: function() {
			jQuery(this).removeClass("ui-corner-all").addClass("ui-corner-top");
		},
		close: function() {
			jQuery(this).removeClass("ui-corner-top").addClass("ui-corner-all");
		},
		select: function(event, ui) {
			var value = ui.item.value;
			var result = value.match(/info:fedora\/(.*)/)[1];
			jQuery("#name").val(ui.item.label);
			jQuery("#pid").val(result);
			return false;
		},
	    focus: function(event, ui) {
	    	jQuery("#itemSearch").val(ui.item.label);
	        return false; // Prevent the widget from inserting the value.
	    }
	});
});