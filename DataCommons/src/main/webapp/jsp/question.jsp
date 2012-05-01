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
	<c:when test="${not empty it.questions}">
		<anu:content layout="doublewide" extraClass="nopadtop nopadbottom">
			<jsp:include page="/jsp/statusmessages.jsp">
				<jsp:param value="${it}" name="it" />
			</jsp:include>
		</anu:content>

		<anu:content layout="wide" title="Questions">
			<!-- Question Bank -->
			<form class="anuform" method="post" action="<c:url value='/rest/collreq/question' />">
				<p>
					<label for="idQuestion">New Question</label>
					<input type="text" name="q" id="idQuestion" />
				</p>
				<p>
					<input type="submit" name="submit" value="Add Question" />
				</p>
				<p>
					<select multiple="multiple" id="idQuestionBank" size="10" style="width: 100%">
						<c:forEach var="iQuestion" items="${it.questions}">
							<option value="${iQuestion.id}"><c:out value="${iQuestion.question}" /></option>
						</c:forEach>
					</select>
				</p>
			</form>
		</anu:content>

		<!-- Questions assigned to a specific Pid -->
		<anu:content layout="wide" title="Pid">
			<form class="anuform" name="pidQuestions" method="post" action="<c:url value='/rest/collreq/question' />"
				onsubmit="jQuery('#idPidQ > option').attr('selected', 'selected')">
				<p>
					<label>Pid</label>
					<input type="text" name="pid" />
					<input type="button" value="Fetch" onclick="ajaxGetPidQuestions(document.pidQuestions.pid.value)" />
				</p>
				<p>
					<select id="idPidQ" multiple="multiple" name="qid">
						<option>Click fetch to get questions</option>
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


	<c:when test="${not empty it.questionsPid}">
		<anu:content layout="doublewide" title="Questions">
			<jsp:include page="/jsp/statusmessages.jsp">
				<jsp:param value="${it}" name="it" />
			</jsp:include>

			<ul>
				<c:forEach var="iQuestion" items="${it.questionsPid}">
					<li><c:out value="${iQuestion.question}" /></li>
				</c:forEach>
			</ul>
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

<jsp:include page="/jsp/footer.jsp" />
