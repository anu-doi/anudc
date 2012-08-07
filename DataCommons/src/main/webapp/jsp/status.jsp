<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<anu:box style="solid">
	<c:if test="${it.fedoraObject.published}">
		<b>Status:</b> Published<br />
		<b>Published To:</b><br />
		<c:forEach var="location" items="${it.fedoraObject.publishedLocations}">
			- ${location.name} <br />
		</c:forEach>
	</c:if>
	<c:if test="${not it.fedoraObject.published}">
		<b>Status:</b> Unpublished<br />
	</c:if>
	<c:if test="${not empty it.fedoraObject.object_id}">
		<b>Identifier:</b> ${it.fedoraObject.object_id}<br />
	</c:if>
</anu:box>