<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<anu:header id="1998" title="Login" description="description" subject="subject" respOfficer="Doug Moncur" respOfficerContact="mailto:doug.moncur@anu.edu.au"
	ssl="true">
	<script type="text/javascript" src="<c:url value='/js/global.js' />"></script>

</anu:header>

<jsp:include page="/jsp/header.jsp" />

<anu:content layout="doublenarrow" extraClass="nopadbottom" title="Select Login Method">
	<div id="login-error">${error}</div>

	<label for="loginmethod">Select Login Method</label>
	<form method="GET">
		<select name="method" id="method" onchange="submit(true)">
			<option value="">Please select a login method</option>
			<option value="anu">ANU User</option>
			<option value="registered">Registered User</option>
		</select>
	</form>
	<br />
	<a href="<c:url value='/rest/user/new' />">Register</a>

</anu:content>

<jsp:include page="/jsp/footer.jsp" />
