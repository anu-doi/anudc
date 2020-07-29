

jQuery(document).ready(function()
{
	jQuery("#questionTable > tbody").sortable();
});

/**
 * ajaxGetPidInfo
 * 
 * Australian National University Data Commons
 * 
 * This method submits an AJAX request for a list Datastreams of a Pid and Questions mapped to a Pid to CollectionRequestService. Response is in JSON format.
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		30/04/2012	Rahul Khanna (RK)		Initial
 * 0.2		27/06/2012	Genevieve Turner (GT)	Changed trim method to use jQuery.trim
 * </pre>
 * 
 * @param pid
 *            Pid whose information is retrieved.
 */
function ajaxGetPidInfo(pid)
{
	if (jQuery.trim(pid) == "")
		return;

	// Empty the container element for Questions that need to be answered.
	jQuery("#idQuestionsContainer").empty();
	jQuery("#idQuestionsContainer").append(
			jQuery("<p></p").text("Getting Questions... ").append(jQuery("<img></img>").attr("src", "/DataCommons/images/ajax-loader.gif")));

	// Get list of questions mapped to the Pid and add each to the container.
	jQuery.getJSON("/DataCommons/rest/collreq/json?task=listPidQuestions&pid=" + pid, function(qList)
	{
		jQuery("#idQuestionsContainer").empty();
		jQuery.each(qList.question, function(id, question) {
			console.log(question);

			var pQuestion = jQuery("<div></div>");
			var labelDiv = jQuery("<div></div>");
			var label = jQuery("<label></label>").text(question.question);
			if (question.required) {
				label.attr("class", "req");
			}
			labelDiv.append(label);
			pQuestion.append(labelDiv);

			if (question.options.length > 0) {
				jQuery.each(question.options, function (id, value) {
					console.log(value);
					var optionDiv = jQuery("<div></div>");
					var radioButton = jQuery("<input type='radio'>").attr("name", "q"+question.id).attr("value", value);

					if (question.required) {
						radioButton.attr("class", "required");
					}
					optionDiv.append(radioButton);
					var radioButtonLabel = value;
					optionDiv.append(radioButtonLabel);
					pQuestion.append(optionDiv);
				});
				pQuestion.append(jQuery("<div>"))
			} else {
				var textField = jQuery("<textarea rows='5' cols='50' maxlength='255'></textarea>").attr("name", "q" + question.id);
				if (question.required) {
					textField.attr("class", "required fullwidth");
				}
				else {
					textField.attr("class", "fullwidth");
				}
				var answerDiv = jQuery("<div></div>");
				answerDiv.append(textField);
				pQuestion.append(answerDiv);
			}
			
			jQuery("#idQuestionsContainer").append(pQuestion);
		});
	});
}

/**
 * ajaxGetPidQuestions
 * 
 * Australian National University Data Commons
 * 
 * This method retrieves the list of questions mapped to a pid.
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		30/04/2012	Rahul Khanna (RK)		Initial
 * 0.2		27/06/2012	Genevieve Turner (GT)	Changed trim method to use jQuery.trim
 * 0.3		04/04/2013	Genevieve Turner (GT)	Updated to use a function to perform the json query
 * </pre>
 * 
 * @param pid
 *            The pid whose questions are to be retrieved.
 */
function ajaxGetPidQuestions(pid)
{
	if (jQuery.trim(pid) == "")
		return;
	
	getQuestions("listPidQuestions");
}

/**
 * ajaxGetGroupQuestions
 * 
 * Australian National University Data Commons
 * 
 * This method retrieves the list of questions mapped to a pid.
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.3		04/04/2013	Genevieve Turner (GT)	Initial
 * </pre>
 */
function ajaxGetGroupQuestions() {
	jQuery("#pid").val('');
	getQuestions("listGroupQuestions");
}

/**
 * getQuestions
 * 
 * Australian National University Data Commons
 * 
 * This method retrieves the list of questions mapped to a pid.
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.3		04/04/2013	Genevieve Turner (GT)	Initial
 * </pre>
 * 
 * @param task The task to perform
 */
function getQuestions(task) {
	jQuery.getJSON("/DataCommons/rest/collreq/json", {task: task, pid: jQuery("#pid").val(), group: jQuery("#group").val(), }, function(data) {
		jQuery("#questionTable > tbody").empty();
		jQuery.each(data.question, function(key, val) {
			addRow(val.id, val.question, val.required);
//			var tableBody = jQuery("#questionTable > tbody");
//			var tableRow = jQuery("<tr></tr>");
//			
//			var idField = jQuery("<input type='text' />").attr("value", val.id).attr("name","qid");
//			
//			var questionColumn = jQuery("<td></td>").text(val.question);
//			questionColumn.append(idField);
////			questionColumn.append("")
//			tableRow.append(questionColumn);
//			
//			var requiredColumn = jQuery("<td></td>");
//			var requiredCheckbox = jQuery("<input type='checkbox' name='required'/>").attr('checked',val.required);
//			requiredColumn.append(requiredCheckbox);
//			tableRow.append(requiredColumn);
//			
//			tableBody.append(tableRow);
		});
	});
}

/**
 * addQuestions
 * 
 * This method adds questions to the required or optional question lists
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		05/04/2013	Genevieve Turner (GT)	Initial - Replaces addRemovePidQuestions
 * </pre>
 * 
 * @param field The field to add questions to
 */
function addQuestions(field) {
	// Get array of selected option elements in the question bank
	var options = jQuery("#idQuestionBank :selected");
	
	// Add each question selected in the question bank to the list of questions for the Pid.
	var tableBody = jQuery("#questionTable > tbody");
	for (var i = 0; i < options.length; i++)
	{
		// Check if the option's already in the list.  If yes, don't add, else add
		if (jQuery("#idOptQ > option[value='" + options[i].value + "']").length == 0 && jQuery("#idPidQ > option[value='" + options[i].value + "']").length == 0)
		{
//			var addedOption = jQuery("<option></option>").attr("value", options[i].value).attr("title", jQuery.trim(jQuery(options[i]).text())).text(jQuery.trim(jQuery(options[i]).text()));
//			jQuery(field).append(addedOption);
		}
		console.log(jQuery("#idQuestionBank > input[value='"+qid+"']").length);
		if (jQuery("#idQuestionBank > input[value='"+qid+"']").length == 0) {
			console.log('Already in list');
			var qid = options.attr('value');
			var question = options.text();
			addRow(qid, question, false);
		}
	}
}

function addRow(qid, question, required) {
	var tableBody = jQuery("#questionTable > tbody");
	var tableRow = jQuery("<tr></tr>");
	
	var idField = jQuery("<input />", {
		type: 'hidden',
		value: qid,
		name: 'qid'
	});
	
	var questionColumn = jQuery("<td></td>").text(question);
	questionColumn.append(idField);
	tableRow.append(questionColumn);
	
	var requiredColumn = jQuery("<td></td>");
	var requiredCheckbox = jQuery("<input />", {
		type: 'checkbox',
		name: 'required',
		checked: required,
		value: qid
	});
	
	requiredColumn.append(requiredCheckbox);
	tableRow.append(requiredColumn);
	
	var deleteColumn = jQuery("<td></td>");
	
	var deleteImg = jQuery("<img/>", {
		src: '//style.anu.edu.au/_anu/images/icons/silk/cross.png',
		title: 'Remove question ' + question,
		click: function(e) {
			removeRow(this);
		}
	});
	
	deleteColumn.append(deleteImg);
	tableRow.append(deleteColumn);
	
	tableBody.append(tableRow);
}

function removeRow(question) {
	jQuery(question).closest("tr").remove();
}

/**
 * validateAddQuestionForm
 * 
 * Australian National University Data Commons
 * 
 * Validates the Add Questions Form before submission.
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		30/04/2012	Rahul Khanna (RK)		Initial
 * 0.2		27/06/2012	Genevieve Turner (GT)	Changed trim method to use jQuery.trim
 * </pre>
 * 
 * @returns {Boolean} true if valid, false otherwise.
 * 
 */
function validateAddQuestionForm()
{
	var isValid = true;

	document.questionBankForm.pid.value = document.pidQuestions.pid.value;
	if (jQuery.trim(document.questionBankForm.q.value) == "")
	{
		alert("Question cannot be blank. Please enter a question.");
		isValid = false;
	}

	return isValid;
}
