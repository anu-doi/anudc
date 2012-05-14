<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>

<form class="anuform" name="frmBasicSearch" action="<c:url value='/rest/search/'></c:url>" method="get">
	<p>
		<label for="basicSearchTerms">Search</label>
		<input class="text" type="text" name="q" id="idBasicSearchTerms" size="30" value="<c:out value="${param.q}" />" />
		<!-- Display a dropdown when a user's logged in allowing one to select filters such as Published, Personal, Team. Does not display for Guests. -->
		<c:if test="${not empty pageContext.request.remoteUser}">
			<select name="filter">
				<option value="all" selected="selected">All</option>
				<option value="published">Published</option>
				<option value="personal">Personal</option>
				<option value="team">Team</option>
			</select>
		</c:if>
		<input type="hidden" name="limit" value="<c:out value='${searchItemsPerPage}' />" />
		<input type="submit" value="Search" />
	</p>
</form>