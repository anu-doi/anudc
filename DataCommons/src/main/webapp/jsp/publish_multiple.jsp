<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<anu:header id="1998" title="Publish Multiple Records" description="description" subject="subject" respOfficer="ANU Library" respOfficerContact="mailto:repository.admin@anu.edu.au"
	ssl="true">

	<link rel="stylesheet" type="text/css" href="<c:url value='/css/default.css' />" />
	<script type="text/javascript" src="<c:url value='/js/jquery.validate.min.js' />"></script>
	<script type="text/javascript" src="<c:url value='/js/publish.js' />"></script>
</anu:header>

<jsp:include page="/jsp/header.jsp" />

<anu:container type="container">
<anu:content layout="doublewide" title="Publish multiple records">
	<c:choose>
		<c:when test="${not empty it.groups}">
			<form id="groupForm" method="GET" action="">
			<fieldset>
				<legend>Groups:</legend>
				<div class="field d-flex align-items-center justify-content-between">
					<div class="d-flex align-items-center">
						<select aria-label="Select Group" name="group">
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
					</div>
					<div class="field float-end">
						<input class="btn btn-primary" type="submit" value="Find Records" />
					</div>
				</div>
			</fieldset> 
			</form>
			<c:if test="${not empty it.results.documentList}">
				<hr/>
				<form id="publishForm" method="POST" action="">
				<fieldset>
					<legend>Locations to Publish To:</legend>
					<label for="publishLocation" class="error">Please select location(s) to publish to<br/></label>
					<div class="field">
					<c:forEach var="publishLocation" items="${it.publishers}">
						<input type="checkbox" name="publishLocation" aria-label="${publishLocation.name}" value="${publishLocation.id}" class="required" /> ${publishLocation.name} <br/>
					</c:forEach>
					</div>
					<div class="field border-bottom border-top py-1 d-flex align-items-center justify-content-between">
					<div class="d-flex align-items-center gap-2">
					<a style="text-decoration: underline;" href="#publishButton" onclick="selectAll('ids')">Select All Records</a><a style="text-decoration: underline;" href="#publishButton" onclick="deselectAll('ids')">De-select All Records</a>
					</div>
					<input class="btn btn-primary" type="submit" id="publishButton" name="publishButton" value="Publish" />
					</div>
					<legend>Records to Publish:</legend>
					<label for="ids" class="error">Please select record(s) to publish<br/></label>
					<div class="field">
						<c:forEach items="${it.results.documentList}" var="row"> 
							<input type="checkbox" aria-label="${row['id']}" name="ids" value="${row['id']}" class="required" />${row['unpublished.name']}&nbsp;&nbsp;<span class="text-grey50">[${row['id']}]</span><br/>
						</c:forEach>
					</div>
				</fieldset>
				</form>
				
				<c:set var="curPage" value="${(param.page == null ? 1 : param.page + 1)}" />
				<fmt:formatNumber var="numPages" pattern="#" value="${((it.results.numFound - 1) / 100) - (((it.results.numFound - 1) / 100) mod 1)}" />
				<c:set var="start" value="${curPage - 2 > 1 ? curPage - 2 : 1 }" />
				<c:set var="end" value="${curPage + 2 < numPages ? curPage + 2 : numPages + 1}" />
				
				<c:url var="publishURL" value="/rest/publish/multiple">
					<c:param name="page" value="0" />
					<c:param name="group" value="${param.group}" />
				</c:url>
				<a class="nounderline" aria-label="Previous page" href="${publishURL}">&lt;&lt;</a>
				<c:if test="${start > 1}">...</c:if>
				<c:forEach begin="${start}" end="${end}" var="i">
					<c:url var="publishURL" value="/rest/publish/multiple">
						<c:param name="page" value="${i - 1}" />
						<c:param name="group" value="${param.group}" />
					</c:url>
					<a class="nounderline" aria-label="page ${i}" href="${publishURL}">${i}</a>
				</c:forEach>
				<c:if test="${end < numPages}">...</c:if>
				<c:url var="publishURL" value="/rest/publish/multiple">
					<c:param name="page" value="${numPages}" />
					<c:param name="group" value="${param.group}" />
				</c:url>
				<a class="nounderline" aria-label="Next page" href="${publishURL}">&gt;&gt;</a>
			</c:if>
		</c:when>
		<c:otherwise>
			You do not have permission to perform mass publication for any groups.
		</c:otherwise>
	</c:choose>
</anu:content>
</anu:container>
<jsp:include page="/jsp/footer.jsp" />