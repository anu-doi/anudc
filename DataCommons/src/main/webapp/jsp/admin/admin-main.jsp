<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>

<anu:header id="1998" title="Page" description="Administration links page" subject="administration" respOfficer="ANU Library" respOfficerContact="mailto:repository.admin@anu.edu.au"
	ssl="true">
	<link rel="stylesheet" type="text/css" href="<c:url value='/css/default.css' />" />
	<link rel="stylesheet" type="text/css" href="<c:url value='/css/jquery-ui-1.8.20.custom.css' />" />
	<script type="text/javascript" src="<c:url value='/js/jquery.validate.min.js' />"></script>
	<script type="text/javascript" src="<c:url value='/js/jquery-ui-1.8.20.custom.min.js' />"></script>
	<script type="text/javascript" src="<c:url value='/js/global.js' />"></script>
	<script type="text/javascript" src="<c:url value='/js/page.js' />"></script>
	<script type="text/javascript" src="<c:url value='/js/prepopulate.js' />"></script>
</anu:header>

<jsp:include page="../header.jsp" />

<anu:content layout="full" title="Administration">
<h2>Review Functions</h2>
<ul>
	<li><a href="/DataCommons/rest/ready/list/rejected">More Work Required</a></li>
	<li><a href="/DataCommons/rest/ready/list/review">Ready for Review</a></li>
	<li><a href="/DataCommons/rest/ready/list/publish">Ready for Publish</a></li>
</ul>
<h2>Validate/Publish</h2>
<ul>
	<li><a href="/DataCommons/rest/publish/validate/multiple">Validate Multiple Records</a></li>
	<li><a href="/DataCommons/rest/publish/multiple">Publish Multiple Records</a></li>
</ul>
<h2>Administration Functions</h2>
<ul>
	<li><a href="/DataCommons/rest/admin/domains">Domain Administration</a></li>
	<li><a href="/DataCommons/rest/admin/groups">Group Administration</a></li>
	<li><a href="/DataCommons/rest/user/permissions">User Administration</a></li>
	<li><a href="/DataCommons/rest/search/admin">Update Index</a></li>
	<li><a href="/DataCommons/rest/reload">Reload</a></li>
	<li><a href="/DataCommons/rest/report">Report</a></li>
</ul>
</anu:content>

<jsp:include page="../footer.jsp" />