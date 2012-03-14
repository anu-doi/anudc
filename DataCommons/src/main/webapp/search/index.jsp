<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<script type="text/javascript" src="<%=request.getContextPath()%>/js/global.js"></script>
<title>Search</title>
</head>
<body>
	<h1>Search</h1>
	<div id="divBasicSearch">
		<form name="frmBasicSearch" action="<%=request.getContextPath()%>/search/search.do" method="get">
			<label for="basicSearchTerms">Search for: </label>
			<input type="text" name="terms" id="idBasicSearchTerms" size="30" value="<c:out value="${param.terms}" />" />
			&nbsp;
			<input type="hidden" name="dt" value="on" />
			<input type="hidden" name="format" value="Sparql" />
			<input type="hidden" name="lang" value="sparql" />
			<input type="hidden" name="limit" value="1000" />
			<input type="hidden" name="type" value="tuples" />
			<input type="hidden" name="resultsfmt" value="doc" />
			<input type="submit" value="Search" />
		</form>
	</div>
	<div id="divSearchResults">
		<c:if test="${resultSet != null}">
			<hr />
			<c:forEach items="${resultSet.allResults}" var="row">
				<c:forEach var="iCol" begin="0" end="${resultSet.numCols - 1}">
					<c:choose>
						<c:when test="${iCol == 1}">
							<!-- Label -->
							<!-- Change URL href to the item details page. -->
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

		</c:if>
	</div>
</body>
</html>