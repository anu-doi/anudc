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

<anu:content layout="full" title="Collection Request">
	<jsp:include page="/jsp/statusmessages.jsp">
		<jsp:param value="${it}" name="it" />
	</jsp:include>
	<form class="anuform" name="collReqUpdateForm" method="post" action="<c:url value='/rest/collreq/' />${it.collReq.id}">
		<p>
			<label>Request Id</label>
			<c:out value="${it.collReq.id}" />
		</p>
		<p>
			<label>Item ID</label> <a href="<c:url value='/rest/display/${it.collReq.pid}'><c:param name='layout' value='def:display' /></c:url>"><c:out
					value="${it.collReq.pid}" /></a>

		</p>
		<p>
			<label>Requestor</label>
			<c:out value="${it.collReq.requestor.displayName} (${it.collReq.requestor.username})" />
		</p>
		<p class="instruction"></p>
		<p>
			<label>Created</label>
			<fmt:formatDate value="${it.collReq.timestamp}" pattern="dd MMM yyyy" />
		</p>
		<p>
			<label>Status</label>
			<c:out value="${it.collReq.lastStatus.status}" />
		</p>
		<!-- Answers -->
		<c:if test="${not empty it.collReq.answers}">
			<hr />
			<p>
			<c:forEach var="answer" items="${it.collReq.answers}">
				<div onclick="jQuery(this).next('div').slideToggle()" class="box-header" style="cursor: pointer">
					<c:out value="${answer.question.questionText}" />
				</div>
				<div class="box-solid" style="display: none">
					<c:out value="${answer.answer}" />
				</div>
			</c:forEach>
			</p>
		</c:if>
		<c:set var="fedoraObject" value="${it.collReq.fedoraObject}" />
		<sec:authorize access="hasPermission(#fedoraObject,'REVIEW')">
			<c:if test="${not empty it.downloadables}">
				<hr />
				<p>
					<!-- Files for approval -->
				<ul>
					<c:forEach var="iFile" items="${it.downloadables.getChildrenRecursive()}">
						<c:if test="${iFile.type == 'FILE'}">
							<li><input type="checkbox" name="file" value="${iFile.relFilepath}"
									<c:forEach items="${it.collReq.items}" var="iCurItem">
							<c:if test="${iCurItem.item == iFile.relFilepath}">
								checked="checked"
							</c:if>
							</c:forEach> />
							<c:out value="${iFile.relFilepath} (${iFile.friendlySize})" /></li>
						</c:if> 
					</c:forEach>
				</ul>
				</p>
			</c:if>
			<hr />
			<h2>Update Status</h2>
			<p>
				<label>Status</label> <select name="status">
					<option value=""></option>
					<option value="SUBMITTED" <c:if test="${it.collReq.lastStatus.status == 'SUBMITTED'}"> selected="selected"</c:if> value="0">Submitted</option>
					<option value="ACCEPTED" <c:if test="${it.collReq.lastStatus.status == 'ACCEPTED'}"> selected="selected"</c:if> value="1">Accepted</option>
					<option value="REJECTED" <c:if test="${it.collReq.lastStatus.status == 'REJECTED'}"> selected="selected"</c:if> value="2">Rejected</option>
					<option value="PENDING" <c:if test="${it.collReq.lastStatus.status == 'PENDING'}"> selected="selected"</c:if> value="3">Pending</option>
				</select>
			</p>

			<p>
				<label>Reason</label>
				<textarea name="reason" maxlength="250" rows="5" cols="50"></textarea>
			</p>
			<p class="instruction">Max 250 chars.</p>
			<!-- Button to be conditionally displayed based on user permissions. -->
			<p class="text-right">
				<input type="submit" value="Change Status" />
			</p>
		</sec:authorize>
	</form>
	<table id="idStatusHistoryContainter" class="fullwidth">
		<tr>
			<th>Date</th>
			<th>Status</th>
			<th>Reason</th>
			<th>Changed By</th>
		</tr>
		<c:forEach var="iStatus" items="${it.collReq.status}">
			<tr>
				<td><fmt:formatDate value="${iStatus.timestamp}" pattern="dd MMM yyyy HH:mm" /></td>
				<td><c:out value="${iStatus.status}" /></td>
				<td><c:out value="${iStatus.reason}" /></td>
				<td><c:out value="${iStatus.user.username}" /></td>
			</tr>
		</c:forEach>
	</table>
</anu:content>

<jsp:include page="/jsp/footer.jsp" />