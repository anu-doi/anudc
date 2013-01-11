<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<anu:header id="1998" title="Error" description="description" subject="subject" respOfficer="Doug Moncur" respOfficerContact="mailto:doug.moncur@anu.edu.au" ssl="true">
	<script type="text/javascript" src="<c:url value='/js/global.js' />"></script>
	<script type="text/javascript" src="<c:url value='/js/login.js' />"></script>

</anu:header>

<jsp:include page="header.jsp" />
<anu:content layout="doublenarrow">
	<c:choose>
		<c:when test="${not empty it.messages}">
			<p>
				<c:forEach var="message" items="${it.messages}">
					${message}<br/>
				</c:forEach>
			</p>
		</c:when>
		<c:when test="${not empty it.message}">
			<p>${it.message}</p>
		</c:when>
		<c:otherwise>
			<p>Error retrieving page</p>
		</c:otherwise>
	</c:choose>
</anu:content>