function ajaxPopup()
{
	jQuery.setupJMPopups(
	{
		screenLockerBackground : "#000000",
		screenLockerOpacity : "0.5"
	});

	jQuery.openPopupLayer(
	{
		name : "myPopup",
		width : 400,
		url : "/DataCommons/jsp/searchbox.jsp"
	});
}

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

	// Empty the container element for Datastream List.
	jQuery("#idFileListContainer").empty();
	jQuery("#idFileListContainer").append(
			jQuery("<p></p>").text("Getting item list... ").append(jQuery("<img></img>").attr("src", "/DataCommons/images/ajax-loader.gif")));

	// Get Datastreams for the pid and add each to the container.
	jQuery.getJSON("/DataCommons/rest/collreq/json?task=listPidItems&pid=" + pid, function(fileList)
	{
		jQuery("#idFileListContainer").empty();
		jQuery.each(fileList, function(i, fileItem)
		{
			jQuery("<input></input>").attr("type", "checkbox").attr("name", "file").attr("value", fileItem.filename).appendTo("#idFileListContainer");
			jQuery("#idFileListContainer").append(fileItem.filename);
			jQuery("#idFileListContainer").append("<br />");
		});
	});

	// Empty the container element for Questions that need to be answered.
	jQuery("#idQuestionsContainer").empty();
	jQuery("#idQuestionsContainer").append(
			jQuery("<p></p").text("Getting Questions... ").append(jQuery("<img></img>").attr("src", "/DataCommons/images/ajax-loader.gif")));

	// Get list of questions mapped to the Pid and add each to the container.
	jQuery.getJSON("/DataCommons/rest/collreq/json?task=listPidQuestions&pid=" + pid, function(qList)
	{
		jQuery("#idQuestionsContainer").empty();
		jQuery.each(qList, function(id, question)
		{
			var pQuestion = jQuery("<p></p>");
			pQuestion.append(jQuery("<label></label>").text(question));
			pQuestion.append(jQuery("<textarea rows='5' cols='50'></textarea>").attr("name", "q" + id));
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
 * </pre>
 * 
 * @param pid
 *            The pid whose questions are to be retrieved.
 */
function ajaxGetPidQuestions(pid)
{
	if (jQuery.trim(pid) == "")
		return;
	
	jQuery.getJSON("/DataCommons/rest/collreq/json?task=listPidQuestions&pid=" + pid, function(data)
	{
		jQuery("#idPidQ").empty();
		jQuery.each(data, function(key, val)
		{
			jQuery("<option></option>").attr("value", key).text(val).appendTo("#idPidQ");
		});
	});
}

/**
 * addRemovePidQuestions
 * 
 * Australian National University Data Commons
 * 
 * This method updates the list of questions in the Select Box on page by adding or removing the selected items.
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		30/04/2012	Rahul Khanna (RK)		Initial
 * 0.2		27/06/2012	Genevieve Turner (GT)	Changed trim method to use jQuery.trim
 * </pre>
 * 
 * @param action
 *            The action to be taken. "Add" or "Remove".
 */
function addRemovePidQuestions(action)
{
	if (jQuery.trim(action) == "Add")
	{
		// Get array of selected option elements in the question bank.
		var options = jQuery("#idQuestionBank :selected");

		// Add each question selected in the question bank to the list of questions for the Pid.
		for ( var i = 0; i < options.length; i++)
		{
			// Check if the option's already in the list. If yes, don't add, else add.
			if (jQuery("#idPidQ > option[value='" + options[i].value + "']").length == 0)
			{
				var addedOption = jQuery("<option></option>").attr("value", options[i].value).text(jQuery.trim(jQuery(options[i]).text()));
				jQuery("#idPidQ").append(addedOption);
			}
		}
	}
	else if (action == "Remove")
	{
		jQuery("#idPidQ :selected").remove();
	}
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
