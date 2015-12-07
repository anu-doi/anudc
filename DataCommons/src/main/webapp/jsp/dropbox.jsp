<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<anu:header id="1998" title="TITLE" description="DESCRIPTION" subject="SUBJECT" respOfficer="Doug Moncur" respOfficerContact="doug.moncur@anu.edu.au" ssl="true">

</anu:header>

<jsp:include page="/jsp/header.jsp" />

<c:choose>
	<c:when test="${it.dropboxes != null}">
		<!-- Display list of dropboxes -->
		<anu:content layout="doublewide" title="Dropboxes">
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
						<td><input type="checkbox" <c:if test="${iDropbox.notifyOnPickup == true}">checked="checked"</c:if> /></td>
						<td><input type="checkbox" <c:if test="${iDropbox.active == true}">checked="checked"</c:if> /></td>
						<td><a href="<c:url value='/rest/collreq' />/${iDropbox.collectionRequest.id}"><c:out value="${iDropbox.collectionRequest.id}" /></a></td>
						<td><a href="<c:url value='/rest/collreq/dropbox/access' />/${iDropbox.accessCode}?p=${iDropbox.accessPassword}">Download</a>
					</tr>
				</c:forEach>
			</table>
		</anu:content>
	</c:when>

	<c:when test="${not empty it.dropbox}">
		<!-- Display details of a specific dropbox. -->
		<anu:content layout="doublewide" title="Dropbox Administration">
			<jsp:include page="/jsp/statusmessages.jsp">
				<jsp:param value="${it}" name="it" />
			</jsp:include>

			<form class="anuform" method="post" action="<c:url value="/rest/collreq/dropbox" />">
				<p>
					<label>Dropbox ID</label>
					<c:out value="${it.dropbox.id}" />
					<input type="hidden" name="dropbox" value="${it.dropbox.id}" />
				</p>
				<p>
					<label>Access Code</label>
					<c:out value="${it.dropbox.accessCode}" />
				</p>
				<p>
					<label>Creator</label>
					<c:out value="${it.dropbox.creator.displayName}" />
				</p>
				<p class="instruction"><c:out value="${it.dropbox.creator.username}" /></p>
				<p>
					<label>Created</label>
					<fmt:formatDate value="${it.dropbox.timestamp}" pattern="dd MMM yyyy"/>
				</p>
				<p>
					<label>Expiry</label>
					<fmt:formatDate value="${it.dropbox.expiry}" pattern="dd MMM yyyy"/>
				</p>
				<p>
					<label>Notify Creator</label>
					<input type="checkbox" name="notify" value="true" <c:if test='${it.dropbox.notifyOnPickup == true}'>checked="checked"</c:if> />
				</p>
				<p>
					<label>Active</label>
					<input type="checkbox" name="active" value="true" <c:if test='${it.dropbox.active == true}'>checked="checked"</c:if> />
				</p>
				<p>
					<label>Request ID</label> <a href="<c:url value='/rest/collreq/${it.dropbox.collectionRequest.id}' />"><c:out
							value="${it.dropbox.collectionRequest.id}" /></a>
				</p>
				<p class="text-right">
					<input type="submit" value="Submit" />
				</p>
			</form>
		</anu:content>
	</c:when>

	<c:otherwise>
		<anu:content layout="doublewide" title="Dropbox">
			<jsp:include page="/jsp/statusmessages.jsp">
				<jsp:param value="${it}" name="it" />
			</jsp:include>
		</anu:content>
	</c:otherwise>
</c:choose>

<jsp:include page="/jsp/footer.jsp" />
