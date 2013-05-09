<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>

<c:set var="layoutstyle" value="doublenarrow" />
<c:if test="${empty it.sidepage}">
	<c:set var="layoutstyle" value="doublewide" />
</c:if>
<anu:content layout="${layoutstyle}">
	<sec:accesscontrollist hasPermission="READ,WRITE,REVIEW,PUBLISH,ADMINISTRATION" domainObject="${it.fedoraObject}">
		<c:if test="${not empty it.fedoraObject.reviewReady}">
			<anu:message type="info">
				Ready for Review
			</anu:message>
			<br />
		</c:if>
		<c:if test="${not empty it.fedoraObject.publishReady}">
			<anu:message type="info">
				Ready for Publishing
			</anu:message>
			<br />
		</c:if>
		<c:if test="${not empty it.fedoraObject.reviewReject}">
			<anu:message type="info">
				<strong>Rejection Reasons:</strong><br />
				${it.fedoraObject.reviewReject.reason}
			</anu:message>
			<br />
		</c:if>
	</sec:accesscontrollist>
<c:out escapeXml="false" value="${it.page}" />

<input type="hidden" readonly="readonly" name="itemType" id="itemType" value="${it.itemType}" />

<div id="popupFindData">
	<a id="popupFindDataClose">x</a>
	<h1>Import Metadata</h1>
	<label for="findEmail">Email:</label><input type="text" id="findEmail" name="findEmail" /><br/>
	<label for="findGiven">Given Name:</label><input type="text" id="findGiven" name="findGiven" /><br/>
	<label for="findSurname">Surname:</label><input type="text" id="findSurname" name="findSurname" /><br/>
	<input type="button" id="searchData" name="searchData" value="Find"/></p>
	<p>
		<div id="findDataContent">
			Search For Data
		</div>
	</p>
	<p><input type="button" id="selectData" name="selectData" value="Select" /></p>
</div>
<div id="backgroundPopup"></div>

</anu:content>