<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>


<anu:header id="1998" title="Edit Request Questions" description="DESCRIPTION" subject="SUBJECT" respOfficer="ANU Library" respOfficerContact="mailto:repository.admin@anu.edu.au" ssl="true">
	
	<link rel="stylesheet" type="text/css" href="<c:url value='/css/jquery-ui.min.css' />" />
	<script type="text/javascript" src="<c:url value='/js/jquery-ui.min.js' />"></script>
	<script type="text/javascript" src="<c:url value='/js/collreq.js' />"></script>
</anu:header>

<jsp:include page="/jsp/header.jsp" />

<anu:container type="container">
<anu:content layout="doublewide" extraClass="pb-0">
	<jsp:include page="/jsp/statusmessages.jsp">
		<jsp:param value="${it}" name="it" />
	</jsp:include>
</anu:content>

<anu:content layout="full" title="Questions">
	<!-- Question Bank -->
	<form class="anuform" method="post" name="questionBankForm" action="<c:url value='/rest/collreq/question' />" onsubmit="return validateAddQuestionForm()">
	<fieldset>
		<div class="field">
			<label for="idQuestion" class="req">New Question</label>
			<div class="field-span">
				<input class="text tfull" type="text" name="q" id="idQuestion" size="30" required="required" />
			</div>
		</div>
		<div class="float-end">
			<input class="btn btn-primary" type="submit" name="submit" value="Add Question" />
		</div>
		<div class="field">
			<select class="w-100 mt-2 mr-2" multiple="multiple" id="idQuestionBank" size="10">
				<c:forEach var="iQuestion" items="${it.questions}">
					<option value="${iQuestion.id}" title="${iQuestion.questionText}">
						<c:out value="${iQuestion.questionText}" />
					</option>
				</c:forEach>
			</select>
		</div>
		</fieldset>
	</form>
</anu:content>

<anu:content layout="full" title="Question Management">
	<p>Please select a group or item to retrieve questions for.  The questions can be sorted via drag and drop.</p>
	<form class="anuform" name="pidQuestions" method="post" action="<c:url value='/rest/collreq/question' />">
	<fieldset>
	<div class="field">
		<label>Group</label>
		<div class="field-span">
		<select name="group" id="group">
			<option value="">--No Value Selected--</option>
			<c:forEach items="${it.groups}" var="group">
				<option value="${group.id}" title="${group.group_name}">${group.group_name}</option>
			</c:forEach>
		</select>
		<input class="btn btn-primary" type="button" value="Get Questions" onclick="ajaxGetGroupQuestions()" />
		</div>
		
	</div>
	<div class="field">
		<label>Item ID</label>
		<div class="field-span">
		<input type="text" name="pid" id="pid" value="<c:out value='${param.pid}' />" />
		<input class="btn btn-primary" type="button" value="Get Questions" onclick="ajaxGetPidQuestions(document.pidQuestions.pid.value)" />
		</div>
		
	</div>
		<div>
			<input class="btn btn-primary" type="button" value="Add" onclick="addQuestions('#idPidQ')" />
			<input class="btn btn-danger" type="button" value="Remove" onclick="removeQuestions('#idPidQ')" />
		</div>
		<div>
			<table id="questionTable">
				<thead>
				<tr>
					<th>Question</th>
					<th>Required</th>
				</tr>
				</thead>
				<tbody>
				<tr>
				</tr>
				</tbody>
			</table>
		</div>
		<div>
			<input class="btn btn-primary" type="submit" name="submit" value="Save" />
		</div>
	</fieldset>
	</form>
</anu:content>
</anu:container>
<script type="text/javascript">
	jQuery(document).ready(ajaxGetPidQuestions(document.pidQuestions.pid.value));
</script>

<jsp:include page="/jsp/footer.jsp" />
