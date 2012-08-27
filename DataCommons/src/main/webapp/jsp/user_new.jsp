<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>

<anu:header id="1998" title="ANU Data Commons - Register" description="description" subject="subject" respOfficer="Doug Moncur" respOfficerContact="mailto:doug.moncur@anu.edu.au"
	ssl="true">
	<!-- Possible bug in the ANU taglib. The following CSS should not be referenced here. Should be referenced in the taglib. -->
	<link href="http://styles.anu.edu.au/_anu/3/style/anu-forms.css" rel="stylesheet" type="text/css" />
	<link rel="stylesheet" type="text/css" href="<c:url value='/css/default.css' />" />
	<script type="text/javascript" src="<c:url value='/js/user.js' />"></script>
</anu:header>

<jsp:include page="/jsp/header.jsp" />

<anu:content layout="doublewide" title="Register User">
	<form id="form" class="anuform" method="POST">
		<label for="firstname" class="req">First Name</label>
		<input type="text" id="firstname" name="firstname" class="required"/>
		<br/>
		<label for="lastname" class="req">Last Name</label>
		<input type="text" id="lastname" name="lastname" class="required"/>
		<br/>
		<label for="email" class="req">Email</label>
		<input type="text" id="email" name="email" class="required email"/>
		<br/>
		<label for="password" class="req">Password</label>
		<input type="password" id="password" name="password" class="required"/>
		<br/>
		<label for="password2" class="req">Repeat Password</label>
		<input type="password" id="password2" name="password2" class="required "/>
		<br/>
		<label for="institution" class="req">Institutional Affiliation</label>
		<input type="text" id="institution" name="institution" class="required"/>
		<br/>
		<label for="phone">Phone</label>
		<input type="text" id="phone" name="phone"/>
		<br/>
		<label for="address">Address</label>
		<textarea id="address" name="address"></textarea>
		<br/>
		<input type="submit" value="Submit" />
	</form>
</anu:content>

<jsp:include page="/jsp/footer.jsp" />