<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<fmt:formatNumber var="numPages" value="${((it.resultSet.numFound - 1) / searchItemsPerPage) - (((it.resultSet.numFound - 1) / searchItemsPerPage) % 1)}" groupingUsed="false" />
<fmt:bundle basename='global'>
	<fmt:message var="searchItemsPerPage" key='search.resultsPerPage' />
</fmt:bundle>
<c:set var="curPage" value="${(param.offset == null ? 0 : param.offset) / searchItemsPerPage}" />

<c:if test="${it.resultSet.numFound > 0}">
	<p class="text-centre">
		Pages&nbsp;
		<fmt:formatNumber var="numPages" value="${((it.resultSet.numFound - 1) / searchItemsPerPage) - (((it.resultSet.numFound - 1) / searchItemsPerPage) % 1)}" groupingUsed="false" />
		<c:url var="startURL" value="${param.searchURLPart}">
			<c:param name="limit">${searchItemsPerPage}</c:param>
			<c:param name="offset">0</c:param>
			<c:param name="q">${param.q}</c:param>
		</c:url>
		<a class="nounderline" href="${startURL}">&lt;&lt;</a>
		<c:forEach begin="0" end="${numPages}" var="pageVal">
			<c:url var="pageURL" value="${param.searchURLPart}">
				<c:param name="limit">${searchItemsPerPage}</c:param>
				<c:param name="offset">${pageVal * searchItemsPerPage}</c:param>
				<c:param name="q">${param.q}</c:param>
			</c:url>
			<c:choose>
				<c:when test="${pageVal == curPage}">
					<a class="nounderline" href="${pageURL}"><strong>[ ${pageVal + 1} ]</strong></a>
				</c:when>
				<c:when test="${pageVal > curPage - 5 and pageVal < curPage + 5}">
					<a class="nounderline" href="${pageURL}">[ ${pageVal + 1} ]</a>
				</c:when>
			</c:choose>
		</c:forEach>
		
		<c:url var="endURL" value="${param.searchURLPart}">
			<c:param name="limit">${searchItemsPerPage}</c:param>
			<c:param name="offset">${numPages * searchItemsPerPage}</c:param>
			<c:param name="q">${param.q}</c:param>
		</c:url>
		<a class="nounderline" href="${endURL}">&gt;&gt;</a>
	</p>
</c:if>
