<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>

<anu:header id="1998" title="Search" description="description" subject="subject" respOfficer="Doug Moncur" respOfficerContact="mailto:doug.moncur@anu.edu.au"
	ssl="true">
</anu:header>

<jsp:include page="/jsp/header.jsp" />

<anu:content layout="doublenarrow" extraClass="nopadbottom" title="Search">
	<div id="divBasicSearch">
		<form class="anuform" name="frmBasicSearch" action="<c:url value='/search/search.do' />" method="get">
			<label for="basicSearchTerms">Search for: </label>
			<input type="text" name="terms" id="idBasicSearchTerms" size="30" value="<c:out value="${param.terms}" />" />
			<input type="hidden" name="dt" value="on" />
			<input type="hidden" name="format" value="Sparql" />
			<input type="hidden" name="lang" value="sparql" />
			<input type="hidden" name="limit" value="1000" />
			<input type="hidden" name="type" value="tuples" />
			<input type="hidden" name="resultsfmt" value="doc" />
			<input type="submit" value="Search" />
		</form>
		<script type="text/javascript">
			document.frmBasicSearch.terms.focus();
		</script>
	</div>
	<div id="divSearchResults">
		<c:if test="${resultSet != null}">
			<hr />
			<c:forEach items="${resultSet.allResults}" var="row">
				<c:forEach var="iCol" begin="0" end="${resultSet.numCols - 1}">
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
			<p><c:out value="${resultSet.numResults}" /> result(s) found.</p>
		</c:if>
	</div>
</anu:content>

<jsp:include page="/jsp/footer.jsp" />
