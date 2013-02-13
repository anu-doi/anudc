<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>

<anu:header id="1998" title="${it.fo.object_id}" description="DESCRIPTION" subject="SUBJECT" respOfficer="Doug Moncur"
	respOfficerContact="doug.moncur@anu.edu.au" ssl="true">
	<!-- Possible bug in the ANU taglib. The following CSS should not be referenced here. Should be referenced in the taglib. -->
	<link href="http://styles.anu.edu.au/_anu/3/style/anu-forms.css" rel="stylesheet" type="text/css" />
	<script type="text/javascript" src="<c:url value='/js/bagfiles.js' />"></script>
</anu:header>

<jsp:include page="/jsp/header.jsp" />

<c:choose>
	<c:when test="${it.bagSummary != null}">
		<!-- Display list of files -->

		<anu:content layout="doublewide" title="Bag Files">
			<jsp:include page="/jsp/statusmessages.jsp">
				<jsp:param value="${it}" name="it" />
			</jsp:include>

			<div id="tabs" class="pagetabs-nav">
				<ul>
					<li><a href="javascript:void(0)" onclick="tabSelect(this, '#files')" class="pagetabs-select">Files</a></li>
					<li><a href="javascript:void(0)" onclick="tabSelect(this, '#info')">Archive Info</a></li>
					<li><a href="javascript:void(0)" onclick="tabSelect(this, '#extRefs')">External References</a></li>
				</ul>
			</div>

			<table class="small w-doublewide" id="files">
				<tr>
					<th>File</th>
					<th>Format</th>
					<th>Pronom PUID</th>
					<th>Size</th>
					<th>MD5</th>
					<th>Virus</th>
					<th>Expand</th>
					<th>Delete File</th>
				</tr>
				<c:forEach var="iFile" items="${it.bagSummary.fileSummaryMap}">
					<tr>
						<td><a href="<c:url value='${it.dlBaseUri}${iFile.key.filepath}' />">${iFile.key.filepath}</a></td>
						<c:choose>
							<c:when test="${not empty iFile.value.format}">
								<td><c:out value="${iFile.value.format}" /></td>
								<td><a target="_blank" href="http://www.nationalarchives.gov.uk/pronom/${iFile.value.formatPuid}">${iFile.value.formatPuid}</a></td>
							</c:when>
							<c:otherwise>
								<td>Unknown</td>
								<td>Unknown</td>
							</c:otherwise>
						</c:choose>
						<td><c:out value="${iFile.value.friendlySize}" /></td>
						<td><c:out value="${iFile.value.md5}" /></td>
						<td><c:out value="${iFile.value.scanResult}" /></td>
						<td onclick="jQuery('#${iFile.value.md5}').slideToggle()"><a href="#" onclick="return false">Expand</a></td>
						<td><a href="javascript:void(0);" onclick="deleteFile('<c:url value='${it.dlBaseUri}${iFile.key.filepath}' />', '${iFile.key.filepath}')">X</a>
					</tr>
					<tr id="<c:out value='${iFile.value.md5}' />" style="display: none;">
						<td colspan="7">
							<table class=small>
								<c:forEach var="iProperty" items="${iFile.value.metadata}">
									<tr>
										<th><c:out value="${iProperty.key}" /></th>
										<c:forEach var="iPropertyVal" items="${iProperty.value}">
											<td><c:out value="${iPropertyVal}" /></td>
										</c:forEach>
									</tr>
								</c:forEach>
							</table>
						</td>
					</tr>
				</c:forEach>
				<tr class="bg-uni50 text-center">
					<td colspan="8"><a href="${it.downloadAsZipUrl}">Download all as Zip</a></td>
				</tr>
			</table>

			<table class="small w-doublewide" id="info" style="display: none;">
				<tr>
					<th>
						Public
					</th>
					<td>
						<c:out value="${it.isFilesPublic}" />&nbsp;
						<sec:accesscontrollist hasPermission="PUBLISH"
							domainObject="${it.fedoraObject}">
							<a href="javascript:void(0)"
								onclick="toggleIsFilesPublic('${it.fo.object_id}', ${it.isFilesPublic})">Change</a>
						</sec:accesscontrollist>
					</td>
				</tr>
				<c:forEach var="iEntry" items="${it.bagInfoTxt}">
					<tr>
						<th><c:out value="${iEntry.key}" /></th>
						<td><c:out value="${iEntry.value}" /></td>
					</tr>
				</c:forEach>
			</table>

			<div class="small w-doublewide" id="extRefs" style="display: none;">
				<!-- External relations -->
				<button onclick="addExtRef('${it.bagSummary.pid}')">Add External Reference</button>
				<c:if test="${not empty it.extRefsTxt}">
					<ul>
						<c:forEach var="iEntry" items="${it.extRefsTxt}">
							<li><a href="${iEntry.value}"><c:out value='${iEntry.value}' /></a>&nbsp;&nbsp;<a href="javascript:void(0);"
								onclick="deleteExtRef('${it.bagSummary.pid}', '${iEntry.value}')">[Delete]</a></li>
						</c:forEach>
					</ul>
				</c:if>
			</div>
			
			<img id="loading" src="<c:url value='/images/ajax-loader.gif' />" style="display: none"></img>
		</anu:content>
	</c:when>
</c:choose>

<jsp:include page="/jsp/footer.jsp" />
