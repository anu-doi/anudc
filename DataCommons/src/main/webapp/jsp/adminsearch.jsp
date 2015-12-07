<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<anu:header id="1998" title="Admin Search Item" description="description" subject="subject" respOfficer="Doug Moncur" respOfficerContact="mailto:doug.moncur@anu.edu.au"
	ssl="true">

</anu:header>

<jsp:include page="/jsp/header.jsp" />

<anu:content layout="doublenarrow">
	<c:if test="${not empty it.message}">
		<anu:message type="info">
		${it.message}
		</anu:message>
	</c:if>
	<form name="frmBasicSearch" action="<c:url value='/rest/search/admin'></c:url>" method="get">
		<p>
			<label for="basicSearchTerms">Search</label>
			<input class="text" type="text" name="q" id="idBasicSearchTerms" size="30" value="<c:out value="${param.q}" />" />
			<!-- Display a dropdown when a user's logged in allowing one to select filters such as Published, Personal, Team. Does not display for Guests. -->
			<input type="submit" value="Search" />
		</p>
	</form>
	<div id="divSearchResults">
		<hr />
		<c:if test="${it.resultSet.numResults > 0}">
			<form name="frmUpdates" action="<c:url value='/rest/search/admin'><c:param name="q">${param.q}</c:param></c:url>" method="post">
				<table>
					<tr>
						<th> </th>
						<th>ID</th>
						<th>Title</th>
					</tr>
				<c:forEach items="${it.resultSet.allResults}" var="row">
					<tr>
						<td><input type="checkbox" name="itemList" value="${row[0]}" /></td>
						<td>${row[0]}</td>
						<td>
							<a href="<c:url value="/rest/display/${row[0]}?layout=def:display" />">${row[1]}</a>
						</td>
					</tr>
				</c:forEach>
				</table>
				<p class="msg-success margintop">
					<c:out value="${it.resultSet.numResults}">0</c:out>
					result(s) found.
				</p>
				<input type="submit" name="updateIndex" value="Update Index" />
			</form>
		</c:if>
	</div>
</anu:content>

<jsp:include page="/jsp/footer.jsp" />
</body>
</html>