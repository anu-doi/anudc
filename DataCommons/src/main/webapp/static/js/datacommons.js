$(document).ready(function(){
	$(".btn-add").click(function() {
		var clonedRow = $(this).closest(".form-group").find(".input-group:last").clone();
		clonedRow.find("input:input").val('');
		clonedRow.find("select option:first-child").attr("selected", "selected");
		clonedRow.find("textarea").val('');
		
		var lastRow = $(this).closest(".form-group").find(".input-group:last");
		clonedRow.insertAfter(lastRow);
	});
	$(".form-group").on('click','.btn-remove',function() {
		if ($(this).closest(".form-group").find(".input-group").length > 1) {
			$(this).closest(".input-group").remove();
		}
		else {
			console.log("Only one row found");
			var thisRow = $(this).closest(".input-group");
			thisRow.find("input:input").val('');
			thisRow.find("select option:first-child").attr("selected", "selected");
			thisRow.find("textarea").val('');
		}
	});
	
	$.validator.setDefaults({
		ignore: [],
		showErrors: function(errorMap, errorList) {
			this.defaultShowErrors();
			$("#form .tab-pane").each(function() {
				var numberOfErrors = $(this).find(".error").length;
				var numberOfErrorLabels = $(this).find("label.error").length;
				
				var tabId = $(this).attr('id');
				tabId = tabId.substring(4,tabId.length);
				var modifyTabClass = "#nav-"+tabId+"-tab";
				if ((numberOfErrors - numberOfErrorLabels) > 0) {
					$(modifyTabClass).addClass('field-error');
				}
				else {
					$(modifyTabClass).removeClass('field-error');
				}
			});
		}
	});
	var validator = $("#form").validate({
		errorPlacement: function(error, element) {
			var id = element.attr("name");
			var label = $("label[for='"+id+"'");
			error.insertAfter(label);
		}
	});
	$("#form").on('submit', function() {
		validator.form();
	});
});

/**
 * As the user to confirm whether they want to delete the item or not.
 * 
 * @returns true or false
 */
function confirmDelete() {
	var r = confirm("Do you really want to delete this item?");
	return r;
}