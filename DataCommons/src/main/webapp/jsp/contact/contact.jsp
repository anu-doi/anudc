<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<anu:header id="1998" title="Page" description="Administration links page" subject="administration" respOfficer="ANU Library" respOfficerContact="mailto:repository.admin@anu.edu.au"
	ssl="true">
	<link rel="stylesheet" type="text/css" href="<c:url value='/css/default.css' />" />
</anu:header>

<jsp:include page="../header.jsp" />

<fmt:bundle basename='global'>
	<fmt:message var="rejectedTitle" key="review.rejected.title" />
</fmt:bundle>

<fmt:setBundle basename='global'/>

<anu:content layout="full" title="Contact">

	<h2>Functional contacts</h2>
	<table class="fullwidth">
		<tr>
			<th>Name</th>
			<th>Contact details</th>
		</tr>
		<tr>
			<td><fmt:message key="contact.functional.name" /></td>
			<td>
				<div><img class="hpad absmiddle" src="//style.anu.edu.au/_anu/images/icons/web/mail.png" alt="Email"/><a href='mailto:<fmt:message key="contact.functional.email" />'><fmt:message key="contact.functional.email" /></a></div>
				<div><img class="hpad absmiddle" src="//style.anu.edu.au/_anu/images/icons/web/phone.png" alt="Phone"/><fmt:message key="contact.functional.phone" /></div>
				<div><img class="hpad absmiddle" src="//style.anu.edu.au/_anu/images/icons/web/link.png" alt="Link"/><a href='<c:url value="/"/>'><fmt:message key="contact.functional.website.title" /></a></div>
			</td>
		</tr>
	</table>

	<h2>People</h2>
	<table class="fullwidth">
		<tr>
			<th>Name</th>
			<th>Contact Details</th>
		</tr>
		<tr>
			<td><div class="large"><fmt:message key="contact.director.name" /></div><fmt:message key="contact.director.title" /></td>
			<td><img class="hpad absmiddle" src="//style.anu.edu.au/_anu/images/icons/web/mail.png" alt="Email"/><a href='mailto:<fmt:message key="contact.director.email" />'><fmt:message key="contact.director.email" /></a></td>
		</tr>
		<tr>
			<td><div class="large"><fmt:message key="contact.manager.name" /></div><fmt:message key="contact.manager.title" /></td>
			<td>
			<div><img class="hpad absmiddle" src="//style.anu.edu.au/_anu/images/icons/web/mail.png" alt="Email"/><a href='mailto:<fmt:message key="contact.manager.email" />'><fmt:message key="contact.manager.email" /></a></div>
			<div><img class="hpad absmiddle" src="//style.anu.edu.au/_anu/images/icons/web/phone.png" alt="Phone"/><fmt:message key="contact.manager.phone" /></div>
			</td>
		</tr>
	</table>
	<div class="centre w280px anu-feedback-uni">
		<a href="//eforms.anu.edu.au/Inifinit_Prod/Produces/wizard/8382ffaf-f52e-4f8a-bb5af-cbfe751d8b0">
			<img src="//style.anu.edu.au/_anu/4/images/feedback/anu-feedback-uni.png" onmouseover="this.src='//style.anu.edu.au/_anu/4/images/feedback/anu-feedback-uni-over.png'" onmouseout="this.src='//style.anu.edu.au/_anu/4/images/feedback/anu-feedback-uni.png'" alt="University feedback"/>
		</a>
	</div>
</anu:content>