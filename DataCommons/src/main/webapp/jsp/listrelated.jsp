<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>

<c:if test="${not empty it.resultSet}">
<anu:boxheader text="Related Items"/>
<anu:box style="solid">
	<c:forEach items="${it.resultSet}" var="result">
		-
		<c:choose>
			<c:when test="${not empty result.fields.title.value}">
				<c:set var="itemVal" value="${fn:substringAfter(result.fields.item.value, 'info:fedora/')}" />
				<a href='<c:url value="/rest/display/${itemVal}?layout=def:display" />'>${result.fields.title.value}</a>
			</c:when>
			<c:otherwise>
				<a href='#'>${result.fields.item.value}</a>
			</c:otherwise>
		</c:choose>
		<br/>
	</c:forEach>
</anu:box>
</c:if>