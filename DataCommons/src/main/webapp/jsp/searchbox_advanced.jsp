<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<form name="frmBasicSearch" class="anuform" action="<c:url value='/rest/search/advanced'></c:url>" method="get">
	<fmt:bundle basename='global'>
		<fmt:message var="searchItemsPerPage" key='search.resultsPerPage' />
	</fmt:bundle>
	<div class="clearfix">
	<fieldset style="padding-right: 3%;">
		<legend>Search ANU Data Commons</legend>
		<div class="marginbottom margintop">Search
			<c:if test="${not empty pageContext.request.remoteUser}">
				<select name="filter" id="filter">
					<option value="all" <c:if test="${param.filter == 'all'}">selected="selected" </c:if>>All of ANU Data Commons</option>
					<option value="published" <c:if test="${param.filter == 'published'}">selected="selected" </c:if>>Published</option>
					<option value="team" <c:if test="${param.filter == 'team'}">selected="selected" </c:if>>Team</option>
				</select>
			</c:if>
		</div>
		<div id="search-terms">
			<c:if test="${empty param['search-val']}">
				<table id="searchTable" class="noborder">
					<tr>
						<td>Search field</td>
						<td>
							<select	name="value-type">
								<option value="all">All</option>
								<option value="type">Type</option>
								<option value="name">Title</option>
								<option value="briefDesc">Brief Description</option>
								<option value="fullDesc">Full Description</option>
								<option value="locSubject">Keywords</option>
								<option value="anzforSubject">Field of Research</option>
								<option value="anzseoSubject">Socio-Economic Objective</option>
								<option value="anztoaSubject">Type of Research Activity</option>
							</select>
						</td>
						<td>
							<input type="text" size="45" name="search-val" />
						</td>
					</tr>
				</table>
			</c:if>
			<c:if test="${not empty param['search-val']}">
				<table id="searchTable" class="noborder">
					<c:forEach items="${paramValues['search-val']}" var="searchVal" varStatus="searchStatus">
						<c:if test="${not empty searchVal}">
							<tr>
								<td>Search field</td>
								<td>
									<select	name="value-type">
										<option value="all">All</option>
										<option value="type" <c:if test="${paramValues['value-type'][searchStatus.index] == 'type'}">selected="selected"</c:if>>Type</option>
										<option value="name" <c:if test="${paramValues['value-type'][searchStatus.index] == 'name'}">selected="selected"</c:if>>Title</option>
										<option value="briefDesc" <c:if test="${paramValues['value-type'][searchStatus.index] == 'briefDesc'}">selected="selected"</c:if>>Brief Description</option>
										<option value="fullDesc" <c:if test="${paramValues['value-type'][searchStatus.index] == 'fullDesc'}">selected="selected"</c:if>>Full Description</option>
										<option value="locSubject" <c:if test="${paramValues['value-type'][searchStatus.index] == 'locSubject'}">selected="selected"</c:if>>Keywords</option>
										<option value="anzforSubject" <c:if test="${paramValues['value-type'][searchStatus.index] == 'anzforSubject'}">selected="selected"</c:if>>Field of Research</option>
										<option value="anzseoSubject" <c:if test="${paramValues['value-type'][searchStatus.index] == 'anzseoSubject'}">selected="selected"</c:if>>Socio-Economic Objective</option>
										<option value="anztoaSubject" <c:if test="${paramValues['value-type'][searchStatus.index] == 'anztoaSubject'}">selected="selected"</c:if>>Type of Research Activity</option>
									</select>
								</td>
								<td>
									<input type="text" size="45" name="search-val" value="${searchVal}" />
								</td>
							</tr>
						</c:if>
					</c:forEach>
				</table>
			</c:if>
		</div>
		<input type="button" name="add-term" id="add-term" value="Add Field" onClick="addTableRow('searchTable')" />
	</fieldset>
	<input type="hidden" name="limit" value="<c:out value='${searchItemsPerPage}' />" />
	<input type="submit" class="right btn-medium btn-uni-grad" value="Search" />
	<input class="right btn-uni-grad btn-medium marginright" type="button" onclick='location.href="<c:url value="/rest/search/advanced"/>"' value="Reset"/>
	</div>
</form>