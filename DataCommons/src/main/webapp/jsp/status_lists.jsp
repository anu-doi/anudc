<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<anu:header id="1998" title="${it.title}" description="description" subject="subject" respOfficer="ANU Library" respOfficerContact="mailto:repository.admin@anu.edu.au"
	ssl="true">

</anu:header>

<jsp:include page="/jsp/header.jsp" />

<anu:content layout="doublewide" title="${it.title }">
	Records:<br />
	<c:forEach items="${it.resultList}" var="result">
		<a href='<c:url value="/rest/display/${result.fields.id.value}?layout=def:display" />'>${result.fields.name.value}</a>
		<br />
	</c:forEach>
</anu:content>

<jsp:include page="/jsp/footer.jsp" />