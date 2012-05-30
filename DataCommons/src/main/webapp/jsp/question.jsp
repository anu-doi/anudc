<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>

<anu:header id="1998" title="TITLE" description="DESCRIPTION" subject="SUBJECT" respOfficer="Doug Moncur" respOfficerContact="doug.moncur@anu.edu.au" ssl="true">
	<!-- Possible bug in the ANU taglib. The following CSS should not be referenced here. Should be referenced in the taglib. -->
	<link href="http://styles.anu.edu.au/_anu/3/style/anu-forms.css" rel="stylesheet" type="text/css" />
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
							<option value="${iQuestion.id}">
								<c:out value="${iQuestion.questionText}" />
							</option>
						</c:forEach>
					</select>
				</p>
				<input type="hidden" name="pid" value="" />
			</form>
		</anu:content>

		<!-- Questions assigned to a specific Pid -->
		<anu:content layout="wide" title="Pid">
			<form name="pidQuestions" method="post" action="<c:url value='/rest/collreq/question' />"
				onsubmit="jQuery('#idPidQ > option').attr('selected', 'selected')">
				<p>
					<label>Pid</label>
					<input type="text" name="pid" value="<c:out value='${param.pid}' />" />
					<input type="button" value="Get Questions" onclick="ajaxGetPidQuestions(document.pidQuestions.pid.value)" />
				</p>
				<p>
					<select id="idPidQ" multiple="multiple" name="qid" size="10" style="width:100%">
						<option>[Click Get Questions]</option>
					</select>
				</p>
				<p>
					<input type="button" value="Add" onclick="addRemovePidQuestions(this.value)" />
					<input type="button" value="Remove" onclick="addRemovePidQuestions(this.value)" />
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
