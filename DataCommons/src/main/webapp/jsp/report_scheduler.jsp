<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<anu:header id="1998" title="Schedule Report" description="description" subject="subject" respOfficer="Doug Moncur" respOfficerContact="mailto:doug.moncur@anu.edu.au" ssl="true">
	<link rel="stylesheet" type="text/css" href="<c:url value='/css/default.css' />" />
	<link rel="stylesheet" type="text/css" href="<c:url value='/css/scheduler.css' />" />
	<script type="text/javascript" src="<c:url value='/js/global.js' />"></script>
	<script type="text/javascript" src="<c:url value='/js/scheduler.js' />"></script>
	<script type="text/javascript" src="<c:url value='/js/jquery.validate.min.js' />"></script>
</anu:header>

<jsp:include page="header.jsp" />

<anu:content layout="doublewide">
<h1>Schedule Reports</h1>
<form id="form" name="form" method="POST">
	<p>
		I want to run the report
		<select id="report" name="report">
			<option value="">--None Selected--</option>
			<c:forEach items="${it.reports}" var="report">
				<option value="${report.id}">${report.reportName}</option>
			</c:forEach>
		</select>
	</p>
	<p>	once a week on
		<select id="dayOfWeek" name="dayOfWeek">
			<option value="MON">Monday</option>
			<option value="TUE">Tuesday</option>
			<option value="WED">Wednesday</option>
			<option value="THU">Thursday</option>
			<option value="FRI">Friday</option>
			<option value="SAT">Saturday</option>
			<option value="SUN">Sunday</option>
		</select>
	</p>
	<p>At <input id="hour" class="small-input" name="hour" maxlength="2" value="3"/> <strong>:</strong> <input id="minute" class="small-input" name="minute" maxlength="2" value="47"/></p>
	<p>
		And send it to the email address
		<input id="email" name="email" maxlength="255" />
	</p>
	<p>
		With the parameters
		<div id="reportparams">No Report Selected</div>
	</p>
	<input type="submit" value="Submit" />
</form>
</anu:content>

<jsp:include page="footer.jsp" />