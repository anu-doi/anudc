<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>

<anu:header id="1998" title="Dropbox Access" description="DESCRIPTION" subject="SUBJECT" respOfficer="ANU Library" respOfficerContact="mailto:repository.admin@anu.edu.au" ssl="true">

</anu:header>

<jsp:include page="/jsp/header.jsp" />

<anu:container type="container">
<c:choose>
	<c:when test="${not empty it.dropbox}">
		<anu:content layout="doublewide" title="Dropbox Access">
			<jsp:include page="/jsp/statusmessages.jsp">
				<jsp:param value="${it}" name="it" />
			</jsp:include>

			<form method="get" action="<c:url value='/rest/collreq/dropbox/access' />/${it.dropbox.accessCode}" class="anuform">
				<fieldset>
				<div class="field">
					<label>Access Code</label>
					<div class="field-span">
					<c:out value="${it.dropbox.accessCode}" />
					</div>
				</div>
				<div class="field">
					<label>Status</label>
					<div class="field-span">
					<c:choose>
						<c:when test="${it.dropbox.active == true}">
							<img alt="Active status" src="<c:url value='/images/accept.png'/>" />&nbsp;Active
						</c:when>
						<c:otherwise>
							<img alt="Inactive status" src="<c:url value='/images/cancel.png'/>" />&nbsp;Inactive
						</c:otherwise>
					</c:choose>
					</div>
				</div>
				<div class="field">
					<label>Expires</label>
					<div class="field-span">
					<c:out value="${it.dropbox.expiry}" />
					</div>
				</div>
				<p>
					<label for="idP">Password</label>
					<input type="password" name="p" id="idP" value="<c:out value='${param.p}' />" />
				</p>
				<p class="float-end">
					<input class="btn btn-primary mr-1" type="submit" value="Submit" />
				</p>
				</fieldset>
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
</anu:container>
<jsp:include page="/jsp/footer.jsp" />
