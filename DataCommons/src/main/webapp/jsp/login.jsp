<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib" %>

<anu:header id="1998" title="Login" description="description" subject="subject" respOfficer="Doug Moncur" respOfficerContact="mailto:doug.moncur@anu.edu.au" ssl="true">
	<script type="text/javascript" src="<c:url value='/js/global.js' />"></script>
	<script type="text/javascript" src="<c:url value='/js/login.js' />"></script>
</anu:header>

<jsp:include page="/jsp/header.jsp" />

<anu:content layout="doublewide" extraClass="nopadbottom" title="Login">
	<c:if test="${not empty error}">
		<div id="login-error" class="msg-error">${error}</div>
	</c:if>
	
	<form class="anuform" name="frmLogin" method="post" onsubmit="usernameToLowerCase()" action='<c:url value="/login" />'>
		<p>
			<label class="req" for="username">Username: </label>
			<input type="text" class="text" name="username" value="" autofocus="autofocus" size="40">
		</p>
		<p>
			<label for="j_password">Password: </label>
			<input type='password' name='password' size="40" />
		</p>
		<p>
		<p class="text-right">
			<input name="submit" type="submit" value="Submit" />
		</p>

	</form>
	<a href="<c:url value='/rest/user/new' />">Register</a> | 
	<a href="<c:url value='/rest/user/forgotpassword' />">Forgot Password</a>

</anu:content>

<jsp:include page="/jsp/footer.jsp" />
