<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>

<anu:header id="1998" title="TITLE" description="DESCRIPTION" subject="SUBJECT" respOfficer="Doug Moncur" respOfficerContact="doug.moncur@anu.edu.au" ssl="true">
	<!-- Possible bug in the ANU taglib. The following CSS should not be referenced here. Should be referenced in the taglib. -->
	<link href="http://styles.anu.edu.au/_anu/3/style/anu-forms.css" rel="stylesheet" type="text/css" />
</anu:header>

<jsp:include page="/jsp/header.jsp" />

<c:choose>
	<c:when test="${it.downloadables != null}">
		<!-- Display list of files -->

		<anu:content layout="doublewide" title="Bag Files">
			<jsp:include page="/jsp/statusmessages.jsp">
				<jsp:param value="${it}" name="it" />
			</jsp:include>

			<table class="small">
				<tr>
					<th>File</th>
					<th>Format</th>
					<th>Pronom PUID</th>
					<th>Size</th>
					<th>MD5</th>
					<th>Expand</th>
				</tr>
				<c:forEach var="iFile" items="${it.downloadables}">
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
						<td onclick="jQuery('#${iFile.value.md5}').slideToggle()"><a href="#" onclick="return false">Expand</a></td>
					</tr>
					<tr id="<c:out value='${iFile.value.md5}' />" style="display: none;">
						<td colspan="6">
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
		</anu:content>
	</c:when>
</c:choose>

<jsp:include page="/jsp/footer.jsp" />
