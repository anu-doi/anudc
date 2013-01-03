<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<anu:header id="1998" title="Validate" description="description" subject="subject" respOfficer="Doug Moncur" respOfficerContact="mailto:doug.moncur@anu.edu.au" ssl="true">
	<link rel="stylesheet" type="text/css" href="<c:url value='/css/default.css' />" />
	<script type="text/javascript" src="<c:url value='/js/global.js' />"></script>
	<script type="text/javascript" src="<c:url value='/js/jquery.validate.min.js' />"></script>
</anu:header>

<jsp:include page="header.jsp" />

<anu:content layout="doublewide">
	<anu:breadcrumbs>
		<c:url value="/rest/search" var="searchLink" />
		<anu:crumb title="Search" href="${searchLink}"/>
		<c:url value="/rest/display/${it.item}" var="displayURL">
			<c:param name="layout">def:display</c:param>
		</c:url>
		<anu:crumb title="Display" href='${displayURL}' />
		<anu:crumb title="Validate" />
	</anu:breadcrumbs>
	 
	<h1>Publication Validation</h1>
	
	<c:if test="${not empty it.message}">
		Published to:<br />
		${it.message}<br />
	</c:if>
	<form id="form" method="post" action="">
		<c:forEach items="${it.publishLocations}" var="publishLocation">
			<input type="radio" name="publish" value="${publishLocation.id}" />${publishLocation.code} - ${publishLocation.name} <br />
		</c:forEach>
		<input id="publishSubmit" type="submit" value="Check Validity" />
	</form>
	<c:if test="${not empty it.validateMessages}">
		<p class="msg-info">
			<c:forEach items="${it.validateMessages}" var="validateMessage">
				${validateMessage} <br/>
			</c:forEach>
		</p>
	</c:if>
	<c:if test="${pageContext.request.method=='POST' and empty it.validateMessages}">
		<p class="msg-success">Record successfully validated</p>
	</c:if>
</anu:content>


<jsp:include page="footer.jsp" />