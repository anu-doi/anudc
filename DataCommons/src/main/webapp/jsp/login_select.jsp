<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<anu:header id="1998" title="Login" description="description" subject="subject" respOfficer="Doug Moncur" respOfficerContact="mailto:doug.moncur@anu.edu.au"
	ssl="true">
	<script type="text/javascript" src="<c:url value='/js/global.js' />"></script>

</anu:header>

<jsp:include page="/jsp/header.jsp" />

<anu:content layout="doublewide" extraClass="nopadbottom" title="Login">
	<c:if test="${not empty error}">
		<div id="login-error" class="msg-error">${error}</div>
	</c:if>

	<form class="anuform labelwide" name="frmLogin" method="post" onsubmit="usernameToLowerCase()" action='<c:url value="/login" />'>
		<fieldset>
			<p>
				<label for="username">Uni ID or Email</label>
				<input type="text" class="text tfull" name="username" value="" size="60" autofocus="autofocus">
			</p>
			<p class="instruction">ANU Uni ID (e.g. u1234567, a123456) or external Email address (e.g. john.smith@gmail.com)</p>
			<p>
				<label for="password">Password</label>
				<input type="password" class="text tfull" name="password" size="60" />
			</p>
			<p>
			<p class="text-right">
				<input name="submit" type="submit" value="Login" />
			</p>
	
		</fieldset>
	</form>
	<a href="<c:url value='/rest/user/new' />">Register</a>&nbsp;|&nbsp; 
	<a href="<c:url value='/rest/user/forgotpassword' />">Forgot Password</a>

</anu:content>

<jsp:include page="/jsp/footer.jsp" />
