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
<anu:container type="container">
<anu:content layout="full" title="Collection Request">
	<jsp:include page="/jsp/statusmessages.jsp">
		<jsp:param value="${it}" name="it" />
	</jsp:include>
	<form class="anuform" name="collReqUpdateForm" method="post" action="<c:url value='/rest/collreq/' />${it.collReq.id}">
	<fieldset>
		<div class="field">
			<label>Request Id</label>
			<div class="field-span">
				<c:out value="${it.collReq.id}" />
			</div>
		</div>
		<div class="field">
			<label>Item ID</label> 
			<div class="field-span">
			<a href="<c:url value='/rest/display/${it.collReq.pid}'><c:param name='layout' value='def:display' /></c:url>"><c:out
					value="${it.collReq.pid}" /></a>
			</div>
		</div>
		<div class="field">
			<label>Requestor</label>
			<div class="field-span">
			<c:out value="${it.collReq.requestor.displayName} (${it.collReq.requestor.username})" />
			</div>
		</div>
		<div class="instruction"></div>
		<div class="field">
			<label>Created</label>
			<div class="field-span">
			<fmt:formatDate value="${it.collReq.timestamp}" pattern="dd MMM yyyy" />
			</div>
		</div>
		<div class="field">
			<label>Status</label>
			<div class="field-span">
			<c:out value="${it.collReq.lastStatus.status}" />
			</div>
		</div>
		</fieldset>
		<!-- Answers -->
		<c:if test="${not empty it.collReq.answers}">
			<hr />
			<p>
			<c:forEach var="answer" items="${it.collReq.answers}">
				<div onclick="jQuery(this).next('div').slideToggle()" style="cursor: pointer"> 				
				<anu:boxheader borderColour="tint" text="${answer.question.questionText}"/>
				</div>
				<div style="display: none">
				<anu:box style="bdr" styleColour="gold">
					<c:out value="${answer.answer}" />
				</anu:box>
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
			<fieldset>
			<div class="field">
				<label for="status">Status</label> 
				<div class="field-span">
				<select id="status" name="status">
					<option value=""></option>
					<option value="SUBMITTED" <c:if test="${it.collReq.lastStatus.status == 'SUBMITTED'}"> selected="selected"</c:if> value="0">Submitted</option>
					<option value="ACCEPTED" <c:if test="${it.collReq.lastStatus.status == 'ACCEPTED'}"> selected="selected"</c:if> value="1">Accepted</option>
					<option value="REJECTED" <c:if test="${it.collReq.lastStatus.status == 'REJECTED'}"> selected="selected"</c:if> value="2">Rejected</option>
					<option value="PENDING" <c:if test="${it.collReq.lastStatus.status == 'PENDING'}"> selected="selected"</c:if> value="3">Pending</option>
				</select>
				</div>
			</div>

			<div class="field">
				<label for="reason">Reason</label>
				<div class="field-span">
				<textarea id="reason" name="reason" maxlength="250" rows="5" cols="50"></textarea>
				<p class="instruction">Max 250 chars.</p>
				</div>
			</div>
			
			<!-- Button to be conditionally displayed based on user permissions. -->
			<p class="float-end">
				<input class="btn btn-primary mr-1" type="submit" value="Change Status" />
			</p>
			</fieldset>
		</sec:authorize>
	</form>
	<table id="idStatusHistoryContainter">
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
</anu:container>
<jsp:include page="/jsp/footer.jsp" />