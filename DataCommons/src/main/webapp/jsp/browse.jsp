<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>

<anu:header id="1998" title="Browse" description="description" subject="subject" respOfficer="ANU Library" respOfficerContact="mailto:repository.admin@anu.edu.au"
	ssl="true">

</anu:header>

<jsp:include page="/jsp/header.jsp" />

<anu:content layout="doublewide" title="Browse">
	<fmt:bundle basename='global'>
		<fmt:message var="searchItemsPerPage" key='search.resultsPerPage' />
	</fmt:bundle>
	<c:url value="/rest/search/browse" var="allURL">
		<c:param name="field">${param.field}</c:param>
	</c:url>
	<c:url value="/rest/search/browse" var="teamURL">
		<c:param name="field">${param.field}</c:param>
		<c:param name="filter">team</c:param>
	</c:url>
	<c:url value="/rest/search/browse" var="publishedURL">
		<c:param name="field">${param.field}</c:param>
		<c:param name="filter">published</c:param>
	</c:url>
	
	<sec:authorize access="isAuthenticated()">
		Show keywords for: 
		<c:if test="${empty param.filter}">
			<b>
		</c:if>
		<a href="${allURL}">All</a>
		<c:if test="${empty param.filter}">
			</b>
		</c:if>
		| 
		<c:if test='${"team" == param.filter}'>
			<b>
		</c:if>
		<a href="${teamURL}">Team</a>
		<c:if test='${"team" == param.filter}'>
			</b>
		</c:if>
		| 
		<c:if test='${"published" == param.filter}'>
			<b>
		</c:if>
		<a href="${publishedURL}">Published</a>
		<c:if test='${"published" == param.filter}'>
			</b>
		</c:if>
	</sec:authorize>
	<hr/>
	<c:if test="${it.resultSet != null}">
		<c:forEach items="${it.resultSet.facetFields}" var="facetField">
			<p>
				<c:forEach items="${facetField.values}" var="facetCount">
					<c:url value="/rest/search/browse/results" var="facetURL">
						<c:param name="field" value="${facetField.name}"/>
						<c:param name="field-select" value='"${facetCount.name}"' />
						<c:param name="limit" value="${searchItemsPerPage}" />
						<c:param name="filter" value="${param.filter}" />
					</c:url>
					<a href="${facetURL}">${facetCount.name} (${facetCount.count})</a><br/>
				</c:forEach>
			</p>
		</c:forEach>
	</c:if>

</anu:content>

<jsp:include page="/jsp/footer.jsp" />