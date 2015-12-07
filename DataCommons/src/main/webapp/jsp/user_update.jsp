<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<anu:header id="1998" title="ANU Data Commons - User Information" description="description" subject="subject" respOfficer="Doug Moncur" respOfficerContact="mailto:doug.moncur@anu.edu.au"
	ssl="true">

	<link rel="stylesheet" type="text/css" href="<c:url value='/css/default.css' />" />
	<script type="text/javascript" src="<c:url value='/js/user.js' />"></script>
</anu:header>

<jsp:include page="/jsp/header.jsp" />

<anu:content layout="doublenarrow" title="Update User Information">
	<c:if test="${not empty it.error}">
		<anu:message type="error">${it.error}</anu:message><br/>
	</c:if>
	<form id="form" class="anuform" method="POST">
		<label for="password" class="req">Password</label>
		<input type="password" id="password" name="password" class="required "/>
		<br/>
		<label for="firstname" class="req">First Name</label>
		<input type="text" id="firstname" name="firstname" class="required" value="${it.user.user_registered.given_name}"/>
		<br/>
		<label for="lastname" class="req">Last Name</label>
		<input type="text" id="lastname" name="lastname" class="required" value="${it.user.user_registered.last_name}"/>
		<br/>
		<label for="newpassword">New Password</label>
		<input type="password" id="newpassword" name="newpassword"/>
		<br/>
		<label for="newpassword2">Repeat New Password</label>
		<input type="password" id="newpassword2" name="newpassword2"/>
		<br/>
		<label for="institution" class="req">Institutional Affiliation</label>
		<input type="text" id="institution" name="institution" class="required" value="${it.user.user_registered.institution}"/>
		<br/>
		<label for="phone">Phone</label>
		<input type="text" id="phone" name="phone" value="${it.user.user_registered.phone}"/>
		<br/>
		<label for="address">Address</label>
		<textarea id="address" name="address">${it.user.user_registered.address}</textarea>
		<br/>
		<input type="submit" value="Submit" />
	</form>
</anu:content>

<jsp:include page="/jsp/footer.jsp" />