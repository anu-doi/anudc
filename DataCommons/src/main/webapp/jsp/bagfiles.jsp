<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec"	uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>

<anu:header id="1998" title="${it.fo.object_id}"
	description="DESCRIPTION" subject="SUBJECT" respOfficer="Doug Moncur"
	respOfficerContact="doug.moncur@anu.edu.au" ssl="true">
	<!-- Possible bug in the ANU taglib. The following CSS should not be referenced here. Should be referenced in the taglib. -->
	<link href="//styles.anu.edu.au/_anu/3/style/anu-forms.css"
		rel="stylesheet" type="text/css" />
	<script type="text/javascript" src="<c:url value='/js/bagfiles.js' />"></script>
	<script type="text/javascript">
		jQuery(document).ready(function() {
			documentReady();
		});
	</script>
</anu:header>

<jsp:include page="/jsp/header.jsp" />

<anu:content layout="doublewide"
	title="Data Files [${it.fo.object_id}]">
	<jsp:include page="/jsp/statusmessages.jsp">
		<jsp:param value="${it}" name="it" />
	</jsp:include>
	<img id="loading" src="<c:url value='/images/ajax-loader.gif' />" style="display: none"></img>

	<div id="tabs" class="pagetabs-nav nopadbottom">
		<ul>
			<li><a href="#files">Files</a></li>
			<li><a href="#info">Archive Info</a></li>
			<li><a href="#extRefs">External References</a></li>
			<sec:authorize access="isAuthenticated()">
				<sec:accesscontrollist hasPermission="WRITE,ADMINISTRATION" domainObject="${it.fo}">
					<li><a href="#uploadFiles">Upload Files</a></li>
				</sec:accesscontrollist>
			</sec:authorize>
		</ul>
	</div>
</anu:content>

<!-- Files list -->
<div class="doublewide nopadtop" id="files" style="display: none;">
	<c:choose>
		<c:when test="${it.bagSummary != null}">
			<p class="msg-info">Bag contains ${it.bagSummary.numFiles} file(s) totalling ${it.bagSummary.friendlySize}.</p>
			<table class="small w-doublewide" id="tblFiles">
				<!-- Column headers -->
				<tr>
					<th><input type="checkbox" onchange="toggleCheckboxes(this)" value="" /></th>
					<th>File</th>
					<th>Format</th>
					<th>Size</th>
					<th>MD5</th>
					<th>Virus</th>
					<th>Preserved</th>
					<th>File Metadata</th>
					<sec:authorize access="isAuthenticated()"><sec:accesscontrollist hasPermission="WRITE,ADMINISTRATION" domainObject="${it.fo}">
						<th>Delete File</th>
					</sec:accesscontrollist></sec:authorize>
				</tr>
				<c:forEach var="iFile" items="${it.bagSummary.fileSummaryMap}">
					<tr>
						<!-- Selection checkbox. -->
						<td><input type="checkbox" value="${iFile.key}" onclick="condEnableSelTasks()" />
						
						<!-- Filename as download link -->
						<td><a href="<c:url value='${it.dlBaseUri}${iFile.key}' />">${iFile.value.filename}</a></td>
						
						<!-- File format and PUID -->
						<c:choose>
							<c:when test="${not empty iFile.value.pronomFormat.formatName}">
								<td><c:out value="${iFile.value.pronomFormat.formatName}" /> <a class="link-ext" target="_blank"
									href="http://www.nationalarchives.gov.uk/pronom/${iFile.value.pronomFormat.puid}">${iFile.value.pronomFormat.puid}</a></td>
							</c:when>
							<c:otherwise>
								<td>Unknown</td>
							</c:otherwise>
						</c:choose>
						
						<!-- Friendly Size -->
						<td><c:out value="${iFile.value.friendlySize}" /></td>
						
						<!-- File Message Digest -->
						<td class="small"><c:out value="${iFile.value.messageDigests.MD5}" /></td>
						
						<!-- Virus scan result -->
						<td class="text-center">
						<c:choose>
							<c:when test="${fn:toLowerCase(iFile.value.scanResult) eq 'passed'}">
								<img src="<c:url value='/images/circle_green.png' />" width="12" height="12" title="${iFile.value.scanResult}" alt="${iFile.value.scanResult}" />
							</c:when>
							<c:when test="${fn:containsIgnoreCase(iFile.value.scanResult, 'failed')}">
								<img src="<c:url value='/images/circle_red.png' />" width="12" height="12" title="${iFile.value.scanResult}" alt="${iFile.value.scanResult}" />
							</c:when>
							<c:otherwise>
								<img src="<c:url value='/images/circle_yellow.png' />" width="12" height="12" title="${iFile.value.scanResult}" alt="${iFile.value.scanResult}" />
							</c:otherwise>
						</c:choose>
						</td>
						
						<!-- Preserved file download -->
						<td>
							<c:if test="${not empty iFile.value.presvFilepath}">
								<a href="<c:url value='${it.dlBaseUri}${iFile.value.presvFilepath}' />">
									<img src="<c:url value='/images/ice_icon.png' />" title="Download preserved format" />
								</a>
							</c:if>
						</td>
						
						<!-- Metadata slider -->
						<c:set var="search" value="\'" />
						<c:set var="replace" value="\\\'" />
						<c:set var="escapedFilepath" value="${fn:replace(iFile.key, search, replace)}" />
						<td onclick="showMetadataRow('${escapedFilepath}')"><a href="javascript:void(0);">Expand</a></td>
						
						<!-- Delete file -->
						<sec:authorize access="isAuthenticated()"><sec:accesscontrollist hasPermission="WRITE,ADMINISTRATION" domainObject="${it.fo}">
							<td class="text-center">
								<c:set var="escapedFileUri" value="${it.dlBaseUri}${escapedFilepath}" />
								<a href="javascript:void(0);" onclick="deleteFile('${escapedFileUri}');">
									<img src="<c:url value='/images/delete_red.png' />" width="12" height="12" title="Delete ${iFile.value.filename}" />
								</a>
							</td>
						</sec:accesscontrollist></sec:authorize>
					</tr>

					<!-- Metadata row for the file above -->
					<tr id="meta-<c:out value='${iFile.key}' />"
						style="display: none;">
						<td colspan="8">
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
			</table>
			<p><sec:authorize access="isAuthenticated()"><sec:accesscontrollist hasPermission="WRITE,ADMINISTRATION" domainObject="${it.fo}">
				<input type="button" id="idDelSelected" value="Delete Selected" onclick="deleteSelected('${it.fo.object_id}')" />
				</sec:accesscontrollist></sec:authorize>
				<input type="button" id="idDownloadZipSelected" value="Download Selected as Zip" onclick="downloadAsZip('${it.downloadAsZipUrl}')" />
			</p>
		</c:when>
		<c:otherwise>
			<p class="msg-info">This collection doesn't contain any files. Click "Upload Files" tab to upload files to this collection, or
			click External References tab to add references to externally hosted resources.</p>
		</c:otherwise>
	</c:choose>
</div>

<div class="doublewide nopadtop" id="info" style="display: none;">
	<c:choose>
		<c:when test="${it.bagSummary != null}">
			<table class="small w-doublewide">
				<tr>
					<th>Public</th>
					<td><c:out value="${it.isFilesPublic}" />&nbsp;
						<sec:accesscontrollist hasPermission="PUBLISH,ADMINISTRATION" domainObject="${it.fo}">
							<a href="javascript:void(0);" onclick="toggleIsFilesPublic('${it.fo.object_id}', '${it.isFilesPublic}')">Change</a>
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
		</c:when>
	</c:choose>
</div>

<div class="doublewide nopadtop" id="extRefs" style="display: none;">
	<div class="small w-doublewide">
		<!-- External relations -->
		<sec:authorize access="isAuthenticated()">
			<sec:accesscontrollist hasPermission="WRITE,ADMINISTRATION" domainObject="${it.fo}">
				<button onclick="addExtRef('${it.fo.object_id}')">Add	External Reference</button>
			</sec:accesscontrollist>
		</sec:authorize>
		<c:if test="${not empty it.extRefs}">
			<ul>
				<c:forEach var="iEntry" items="${it.extRefs}">
					<li><a href="${iEntry}"><c:out
								value='${iEntry}' /></a>&nbsp;&nbsp;<a
						href="javascript:void(0);"
						onclick="deleteExtRef('${it.fo.object_id}', '${iEntry}')">[Delete]</a></li>
				</c:forEach>
			</ul>
		</c:if>
	</div>
</div>

<sec:authorize access="isAuthenticated()">
	<sec:accesscontrollist hasPermission="WRITE,ADMINISTRATION" domainObject="${it.fo}">
		<div class="doublewide nopadtop" id="uploadFiles" style="display: none;">
			<p class="msg-info">The Java upload applet below may take a few moments to display. When it does, either drag and drop files from your
			system into the applet, or click on	the <em>Browse</em> button to select files from a dialog box.</p>
			<form class="anuform" name="uploadForm" id="idUploadForm" enctype="multipart/form-data" method="post" action="/">
				<applet code="wjhk.jupload2.JUploadApplet.class" name="JUpload" archive="<c:url value='/plugins/jupload-5.0.8.jar' />" width="680" height="500" mayscript
					alt="The java plugin must be installed.">
					<param name="postURL" value="<c:url value='/rest/upload/${it.fo.object_id};jsessionid=${cookie.JSESSIONID.value}' />" />
					<param name="stringUploadSuccess" value="^SUCCESS$" />
					<param name="stringUploadError" value="^ERROR: (.*)$" />
					<param name="stringUploadWarning" value="^WARNING: (.*)$" />
					<param name="debugLevel" value="1" />
					<param name="maxChunkSize" value="10485760" />
					<param name="lang" value="en" />
					<param name="formdata" value="uploadForm" />
					<param name="showLogWindow" value="false" />
					<param name="showStatusBar" value="true" />
					<param name="sendMD5Sum" value="true" />
					<param name="readCookieFromNavigator" value="false" />
					<param name="type" value="application/x-java-applet;version=1.6">
					<param name="afterUploadURL" value="<c:url value='/rest/upload/bag/${it.fo.object_id}' />" />
					This Java Applet requires Java 1.6 or higher.
				</applet>
			</form>
		</div>
	</sec:accesscontrollist>
</sec:authorize>

<jsp:include page="/jsp/footer.jsp" />
