<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<c:if test="${not empty it.resultSet}">
<anu:boxheader text="Related Items"/>
<anu:box style="solid">
	<ul class="noindent">
	<c:forEach items="${it.resultSet}" var="result">
		<fmt:bundle basename='global'>
			<fmt:message var="relatedURI" key='fedora.relatedURI' />
		</fmt:bundle>
		<c:set var="predicateVal" value="${fn:substringAfter(result.fields.predicate.value, relatedURI)}" />
		<li class="small">
		<c:choose>
			<c:when test="${not empty result.fields.title.value}">
				<c:set var="itemVal" value="${fn:substringAfter(result.fields.item.value, 'info:fedora/')}" />
				${predicateVal}: <a href='<c:url value="/rest/display/${itemVal}?layout=def:display" />' title="${predicateVal}">${result.fields.title.value}</a> [${itemVal}]
			</c:when>
			<c:otherwise>
				<a href='${result.fields.item.value}' title="${predicateVal}">${result.fields.item.value}</a>
			</c:otherwise>
		</c:choose>
		</li>
	</c:forEach>
	</ul>
</anu:box>
</c:if>