<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<div class="clear box20 bg-uni25 bdr-top-solid bdr-white bdr-medium nomargin nomarginbottom">
	<div><p>ANU Data Commons is the repository for data created by ANU researchers across a wide range of disciplines</p></div>
	<div class="bigsearch nopadtop padbottom">
		<fmt:bundle basename='global'>
			<fmt:message var="searchItemsPerPage" key='search.resultsPerPage' />
		</fmt:bundle>
		<form name="frmBasicSearch" action="<c:url value='/rest/search/'></c:url>" method="get">
			<input class="text" type="text" name="q" id="idBasicSearchTerms" size="30" value="<c:out value="${param.q}" />" />
			<input type="hidden" name="limit" value="<c:out value='${searchItemsPerPage}' />" />
			<input type="submit" value="GO" />
		</form>
	</div>
	<div><a class="nounderline" href='<c:url value="/rest/search/advanced"/>'>Advanced search &gt;&gt;</a></div>
	<hr/>
	Browse by: <a class="nounderline" href='<c:url value="/rest/search/browse?field=keyword" />'>Keywords</a>
</div>