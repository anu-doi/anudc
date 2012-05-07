<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>

<anu:header id="1998" title="Upload" description="DESCRIPTION" subject="SUBJECT" respOfficer="Doug Moncur" respOfficerContact="doug.moncur@anu.edu.au"
	ssl="true">
	<!-- Possible bug in the ANU taglib. The following CSS should not be referenced here. Should be referenced in the taglib. -->
	<link href="http://styles.anu.edu.au/_anu/3/style/anu-forms.css" rel="stylesheet" type="text/css" />
	<link href="<c:url value='/css/upload.css' />" rel="stylesheet" type="text/css" />
	<script src="<c:url value='/js/upload.js' />" type="text/javascript"></script>
</anu:header>

<jsp:include page="/jsp/header.jsp" />

<anu:content layout="doublewide" title="Upload">
	<iframe id="uploadFrameID" name="uploadFrame" height="0" width="0" style="display: none;"></iframe>
	<form class="anuform" id="idUploadForm" enctype="multipart/form-data" method="post" target="uploadFrame"
		onsubmit="getProgressStatusAjax();"
		action="<c:url value='/upload/upload.do' />">
		<fieldset>
			<legend>Fedora Object</legend>
			<p>
				<label class="req" for="idPid">Pid: </label>
				<input class="text" type="text" name="Pid" size="40" />
			</p>
		</fieldset>
		<fieldset>
			<legend>Datastream</legend>
			<p>
				<label class="req" for="idDsId">Datastream ID: </label>
				<input class="text" type="text" name="Id" size="40" />
			</p>
			<p class="instruction">Instructions for user</p>
			<p>
				<label for="idState">State: </label> <select name="State">
					<option value="A" selected="selected">Active</option>
					<option value="I">Inactive</option>
					<option value="D">Deleted</option>
				</select>
			</p>
			<p>
				<label for="idControlGroup">Control Group: </label> <select name="ControlGroup">
					<option value="X">Inline XML</option>
					<option value="M" selected="selected">Managed Content</option>
					<option value="R">Redirect</option>
					<option value="E">External Referenced</option>
				</select>
			</p>
			<p>
				<label for="idLabel">Label: </label>
				<input class="text" type="text" name="Label" />
			</p>
			<p>
				<label for="idFileToUpload">Upload File</label>
				<input type="file" name="txtFile" id="idFileToUpload" size="50" />
			</p>

		</fieldset>
		<p class="text-right">
			<input type="submit" id="idSubmit" value="Upload" />
		</p>
	</form>
	<p class="msg-info" id="idStatusArea">
		<span id="idStatusText">Upload Status Text</span><span class="progressBar" id="idProgressBar"><span class="progressBarFill" id="idProgressBarFill"
			style="width: 0%;"></span> </span>
	</p>

</anu:content>

<jsp:include page="/jsp/footer.jsp" />
