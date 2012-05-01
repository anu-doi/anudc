<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>

<anu:header id="1998" title="TITLE" description="DESCRIPTION" subject="SUBJECT" respOfficer="Doug Moncur" respOfficerContact="doug.moncur@anu.edu.au" ssl="true">
	<!-- Possible bug in the ANU taglib. The following CSS should not be referenced here. Should be referenced in the taglib. -->
	<link href="http://styles.anu.edu.au/_anu/3/style/anu-forms.css" rel="stylesheet" type="text/css" />
</anu:header>

<jsp:include page="/jsp/header.jsp" />

<c:choose>
	<c:when test="${empty it}">
		<anu:content layout="doublewide" title="Dropboxes">
			No dropboxes specified.
		</anu:content>
	</c:when>
	<c:when test="${not empty it.dropboxes}">
		<anu:content layout="doublewide" title="Dropboxes">
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
				</tr>
				<c:forEach var="iDropbox" items="${it.dropboxes}">
					<tr>
						<td><a href="<c:url value='/rest/collreq/dropbox' />/${iDropbox.id}"><c:out value="${iDropbox.id}" /></a></td>
						<td><a href="<c:url value='/rest/collreq/dropbox' />/${iDropbox.id}"><c:out value="${iDropbox.accessCode}" /></a></td>
						<td><c:out value="${iDropbox.creatorUserId}" /></td>
						<td><c:out value="${iDropbox.timestamp}" /></td>
						<td><c:out value="${iDropbox.expiry}" /></td>
						<td><c:out value="${iDropbox.notifyOnPickup}" /></td>
						<td><c:out value="${iDropbox.active}" /></td>
						<td><c:out value="${iDropbox.collectionRequest.id}" /></td>
					</tr>
				</c:forEach>
			</table>
		</anu:content>
	</c:when>
	<c:when test="${not empty it.dropbox}">
		<anu:content layout="doublewide" title="Dropbox">
			<form class="anuform">
				<p>
					<label>Dropbox ID</label>
					<input type="text" readonly="readonly" value="<c:out value="${it.dropbox.id}" />" />
				</p>
				<p>
					<label>Access Code</label>
					<input type="text" readonly="readonly" value="<c:out value="${it.dropbox.accessCode}" />" />
				</p>
				<p>
					<label>Creator</label>
					<input type="text" readonly="readonly" value="<c:out value="${it.dropbox.creatorUserId}" />" />
				</p>
				<p>
					<label>Created</label>
					<input type="text" readonly="readonly" value="<c:out value="${it.dropbox.timestamp}" />" />
				</p>
				<p>
					<label>Expiry</label>
					<input type="text" value="<c:out value="${it.dropbox.expiry}" />" />
				</p>
				<p>
					<label>Notify Creator</label>
					<input type="text" value="<c:out value="${it.dropbox.notifyOnPickup}" />" />
				</p>
				<p>
					<label>Active</label>
					<input type="text" value="<c:out value="${it.dropbox.active}" />" />
				</p>
				<p>
					<label>Request ID</label>
					<input type="text" readonly="readonly" value="<c:out value="${it.dropbox.collectionRequest.id}" />" />
				</p>
				<p class="text-right">
					<input type="submit" value="Submit" />
				</p>
			</form>
		</anu:content>
	</c:when>
</c:choose>

<jsp:include page="/jsp/footer.jsp" />
