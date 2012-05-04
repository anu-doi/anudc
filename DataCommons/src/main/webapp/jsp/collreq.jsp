<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>

<anu:header id="1998" title="TITLE" description="DESCRIPTION" subject="SUBJECT" respOfficer="Doug Moncur" respOfficerContact="doug.moncur@anu.edu.au" ssl="true">
	<!-- Possible bug in the ANU taglib. The following CSS should not be referenced here. Should be referenced in the taglib. -->
	<link href="http://styles.anu.edu.au/_anu/3/style/anu-forms.css" rel="stylesheet" type="text/css" />
	<script type="text/javascript" src="<c:url value='/js/jquery.jmpopups-0.5.1.js' />"></script>
	<script type="text/javascript" src="<c:url value='/js/collreq.js' />"></script>
	<script type="text/javascript">
		jQuery(document).ready(ajaxGetCollReqs());
	</script>
</anu:header>

<jsp:include page="/jsp/header.jsp" />

<c:choose>
	<c:when test="${not empty it.collReq}">
		<!-- Display information about a specific Collection Request. -->
		<anu:content layout="doublewide" title="Dataset Request">
			<jsp:include page="/jsp/statusmessages.jsp">
				<jsp:param value="${it}" name="it" />
			</jsp:include>

			<form class="anuform" name="collReqUpdateForm" method="post" action="<c:url value='/rest/collreq/' />${it.collReq.id}">
				<p>
					<label>Request Id</label>
					<c:out value="${it.collReq.id}" />
				</p>
				<p>
					<label>Pid</label>
					<c:out value="${it.collReq.pid}" />
				</p>
				<p>
					<label>Requestor</label>
					<c:out value="${it.collReq.requestorId}" />
				</p>
				<p class="instruction">[Placeholder for requestor name]</p>
				<p>
					<label>Created</label>
					<c:out value="${it.collReq.timestamp}" />
				</p>
				<p>
					<label>Status</label>
					<select name="status">
						<option value=""></option>
						<option value="SUBMITTED" <c:if test="${it.collReq.lastStatus.status == 'SUBMITTED'}"> selected="selected"</c:if> value="0">Submitted</option>
						<option value="ACCEPTED" <c:if test="${it.collReq.lastStatus.status == 'ACCEPTED'}"> selected="selected"</c:if> value="1">Accepted</option>
						<option value="REJECTED" <c:if test="${it.collReq.lastStatus.status == 'REJECTED'}"> selected="selected"</c:if> value="2">Rejected</option>
						<option value="PENDING" <c:if test="${it.collReq.lastStatus.status == 'PENDING'}"> selected="selected"</c:if> value="3">Pending</option>
					</select>
				</p>
				<p>
					<label>Reason</label>
					<textarea name="reason" maxlength="250"></textarea>
				</p>
				<p class="instruction">Max 250 chars.</p>
				<!-- Button to be conditionally displayed based on user permissions. -->
				<p class="text-right">
					<input type="submit" value="Change Status" />
				</p>
			</form>
			<table id="idStatusHistoryContainter" class="doublewide">
				<tr>
					<th>Date</th>
					<th>Status</th>
					<th>Reason</th>
					<th>Changed By</th>
				</tr>
				<c:forEach var="iStatus" items="${it.collReq.status}">
					<tr>
						<td><c:out value="${iStatus.timestamp}" /></td>
						<td><c:out value="${iStatus.status}" /></td>
						<td><c:out value="${iStatus.reason}" /></td>
						<td><c:out value="${iStatus.userId}" /></td>
					</tr>
				</c:forEach>
			</table>
		</anu:content>
	</c:when>

	<c:otherwise>
		<!-- Display form to submit a Collection Request -->
		<anu:content layout="doublewide" title="Collection Request">
			<jsp:include page="/jsp/statusmessages.jsp">
				<jsp:param value="${it}" name="it" />
			</jsp:include>

			<form name="collReqSubmitForm" class="anuform" method="post" action="<c:url value='/rest/collreq/' />">
				<p>
					<label class="req" for="idPid">Pid</label>
					<input class="text" type="text" id="idPid" name="pid" value="<c:out value='${param.pid}' />" />
					<input type="button" onclick="ajaxPopup()" value="Search" />
					<input type="button" onclick="ajaxGetPidInfo(document.collReqSubmitForm.pid.value)" value="Get Datastreams" />
				</p>
				<p id="idDsListContainer">
					<!-- Container for Datastream List. -->
				</p>
				<p id="idQuestionsContainer">
					<!-- Container for questions that need to be answered as part of collection request. -->
				</p>
				<p class="text-right">
					<input type="submit" value="Submit">
				<p>
			</form>
			<div id="idReqStatusContainer">
				<hr />
				<table id="idReqStatusTable" class="tbl-row-bdr w-doublewide">
					<tr>
						<th>Id</th>
						<th>Pid</th>
						<th>Date</th>
						<th>Requestor</th>
						<th>Status</th>
						<th>Details</th>
					</tr>
				</table>
			</div>
		</anu:content>
	</c:otherwise>
</c:choose>

<jsp:include page="/jsp/footer.jsp" />
