<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<anu:header id="1998" title="Publish Multiple Records" description="description" subject="subject" respOfficer="ANU Library" respOfficerContact="mailto:repository.admin@anu.edu.au"
	ssl="true">

</anu:header>

<jsp:include page="/jsp/header.jsp" />

<anu:content layout="doublewide" title="Publication Results">
	<c:forEach var="item" items="${it.information.documentList}">
		<strong>${item['unpublished.name']}</strong><br/>
		<c:url value="/rest/display/${item['id']}" var="itemURL">
			<c:param name="layout">def:display</c:param>
		</c:url>
		Identifier: <a href="${itemURL}">${item['id']}</a><br/>
		<c:choose>
			<c:when test="${it.published[item['id']] == 'success'}">
				<p class="msg-success">
				This record was successfully published
				</p>
			</c:when>
			<c:when test="${it.published[item['id']] == 'failure'}">
				<p class="msg-error">
				There was an error publishing this record
				</p>
			</c:when>
			<c:otherwise>
				This record was not found
			</c:otherwise>
		</c:choose>
	</c:forEach>
</anu:content>

<jsp:include page="/jsp/footer.jsp" />