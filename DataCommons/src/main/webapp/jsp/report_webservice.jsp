<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>

<anu:header id="1998" title="ANU Data Commons - Reports" description="description" subject="subject" respOfficer="ANU Library" respOfficerContact="mailto:repository.admin@anu.edu.au"
	ssl="true">

	<link rel="stylesheet" type="text/css" href="<c:url value='/css/jquery-ui-1.8.20.custom.css' />" />
	<link rel="stylesheet" type="text/css" href="<c:url value='/css/default.css' />" />
	<script type="text/javascript" src="<c:url value='/js/report.js' />"></script>
	<script type="text/javascript" src="<c:url value='/js/jquery-ui-1.8.20.custom.min.js' />"></script>
</anu:header>

<jsp:include page="/jsp/header.jsp" />

<anu:content layout="doublenarrow" title="Reports">
	<form class="anuform" method="POST"  action="<c:url value='/rest/report' />">
		<label for="wid">Web Service ID</label><input type="text" id="rid" name="rid"/><br/>
		<label for="format">Format</label>
		<select id="format" name="format">
			<option value="pdf">PDF</option>
			<option value="html">HTML</option>
		</select>
		<br/>
		<label for="report">Report</label>
		<select id="report" name="report">
			<option value="8">Web Service Report</option>
		</select>
		<br/>
		<input type="submit" class="right" value="Get Report" />
	</form>
</anu:content>

<jsp:include page="/jsp/footer.jsp" />