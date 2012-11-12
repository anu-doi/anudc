<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>

<anu:header id="1998" title="ANU Data Commons - Reports" description="description" subject="subject" respOfficer="Doug Moncur" respOfficerContact="mailto:doug.moncur@anu.edu.au"
	ssl="true">
	<!-- Possible bug in the ANU taglib. The following CSS should not be referenced here. Should be referenced in the taglib. -->
	<link href="http://styles.anu.edu.au/_anu/3/style/anu-forms.css" rel="stylesheet" type="text/css" />
	<link rel="stylesheet" type="text/css" href="<c:url value='/css/jquery-ui-1.8.20.custom.css' />" />
	<link rel="stylesheet" type="text/css" href="<c:url value='/css/default.css' />" />
	<script type="text/javascript" src="<c:url value='/js/report.js' />"></script>
	<script type="text/javascript" src="<c:url value='/js/jquery-ui-1.8.20.custom.min.js' />"></script>
</anu:header>

<jsp:include page="/jsp/header.jsp" />

<anu:content layout="doublenarrow" title="Reports">
	<form class="anuform" method="POST"  action="<c:url value='/rest/report' />">
		<label for="report">Report</label>
		<select id="report" name="report">
			<option value="6">Reports Published To Location</option>
		</select>
		<br/>
		<label for="location">Location</label>
		<select name="location" id="location">
			<c:forEach items="${it.publishLocations}" var="location">
				<option value="${location.code}">${location.name}</option>
			</c:forEach>
		</select>
		<br/>
		<label for="format">Format</label>
		<select id="format" name="format">
			<option value="pdf">PDF</option>
			<option value="html">HTML</option>
		</select>
		<br/>
		<input type="submit" class="right" value="Get Report" />
	</form>
</anu:content>

<jsp:include page="/jsp/footer.jsp" />