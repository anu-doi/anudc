<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<anu:header id="1998" title="ANU Data Commons - User Information" description="description" subject="subject" respOfficer="Doug Moncur" respOfficerContact="mailto:doug.moncur@anu.edu.au"
	ssl="true">

	<link rel="stylesheet" type="text/css" href="<c:url value='/css/default.css' />" />
	<script type="text/javascript" src="<c:url value='/js/user.js' />"></script>
</anu:header>

<jsp:include page="/jsp/header.jsp" />

<anu:content layout="doublewide">
	<c:if test="${not empty it.error}">
		<anu:message type="error">${it.error}</anu:message><br/>
	</c:if>
	<form id="form" class="anuform" method="POST">
		<label for="email">Email:</label>
		<input type="text" id="email" name="email" />
		<br/>
		<input type="submit" value="Submit" />
	</form>
</anu:content>

<jsp:include page="/jsp/footer.jsp" />