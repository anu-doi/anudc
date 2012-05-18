<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>

<!-- Display status messages if any. -->
<c:if test="${not empty it.messages}">
	<c:if test="${not empty it.messages.errors}">
		<c:forEach var="error" items="${it.messages.errors}">
			<p class="msg-error">
				<c:out value="${error}" escapeXml="false" />
			</p>
		</c:forEach>
	</c:if>

	<c:if test="${not empty it.messages.warnings}">
		<c:forEach var="warning" items="${it.messages.warnings}">
			<p class="msg-warn">
				<c:out value="${warning}" escapeXml="false" />
			</p>
		</c:forEach>
	</c:if>

	<c:if test="${not empty it.messages.infos}">
		<c:forEach var="info" items="${it.messages.infos}">
			<p class="msg-info">
				<c:out value="${info}" escapeXml="false" />
			</p>
		</c:forEach>
	</c:if>

	<c:if test="${not empty it.messages.successes}">
		<c:forEach var="success" items="${it.messages.successes}">
			<p class="msg-success">
				<c:out value="${success}" escapeXml="false" />
			</p>
		</c:forEach>
	</c:if>
</c:if>
