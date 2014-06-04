<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<anu:header id="1998" title="Scheduled Reports" description="description" subject="subject" respOfficer="Doug Moncur" respOfficerContact="mailto:doug.moncur@anu.edu.au" ssl="true">
	<link rel="stylesheet" type="text/css" href="<c:url value='/css/default.css' />" />
	<link rel="stylesheet" type="text/css" href="<c:url value='/css/scheduler.css' />" />
	<script type="text/javascript" src="<c:url value='/js/global.js' />"></script>
	<script type="text/javascript" src="<c:url value='/js/scheduler.js' />"></script>
	<script type="text/javascript" src="<c:url value='/js/jquery.validate.min.js' />"></script>
</anu:header>

<jsp:include page="header.jsp" />

<anu:content layout="doublewide">
	<h1>Scheduled Reports</h1>
	<p><a href="<c:url value="/rest/report/schedule" />">Schedule another report to run</a></p>
	<table>
		<th>Report</th>
		<th>Email</th>
		<th>Day of Week</th>
		<th>Time</th>
		<th>Format</th>
		<th></th>
		<c:forEach items="${it.scheduled}" var="scheduledReport">
			<tr>
				<td>${scheduledReport.reportName}</td>
				<td>${scheduledReport.email}</td>
				<td>${scheduledReport.daysOfWeek}</td>
				<td>${scheduledReport.hours}:${scheduledReport.minutes}</td>
				<td>${scheduledReport.format}</td>
				<td>
					<c:url var="deleteUrl" value="/rest/report/scheduled/${scheduledReport.reportAutoId}" />
					<a href="javascript:void(0);" id="delete-${scheduledReport.reportAutoId}" onclick="deleteReportAuto('${deleteUrl}')">
						<img class="clickable-icon delete-icon" src="<c:url value='/images/delete_red.png' />" title="Delete Scheduled Report" />
					</a>
				</td>
			</tr>
		</c:forEach>
	</table>
</anu:content>

<jsp:include page="footer.jsp" />