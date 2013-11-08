<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>

<anu:header id="1998" title="TITLE" description="DESCRIPTION" subject="SUBJECT" respOfficer="Doug Moncur" respOfficerContact="doug.moncur@anu.edu.au" ssl="true">
	<!-- Possible bug in the ANU taglib. The following CSS should not be referenced here. Should be referenced in the taglib. -->
	<link href="http://styles.anu.edu.au/_anu/3/style/anu-forms.css" rel="stylesheet" type="text/css" />
</anu:header>

<jsp:include page="/jsp/header.jsp" />

<c:choose>
	<c:when test="${not empty it.dropbox}">
		<anu:content layout="doublewide" title="Dropbox Access">
			<jsp:include page="/jsp/statusmessages.jsp">
				<jsp:param value="${it}" name="it" />
			</jsp:include>

			<form method="get" action="<c:url value='/rest/collreq/dropbox/access' />/${it.dropbox.accessCode}" class="anuform">
				<p>
					<label>Access Code</label>
					<c:out value="${it.dropbox.accessCode}" />
				</p>
				<p>
					<label>Status</label>
					<c:choose>
						<c:when test="${it.dropbox.active == true}">
							<img src="<c:url value='/images/accept.png' />" />&nbsp;Active
						</c:when>
						<c:otherwise>
							<img src="<c:url value='/images/cancel.png' />" />&nbsp;Inactive
						</c:otherwise>
					</c:choose>
				</p>
				<p>
					<label>Expires</label>
					<c:out value="${it.dropbox.expiry}" />
				</p>
				<p>
					<label for="idP">Password</label>
					<input type="password" name="p" id="idP" value="<c:out value='${param.p}' />" />
				</p>
				<p class="text-right">
					<input type="submit" value="Submit" />
				</p>
			</form>

			<c:if test="${it.downloadables != null}">
				<table class="w-doublewide" >
					<tr>
						<th>Item</th>
						<th>Link</th>
					</tr>
					<c:forEach var="downloadable" items="${it.downloadables}">
						<tr>
							<td><c:out value="${downloadable.key}" /></td>
							<td><a href="<c:url value='${downloadable.value}' />">Download</a></td>
						</tr>
					</c:forEach>
				</table>
			</c:if>
			
			<c:if test="${it.downloadAsZipUrl != null}">
				<a href="<c:url value='${it.downloadAsZipUrl}' />">Download all files as Zip</a>
			</c:if>

			<c:if test="${it.fetchables != null}">
				<table>
					<tr>
						<th>External Link</th>
					</tr>
					<c:forEach var="fetchable" items="${it.fetchables}">
						<tr>
							<td><a href="<c:url value='${fetchable}' />"><c:out value="${fetchable}" /></a></td>
						</tr>
					</c:forEach>
				</table>
			</c:if>
		</anu:content>
	</c:when>
	<c:otherwise>
		<anu:content layout="doublewide" title="Dropbox Access">
			<jsp:include page="/jsp/statusmessages.jsp">
				<jsp:param value="${it}" name="it" />
			</jsp:include>
		</anu:content>
	</c:otherwise>
</c:choose>

<jsp:include page="/jsp/footer.jsp" />
