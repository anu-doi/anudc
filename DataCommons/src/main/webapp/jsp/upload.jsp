<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>

<anu:header id="1998" title="Upload" description="DESCRIPTION" subject="SUBJECT" respOfficer="Doug Moncur" respOfficerContact="doug.moncur@anu.edu.au"
	ssl="true">
	<!-- Possible bug in the ANU taglib. The following CSS should not be referenced here. Should be referenced in the taglib. -->
	<link href="http://styles.anu.edu.au/_anu/3/style/anu-forms.css" rel="stylesheet" type="text/css" />
	<script type="text/javascript" src="<c:url value='/js/upload.js' />"></script>
</anu:header>

<jsp:include page="/jsp/header.jsp" />

<anu:content layout="doublewide" title="Upload">
	<form class="anuform" name="uploadForm" id="idUploadForm" enctype="multipart/form-data" method="post" action="<c:url value='/rest/upload' />">
		<fieldset>
			<legend>Fedora Object</legend>
			<p>
				<label class="req" for="idPid">Pid</label>
				<input class="text" type="text" name="pid" size="40" />
			</p>
		</fieldset>
		<fieldset>
			<legend>Datastream</legend>
			<p>
				<label class="req" for="idDsId">Datastream ID</label>
				<input class="text" type="text" name="dsid" size="40" />
			</p>
			<p class="instruction">Instructions for user</p>
			<p>
				<label for="idLabel">Label: </label>
				<input class="text" type="text" name="Label" size="40" />
			</p>
		</fieldset>
		<fieldset>
			<legend>
				URL References
				<input type="button" id="button1" onclick="cloneUrlFields(this.parentNode.parentNode)" value=" + " />
			</legend>
			<p>
				<label>URL</label>
				<input class="text" type="text" name="url" size="40" />
				<input type="button" onclick="removeElement(this.parentNode)" value=" - " />
			</p>
		</fieldset>
		<input type="hidden" name="state" value="A" />
		<input type="hidden" name="cg" value="E" />
		<sec:authorize access="isAuthenticated()">
			<input type="hidden" name="username" value="<sec:authentication property='principal.username' />" />
		</sec:authorize>

		<!-- JUpload applet. Using <applet> tag instead of <object> as per http://docs.oracle.com/javase/7/docs/technotes/guides/plugin/developer_guide/using_tags.html .
		Warnings expected. -->
		<applet code="wjhk.jupload2.JUploadApplet.class" name="JUpload" archive="/DataCommons/plugins/wjhk.jupload.jar" width="680" height="500" mayscript
			alt="The java plugin must be installed.">
			<param name="postURL" value="<c:url value='/rest/upload' />" />
			<param name="stringUploadSuccess" value="^SUCCESS$" />
			<param name="stringUploadError" value="^ERROR: (.*)$" />
			<param name="stringUploadWarning" value="^WARNING: (.*)$" />
			<param name="debugLevel" value="99" />
			<param name="maxChunkSize" value="10485760" />
			<param name="afterUploadURL" value="javascript:alert('Upload successful.');" />
			<param name="formdata" value="uploadForm" />
			<param name="showLogWindow" value="true" />
			<param name="showStatusBar" value="true" />
			<param name="sendMD5Sum" value="true" />
			This Java Applet requires Java 1.5 or higher.
		</applet>
	</form>
</anu:content>

<jsp:include page="/jsp/footer.jsp" />
