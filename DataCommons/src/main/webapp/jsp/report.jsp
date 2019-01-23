<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>

<anu:header id="1998" title="ANU Data Commons - Reports" description="description" subject="subject" respOfficer="ANU Library" respOfficerContact="mailto:repository.admin@anu.edu.au"
	ssl="true">

	<link rel="stylesheet" type="text/css" href="<c:url value='/css/default.css' />" />
</anu:header>

<jsp:include page="/jsp/header.jsp" />

<anu:content layout="doublenarrow" title="Reports">
	<sec:authorize access="hasRole('ROLE_ADMIN')">
	<p><a href="<c:url value="/rest/report/scheduled" />">Schedule Reports</a></p>
	</sec:authorize>
	<strong>Report Types</strong>
	<ul class="nobullet">
		<li><a href="<c:url value="/rest/report/single" />">Single Item Reports</a></li>
		<li><a href="<c:url value="/rest/report/multiple" />">Multiple Item Reports</a></li>
		<li><a href="<c:url value="/rest/report/group" />">Group Reports</a></li>
		<li><a href="<c:url value="/rest/report/published" />">Published To Location Reports</a></li>
		<li><a href="<c:url value="/rest/report/webservice" />">Web Service Reports</a></li>
	</ul>
</anu:content>

<jsp:include page="/jsp/footer.jsp" />