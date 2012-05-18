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
 * </pre>
 * 
 * @param pid
 *            Pid whose information is retrieved.
 */
function ajaxGetPidInfo(pid)
{
	// Empty the container element for Datastream List.
	jQuery("#idDsListContainer").empty();
	jQuery("#idDsListContainer").append(
			jQuery("<p></p>").text("Getting Datastreams... ").append(jQuery("<img></img>").attr("src", "/DataCommons/images/ajax-loader.gif")));

	// Get Datastreams for the pid and add each to the container.
	jQuery.getJSON("/DataCommons/rest/collreq/json?task=listDs&pid=" + pid, function(dsList)
	{
		jQuery("#idDsListContainer").empty();
		jQuery.each(dsList, function(i, dsListItem)
		{
			jQuery("<input></input>").attr("type", "checkbox").attr("name", "dsid").attr("value", dsListItem.dsId).appendTo("#idDsListContainer");
			jQuery("#idDsListContainer").append(dsListItem.dsLabel);
			jQuery("#idDsListContainer").append("<br />");
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
 * ajaxGetCollReqs
 * 
 * Australian National University Data Commons
 * 
 * Gets a list of Collection Requests from CollectionRequestService.
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		30/04/2012	Rahul Khanna (RK)		Initial
 * </pre>
 */
function ajaxGetCollReqs()
{
	console.log("Retrieving Collection Requests...");
	jQuery.getJSON("/DataCommons/rest/collreq/json?task=listCollReq", function(data)
	{
		console.log("IN");
		console.log("Data Length: " + data.length);
		if (data.length > 0)
		{
			console.log("JSON data returned for status list query.");
			jQuery.each(data, function(i, collReq)
			{
				// console.log("Key: " + key + ". Value: " + val);
				var tableRow = jQuery("<tr></tr>");

				(jQuery("<td></td>").append(jQuery("<a></a>").attr("href", "/DataCommons/rest/collreq/" + collReq.id).text(collReq.id))).appendTo(tableRow);
				jQuery("<td></td>").text(collReq.pid).appendTo(tableRow);
				jQuery("<td></td>").text(collReq.timestamp).appendTo(tableRow);
				jQuery("<td></td>").text(collReq.requestor).appendTo(tableRow);
				jQuery("<td></td>").text(collReq.lastStatus).appendTo(tableRow);
				jQuery("<td></td>").text(collReq.id).appendTo(tableRow);
				jQuery("#idReqStatusContainer > table").append(tableRow);
			});
			jQuery("#idReqStatusContainer").show("slow");
		}
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
 * Version	Date		Developer			Description
 * 0.1		30/04/2012	Rahul Khanna (RK)	Initial
 * </pre>
 * 
 * @param pid
 *            The pid whose questions are to be retrieved.
 */
function ajaxGetPidQuestions(pid)
{
	if (pid.trim() == "")
		return;

	console.log("Retrieving questions for PID " + pid);
	jQuery.getJSON("/DataCommons/rest/collreq/json?task=listPidQuestions&pid=" + pid, function(data)
	{
		console.log("Populating SELECT with options.");
		jQuery("#idPidQ").empty();
		jQuery.each(data, function(key, val)
		{
			console.log(key + " " + val);
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
 * Version	Date		Developer			Description
 * 0.1		30/04/2012	Rahul Khanna (RK)	Initial
 * </pre>
 * 
 * @param action
 *            The action to be taken. "Add" or "Remove".
 */
function addRemovePidQuestions(action)
{
	if (action == "Add")
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
