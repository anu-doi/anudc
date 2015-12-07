<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<anu:header id="1998" title="Validate Multiple Records" description="description" subject="subject" respOfficer="Doug Moncur" respOfficerContact="mailto:doug.moncur@anu.edu.au"
	ssl="true">

	<link rel="stylesheet" type="text/css" href="<c:url value='/css/default.css' />" />
	<script type="text/javascript" src="<c:url value='/js/jquery.validate.min.js' />"></script>
	<script type="text/javascript" src="<c:url value='/js/publish.js' />"></script>
</anu:header>

<jsp:include page="/jsp/header.jsp" />

<anu:content layout="doublewide" title="Validate Multiple Records">
	<c:choose>
		<c:when test="${not empty it.groups}">
			<c:if test="${it.errorMsg}">
				<p class="msg-error">
					${it.errorMsg}
				</p>
			</c:if>
			<form id="groupForm" method="GET" action=""> 
				Groups:<br/>
				<select name="group">
					<c:forEach var="group" items="${it.groups}">
						<option value="${group.id}"
						<c:if test="${param.group == group.id }">
							selected="selected"
						</c:if>
							title="${group.group_name}"
						>
							${group.group_name}
						</option>
					</c:forEach>
				</select>
				<input type="submit" value="Find Records" />
			</form>
			<c:if test="${not empty it.results.documentList}">
				<hr/>
				<form id="validateForm" method="POST" action="">
					Locations to Validate:<br/>
					<label for="publishLocation" class="error">Please select location(s) to perform validation on<br/></label>
					<c:forEach var="publishLocation" items="${it.publishers}">
						<input type="checkbox" name="publishLocation" value="${publishLocation.id}" class="required" /> ${publishLocation.name} <br/>
					</c:forEach>
					<hr/>
					<a href="#validateButton" onclick="selectAll('ids')">Select All Records</a> <a href="#validateButton" onclick="deselectAll('ids')">De-select All Records</a>
					<input class="right" type="submit" id="validateButton" name="validateButton" value="Validate" />
					<hr/>
					Records to Validate:<br/>
					<label for="ids" class="error">Please select record(s) to validate<br/></label>
					<c:forEach items="${it.results.documentList}" var="row"> 
						<input type="checkbox" name="ids" value="${row['id']}" class="required" />${row['unpublished.name']}&nbsp;&nbsp;<span class="text-grey50">[${row['id']}]</span><br/>
					</c:forEach>
				</form>
				
				<c:set var="curPage" value="${(param.page == null ? 1 : param.page + 1)}" />
				<fmt:formatNumber var="numPages" pattern="#" value="${((it.results.numFound - 1) / 100) - (((it.results.numFound - 1) / 100) mod 1)}" />
				<c:set var="start" value="${curPage - 2 > 1 ? curPage - 2 : 1 }" />
				<c:set var="end" value="${curPage + 2 < numPages ? curPage + 2 : numPages + 1}" />
				
				<c:url var="validateURL" value="/rest/publish/validate/multiple">
					<c:param name="page" value="0" />
					<c:param name="group" value="${param.group}" />
				</c:url>
				<a class="nounderline" href="${validateURL}">&lt;&lt;</a>
				<c:if test="${start > 1}">...</c:if>
				<c:forEach begin="${start}" end="${end}" var="i">
					<c:url var="validateURL" value="/rest/publish/validate/multiple">
						<c:param name="page" value="${i - 1}" />
						<c:param name="group" value="${param.group}" />
					</c:url>
					<a class="nounderline" href="${validateURL}">${i}</a>
				</c:forEach>
				<c:if test="${end < numPages}">...</c:if>
				<c:url var="validateURL" value="/rest/publish/validate/multiple">
					<c:param name="page" value="${numPages}" />
					<c:param name="group" value="${param.group}" />
				</c:url>
				<a class="nounderline" href="${validateURL}">&gt;&gt;</a>
			</c:if>
		</c:when>
		<c:otherwise>
			You do not have permission to validate any groups
		</c:otherwise>
	</c:choose>
</anu:content>

<jsp:include page="/jsp/footer.jsp" />