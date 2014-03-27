<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>

<anu:header id="1998" title="${it.fo.object_id}" description="DESCRIPTION" subject="SUBJECT" respOfficer="Doug Moncur"
	respOfficerContact="doug.moncur@anu.edu.au" ssl="true">
	<!-- Possible bug in the ANU taglib. The following CSS should not be referenced here. Should be referenced in the taglib. -->
	<link href="//styles.anu.edu.au/_anu/3/style/anu-forms.css" rel="stylesheet" type="text/css" />
	<link href="<c:url value='/css/storage.css' />" rel="stylesheet" type="text/css" />
	<script src="//crypto-js.googlecode.com/svn/tags/3.1.2/build/rollups/md5.js"></script>
	<script src="//crypto-js.googlecode.com/svn/tags/3.1.2/build/components/lib-typedarrays-min.js"></script>
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
			<sec:authorize access="isAuthenticated()">
				<sec:accesscontrollist hasPermission="WRITE,ADMINISTRATION" domainObject="${it.fo}">
					<li><a href="#uploadFiles">Upload Files</a></li>
				</sec:accesscontrollist>
			</sec:authorize>
		</ul>
	</div>
</anu:content>

<div class="doublewide nopadtop" id="files" class="list_view">
	<c:choose>
		<c:when test="${not empty it.rdi}">
			<p class="msg-info">Record contains ${it.rdi.numFiles} file(s) totalling ${it.rdi.friendlySize}.</p>
			
			<form name="frmFiles" action="?action=zip" method="post" class="anuform">
				<!-- Navigation Breadcrumbs -->
				<div class="nav-breadcrumbs">
					<c:set var="parents" value="${it.rdi.getParents(it.path)}" />
					<c:set var="parentUrl" value="" />
					<c:forEach var="iLevel" begin="1" end="${fn:length(parents)}">
						<c:set var="parentUrl" value="${parentUrl}../" />
					</c:forEach>
	
					<c:set var="baseDataUrl" value="${parentUrl}" />
					<a class="large" href="<c:url value='${baseDataUrl}'/>">Data</a>
					<c:forEach var="iParent" items="${parents}" varStatus="stat">
						&nbsp;&gt;
						<c:set var="parentUrl" value="" />
						<c:forEach var="iLevel" begin="1" end="${fn:length(parents) - stat.count}">
							<c:set var="parentUrl" value="${parentUrl}../" />
						</c:forEach>
						
						<a class="large" href="<c:url value='${parentUrl}'/>"><c:out value="${iParent.filename}" /></a>
					</c:forEach>

					<!-- Actions -->
					<div id="div-action-icons" class="right text-right">
						<sec:authorize access="isAuthenticated()">
							<sec:accesscontrollist hasPermission="WRITE,ADMINISTRATION" domainObject="${it.fo}">
								<!-- Create Folder icon -->
								<img id="action-create-folder" class="clickable-icon" src="<c:url value='/images/folder-new.png' />"></img>
								<!-- Delete Selected Files icon -->
								<img id="action-del-selected" class="clickable-icon" src="<c:url value='/images/delete_red.png' />"></img>
							</sec:accesscontrollist>
						</sec:authorize>
						
						<!-- Download selected files as Zip icon -->
						<img id="action-dl-zip" class="clickable-icon" src="<c:url value='/images/zip.png' />"></img>
						
						<!-- Check bag files icon -->
						<sec:authorize access="hasRole('ROLE_ADMIN')">
							<a href="<c:url value='${baseDataUrl}../admin?task=verify' />">
								<img id="action-verify-files" class="clickable-icon" src="//styles.anu.edu.au/_anu/images/icons/web/check.png"></img>
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
					
					<c:forEach var="iFile" items="${it.rdi.getFiles(it.path)}" varStatus="stat">
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
							<td class="col-filename"><a class="nounderline" href="<c:url value='${relUrl}'/>" title="${iFile.relFilepath}">
								<c:choose>
									<c:when test="${iFile.type == 'DIR'}">
										<img class="clickable-icon" src="//styles.anu.edu.au/_anu/images/icons/web/folder.png"
												onmouseover="this.src='//styles.anu.edu.au/_anu/images/icons/web/folder-over.png'"
												onmouseout="this.src='//styles.anu.edu.au/_anu/images/icons/web/folder.png'" />
									</c:when>
									<c:when test="${iFile.type == 'FILE'}">
										<img class="clickable-icon" src="//styles.anu.edu.au/_anu/images/icons/web/paper.png"
												onmouseover="this.src='//styles.anu.edu.au/_anu/images/icons/web/paper-over.png'"
												onmouseout="this.src='//styles.anu.edu.au/_anu/images/icons/web/paper.png'" />
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
									<a href="<c:url value='${baseDataUrl}${iFile.presvPath}' />">
										<img class="clickable-icon" src="<c:url value='/images/ice_icon.png' />" title="Download preserved format" />
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
								
								<!-- Delete icon -->
								<sec:authorize access="isAuthenticated()">
									<sec:accesscontrollist hasPermission="WRITE,ADMINISTRATION" domainObject="${it.fo}">
										<a href="javascript:void(0);" onclick="deleteFile('${relUrl}')">
											<img class="clickable-icon" src="<c:url value='/images/delete_red.png' />" title="Delete ${iFile.filename}" />
										</a>
									</sec:accesscontrollist>
								</sec:authorize>
								
								<!-- Expand -->
								<img class="clickable-icon" id="expand-${stat.count}" src="<c:url value='/images/arrow-right.png' />" />
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
	<div id="dragandrophandler" class="w-doublewide"></div>
</div>

<div class="doublewide nopadtop" id="extRefs" style="display: none;">
	<div class="small w-doublewide">
		<!-- External references -->
		<sec:authorize access="isAuthenticated()">
			<sec:accesscontrollist hasPermission="WRITE,ADMINISTRATION" domainObject="${it.fo}">
				<button onclick="addExtRef()">Add External Reference</button>
			</sec:accesscontrollist>
		</sec:authorize>
		<c:if test="${not empty it.rdi.extRefs}">
			<ul>
				<c:forEach var="iEntry" items="${it.rdi.extRefs}">
					<li class="large">
						<a href="${iEntry}"><c:out value='${iEntry}' /></a>&nbsp;&nbsp;
						<img class="clickable-icon" src="<c:url value='/images/delete_red.png' />" onclick="deleteExtRef('${iEntry}');" />
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

<sec:authorize access="isAuthenticated()">
	<sec:accesscontrollist hasPermission="WRITE,ADMINISTRATION" domainObject="${it.fo}">
		<div class="doublewide nopadtop" id="uploadFiles" style="display: none;">
			<p class="msg-info">
				The Java upload applet below may take a few moments to display. When it does, either drag and drop files from your system into the applet, or click on the <em>Browse</em>
				button to select files from a dialog box.
			</p>
			<form class="anuform" name="uploadForm" id="idUploadForm" enctype="multipart/form-data" method="post" action="/">
				<applet code="wjhk.jupload2.JUploadApplet.class" name="JUpload" archive="<c:url value='/plugins/jupload-5.0.8.jar' />" width="680" height="500" mayscript
					alt="The java plugin must be installed.">
					<param name="postURL" value="<c:url value=';jsessionid=${cookie.JSESSIONID.value}?src=jupload' />" />
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
					<param name="readCookieFromNavigator" value="true" />
					<param name="type" value="application/x-java-applet;version=1.6">
					<param name="afterUploadURL" value="javascript:location.reload();" />
					This Java Applet requires Java 1.6 or higher.
				</applet>
			</form>
		</div>
	</sec:accesscontrollist>
</sec:authorize>


<jsp:include page="/jsp/footer.jsp" />