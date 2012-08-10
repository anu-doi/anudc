<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>

<jsp:include page="pambuheader.jsp" />
<anu:content layout="doublewide">
	<anu:breadcrumbs>
		<c:url value="/rest/search/pambu" var="catalogueLink" />
		<anu:crumb title="Catalogue search" href='${catalogueLink}' />
		<anu:crumb title="Search Result" />
	</anu:breadcrumbs>
	<h1 class="doublewide nopadbottom nopadtop">Microfilm catalogue</h1>
	
	<hr />

	<c:if test="${not empty it.resultSet}">
		<c:set var="curPage" value="${(param.offset == null ? 0 : param.offset) / searchItemsPerPage + 1}" />
		<c:set var="searchItemsPerPage" value="5" />
		<h2>Search result</h2>
		<em>Your search produced <strong>${it.resultSet.numFound}</strong> results.</em><br />
		<table class="tbl-col-bdr tbl-cell-bdr" style="width: 100%;">
		<c:forEach items="${it.resultSet.documentList}" var="row">
			<tr>
				<td style='width: 80px;'>
					${row['published.serialNum'][0]}
				</td>
				<td style='width: 450px;'>
					<strong>${row['published.combinedAuthors']}<br /></strong>
					<span class="text-blue"><strong>Title:</strong></span> ${row['published.name']}<br />
					<c:if test="${not empty row['published.combinedDates.formatted']}">
						<span class="text-blue"><strong>Dates:</strong></span> ${row['published.combinedDates.formatted']}<br />
					</c:if>
					<c:if test="${not empty row['published.numReels'] or not empty row['published.format']}">
						<span class="text-blue"><strong>Reels &amp; Format:</strong></span> ${row['published.numReels'][0]}, ${row['published.format'][0]}<br />
					</c:if>
					<c:if test="${not empty row['published.holdingLocation']}">
						<span class="text-blue"><strong>Holding:</strong></span> ${row['published.holdingLocation'][0]}<br />
					</c:if>
					<c:if test="${not empty row['published.accessRights']}">
						<span style='color: #AF1E2D;'>${row['published.accessRights'][0]}</span><br />
					</c:if>
					<c:if test="${not empty row['published.briefDesc']}">
						${row['published.briefDesc'][0]}<br />
					</c:if>
					<c:if test="${not empty row['published.fullDesc']}">
						${row['published.fullDesc'][0]}<br />
					</c:if>
				</td>
			</tr>
		</c:forEach>
		</table>
		<p class="text-centre">
			Pages&nbsp;
			
			<c:forEach begin="0" end="${((it.resultSet.numFound - 1) / searchItemsPerPage) - (((it.resultSet.numFound - 1) / searchItemsPerPage) % 1)}" var="i">
				<c:url var="searchURL" value='/rest/search/pambu'>
					<c:param name="selection" value='${param.selection}' />
					<c:param name="pmbHolding" value='${param.pmbHolding}' />
					<c:param name="modifier" value='${param.modifier}' />
					<c:param name="preferredOrder" value='${param.preferredOrder}' />
					<c:param name="output" value='${param.output}' />
					<c:param name="entry" value='${param.entry}' />
					<c:param name="page" value='${i + 1}' />
					<c:if test="${not empty param.submit}">
						<c:param name="submit" value='${param.submit}' />
					</c:if>
					<c:if test="${not empty param.browseAll}">
						<c:param name="browseAll" value='${param.browseAll}' />
					</c:if>
				</c:url>
				<a class="nounderline"
					href="${searchURL}">
					<c:if test="${i == curPage - 1}">
						<strong>
					</c:if>
					<c:out value="[ ${i + 1} ]" />
					<c:if test="${i == curPage - 1}">
						</strong>
					</c:if>
				</a>
			</c:forEach>
		</p>
	</c:if>
	<c:if test="${empty it.resultSet}">
		No results returned
	</c:if>
</anu:content>

<jsp:include page="pambufooter.jsp" />