<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<anu:content layout="narrow">
	<jsp:include page="status.jsp" />
	<anu:box style="solid">
		<c:if test="${it.itemType == 'Collection'}">
			<sec:authorize access="isAnonymous()">
				Please login to request access to this dataset
			</sec:authorize>
		</c:if>
		<sec:authorize access="isAuthenticated()">
			<c:if test="${it.itemType == 'Collection'}">
				<sec:authorize access="hasRole('ROLE_REGISTERED')">
					<c:url value="/rest/collreq" var="collReqLink">
						<c:param name="pid" value="${it.fedoraObject.object_id}" />
					</c:url>
					<p>
						<input type="button" id="collReqButton" name="colReqButton" value="Request Collection Files" onclick="window.location='${collReqLink}'" />
					</p>
				</sec:authorize>
			</c:if>
			<sec:authorize access="hasRole('ROLE_ANU_USER')">
				<sec:accesscontrollist hasPermission="ADMINISTRATION" domainObject="${it.fedoraObject}">
					<c:url value="/rest/report" var="reportLink">
						<c:param name="pid" value="${it.fedoraObject.object_id}" />
					</c:url>
					<p>
						<input type="button" id="reportButton" name="reportButton" value="Get Report" onclick="window.location='${reportLink}'" />
					</p>
				<c:url value="/rest/display/edit/${it.fedoraObject.object_id}" var="editLink">
					<c:param name="tmplt" value="${param.tmplt}" />
					<c:param name="layout" value="${param.layout}" />
				</c:url>
				<sec:accesscontrollist hasPermission="WRITE,ADMINISTRATION" domainObject="${it.fedoraObject}">
					<p>
						<input type="button" id="editButton" name="editButton" value="Edit Metadata" onclick="window.location='${editLink}'" />
					</p>
				</sec:accesscontrollist>
				</sec:accesscontrollist>
				<c:if test="${it.itemType == 'Collection'}">
					<sec:accesscontrollist hasPermission="WRITE,ADMINISTRATION" domainObject="${it.fedoraObject}">
						<c:url value="/rest/upload" var="uploadLink">
							<c:param name="pid" value="${it.fedoraObject.object_id}" />
						</c:url>
						<p>
							<input type="button" id="uploadButton" name="uploadButton" value="Upload Files" onclick="window.location='${uploadLink}'" />
						</p>
					</sec:accesscontrollist>
				
					<sec:accesscontrollist hasPermission="REVIEW,PUBLISH" domainObject="${it.fedoraObject}">
						<c:url value="/rest/collreq/question" var="questionLink">
							<c:param name="pid" value="${it.fedoraObject.object_id}" />
						</c:url>
						<p>
							<input type="button" id="questionButton" name="questionButton" value="Set Request Questions" onclick="window.location='${questionLink}'" />
						</p>
					</sec:accesscontrollist>
				</c:if>
					
				<jsp:include page="review_status.jsp" />
				<jsp:include page="add_reference.jsp" />
			</sec:authorize>
		</sec:authorize>
	</anu:box>
	<jsp:include page="listrelated.jsp" />
	
	<!-- Bag Summary Begin -->
	<c:if test="${not empty it.bagSummary}">
		<anu:boxheader text="<a href='/DataCommons/rest/upload/bag/${it.fedoraObject.object_id}'>Files</a>" />
		<c:if test="${it.itemType == 'Collection'}">
			<anu:box style="solid">
				<p><c:out value="${it.bagSummary.numFiles}" /> file(s) in collection. Size <c:out value="${it.bagSummary.friendlySize}" /></p>
			</anu:box>
		</c:if>
	</c:if>
	<!-- Bag Summary End -->
</anu:content>
