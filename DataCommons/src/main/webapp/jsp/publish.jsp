<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<anu:header id="1998" title="Publish" description="description" subject="subject" respOfficer="ANU Library" respOfficerContact="mailto:repository.admin@anu.edu.au" ssl="true">
	<link rel="stylesheet" type="text/css" href="<c:url value='/css/default.css' />" />
	<script type="text/javascript" src="<c:url value='/js/global.js' />"></script>
	<script type="text/javascript" src="<c:url value='/js/jquery.validate.min.js' />"></script>
</anu:header>

<jsp:include page="header.jsp" />

<anu:content layout="doublewide">
	<h1>Publishing Page</h1>
	<c:if test="${not empty it.message}">
		Published to:<br />
		${it.message}<br />
	</c:if>
	<c:if test="${not empty it.publishLocations}">
		<form id="form" method="post" action="">
			<c:forEach items="${it.publishLocations}" var="publishLocation">
				<input type="checkbox" name="publish" value="${publishLocation.id}" />${publishLocation.code} - ${publishLocation.name} <br />
			</c:forEach>
			<input id="publishSubmit" type="submit" value="Publish" />
		</form>
	</c:if>
	<c:if test="${empty it.publishLocations}">
		You do not have permission to publish to any locations for this item.
	</c:if>
</anu:content>


<jsp:include page="footer.jsp" />