<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib" %>

<anu:header id="1998" title="Login" description="description" subject="subject" respOfficer="Doug Moncur" respOfficerContact="mailto:doug.moncur@anu.edu.au" ssl="true">
	<script type="text/javascript" src="<c:url value='/js/global.js' />"></script>
	<script type="text/javascript" src="<c:url value='/js/login.js' />"></script>
</anu:header>

<jsp:include page="/jsp/header.jsp" />

<anu:content layout="full" extraClass="nopadbottom" title="Login">
	<c:if test="${not empty error}">
		<div id="login-error" class="msg-error">${error}</div>
	</c:if>
	
	<form class="anuform" name="frmLogin" method="post" onsubmit="usernameToLowerCase()" action='<c:url value="/login" />'>
		<p>
			ANU staff and students can login using your ANU ID and password.  Registered external users can login using their email address and password.
		</p>
		<fieldset>
		<legend>Login</legend>
		<p>
			<label for="username">ANU ID</label>
			<input type="text" class="text tfull" name="username" value="" autofocus="autofocus" size="40">
		</p>
		<p>
			<label for="j_password">Password</label>
			<input type='password' name='password' size="40" class="text tfull" />
		</p>
		<p>
			<a href="<c:url value='/rest/user/forgotpassword' />">Forgot your password?</a>
		</p>
		</fieldset>
		<p class="text-right">
			<input name="submit" type="submit" value="Login" />
		</p>
		<%--	<a href="<c:url value='/rest/user/new' />">Register</a> --%>
	</form>
</anu:content>

<anu:content layout="full" extraClass="">
	<div>External users can register for an account</div>
	<form class="anuform" name="frmRegister" method="post" action='<c:url value="/rest/user/new" />' class="margintop">
		<fieldset>
		<legend>Register</legend>
		<p>
		<label for="firstname" class="req">First Name</label>
		<input type="text" id="firstname" name="firstname" class="required text tfull"/>
		</p>
		<p>
		<label for="lastname" class="req">Last Name</label>
		<input type="text" id="lastname" name="lastname" class="required text tfull"/>
		</p>
		<p>
		
		<label for="email" class="req">Email</label>
		<input type="text" id="email" name="email" class="required email text tfull"/>
		</p>
		<p>
		
		<label for="password" class="req">Password</label>
		<input type="password" id="password" name="password" class="required text tfull"/>
		</p>
		<p>
		
		<label for="password2" class="req">Repeat Password</label>
		<input type="password" id="password2" name="password2" class="required text tfull"/>
		</p>
		<p>
		<label for="institution">Institutional Affiliation</label>
		<input type="text" id="institution" name="institution" class="text tfull"/>
		</p>
		<p>
		<label for="address">Address</label>
		<textarea id="address" name="address" class="text tfull"></textarea>
		</p>
		<p>
		<label for="phone">Phone</label>
		<input type="text" id="phone" name="phone" class="text tfull"/>
		</p>
		</fieldset>
		<p class="text-right">
		<input type="submit" value="Register" />
		</p>
	</form>
</anu:content>

<jsp:include page="/jsp/footer.jsp" />
