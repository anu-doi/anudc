<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>

<anu:header id="1998" title="Site Map" description="description" subject="subject" respOfficer="Doug Moncur" respOfficerContact="mailto:doug.moncur@anu.edu.au" ssl="true">
	<link rel="stylesheet" type="text/css" href="<c:url value='/css/default.css' />" />
	<script type="text/javascript" src="<c:url value='/js/global.js' />"></script>
</anu:header>

<jsp:include page="header.jsp" />

<anu:content layout="doublewide">
	<h1>Site Map</h1>
	<c:forEach items="${it.resultSet.documentList}" var="row" varStatus="count">
		<a href='<c:url value="/rest/display/${row['id']}?layout=def:display" />'>
		${row['published.name']}
		</a><br />
	</c:forEach>
</anu:content>

<jsp:include page="footer.jsp" />