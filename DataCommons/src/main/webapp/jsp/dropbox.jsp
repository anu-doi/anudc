<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<anu:header id="1998" title="Collection Request" description="DESCRIPTION" subject="SUBJECT" respOfficer="ANU Library" respOfficerContact="mailto:repository.admin@anu.edu.au" ssl="true">

</anu:header>

<jsp:include page="/jsp/header.jsp" />

<anu:container type="container">
<c:choose>
	<c:when test="${it.dropboxes != null}">
		<!-- Display list of dropboxes -->
		<anu:content layout="full" title="Dropboxes">
			<jsp:include page="/jsp/statusmessages.jsp">
				<jsp:param value="${it}" name="it" />
			</jsp:include>

			<table>
				<tr>
					<th>ID</th>
					<th>Access Code</th>
					<th>Creator</th>
					<th>Created</th>
					<th>Expiry</th>
					<th>Notification</th>
					<th>Active</th>
					<th>Request</th>
					<th>Download Link</th>
				</tr>
				<c:forEach var="iDropbox" items="${it.dropboxes}">
					<tr>
						<td><c:out value="${iDropbox.id}" /></td>
						<td><c:out value="${iDropbox.accessCode}" /></td>
						<td><c:out value="${iDropbox.creator.username}" /></td>
						<td><fmt:formatDate value="${iDropbox.timestamp}" pattern="dd MMM yyyy"/></td>
						<td><c:out value="${iDropbox.expiry}" /></td>
						<td><input aria-label="Notification checkbox" type="checkbox" <c:if test="${iDropbox.notifyOnPickup == true}">checked="checked"</c:if> /></td>
						<td><input aria-label="Active checkbox" type="checkbox" <c:if test="${iDropbox.active == true}">checked="checked"</c:if> /></td>
						<td><a href="<c:url value='/rest/collreq' />/${iDropbox.collectionRequest.id}"><c:out value="${iDropbox.collectionRequest.id}" /></a></td>
						<td><a href="<c:url value='/rest/collreq/dropbox/access' />/${iDropbox.accessCode}?p=${iDropbox.accessPassword}">Download</a>
					</tr>
				</c:forEach>
			</table>
		</anu:content>
	</c:when>

	<c:when test="${not empty it.dropbox}">
		<!-- Display details of a specific dropbox. -->
		<anu:content layout="full" title="Dropbox Administration">
			<jsp:include page="/jsp/statusmessages.jsp">
				<jsp:param value="${it}" name="it" />
			</jsp:include>

			<form class="anuform" method="post" action="<c:url value="/rest/collreq/dropbox" />">
			<fieldset>
				<div class="field">
					<label aria-label="dropboxID">Dropbox ID</label>
					<span><c:out value="${it.dropbox.id}" />
					<input type="hidden" name="dropbox" value="${it.dropbox.id}" /></span>
				</div>
				<div class="field">
					<label aria-label="accessCode">Access Code</label>
					<span><c:out value="${it.dropbox.accessCode}" /></span>
				</div>
				<div class="field">
					<label aria-label="creator">Creator</label>
					<span style="width:20%"><c:out value="${it.dropbox.creator.displayName}" /></span>
					<p class="instruction"><c:out value="${it.dropbox.creator.username}" /></p>
				</div>
				<div class="field">
					<label  aria-label="created">Created</label>
					<span><fmt:formatDate value="${it.dropbox.timestamp}" pattern="dd MMM yyyy"/></span>
				</div>
				<p>
					<label  aria-label="expiry">Expiry</label>
					<span><fmt:formatDate value="${it.dropbox.expiry}" pattern="dd MMM yyyy"/></span>
				</p>
				<div class="field">
					<label for="notify">Notify Creator</label>
					<span><input type="checkbox" id="notify" name="notify" value="true" <c:if test='${it.dropbox.notifyOnPickup == true}'>checked="checked"</c:if> /></span>
				</div>
				<div class="field">
					<label for="active">Active</label>
					<span><input type="checkbox" id="active" name="active" value="true" <c:if test='${it.dropbox.active == true}'>checked="checked"</c:if> /></span>
				</div>
				<div class="field">
					<label>Request ID</label> <span><a href="<c:url value='/rest/collreq/${it.dropbox.collectionRequest.id}' />"><c:out
							value="${it.dropbox.collectionRequest.id}" /></a></span>
				</div>
				<div class="float-end">
					<input class="btn btn-primary mr-1" type="submit" value="Submit" />
				</div>
				</fieldset>
			</form>
		</anu:content>
	</c:when>

	<c:otherwise>
		<anu:content layout="full" title="Dropbox">
			<jsp:include page="/jsp/statusmessages.jsp">
				<jsp:param value="${it}" name="it" />
			</jsp:include>
		</anu:content>
	</c:otherwise>
</c:choose>
</anu:container>
<jsp:include page="/jsp/footer.jsp" />
