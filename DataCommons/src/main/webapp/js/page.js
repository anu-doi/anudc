/**
 * page.js
 * 
 * Australian National University Data Commons
 * 
 * Global javascript file to be included in all HTML documents. Contains global constants and utility methods. 
 * 
 * Version	Date		Developer			Description
 * 0.1		20/06/2012	Genevieve Turner	Added remove selected function
 * 0.2		02/07/2012	Genevieve Turner	Updated submission function as validation was not correctly occuring
 * 0.3		15/08/2012	Genevieve Turner	Updated to make validation work correctly with the changes to using tabs
 */

/**
 * Performs functions when the html document has completed and is ready. Currently this disables the submit button on the edit page.
 * 
 * Version	Date		Developer			Description
 * 0.1		26/03/2012	Genevieve Turner	Created function
 * 0.2		02/07/2012	Genevieve Turner	Updated submission function as validation was not correctly occuring
 * 0.3		15/08/2012	Genevieve Turner	Updated to make validation work correctly with the changes to using tabs (added validator.setDefaults function)
 * 0.4		13/09/2012	Genevieve Turner	Now clears fields when a value is selected.
 * 0.5		17/09/2012	Genevieve Turner	Addeds a span with an error image
 */
jQuery(document).ready(function()
{
	jQuery(function(){
		jQuery('#anzforSubject2').combobox();
		jQuery('#anzseoSubject2').combobox();
	});

	jQuery.validator.setDefaults({
		ignore: "",
		showErrors: function(errorMap, errorList) {
			this.defaultShowErrors();
			jQuery('.pagetabs-nav span').remove();
			jQuery(".tab-content").each(function() {
				var errorCount = jQuery(this).find(".error").size();
				var labelErrorCount = jQuery(this).find("label.error").size();
				if ((errorCount - labelErrorCount) > 0) {
					var span = jQuery("<span title='" + (errorCount - labelErrorCount) + " field(s) require further attention'> <img src='/DataCommons/images/error.png' /></span>");
					jQuery('#tab-' + this.id).append(span);
				}
			});
		}
	});
	
	jQuery("#form").submit(function() {
		//for some reason this doesn't work?
		//jQuery("select[multiple='multiple'] option").attr("selected", "true");
		
		jQuery("#anzforSubject option").attr("selected", "true");
		jQuery("#anzseoSubject option").attr("selected", "true");
		return jQuery('#form').validate().form();
	});
	
	(function(jQuery) {
		jQuery.widget("ui.combobox", {
			_create: function() {
				var input,
					self = this,
					select = this.element,
					selected = select.children(":selected"),
					value = selected.val() ? selected.text() : "",
					wrapper = jQuery ("<span style=\"white-space: nowrap; \"></span>")
						.addClass ("ui-combobox")
						.insertAfter (select);
				select.hide();
				input = jQuery ("<input>")
					.appendTo(wrapper)
					.val (value)
					.addClass ("ui-state-default")
					.autocomplete({
						delay: 0,
						minLength: 0,
						source: function (request, response) {
							var matcher = new RegExp (jQuery.ui.autocomplete.escapeRegex(request.term), "i");
							response (select.children ("option").map(function() {
								var text = jQuery(this).text();
								if (this.value && (!request.term || matcher.test(text))) {
									return {
										label: text.replace(
											new RegExp(
												"(?![^&;]+;)(?!<[^<>]*)(" +
												jQuery.ui.autocomplete.escapeRegex(request.term) +
												")(?![^<>]*>)(?![^&;]+;)", "gi"
											), "<strong>$1</strong>" ),
										value: text,
										option: this
									};
								}
							}));
						},
						select: function(event, ui) {
							var id = jQuery(select).attr('id');
							var matcher = new RegExp ("2$");
							if (matcher.test(id)) {
								var selectId = "#" + id.substring(0,id.length - 1);
								var key = ui.item.option.value;
								var value = ui.item.option.text;
								jQuery(selectId).append(jQuery('<option>', { value : key }).text(value));
								jQuery(".ui-combobox input").val("");
								return false;
							}
							else {
								self._trigger("selected", event, {
									item: ui.item.option
								});
							}
						},
						change: function(event, ui) {
							if (!ui.item) {
								var matcher = new RegExp ("^" + jQuery.ui.autocomplete.escapeRegex (jQuery(this).val()) + "$"),
									valid = false;
								select.children("option").each(function() {
									if (jQuery(this).text().match(matcher)) {
										this.selected = valid = true;
										return false;
									}
								});
								if (!valid) {
									jQuery(this).val("");
									select.val("");
									input.data("autocomplete").term = "";
									return false;
								}
							}
						}
					})
					.addClass("ui-widget ui-widget-content ui-corner-left");
				input.data("autocomplete")._renderItem = function(ul, item) {
					return jQuery("<li></li>")
						.data("item.autocomplete", item)
						.append("<a>" + item.label + "</a>")
						.appendTo(ul);
				};
			},
			destroy: function() {
				this.wrapper.remove();
				this.element.show();
				jQuery.Widget.prototype.destroy.call(this);
			}
		});
	})(jQuery);
});
