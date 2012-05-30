<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<anu:content layout="narrow">
	<jsp:include page="status.jsp" />
	<anu:box style="solid">
		<sec:authorize access="isAnonymous()">
			Please login to request access to this dataset
		</sec:authorize>
		<sec:authorize access="isAuthenticated()">
			<sec:authorize access="hasRole('ROLE_REGISTERED')">
				<c:url value="/rest/collreq" var="collReqLink">
					<c:param name="pid" value="${it.fedoraObject.object_id}" />
				</c:url>
				<p><input type="button" id="collReqButton" name="colReqButton" value="Request Collection Files" onclick="window.location='${collReqLink}'" /></p>
			</sec:authorize>
			<sec:authorize access="hasRole('ROLE_ANU_USER')">
				<c:url value="/rest/publish" var="publishLink">
					<c:param name="item" value="${it.fedoraObject.object_id}" />
					<c:param name="tmplt" value="${param.tmplt}" />
					<c:param name="layout" value="${param.layout}" />
				</c:url>
				<p><input type="button" id="publishButton" name="publishButton" value="Publish" onclick="window.location='${publishLink}'" /></p>
				<c:url value="/rest/display/edit" var="editLink">
					<c:param name="item" value="${it.fedoraObject.object_id}" />
					<c:param name="tmplt" value="${param.tmplt}" />
					<c:param name="layout" value="${param.layout}" />
				</c:url>
				<c:url value="/rest/upload" var="uploadLink">
					<c:param name="pid" value="${it.fedoraObject.object_id}" />
				</c:url>
				<sec:accesscontrollist hasPermission="WRITE,ADMINISTRATION" domainObject="${it.fedoraObject}">
					<p><input type="button" id="editButton" name="editButton" value="Edit" onclick="window.location='${editLink}'" /></p>
				</sec:accesscontrollist>
				<p><input type="button" id="uploadButton" name="uploadButton" value="Upload" onclick="window.location='${uploadLink}'" /></p>
				<jsp:include page="add_reference.jsp" />
			</sec:authorize>
		</sec:authorize>
	</anu:box>
	<jsp:include page="listrelated.jsp" />
	<c:if test="${not empty it.filelist}">
		<anu:boxheader text="List of files" />
		<anu:box style="solid">${it.filelist}</anu:box>
	</c:if>
</anu:content>
