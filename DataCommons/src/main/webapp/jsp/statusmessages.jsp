<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>

<!-- Display status messages if any. -->
<c:if test="${not empty it.errors}">
	<c:forEach var="error" items="${it.errors}">
		<p class="msg-error">
			<c:out value="${error}" />
		</p>
	</c:forEach>
</c:if>

<c:if test="${not empty it.warnings}">
	<c:forEach var="warning" items="${it.warnings}">
		<p class="msg-warn">
			<c:out value="${warning}" />
		</p>
	</c:forEach>
</c:if>

<c:if test="${not empty it.infos}">
	<c:forEach var="info" items="${it.infos}">
		<p class="msg-info">
			<c:out value="${info}" />
		</p>
	</c:forEach>
</c:if>

<c:if test="${not empty it.successes}">
	<c:forEach var="success" items="${it.successes}">
		<p class="msg-success">
			<c:out value="${success}" />
		</p>
	</c:forEach>
</c:if>
