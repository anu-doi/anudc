<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<anu:header id="1998" title="Create New Item" description="description" subject="subject" respOfficer="ANU Library" respOfficerContact="mailto:repository.admin@anu.edu.au"
	ssl="true">

</anu:header>

<jsp:include page="/jsp/header.jsp" />

<anu:content layout="doublewide">
	<div id="divSearchResults">
		<c:if test="${it.resultSet != null}">
			<hr />
			${it.resultSet.numFound} results found <br /><br />
			<c:forEach items="${it.resultSet.documentList}" var="row">
				<a href="<c:url value="/rest/display/new?layout=def:new&tmplt=${row['id']}" />"><c:out value="${row['template.name']}" /></a> <br />
				${row['template.briefDesc']} <br /><br />
			</c:forEach>
			<br />
			<jsp:include page="/jsp/search_pages.jsp">
				<jsp:param value="/rest/list/template" name="searchURLPart"/>
			</jsp:include>
		</c:if>
		<c:if test="${it.templates != null}">
			<c:forEach items="${it.templates}" var="template">
				<a href="<c:url value='/rest/display/new?layout=def:new&tmplt=${template.templatePid}' />">${template.name}</a><br/>
				${template.description}<br/><br/>
			</c:forEach>
		</c:if>
	</div>
</anu:content>

<jsp:include page="/jsp/footer.jsp" />
</body>
</html>
