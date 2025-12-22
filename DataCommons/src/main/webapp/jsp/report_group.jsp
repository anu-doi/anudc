<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>

<anu:header id="1998" title="ANU Data Commons - Reports" description="description" subject="subject" respOfficer="ANU Library" respOfficerContact="mailto:repository.admin@anu.edu.au"
	ssl="true">

	<link rel="stylesheet" type="text/css" href="<c:url value='/css/jquery-ui-1.12.1.custom.css' />" />
	<link rel="stylesheet" type="text/css" href="<c:url value='/css/default.css' />" />
	<script type="text/javascript" src="<c:url value='/js/report.js' />"></script>
	<script type="text/javascript" src="<c:url value='/js/jquery-ui.min.js' />"></script>
</anu:header>

<jsp:include page="/jsp/header.jsp" />

<anu:container type="container">
<anu:content layout="doublenarrow" title="Reports">
	<form class="anuform" method="POST"  action="<c:url value='/rest/report' />">
		<fieldset>
		<div class="field">
			<label for="groupId">Select Group</label>
			<span>
			<select id="groupId" name="groupId">
				<c:forEach items="${it.groups}" var="group">
					<option value="${group.id}">${group.group_name}</option>
				</c:forEach>
			</select>
			</span>
		</div>
		<div class="field">
			<label for="format">Format</label>
			<span>
			<select id="format" name="format">
				<option value="pdf">PDF</option>
				<option value="html">HTML</option>
				<option value="xlsx">Excel</option>
			</select>
			</span>
		</div>
		<br/>
		<div class="field">
			<label for="report">Report</label>
			<span>
				<select id="report" name="report">
					<option value="9">Record Report</option>
				</select>
			</span>
		</div>
		<br/>
		<input type="submit" class="btn btn-primary float-end mr-1" value="Get Report" />
		</fieldset>
	</form>
</anu:content>
</anu:container>

<jsp:include page="/jsp/footer.jsp" />