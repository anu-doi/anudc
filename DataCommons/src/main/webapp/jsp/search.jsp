<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>

<anu:header id="1998" title="Search" description="description" subject="subject" respOfficer="Doug Moncur" respOfficerContact="mailto:doug.moncur@anu.edu.au"
	ssl="true">
	<!-- Possible bug in the ANU taglib. The following CSS should not be referenced here. Should be referenced in the taglib. -->
	<link href="http://styles.anu.edu.au/_anu/3/style/anu-forms.css" rel="stylesheet" type="text/css" />
</anu:header>

<jsp:include page="/jsp/header.jsp" />

<anu:content layout="doublewide" title="Search">
	<div id="divBasicSearch">
		<jsp:include page="/jsp/searchbox.jsp" />

		<script type="text/javascript">
			document.frmBasicSearch.q.focus();
		</script>
	</div>
	<div id="divSearchResults">
		<c:if test="${it.resultSet != null}">
			<hr />
			<c:forEach items="${it.resultSet.allResults}" var="row">
				<c:forEach var="iCol" begin="0" end="${it.resultSet.numCols - 1}">
					<c:choose>
						<c:when test="${iCol == 1}">
							<!-- Label -->
							<a href="<c:url value="${row[0]}" />"><c:out value="${row[iCol]}" /></a>
						</c:when>
						<c:when test="${iCol == 2}">
							<!-- Description -->
							<br />
							<c:choose>
								<c:when test="${fn:length(row[iCol]) <= 200}">
									<c:out value="${row[iCol]}" />
								</c:when>
								<c:when test="${fn:length(row[iCol]) > 200}">
									<c:out value="${fn:substring(row[iCol], 0, 200)}" />...
								</c:when>
							</c:choose>
						</c:when>
					</c:choose>
					<br />
				</c:forEach>
			</c:forEach>
			<p class="msg-success margintop">
				<c:out value="${it.resultSet.numResults}" />
				result(s) found.
			</p>
		</c:if>
	</div>
</anu:content>

<jsp:include page="/jsp/footer.jsp" />
