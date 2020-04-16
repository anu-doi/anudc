<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

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
<c:if test="${it.templates != null}">
<h2>Contribute your research data</h2>
<ul>
<c:forEach items="${it.templates}" var="template">
	<li><a href="<c:url value='/rest/display/new?layout=def:new&tmplt=${template.templatePid}' />">${template.name}:</a> ${template.description}</li>
</c:forEach>
</ul>
</c:if>
<h2>Review Functions</h2>
<fmt:bundle basename='global'>
	<fmt:message var="rejectedTitle" key="review.rejected.title" />
	<fmt:message var="reviewReadyTitle" key="review.reviewready.title" />
	<fmt:message var="publishReadyTitle" key="review.publishready.title" />
</fmt:bundle>
<ul>
	<li><a href='<c:url value="/rest/ready/list/rejected" />'>${rejectedTitle}</a></li>
	<li><a href='<c:url value="/rest/ready/list/review" />'>${reviewReadyTitle}</a></li>
	<li><a href='<c:url value="/rest/ready/list/publish" />'>${publishReadyTitle}</a></li>
</ul>
<h2>Validate/Publish</h2>
<ul>
	<li><a href='<c:url value="/rest/publish/validate/multiple" />'>Validate multiple records</a></li>
	<li><a href='<c:url value="/rest/publish/multiple" />'>Publish multiple records</a></li>
</ul>
<sec:authorize access="hasRole('ROLE_ADMIN')">
<h2>Administration Functions</h2>
<ul>
	<li><a href='<c:url value="/rest/admin/domains" />'>Domain administration</a></li>
	<li><a href='<c:url value="/rest/admin/groups" />'>Group administration</a></li>
	<li><a href='<c:url value="/rest/user/permissions" />'>User administration</a></li>
	<li><a href='<c:url value="/rest/collreq/question" />'>Set request questions</a></li>
	<li><a href='<c:url value="/rest/search/admin" />'>Update index</a></li>
	<li><a href='<c:url value="/rest/reload" />'>Reload</a></li>
	<li><a href='<c:url value="/rest/report" />'>Report</a></li>
</ul>
</sec:authorize>
</anu:content>

<jsp:include page="../footer.jsp" />