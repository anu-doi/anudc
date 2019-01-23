<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<anu:header id="1998" title="Page" description="description" subject="subject" respOfficer="ANU Library" respOfficerContact="mailto:repository.admin@anu.edu.au"
	ssl="true">
	<link rel="stylesheet" type="text/css" href="<c:url value='/css/default.css' />" />
	<link rel="stylesheet" type="text/css" href="<c:url value='/css/jquery-ui-1.8.20.custom.css' />" />
	<script type="text/javascript" src="<c:url value='/js/jquery.validate.min.js' />"></script>
	<script type="text/javascript" src="<c:url value='/js/jquery-ui-1.8.20.custom.min.js' />"></script>
	<script type="text/javascript" src="<c:url value='/js/global.js' />"></script>
	<script type="text/javascript" src="<c:url value='/js/page.js' />"></script>
	<script type="text/javascript" src="<c:url value='/js/prepopulate.js' />"></script>
</anu:header>

<jsp:include page="header.jsp" />
<jsp:include page="/jsp/statusmessages.jsp">
	<jsp:param value="${it}" name="it" />
</jsp:include>

<jsp:include page="body.jsp" />
<c:if test="${not empty it.sidepage}">
	<c:import url="${it.sidepage}" />
</c:if>
<jsp:include page="footer.jsp" />