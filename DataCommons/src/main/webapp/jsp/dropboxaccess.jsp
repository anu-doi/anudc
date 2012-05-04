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
					<c:out value="${it.dropbox.active}" />
				</p>
				<p>
					<label>Valid Until</label>
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
			<c:if test="${not empty it.downloadables}">
				<table>
					<tr>
						<th>Item</th>
						<th>Link</th>
					</tr>
					<c:forEach var="downloadable" items="${it.downloadables}">
						<tr>
							<td><c:out value="${downloadable.key}" /></td>
							<td><a href="<c:out value='${downloadable.value}' />">Download</a></td>
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
