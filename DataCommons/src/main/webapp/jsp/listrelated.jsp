<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>

<c:if test="${it.resultSet.numResults > 0}">
<anu:box style="solid">
	<c:forEach items="${it.resultSet.allResults}" var="row">
		<c:forEach var="iCol" begin="0" end="${it.resultSet.numCols - 1}">
			<c:choose>
				<c:when test="${iCol == 1}">
					<!-- Label -->
					<a href="<c:url value="/rest/display?layout=def:display&item=${row[0]}" />"><c:out value="${row[iCol]}" /></a>
				</c:when>
				<c:when test="${iCol > 1}">
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
</anu:box>
</c:if>