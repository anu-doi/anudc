<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="fedoraObject" value="${it.fo}" />

<anu:header id="1998" title="${it.fo.object_id}" description="DESCRIPTION" subject="SUBJECT" respOfficer="ANU Library" respOfficerContact="mailto:repository.admin@anu.edu.au" ssl="true">

	<link href="<c:url value='/css/storage.css' />" rel="stylesheet" type="text/css" />
	<link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
	<script type="text/javascript" src="<c:url value='/js/storage.js' />"></script>
	<script type="text/javascript">
		jQuery(document).ready(function() {
			documentReady();
		});
	</script>
</anu:header>

<jsp:include page="/jsp/header.jsp" />

<anu:content layout="doublewide" title="Data Files [${it.fo.object_id}]">
	<jsp:include page="/jsp/statusmessages.jsp">
		<jsp:param value="${it}" name="it" />
	</jsp:include>
	<img id="loading" src="<c:url value='/images/ajax-loader.gif' />" style="display: none"></img>

	<div id="tabs" class="pagetabs-nav nopadbottom">
		<ul>
			<li><a href="#files">Files</a></li>
			<li><a href="#info">Archive Info</a></li>
			<li><a href="#extRefs">External References</a></li>
		</ul>
	</div>
</anu:content>

<div class="doublewide nopadtop" id="files" class="list_view">
	<c:choose>
		<c:when test="${not empty it.rdi}">
			<p class="msg-info">Record contains approximately ${it.rdi.recordNumFiles} file(s) totalling ${it.rdi.recordFriendlySize}.</p>
			
			<form name="frmFiles" action="?action=zip" method="post" class="anuform">
				<!-- Navigation Breadcrumbs -->
				<div class="nav-breadcrumbs">
					<c:forEach var="iParent" items="${it.parents}" varStatus="stat">
						<c:set var="parentUrl" value="" />
						<c:forEach var="iLevel" begin="1" end="${fn:length(it.parents) - stat.count}">
							<c:set var="parentUrl" value="${parentUrl}../" />
						</c:forEach>
						
						<c:choose>
							<c:when test="${not stat.last}">
								<span class="large"><a href="<c:url value='${parentUrl}'/>"><c:out value="${iParent.filename}" /></a>&nbsp;/</span>
							</c:when>
							<c:otherwise>
								<span class="large"><c:out value="${iParent.filename}" /></span>
							</c:otherwise>
							
						</c:choose>
					</c:forEach>
					
					<c:set var="baseDataUrl" value="" />
					<c:forEach var="iLevel" begin="2" end="${fn:length(it.parents)}">
						<c:set var="baseDataUrl" value="${baseDataUrl}../" />
					</c:forEach> 
					
					
					
					<!-- Actions -->
					<div id="div-action-icons" class="right text-right">
						<sec:authorize access="isAuthenticated()">
							<sec:authorize access="hasPermission(#fedoraObject,'WRITE') or hasPermission(#fedoraObject,'ADMINISTRATION')">
								<!-- Create Folder icon -->
								<a href="javascript:" id="action-create-folder"><i class="material-icons" title="Create new folder">create_new_folder</i></a>
								<!-- Upload Files icon -->
								<a href="?upload"><i class="material-icons" title="Upload files">file_upload</i></a>
								<!-- Delete Selected Files icon -->
								<a href="javascript:" id="action-del-selected"><i class="material-icons" title="Delete selected files">delete</i></a>
							</sec:authorize>
						</sec:authorize>
						
						<!-- Download selected files as Zip icon -->
						<a id="action-dl-zip" href="javascript:"><i class="material-icons" title="Download selected files">file_download</i></a>
						
						<!-- Check bag files icon -->
						<sec:authorize access="hasRole('ROLE_ADMIN')">
							<a href="<c:url value='${baseDataUrl}../admin?task=verify' />" id="action-verify-files">
								<i class="material-icons" title="Verify">playlist_add_check</i>
							</a>
						</sec:authorize>
					</div>
				</div>
				

				<div>
				<table id="tblFiles" class="w-doublewide tbl-row-bdr noborder anu-long-area tbl-files">
					<tr class="anu-sticky-header">
						<th class="col-checkbox"><input id="selectall" type="checkbox" /></th>
						<th class="col-filename">Name</th>
						<th class="col-filetype">Type</th>
						<th class="col-filesize">Size</th>
						<th class="col-action-icons">&nbsp;</th>
					</tr>
					
					<c:forEach var="iFile" items="${it.fileInfo.getChildren('name')}" varStatus="stat">
						<tr id="filerow-${stat.count}" class="file-row">
							<c:choose>
								<c:when test="${iFile.type == 'DIR'}">
									<c:set var="relUrl" value="${iFile.filename}/"></c:set>
								</c:when>
								<c:otherwise>
									<c:set var="relUrl" value="${iFile.filename}"></c:set>
								</c:otherwise>
							</c:choose>
							
							<!-- Selection checkbox. -->
							<td class="col-checkbox"><input type="checkbox" name="i" value="${relUrl}" /></td>
							
							<!-- Icon and filename as hyperlink -->
							<td class="col-filename">
								<a class="nounderline" href="<c:url value='${relUrl}'/>" title="${iFile.relFilepath}">
								<c:choose>
									<c:when test="${iFile.type == 'DIR'}">
										<i class="material-icons">folder</i>
									</c:when>
									<c:when test="${iFile.type == 'FILE'}">
										<i class="material-icons">description</i>
									</c:when>
								</c:choose>
							<c:out value="${iFile.filename}" /></a></td>
	
							<!-- Type column -->
							<td class="col-filetype">
								<c:choose>
									<c:when test="${iFile.type == 'DIR'}">
										DIR
									</c:when>
									<c:when test="${iFile.type == 'FILE'}">
										<c:choose>
											<c:when test="${not empty iFile.pronomFormat.formatName}">
												<a class="link-ext" target="_blank" title="${iFile.pronomFormat.puid}"
														href="http://www.nationalarchives.gov.uk/pronom/<c:url value='${iFile.pronomFormat.puid}'/>">
													<c:out value="${iFile.pronomFormat.formatName}" />
												</a>
											</c:when>
											<c:otherwise>
												Unknown
											</c:otherwise>
										</c:choose>
									</c:when>
								</c:choose>
							</td>
							
							<td class="col-filesize">
								<c:if test="${iFile.type == 'FILE'}">
									<c:out value="${iFile.friendlySize}" />
								</c:if>
							</td>
							
							<td class="col-action-icons">
								<!-- Preserved File Icon -->
								<c:if test="${not empty iFile.presvPath}">
									<a href="<c:url value='${baseDataUrl}${iFile.presvPath}' />" title="Download preserved format">
										<i class="material-icons">archive</i>
									</a>
								</c:if>
								
								<!-- Virus Scan Icon -->
								<c:choose>
									<c:when test="${fn:endsWith(iFile.scanResult, 'FOUND')}">
										<img title="VIRUS FOUND!! ${iFile.scanResult}" class="clickable-icon" src="<c:url value='/images/circle_red.png' />"></img>
									</c:when>
									<c:when test="${fn:endsWith(iFile.scanResult, 'OK')}">
										<!-- No virus found -->
									</c:when>
									<c:otherwise>
										<c:if test="${iFile.type == 'FILE'}">
											<!-- File could not be scanned -->
										</c:if>
									</c:otherwise>
								</c:choose>
								
								<!-- Rename icon -->
								<sec:authorize access="isAuthenticated()">
									<sec:authorize access="hasPermission(#fedoraObject,'WRITE') or hasPermission(#fedoraObject,'ADMINISTRATION')">
										<a href="javascript:" onclick="renameFile('${relUrl}', '${iFile.relFilepath}')">
											<i class="material-icons" title="Rename ${iFile.filename}">edit</i>
										</a>
									</sec:authorize>
								</sec:authorize>
								
								<!-- Delete icon -->
								<sec:authorize access="isAuthenticated()">
									<sec:authorize access="hasPermission(#fedoraObject,'WRITE') or hasPermission(#fedoraObject,'ADMINISTRATION')">
										<c:set var="escapedRelUrl" value="${fn:replace(relUrl, '\\'', '\\\\\\'') }" />
										<a href="javascript:" onclick="deleteFile('${escapedRelUrl}')">
											<i class="material-icons" title="Delete ${iFile.filename}">delete</i>
										</a>
									</sec:authorize>
								</sec:authorize>
								
								<!-- Expand -->
								<a href="javascript:" id="expand-${stat.count}"><i class="material-icons">expand_more</i></a>
							</td>
						</tr>
						
						<!-- Additional file details -->
						<tr id="filerow-extra-${stat.count}" class="file-row-extra" style="display: none">
							<td colspan="0">
								<table>
									<!-- Last Modified -->
									<tr>
										<th>Last Modified</th>
										<td>${iFile.lastModified}</td>
									</tr>
									
									<!-- Message Digests -->
									<c:if test="${not empty iFile.messageDigests}">
										<tr><td colspan="0">&nbsp;</td></tr>
										<c:forEach var="iMd" items="${iFile.messageDigests}">
											<tr>
												<th><c:out value="${iMd.key}" /></th>
												<td><c:out value="${iMd.value}" /></td>
											</tr>
										</c:forEach>
									</c:if>
									
									<!-- File Metadata -->
									<c:if test="${not empty iFile.metadata}">
										<tr><td colspan="0">&nbsp;</td></tr>
										<c:forEach var="iProperty" items="${iFile.metadata}">
											<tr>
												<th><c:out value="${iProperty.key}" /></th>
												<c:forEach var="iPropertyVal" items="${iProperty.value}">
													<td><c:out value="${iPropertyVal}" /></td>
												</c:forEach>
											</tr>
										</c:forEach>
									</c:if>
								</table>
							</td>
						</tr>
					</c:forEach>
				</table>
				</div>
			</form>
		</c:when>
	</c:choose>

	<!-- Drag n Drop -->
	<sec:authorize access="isAuthenticated()">
		<sec:authorize access="hasPermission(#fedoraObject,'WRITE') or hasPermission(#fedoraObject,'ADMINISTRATION')">
			<div id="dragandrophandler" class="w-doublewide"></div>
		</sec:authorize>
	</sec:authorize>
</div>

<div class="doublewide nopadtop" id="extRefs" style="display: none;">
	<div class="small w-doublewide">
		<!-- External references -->
		<sec:authorize access="isAuthenticated()">
			<sec:authorize access="hasPermission(#fedoraObject,'WRITE') or hasPermission(#fedoraObject,'ADMINISTRATION')">
				<button onclick="addExtRef()">Add External Reference</button>
			</sec:authorize>
		</sec:authorize>
		<c:if test="${not empty it.rdi.extRefs}">
			<ul>
				<c:forEach var="iEntry" items="${it.rdi.extRefs}">
					<li class="large">
						<a href="${iEntry}"><c:out value='${iEntry}' /></a>&nbsp;&nbsp;
						<a href="javascript:" onclick="deleteExtRef('${iEntry}');"><i class="material-icons" title="Delete">delete</i></a>
					</li>
				</c:forEach>
			</ul>
		</c:if>
	</div>
</div>

<div class="doublewide nopadtop" id="info" style="display: none;">
	<c:choose>
		<c:when test="${not empty it.rdi}">
			<table class="small w-doublewide">
				<tr>
					<th>Public</th>
					<td><c:out value="${it.isFilesPublic}" />&nbsp;
						
						<sec:authorize access="hasPermission(#fedoraObject,'PUBLISH') or hasPermission(#fedoraObject,'ADMINISTRATION')">
							<c:choose>
								<c:when test="${it.fo.embargoDatePassed}">
									<br/>(Please note that as the embargo lift date has passed to change this value the embargo date will need to be changed/removed and the record republished)
								</c:when>
								<c:otherwise>
									<a href="javascript:void(0);" onclick="toggleIsFilesPublic('${it.fo.object_id}', '${it.isFilesPublic}')">Change</a>
								</c:otherwise>
							</c:choose>
						</sec:authorize>
						<c:if test="${it.isFilesPublic and it.fo.embargoed}">
						(But embargoed until <fmt:formatDate value="${it.fo.embargoDate}" pattern="dd MMM yyyy" />)
						</c:if>
					</td>
				</tr>
				<c:forEach var="iEntry" items="${it.bagInfoTxt}">
					<tr>
						<th><c:out value="${iEntry.key}" /></th>
						<td><c:out value="${iEntry.value}" /></td>
					</tr>
				</c:forEach>
			</table>
		</c:when>
	</c:choose>
</div>

<jsp:include page="/jsp/footer.jsp" />
