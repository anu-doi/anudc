<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<form name="frmBasicSearch" action="<c:url value='/rest/search/'></c:url>" method="get">
	<p>
		<fmt:bundle basename='global'>
			<fmt:message var="searchItemsPerPage" key='search.resultsPerPage' />
		</fmt:bundle>
		<label for="basicSearchTerms">Search</label>
		<input class="text" type="text" name="q" id="idBasicSearchTerms" size="30" value="<c:out value="${param.q}" />" />
		<!-- Display a dropdown when a user's logged in allowing one to select filters such as Published, Personal, Team. Does not display for Guests. -->
		<c:if test="${not empty pageContext.request.remoteUser}">
			<select name="filter">
				<option value="all" <c:if test="${param.filter == 'all'}">selected="selected" </c:if>>All</option>
				<option value="published" <c:if test="${param.filter == 'published'}">selected="selected" </c:if>>Published</option>
				<option value="team" <c:if test="${param.filter == 'team'}">selected="selected" </c:if>>Team</option>
			</select>
		</c:if>
		<input type="hidden" name="limit" value="<c:out value='${searchItemsPerPage}' />" />
		<input type="submit" value="Search" />
	</p>
	<p class="text-right">
		<a href='<c:url value="/rest/search/advanced" />'>Advanced Search</a>
	</p>
</form>