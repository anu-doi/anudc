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
	<!-- Body goes here -->
	<c:if test="${not empty param.err}">
		<p class="msg-error" id="idFormErrors">
			<c:choose>
				<c:when test="${param.err == 'authfail'}">
					<c:out value="Invalid username and/or password." />
				</c:when>
				<c:when test="${param.err == 'blank'}">
					<c:out value="Username or password cannot be blank." />
				</c:when>
			</c:choose>
		</p>
	</c:if>

	<form class="anuform" name="frmLogin" method="post" onsubmit="return loginValidate(this)" action="<c:url value='/login/login.do' />">
		<p>
			<label class="req" for="idUsername">Username</label>
			<input type="text" class="text" name="username" id="idUsername" />
		</p>
		<p class="msg-error" id="idUsernameError" style="display: none;"></p>
		<p>
			<label for="idPassword">Password: </label>
			<input type="password" name="password" id="idPassword" />
		</p>
		<p class="msg-error" id="idPasswordError" style="display: none;"></p>
		<p>
		<p class="text-right">
			<input type="submit" value="Login" />
			<input type="reset" value="Reset" />
		</p>

	</form>
	<script type="text/javascript">
		document.frmLogin.username.focus();
	</script>

</anu:content>

<jsp:include page="/jsp/footer.jsp" />

