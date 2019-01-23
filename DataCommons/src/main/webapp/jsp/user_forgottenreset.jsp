<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<anu:header id="1998" title="ANU Data Commons - User Information" description="description" subject="subject" respOfficer="ANU Library" respOfficerContact="mailto:repository.admin@anu.edu.au"
	ssl="true">

	<link rel="stylesheet" type="text/css" href="<c:url value='/css/default.css' />" />
	<script type="text/javascript" src="<c:url value='/js/user.js' />"></script>
</anu:header>

<jsp:include page="/jsp/header.jsp" />

<anu:content layout="doublewide">
	<form id="form" method="POST" class="anuform">
		<label for="password">New Password</label>
		<input type="password" id="password" name="password" />
		<br/>
		<label for="password2">Retype Password</label>
		<input type="password" id="password2" name="password2" />
		<br/>
		<input type="submit" value="Submit" />
	</form>
</anu:content>

<jsp:include page="/jsp/footer.jsp" />