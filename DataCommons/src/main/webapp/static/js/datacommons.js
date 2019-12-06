$(document).ready(function(){
	/*
	$(function(){
		$('#anzforSubject2').combobox();
		$('#anzseoSubject2').combobox();
	});
	*/
	$(".btn-add").click(function() {
		console.log("Add button clicked");
		
		var clonedRow = $(this).closest(".form-group").find(".input-group:last").clone();
		clonedRow.find("input:input").val('');
		clonedRow.find("select option:first-child").attr("selected", "selected");
		clonedRow.find("textarea").val('');
		
		var lastRow = $(this).closest(".form-group").find(".input-group:last");
		clonedRow.insertAfter(lastRow);
	});
	$(".btn-remove").click(function() {
		console.log("Remove button clicked");
		
		if ($(this).closest(".form-group").find(".input-group").length > 1) {
			console.log("More than one row found");
			console.log($(this).closest(".input-group").find("input").val());
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