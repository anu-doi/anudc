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

			<table>
				<tr>
					<th>File</th>
					<th>MD5</th>
				</tr>
				<c:forEach var="iFile" items="${it.downloadables}">
					<tr>
						<td><a href="<c:url value='${iFile.value}' />">${iFile.key}</a></td>
						<td>Placeholder for MD5</td>
					</tr>
				</c:forEach>
			</table>
		</anu:content>
	</c:when>
</c:choose>

<jsp:include page="/jsp/footer.jsp" />
