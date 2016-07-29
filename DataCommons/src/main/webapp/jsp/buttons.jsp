<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<c:set var="fedoraObject" value="${it.fedoraObject}" />

<anu:content layout="narrow">
	<jsp:include page="status.jsp" />
	<sec:authorize access="isAnonymous()">
		<c:if test="${fn:toLowerCase(it.itemType) eq 'collection' and !fedoraObject.filesPublic}">
			<anu:box style="solid">
				Please login to request access to this dataset
			</anu:box>
		</c:if>
	</sec:authorize>
	<sec:authorize access="isAuthenticated()">
		<anu:box style="solid">
			<c:if test="${fn:toLowerCase(it.itemType) eq 'collection'}">
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
				<sec:authorize access="hasPermission(#fedoraObject,'ADMINISTRATION')">
					<c:url value="/rest/report/single" var="reportLink">
						<c:param name="pid" value="${it.fedoraObject.object_id}" />
					</c:url>
					<p>
						<input type="button" id="reportButton" name="reportButton" value="Get Report" onclick="window.location='${reportLink}'" />
					</p>
				</sec:authorize>
				<sec:authorize access="hasPermission(#fedoraObject,'DELETE') or hasPermission(#fedoraObject,'ADMINISTRATION')">
					<c:url value="/rest/display/delete/${it.fedoraObject.object_id}" var="deleteLink" />
					<p>
						<form>
							<button formmethod="post" formaction="${deleteLink}" onclick="return confirmDelete();">Delete</button>
						</form>
					</p>
				</sec:authorize>
				<sec:authorize access="hasPermission(#fedoraObject,'WRITE') or hasPermission(#fedoraObject,'ADMINISTRATION')">
					<c:set var="editBaseURL" value="/rest/display/edit/${it.fedoraObject.object_id}" />
					<c:url value="${editBaseURL}" var="editLink">
						<c:param name="tmplt" value="${param.tmplt}" />
						<c:param name="layout" value="${param.layout}" />
					</c:url>
					<c:url value="${editBaseURL}" var="fullEditLink">
						<c:param name="tmplt" value="${param.tmplt}" />
						<c:param name="layout" value="${param.layout}" />
						<c:param name="style" value="full" />
					</c:url>
					<p>
						<input type="button" id="editButton" name="editButton" value="Edit Metadata" onclick="window.location='${editLink}'" />
					</p>
					<p>
						<input type="button" id="fullEditButton" name="fullEditButton" value="Edit Whole Metadata" onclick="window.location='${fullEditLink}'" />
					</p>
				</sec:authorize>
				<c:if test="${fn:toLowerCase(it.itemType) eq 'collection'}">
					<sec:authorize access="hasPermission(#fedoraObject,'REVIEW') or hasPermission(#fedoraObject,'PUBLISH') or hasPermission(#fedoraObject,'ADMINISTRATION')">
						<c:url value="/rest/collreq/question" var="questionLink">
							<c:param name="pid" value="${it.fedoraObject.object_id}" />
						</c:url>
						<p>
							<input type="button" id="questionButton" name="questionButton" value="Set Request Questions" onclick="window.location='${questionLink}'" />
						</p>

						<c:url value="/rest/publish/mintdoi/${it.fedoraObject.object_id}" var="mintDoiLink">
							<c:param name="tmplt" value="${param.tmplt}" />
							<c:param name="layout" value="${param.layout}" />
						</c:url>
						<p>
							<input type="button" id="mintDoi" name="mintDoi" value="Mint DOI"
								onclick="if (confirm('This will mint a Digital Object Identifier for this collection. Are you sure?')) window.location='${mintDoiLink}'" />
						</p>
					</sec:authorize>
				</c:if>
				<sec:authorize access="hasPermission(#fedoraObject,'WRITE') or hasPermission(#fedoraObject,'REVIEW') or hasPermission(#fedoraObject,'PUBLISH') or hasPermission(#fedoraObject,'ADMINISTRATION')">
					<c:url value="/rest/publish/validate/${it.fedoraObject.object_id}" var="validateLink">
					</c:url>
					<p>
						<input type="button" id="validateButton" name="validateButton" value="Validation Check" onclick="window.location='${validateLink}'" />
					</p>
				</sec:authorize>
				<jsp:include page="review_status.jsp" />
				<jsp:include page="add_reference.jsp" />
			</sec:authorize>
		</anu:box>
	</sec:authorize>
	<jsp:include page="listrelated.jsp" />

	<!-- Bag Summary Begin -->
	<c:if test="${fn:toLowerCase(it.itemType) eq 'collection'}">
		<anu:boxheader text="Files" />
		<anu:box style="solid">
			<c:if test="${not empty it.rdi}">
				<p>Estimates:</p>
				<ul class="nobullet noindent">
					<li>Files: <c:out value="${it.rdi.recordNumFiles}" /></li>
					<li>Size: <c:out value="${it.rdi.recordFriendlySize}" /></li>
				</ul>
			</c:if>
			<c:if test="${empty it.rdi}">
				<p>No files in collection.</p>
			</c:if>
			<p><a href="<c:url value='/rest/records/${it.fedoraObject.object_id}/data/' />">Data Files</a>
		</anu:box>
	</c:if>
	<!-- Bag Summary End -->
</anu:content>
