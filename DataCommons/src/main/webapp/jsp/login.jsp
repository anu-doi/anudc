<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib" %>

<anu:header id="1998" title="Login" description="description" subject="subject" respOfficer="ANU Library" respOfficerContact="mailto:repository.admin@anu.edu.au" ssl="true">
	<script type="text/javascript" src="<c:url value='/js/global.js' />"></script>
	<script type="text/javascript" src="<c:url value='/js/login.js' />"></script>
</anu:header>

<jsp:include page="/jsp/header.jsp" />

<anu:container type="container">
<div class="row">
<div class="col">
<anu:content layout="full" extraClass="pb-0" title="Login"><!-- extraClass="nopadbottom" -->
	<c:if test="${not empty error}">
		<div id="login-error" class="msg-error">${error}</div>
	</c:if>
	
	<form class="anuform" name="frmLogin" method="post" onsubmit="usernameToLowerCase()" action='<c:url value="/login" />'>
		<p>
			ANU staff and students can login using your ANU ID and password.  Registered external users can login using their email address and password.
		</p>
		<fieldset>
		<legend>Login</legend>
		<div class="field">
			<label class="req" for="j_username">ANU ID</label>
			<span>
			<input type="text" class="text tfull" id="j_username" name="username" value="" autofocus="autofocus" size="40" aria-required="true">
			</span>
		</div>
		<div class="field">
			<label class="req" for="j_password">Password</label>
			<span>
			<input type='password' name='password' id="j_password" size="40" class="text tfull" aria-required="true"/>
			</span>
		</div>
		<div class="field">
			<a href="<c:url value='/rest/user/forgotpassword' />">Forgot your password?</a>
		</div>
		</fieldset>
		<p class="float-end">
			<input class="btn btn-small btn-primary" name="submit" type="submit" value="Login" />
		</p>
		<%--	<a href="<c:url value='/rest/user/new' />">Register</a> --%>
	</form>
</anu:content>
</div>

<anu:content layout="full" extraClass="">
	<div>External users can register for an account</div>
	<form class="anuform" name="frmRegister" method="post" action='<c:url value="/rest/user/new" />'>
		<fieldset>
		<legend>Register</legend>
		<div class="field">
		<label for="firstname" class="req">First Name</label>
		<span>
		<input type="text" id="firstname" name="firstname" class="required text tfull" aria-required="true"/>
		</span>
		</div>
		<div class="field">
		<label for="lastname" class="req">Last Name</label>
		<span>
		<input type="text" id="lastname" name="lastname" class="required text tfull" aria-required="true"/>
		</span>
		</div>
		<div class="field">
		<label for="email" class="req">Email</label>
		<span>
		<input type="text" id="email" name="email" class="required email text tfull" aria-required="true"/>
		</span>
		</div>
		<div class="field">		
		<label for="password" class="req">Password</label>
		<span>
		<input type="password" id="password" name="password" class="required text tfull" aria-required="true"/>
		</span>
		</div>
		<div class="field">
		
		<label for="password2" class="req">Repeat Password</label>
		<span>
		<input type="password" id="password2" name="password2" class="required text tfull" aria-required="true"/>
		</span>
		</div>
		<div class="field">
		<label for="institution">Institutional Affiliation</label>
		<span>
		<input type="text" id="institution" name="institution" class="text tfull"/>
		</span>
		</div>
		<div class="field">
		<label for="address">Address</label>
		<textarea id="address" name="address" class="tfull"></textarea>
		</div>
		<div class="field">
		<label for="phone">Phone</label>
		<span>
		<input type="text" id="phone" name="phone" class="text tfull"/>
		</span>
		</div>
		</fieldset>
		<p class="float-end">
		<input class="btn btn-small btn-primary" type="submit" value="Register" />
		</p>
	</form>
</anu:content>

</div>
</anu:container>
<jsp:include page="/jsp/footer.jsp" />
