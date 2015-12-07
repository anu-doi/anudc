<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!-- Would prefer to use jstl tags however these don't seem to work -->
<% pageContext.setAttribute("newLineChar", "\n"); %>

<anu:header id="226" title="Catalogue - PAMBU - ANU" description="description" subject="subject" respOfficer="Doug Moncur" respOfficerContact="mailto:doug.moncur@anu.edu.au"
	ssl="true">
	<c:set var="pambusite" value="http://asiapacific.anu.edu.au/pambu" scope="page" />

</anu:header>

<jsp:include page="pambuheader.jsp" />
<anu:content layout="doublewide">
	<anu:breadcrumbs>
		<c:url value="/rest/pambu/search" var="catalogueLink" />
		<anu:crumb title="Catalogue search" href='${catalogueLink}' />
		<anu:crumb title="Item List" />
	</anu:breadcrumbs>
	<h1>${it.document['published.serialNum'][0]} - ${it.document['published.name']}</h1>
	<h2>Item List</h2>
	<p>
		<c:choose>
			<c:when test="${it.document['published.holdingType'][0] == 'doc'}">
				<c:set var="docrtf" value="http://asiapacific.anu.edu.au/pambu/reels/docs/DOC${fn:substring(it.document['published.serialNum'][0], 8, 20)}.rtf" />
				<a href="${docrtf}">${it.document['published.serialNum'][0]} RTF</a>
				<br/>
				<c:set var="docpdf" value="http://asiapacific.anu.edu.au/pambu/reels/docs/DOC${fn:substring(it.document['published.serialNum'][0], 8, 20)}.PDF" />
				<a href="${docpdf}">${it.document['published.serialNum'][0]} PDF</a>
			</c:when>
			<c:when test="${it.document['published.holdingType'][0] == 'ms'}">
				<c:set var="msrtf" value="http://asiapacific.anu.edu.au/pambu/reels/manuscripts/PMB${fn:substring(it.document['published.serialNum'][0], 4, 20)}.rtf" />
				<a href="${msrtf}" >${it.document['published.serialNum'][0]} RTF</a><br/>
				<c:set var="mspdf" value="http://asiapacific.anu.edu.au/pambu/reels/manuscripts/PMB${fn:substring(it.document['published.serialNum'][0], 4, 20)}.PDF" />
				<a href="${mspdf}">${it.document['published.serialNum'][0]} PDF</a>
				<br />
			</c:when>
		</c:choose>
	</p>
	<table class="tbl-cell-bdr">
		<c:if test="${not empty it.items}">
			<tr>
				<th>Serial Number</th>
				<th>Item</th>
				<th/>
			</tr>
			<c:forEach items="${it.items.documentList}" var="row">
				<tr>
					<td>
						${row['unpublished.serialNum'][0]}
					</td>
					<td>
						<c:choose>
							<c:when test="${not empty row['unpublished.briefDesc']}">
								${fn:replace(row['unpublished.briefDesc'][0],newLineChar, "<br/>") }
							</c:when>
							<c:otherwise>
								${row['unpublished.name']}
							</c:otherwise>
						</c:choose>
					</td>
					<td>
						<a href='<c:url value="/rest/collreq?pid=${row['id']}" />' >Request</a>
					</td>
				</tr>
			</c:forEach>
		</c:if>
	</table>
</anu:content>

<jsp:include page="pambufooter.jsp" />