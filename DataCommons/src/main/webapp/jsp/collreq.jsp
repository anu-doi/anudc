<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<anu:header id="1998" title="Collection Request" description="DESCRIPTION" subject="SUBJECT" respOfficer="Doug Moncur" respOfficerContact="doug.moncur@anu.edu.au" ssl="true">
	<!-- Possible bug in the ANU taglib. The following CSS should not be referenced here. Should be referenced in the taglib. -->
	<link href="//styles.anu.edu.au/_anu/3/style/anu-forms.css" rel="stylesheet" type="text/css" />
	<script type="text/javascript" src="<c:url value='/js/collreq.js' />"></script>
</anu:header>

<jsp:include page="/jsp/header.jsp" />

<c:choose>
	<c:when test="${not empty it.collReq}">
		<!-- Display information about a specific Collection Request. -->
		<anu:content layout="doublewide" title="Collection Request">
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
				<sec:accesscontrollist hasPermission="REVIEW" domainObject="${it.collReq.fedoraObject}">
					<c:if test="${not empty it.downloadables}">
						<hr />
						<p>
							<!-- Files for approval -->
						<ul>
							<c:forEach var="iFile" items="${it.downloadables.files}">
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
				</sec:accesscontrollist>
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
						<td><fmt:formatDate value="${iStatus.timestamp}" pattern="dd MMM yyyy HH:mm" /></td>
						<td><c:out value="${iStatus.status}" /></td>
						<td><c:out value="${iStatus.reason}" /></td>
						<td><c:out value="${iStatus.user.username}" /></td>
					</tr>
				</c:forEach>
			</table>
		</anu:content>
	</c:when>

	<c:otherwise>
		<!-- Form for submitting a Collection Request -->
		<anu:content layout="doublewide" title="Collection Request">
			<jsp:include page="/jsp/statusmessages.jsp">
				<jsp:param value="${it}" name="it" />
			</jsp:include>
			<c:if test="${empty param.pid}">
				<p>To request access to a collection please perform the following steps:</p>
				<ol>
					<li>Enter the Identifier of the item you wish to request access to</li>
					<li>Select to retrieve the request questions</li>
					<li>Answer the questions that appear on the screen (if any)</li>
					<li>Click the 'Request Access' button</li>
				</ol>
			</c:if>
			<c:if test="${not empty param.pid}">
				<p>To request access to a collection please perform the following steps:</p>
				<ol>
					<li>Answer the questions that appear on the screen (if any)</li>
					<li>Click the 'Request Access' button</li>
				</ol>
			</c:if>
			<form name="collReqSubmitForm" class="anuform" method="post" action="<c:url value='/rest/collreq/' />">
				<p>
					<label class="req" for="idPid">Item ID</label>
					<input class="text" type="text" id="idPid" name="pid" value="<c:out value='${param.pid}' />" <c:if test="${not empty param.pid}">readonly="readonly"</c:if> />
					<c:if test="${empty param.pid}">
						<input type="button" onclick="ajaxGetPidInfo(document.collReqSubmitForm.pid.value)" value="Retrieve Request Questions" />
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
			<div id="reqAction">
				<table id="idReqStatusTable" class="tbl-row-bdr w-doublewide">
					<tr>
						<th>Request Id</th>
						<th>Item ID</th>
						<th>Date Requested</th>
						<th>Requestor</th>
						<th>Status</th>
					</tr>
					<c:forEach var="iCollReq" items="${it.collReqs}">
						<tr>
							<td><a href="<c:url value='/rest/collreq' />/${iCollReq.id}"><c:out value="${iCollReq.id}" /></a></td>
							<td><a href="<c:url value='/rest/display/${iCollReq.pid}'><c:param name='layout' value='def:display' /></c:url>"><c:out value="${iCollReq.pid}" /></a></td>
							<td><fmt:formatDate value="${iCollReq.timestamp}" pattern="dd MMM yyyy" /></td>
							<td><c:out value="${iCollReq.requestor.username}" /></td>
							<td><c:if test="${iCollReq.lastStatus.status eq 'ACCEPTED'}">
									<sec:accesscontrollist hasPermission="REVIEW" domainObject="${iCollReq.fedoraObject}">
										<a href="<c:url value='/rest/collreq/dropbox/${iCollReq.dropbox.id}' />">
									</sec:accesscontrollist>
								</c:if> <c:out value="${iCollReq.lastStatus.status}" /> <c:if test="iCollReq.lastStatus.status">
									<sec:accesscontrollist hasPermission="REVIEW" domainObject="${iCollReq.fedoraObject}">
										</a>
									</sec:accesscontrollist>
								</c:if></td>
						</tr>
					</c:forEach>
				</table>
			</div>
		</anu:content>
		
		<script type="text/javascript">
			jQuery(document).ready(ajaxGetPidInfo(document.collReqSubmitForm.pid.value));
		</script>
	</c:otherwise>
</c:choose>

<jsp:include page="/jsp/footer.jsp" />
