<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>

<anu:header id="1998" title="${it.results.bagId}"
	description="DESCRIPTION" subject="SUBJECT" respOfficer="Doug Moncur"
	respOfficerContact="doug.moncur@anu.edu.au" ssl="true">
	<!-- Possible bug in the ANU taglib. The following CSS should not be referenced here. Should be referenced in the taglib. -->
	<link href="http://styles.anu.edu.au/_anu/3/style/anu-forms.css"
		rel="stylesheet" type="text/css" />
	<script type="text/javascript" src="<c:url value='/js/verificationresults.js' />"></script>
</anu:header>

<jsp:include page="/jsp/header.jsp" />

<anu:content layout="doublewide"
	title="Verification Results - [${it.results.bagId}]">
	<jsp:include page="/jsp/statusmessages.jsp">
		<jsp:param value="${it}" name="it" />
	</jsp:include>

	<p>Timestamp: <c:out value="${it.results.timestamp}" /></p>
	<p>Message Count: <c:out value="${fn:length(it.results.messages)}" /></p>
	<p><input type="button" value="Rescan Files" onclick="recomplete()" /></p>
	
	<table class="small w-doublewide">
		<tr>
			<th>Severity</th>
			<th>Category</th>
			<th>Filepath</th>
			<th>Message</th>
		</tr>
		<c:forEach var="iEntry" items="${it.results.messages}">
			<tr>
				<td><c:out value="${iEntry.severity}" /></td>
				<td><c:out value="${iEntry.category}" /></td>
				<td><c:out value="${iEntry.filepath}" /></td>
				<td><c:out value="${iEntry.message}" /></td>
			</tr>
		</c:forEach>
	</table>
</anu:content>

<jsp:include page="/jsp/footer.jsp" />