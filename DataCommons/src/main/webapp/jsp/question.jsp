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

<anu:content layout="doublewide" extraClass="nopadbottom">
	<jsp:include page="/jsp/statusmessages.jsp">
		<jsp:param value="${it}" name="it" />
	</jsp:include>
</anu:content>

<anu:content layout="full" title="Questions">
	<!-- Question Bank -->
	<form method="post" name="questionBankForm" action="<c:url value='/rest/collreq/question' />" onsubmit="return validateAddQuestionForm()">
		<p>
			<label for="idQuestion" class="req">New Question</label>
			<input type="text" name="q" id="idQuestion" size="30" required="required" />
		</p>
		<p>
			<input type="submit" name="submit" value="Add Question" />
		</p>
		<p>
			<select multiple="multiple" id="idQuestionBank" size="10" style="width: 100%">
				<c:forEach var="iQuestion" items="${it.questions}">
					<option value="${iQuestion.id}" title="${iQuestion.questionText}">
						<c:out value="${iQuestion.questionText}" />
					</option>
				</c:forEach>
			</select>
		</p>
	</form>
</anu:content>

<anu:content layout="full" title="Question Management">
	<p>Please select a group or item to retrieve questions for.  The questions can be sorted via drag and drop.</p>
	<form name="pidQuestions" method="post" action="<c:url value='/rest/collreq/question' />">
	<p>
		<label>Group</label>
		<select name="group" id="group">
			<option value="">--No Value Selected--</option>
			<c:forEach items="${it.groups}" var="group">
				<option value="${group.id}" title="${group.group_name}">${group.group_name}</option>
			</c:forEach>
		</select>
		<input type="button" value="Get Questions" onclick="ajaxGetGroupQuestions()" />
	</p>
	<p>
		<label>Item ID</label>
		<input type="text" name="pid" id="pid" value="<c:out value='${param.pid}' />" />
		<input type="button" value="Get Questions" onclick="ajaxGetPidQuestions(document.pidQuestions.pid.value)" />
	</p>
		<div>
			<input type="button" value="Add" onclick="addQuestions('#idPidQ')" />
			<input type="button" value="Remove" onclick="removeQuestions('#idPidQ')" />
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
			<input type="submit" name="submit" value="Save" />
		</div>
	</form>
</anu:content>

<script type="text/javascript">
	jQuery(document).ready(ajaxGetPidQuestions(document.pidQuestions.pid.value));
</script>

<jsp:include page="/jsp/footer.jsp" />
