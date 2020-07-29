<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>

<c:set var="fedoraObject" value="${it.fedoraObject}" />

<c:set var="layoutstyle" value="doublenarrow" />
<c:if test="${empty it.sidepage}">
	<c:set var="layoutstyle" value="doublewide" />
</c:if>
<anu:content layout="${layoutstyle}">
	<sec:authorize access="hasPermission(#fedoraObject,'READ') or hasPermission(#fedoraObject,'WRITE') or hasPermission(#fedoraObject,'REVIEW') or hasPermission(#fedoraObject,'PUBLISH') or hasPermission(#fedoraObject,'ADMINISTRATION')">
		<c:if test="${not empty it.fedoraObject.reviewReady}">
			<anu:message type="info">
				Ready for Review
			</anu:message>
			<br />
		</c:if>
		<c:if test="${not empty it.fedoraObject.publishReady}">
			<anu:message type="info">
				Ready for Publishing
			</anu:message>
			<br />
		</c:if>
		<c:if test="${not empty it.fedoraObject.reviewReject}">
			<anu:message type="info">
				<strong>Rejection Reasons:</strong><br />
				${it.fedoraObject.reviewReject.reason}
			</anu:message>
			<br />
		</c:if>
	</sec:authorize>
<c:out escapeXml="false" value="${it.page}" />

<input type="hidden" readonly="readonly" name="itemType" id="itemType" value="${it.itemType}" />
</anu:content>