<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<anu:header id="1998" title="Data Request" description="DESCRIPTION" subject="SUBJECT" respOfficer="ANU Library" respOfficerContact="mailto:repository.admin@anu.edu.au" ssl="true">

	<script type="text/javascript" src="<c:url value='/js/collreq.js' />"></script>
</anu:header>

<jsp:include page="/jsp/header.jsp" />

<!-- Form for submitting a Collection Request -->
<anu:content layout="full" title="Data collection request">
	<jsp:include page="/jsp/statusmessages.jsp">
		<jsp:param value="${it}" name="it" />
	</jsp:include>
	<p>To request access to data collections and review your request activity you must first login using your ANU ID and password or your registered account details.</p>
	<p>Once logged in, please enter the identifier of the item you wish to access (e.g. anudc:2652) and submit your request.</p>
	<form name="collReqSubmitForm" class="anuform labelfull" method="post" action="<c:url value='/rest/collreq/' />">
		<p>
			<label class="req" for="idPid">Item ID</label>
			<input class="text" type="text" id="idPid" name="pid" value="<c:out value='${param.pid}' />" <c:if test="${not empty param.pid}">readonly="readonly"</c:if> />
			<c:if test="${empty param.pid}">
				<input type="button" onclick="ajaxGetPidInfo(document.collReqSubmitForm.pid.value)" value="Submit request" />
			</c:if>
		</p>
		<p id="idQuestionsContainer">
			<!-- Container for questions that need to be answered as part of collection request. -->
		</p>
		<p class="text-right">
			<input type="submit" value="Request Access">
		<p>
	</form>
	<hr />
	<h2>Data collection request summary</h2>
	<div id="reqAction">
		<table id="idReqStatusTable" class="tbl-row-bdr w-full">
			<tr>
				<th>Request Id</th>
				<th>Item ID</th>
				<th>Date Requested</th>
				<th>Requestor</th>
				<th>Status</th>
			</tr>
			<c:forEach var="iCollReq" items="${it.collReqs}">
				<c:set var="iCollReqFedoraObjectObject" value="${iCollReq.fedoraObject}" />
				<tr>
					<td><a href="<c:url value='/rest/collreq' />/${iCollReq.id}"><c:out value="${iCollReq.id}" /></a></td>
					<td><a href="<c:url value='/rest/display/${iCollReq.pid}'><c:param name='layout' value='def:display' /></c:url>"><c:out value="${iCollReq.pid}" /></a></td>
					<td><fmt:formatDate value="${iCollReq.timestamp}" pattern="dd MMM yyyy" /></td>
					<td><c:out value="${iCollReq.requestor.username}" /></td>
					<td>
						<c:if test="${iCollReq.lastStatus.status eq 'ACCEPTED'}">
							<sec:authorize access="hasPermission(#iCollReqFedoraObjectObject,'REVIEW')">
								<a href="<c:url value='/rest/collreq/dropbox/${iCollReq.dropbox.id}' />">
							</sec:authorize>
						</c:if> <c:out value="${iCollReq.lastStatus.status}" /> <c:if test="iCollReq.lastStatus.status">
							<sec:authorize access="hasPermission(#iCollReqFedoraObjectObject,'REVIEW')">
								</a>
							</sec:authorize>
						</c:if>
					</td>
				</tr>
			</c:forEach>
		</table>
	</div>
</anu:content>

<anu:content layout="full">
<c:forEach items="${it.approvedRequests}" var="appReq">
<table class="fullwidth tbl-row-bdr">
	<tr>
		<th>ID</th>
		<th>Access Code</th>
		<th>Creator</th>
		<th>Created</th>
		<th>Expiration</th>
		<th>Notification</th>
		<th>Active</th>
		<th>Request ID</th>
	</tr>
	<tr>
		<td><c:out value="${appReq.id}" /></td>
		<td><c:out value="${appReq.accessCode}" /></td>
		<td><c:out value="${appReq.creator.username}" /></td>
		<td><fmt:formatDate value="${appReq.timestamp}" pattern="dd MMM yyyy"/></td>
		<td><c:out value="${appReq.expiry}" /></td>
		<td><input type="checkbox" <c:if test="${appReq.notifyOnPickup == true}">checked="checked"</c:if> /></td>
		<td><input type="checkbox" <c:if test="${appReq.active == true}">checked="checked"</c:if> /></td>
		<td><a href="<c:url value='/rest/collreq' />/${appReq.collectionRequest.id}"><c:out value="${appReq.collectionRequest.id}" /></a></td>
	</tr>
	<tr>
		<td colspan="8"><a href="<c:url value='/rest/collreq/dropbox/access' />/${appReq.accessCode}?p=${appReq.accessPassword}">Download data collection</a></td>
	</tr>
</table>
</c:forEach>
</anu:content>

<script type="text/javascript">
	jQuery(document).ready(ajaxGetPidInfo(document.collReqSubmitForm.pid.value));
</script>

<jsp:include page="/jsp/footer.jsp" />
		