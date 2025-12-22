<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<anu:header id="1998" title="Schedule Report" description="description" subject="subject" respOfficer="ANU Library" respOfficerContact="mailto:repository.admin@anu.edu.au" ssl="true">
	<!--  <link rel="stylesheet" type="text/css" href="<c:url value='/css/default.css' />" />  -->
	<link rel="stylesheet" type="text/css" href="<c:url value='/css/scheduler.css' />" />
	<script type="text/javascript" src="<c:url value='/js/global.js' />"></script>
	<script type="text/javascript" src="<c:url value='/js/scheduler.js' />"></script>
	<script type="text/javascript" src="<c:url value='/js/jquery.validate.min.js' />"></script>
</anu:header>

<jsp:include page="header.jsp" />

<anu:container type="container">
<anu:content layout="doublewide">
<h1>Schedule Reports</h1>
<form class="anuform" id="form" name="form" method="POST">
<fieldset>
	<div class="field">
		<label for="report">I want to run the report </label>
		<span class="ml-1">
		<select id="report" name="report">
			<option value="">--None Selected--</option>
			<c:forEach items="${it.reports}" var="report">
				<option value="${report.id}">${report.reportName}</option>
			</c:forEach>
		</select>
		</span>
	</div>
	<div class="field">	<label for="dayOfWeek"> once a week on </label>
	<span class="ml-1">
		<select id="dayOfWeek" name="dayOfWeek">
			<option value="MON">Monday</option>
			<option value="TUE">Tuesday</option>
			<option value="WED">Wednesday</option>
			<option value="THU">Thursday</option>
			<option value="FRI">Friday</option>
			<option value="SAT">Saturday</option>
			<option value="SUN">Sunday</option>
		</select>
		</span>
	</div>
	<div class="field"><label id="time">At</label> 
	<div role="group" aria-labelledby="time">
	<span class="ml-1">
	<input id="hour" class="small-input" name="hour" maxlength="2" value="3" aria-label="hours"/> <strong>:</strong> <input id="minute" class="small-input" name="minute" maxlength="2" value="47" aria-label="minutes"/>
	</span>
	</div>
	</div>
	<div class="field">
		<label for="format"> In the format </label> 
		<span class="ml-1">
		<select id="format" name="format">
			<option value="html">Html</option>
			<option value="xlsx">Excel</option>
			<option value="pdf" selected="true">PDF</option>
		</select>
		</span>
	</div>
	<div class="field">
		<label for="email"> And send it to the email address </label>
		<span class="ml-1">
		<input id="email" name="email" maxlength="255" />
		</span>
	</div>
	<div class="field"><label>With the parameters</label><span id="reportparams">No Report Selected</span></div>
	<div class="float-end">
		<input class="btn btn-primary mr-1" type="submit" value="Submit" />
	</div>
	</fieldset>
</form>
</anu:content>
</anu:container>
<jsp:include page="footer.jsp" />