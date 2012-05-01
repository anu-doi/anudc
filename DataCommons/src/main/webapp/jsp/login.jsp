<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>

<anu:header id="1998" title="Login" description="description" subject="subject" respOfficer="Doug Moncur" respOfficerContact="mailto:doug.moncur@anu.edu.au" ssl="true">
	<script type="text/javascript" src="<c:url value='/js/global.js' />"></script>
	<script type="text/javascript" src="<c:url value='/js/login.js' />"></script>

</anu:header>

<jsp:include page="/jsp/header.jsp" />

<anu:content layout="doublenarrow" extraClass="nopadbottom" title="Login">
	<div id="login-error">${error}</div>
	
	<form class="anuform" name="frmLogin" method="post" action='<c:url value="/j_spring_security_check" />'>
		<p>
			<label class="req" for="j_username">Username: </label>
			<input type="text" class="text" name="j_username" value="">
		</p>
		<p>
			<label for="j_password">Password: </label>
			<input type='password' name='j_password' />
		</p>
		<p>
		<p class="text-right">
			<input name="submit" type="submit" value="submit" />
		</p>

	</form>

</anu:content>

<jsp:include page="/jsp/footer.jsp" />
