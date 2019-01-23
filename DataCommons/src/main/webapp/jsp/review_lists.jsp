<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<anu:header id="1998" title="Search" description="description" subject="subject" respOfficer="ANU Library" respOfficerContact="mailto:repository.admin@anu.edu.au"
	ssl="true">

</anu:header>

<jsp:include page="/jsp/header.jsp" />

<anu:content layout="doublewide" title="Review Lists Available">
	<fmt:bundle basename='global'>
		<fmt:message var="rejectedTitle" key="review.rejected.title" />
		<fmt:message var="reviewReadyTitle" key="review.reviewready.title" />
		<fmt:message var="publishReadyTitle" key="review.publishready.title" />
	</fmt:bundle>
	<a href='<c:url value="/rest/ready/list/rejected" />'>${rejectedTitle}</a><br />
	<a href='<c:url value="/rest/ready/list/review" />'>${reviewReadyTitle}</a><br />
	<a href='<c:url value="/rest/ready/list/publish" />'>${publishReadyTitle}</a><br />
</anu:content>

<jsp:include page="/jsp/footer.jsp" />