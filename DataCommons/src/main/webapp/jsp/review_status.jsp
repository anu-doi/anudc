<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>

<c:set var="fedoraObject" value="${it.fedoraObject}" />

<html>
	<head>
		<link rel="stylesheet" type="text/css" href="<c:url value='/css/rejectItem.css' />" />
		<script type="text/javascript" src="<c:url value='/js/rejectItem.js' />"></script>
		<script type="text/javascript" src="<c:url value='/js/popup.js' />"></script>
	</head>
	<body>
		<c:if test="${empty it.fedoraObject.reviewReady and empty it.fedoraObject.publishReady}">
			<sec:authorize access="hasPermission(#fedoraObject, 'WRITE') or hasPermission(#fedoraObject,'ADMINISTRATION')">
				<c:url value="/rest/ready/review/${it.fedoraObject.object_id}" var="reviewReadyLink">
					<c:param name="layout" value="${param.layout}" />
					<c:param name="tmplt" value="${param.tmplt}" />
				</c:url>
				<form id="formReviewReady" method="post"  action="${reviewReadyLink}">
					<p>
						<input type="submit" id="reviewReadyButton" name="reviewReadyButton" value="Ready for Review"/>
					</p>
				</form>
			</sec:authorize>
		</c:if>
		<c:if test="${not empty it.fedoraObject.reviewReady}">
			<sec:authorize access="hasPermission(#fedoraObject, 'REVIEW') or hasPermission(#fedoraObject,'ADMINISTRATION')">
				<c:url value="/rest/ready/publish/${it.fedoraObject.object_id}" var="publishReadyLink">
					<c:param name="layout" value="${param.layout}" />
					<c:param name="tmplt" value="${param.tmplt}" />
				</c:url>
				<form id="formPublishReady" method="post"  action="${publishReadyLink}">
					<p>
						<input type="submit" id="publishReadyButton" name="publishReadyButton" value="Ready for Publish"/>
					</p>
				</form>
			</sec:authorize>
		</c:if>
		<c:if test="${not empty it.fedoraObject.publishReady}">
			<sec:authorize access="hasPermission(#fedoraObject, 'PUBLISH') or hasPermission(#fedoraObject,'ADMINISTRATION')">
				<c:url value="/rest/publish/${it.fedoraObject.object_id}" var="publishLink">
					<c:param name="tmplt" value="${param.tmplt}" />
					<c:param name="layout" value="${param.layout}" />
				</c:url>
				<p>
					<input type="button" id="publishButton" name="publishButton" value="Publish" onclick="window.location='${publishLink}'" />
				</p>
			</sec:authorize>
		</c:if>
		<c:if test="${not empty it.fedoraObject.reviewReady or not empty it.fedoraObject.publishReady}">
			<sec:authorize access="hasPermission(#fedoraObject, 'REVIEW') or hasPermission(#fedoraObject, 'PUBLISH') or hasPermission(#fedoraObject,'ADMINISTRATION')">
				<p>
					<input type="button" id="rejectReasonButton" name="rejectReasonButton" value="Reject"/>
				</p>
				<div id="rejectPopup">
					<a id="rejectPopupClose">x</a>
					<p>Reason for Rejection</p>
					
					<c:url value="/rest/ready/reject/${it.fedoraObject.object_id}" var="reviewRejectLink">
						<c:param name="layout" value="${param.layout}" />
						<c:param name="tmplt" value="${param.tmplt}" />
					</c:url>
					<form id="formReviewReject" method="post"  action="${reviewRejectLink}">
						<p>
							<textarea id="rejectReason" name="rejectReason"></textarea>
						</p>
						<input id="rejectReviewButton" name="rejectReviewButton" type="submit" value="Submit" />
					</form>
				</div>
				<div id="backgroundPopup"></div>
			</sec:authorize>
		</c:if>
	</body>
</html>