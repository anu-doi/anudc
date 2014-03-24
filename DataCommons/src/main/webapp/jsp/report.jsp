<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>

<anu:header id="1998" title="ANU Data Commons - Reports" description="description" subject="subject" respOfficer="Doug Moncur" respOfficerContact="mailto:doug.moncur@anu.edu.au"
	ssl="true">
	<!-- Possible bug in the ANU taglib. The following CSS should not be referenced here. Should be referenced in the taglib. -->
	<link href="//styles.anu.edu.au/_anu/3/style/anu-forms.css" rel="stylesheet" type="text/css" />
	<link rel="stylesheet" type="text/css" href="<c:url value='/css/default.css' />" />
</anu:header>

<jsp:include page="/jsp/header.jsp" />

<anu:content layout="doublenarrow" title="Reports">
	<a href="<c:url value="/rest/report/single" />">Single Item Reports</a><br/>
	<a href="<c:url value="/rest/report/multiple" />">Multiple Item Reports</a><br/>
	<a href="<c:url value="/rest/report/published" />">Published To Location Report</a><br/>
	<a href="<c:url value="/rest/report/webservice" />">Web Service Reports</a><br/>
</anu:content>

<jsp:include page="/jsp/footer.jsp" />