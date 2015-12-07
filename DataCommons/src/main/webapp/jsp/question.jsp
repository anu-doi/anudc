<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>

<anu:header id="1998" title="Edit Request Questions" description="DESCRIPTION" subject="SUBJECT" respOfficer="Doug Moncur" respOfficerContact="doug.moncur@anu.edu.au" ssl="true">

	<script type="text/javascript" src="<c:url value='/js/collreq.js' />"></script>
</anu:header>

<jsp:include page="/jsp/header.jsp" />

<c:choose>
	<c:when test="${it.questions != null}">
		<anu:content layout="doublewide" extraClass="nopadbottom">
			<jsp:include page="/jsp/statusmessages.jsp">
				<jsp:param value="${it}" name="it" />
			</jsp:include>
		</anu:content>

		<!-- Question Bank -->
		<anu:content layout="wide" title="Questions">
			<form method="post" name="questionBankForm" action="<c:url value='/rest/collreq/question' />" onsubmit="return validateAddQuestionForm()">
				<p>
					<label for="idQuestion" class="req">New Question</label>
					<input type="text" name="q" id="idQuestion" size="30" required="required"  />
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
				<input type="hidden" name="pid" value="" />
			</form>
		</anu:content>

		<!-- Questions assigned to a specific Pid -->
		<anu:content layout="wide" title="Add Question to Item">
			<form name="pidQuestions" method="post" action="<c:url value='/rest/collreq/question' />"
				onsubmit="jQuery('#idPidQ > option').attr('selected', 'selected'); jQuery('#idOptQ > option').attr('selected', 'selected')">
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
				<p>Required Questions</p>
				<p>
					<select id="idPidQ" multiple="multiple" name="qid" size="5" style="width:100%">
						<option>[Click Get Questions]</option>
					</select>
				</p>
				<p>
					<input type="button" value="Add" onclick="addQuestions('#idPidQ')" />
					<input type="button" value="Remove" onclick="removeQuestions('#idPidQ')" />
				</p>
				<p>Optional Questions</p>
				<p>
					<select id="idOptQ" multiple="multiple" name="optQid" size="5" style="width:100%">
						<option>[Click GetQuestions]</option>
					</select>
				</p>
				<p>
					<input type="button" value="Add" onclick="addQuestions('#idOptQ')" />
					<input type="button" value="Remove" onclick="removeQuestions('#idOptQ')" />
				</p>
				<p>
					<input type="submit" name="submit" value="Save" />
				</p>
			</form>
		</anu:content>
	</c:when>


	<c:otherwise>
		<anu:content layout="doublewide" title="Questions">
			<jsp:include page="/jsp/statusmessages.jsp">
				<jsp:param value="${it}" name="it" />
			</jsp:include>
		</anu:content>
	</c:otherwise>
</c:choose>

<script type="text/javascript">
	jQuery(document).ready(ajaxGetPidQuestions(document.pidQuestions.pid.value));
</script>

<jsp:include page="/jsp/footer.jsp" />
