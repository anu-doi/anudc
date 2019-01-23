<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<anu:header id="1998" title="Validate Multiple Records" description="description" subject="subject" respOfficer="ANU Library" respOfficerContact="mailto:repository.admin@anu.edu.au"
	ssl="true">

</anu:header>

<jsp:include page="/jsp/header.jsp" />

<anu:content layout="doublewide" title="Validation Results">
	<c:url value="/rest/publish/validate/multiple" var="validateURL" />
	<a href="${validateURL}">Return to Request Validation Page</a><br/>
	<c:forEach var="item" items="${it.information.documentList}">
		<h2>${item['unpublished.name']}</h2>
		<c:url value="/rest/display/${item['id']}" var="itemURL">
			<c:param name="layout">def:display</c:param>
		</c:url>
		Identifier: <a href="${itemURL}">${item['id']}</a><br/>
		<c:forEach var="validationMessage" items="${it.validationMessages[item['id']]}">
			<strong>${validationMessage.location}</strong><br/>
			<c:choose>
				<c:when test="${not empty validationMessage.messages}">
					<p class="msg-info">
						<c:forEach var="message" items="${validationMessage.messages}">
							${message}<br/>
						</c:forEach>
					</p>
				</c:when>
				<c:otherwise>
					<p class="msg-success">
						Validation Successful
					</p>
				</c:otherwise>
			</c:choose>
		</c:forEach>
	</c:forEach>
</anu:content>

<jsp:include page="/jsp/footer.jsp" />