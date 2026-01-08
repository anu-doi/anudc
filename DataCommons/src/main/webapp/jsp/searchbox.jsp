<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<anu:container type="container">
		<div id="dcWelcomeSearch" class="bg-tint p-0 overlap-child b-1 rounded">
		<div class="welcome-search-box">
			<div>
				<div class="px-2 mt-2">
				<p class="h4">ANU Data Commons is the repository for data created by ANU researchers across a wide range of disciplines</p>
				</div>
			</div>
			<div>
				<fmt:bundle basename='global'>
					<fmt:message var="searchItemsPerPage" key='search.resultsPerPage' />
				</fmt:bundle>
				<form class="anuform-inline-tint" style="padding-left: 18px;" name="frmBasicSearch" action="<c:url value='/rest/search/'></c:url>" method="get">
				<div class="form-group py-1">
					<input aria-label="search data commons" class="text w70" style="margin-right: 10px;border-radius: 4px;border: 3px solid white;padding: 12px;vertical-align: middle;" type="text" name="q" id="idBasicSearchTerms" size="30" value="<c:out value="${param.q}" />" />
					<input type="hidden" name="limit" value="<c:out value='${searchItemsPerPage}' />" />
					<input class="anu-btn-white" type="submit" style="vertical-align: middle;" value="GO" />
				</div>
				</form>
			</div>
			<p class="left mt-0"><a class="nounderline pl-2" href='<c:url value="/rest/search/advanced"/>'>Advanced search &gt;&gt;</a></p>
		<hr/>
			<p class="left mt-0">Browse by: 
				<a class="nounderline" href='<c:url value="/rest/search/browse?field=keyword" />'>Keywords</a>
			</p>
		</div>
	</div>
</anu:container>