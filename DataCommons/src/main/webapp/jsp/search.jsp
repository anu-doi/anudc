<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<anu:header id="1998" title="Search" description="description" subject="subject" respOfficer="ANU Library" respOfficerContact="mailto:repository.admin@anu.edu.au"
	ssl="true">

</anu:header>

<jsp:include page="/jsp/header.jsp" />

<anu:content layout="doublewide" title="Search">
	<c:set var="maxCharacters" value="200" />
	<div id="divBasicSearch">
		<jsp:include page="/jsp/searchbox.jsp" />
	</div>
	<div id="divSearchResults">
		<c:if test="${it.resultSet != null}">
			<hr />
			${it.resultSet.numFound} results found <br /><br />
			<c:forEach items="${it.resultSet.documentList}" var="row">
				<c:choose>
					<c:when test="${not empty row['published.name']}">
						<a href="<c:url value="/rest/display/${row['id']}?layout=def:display" />"><c:out value="${row['published.name']}" /></a>&nbsp;&nbsp;<span class="text-grey50">[${row['id']}]</span><br />
						
						<c:set var="desc" value="${fn:substring(row['published.briefDesc'][0],0,maxCharacters) }" />
						${desc}<c:if test="${fn:length(row['published.briefDesc'][0]) > maxCharacters}"><b>...</b></c:if>
						<c:if test="${empty row['published.briefDesc'][0]}">
							<c:set var="desc" value="${fn:substring(row['published.fullDesc'][0],0,maxCharacters) }" />
							${desc}<c:if test="${fn:length(row['published.fullDesc'][0]) > maxCharacters}"><b>...</b></c:if>
						</c:if>
						<br />
					</c:when>
					<c:when test="${not empty row['unpublished.name']}">
						<a href="<c:url value="/rest/display/${row['id']}?layout=def:display" />"><c:out value="${row['unpublished.name']}" /></a>&nbsp;&nbsp;<span class="text-grey50">[${row['id']}]</span><br />
						<c:set var="desc" value="${fn:substring(row['unpublished.briefDesc'][0],0,maxCharacters) }" />
						${desc}<c:if test="${fn:length(row['unpublished.briefDesc'][0]) > maxCharacters}"><b>...</b></c:if>
						<c:if test="${empty row['unpublished.briefDesc'][0]}">
							<c:set var="desc" value="${fn:substring(row['unpublished.fullDesc'][0],0,maxCharacters) }" />
							${desc}<c:if test="${fn:length(row['unpublished.fullDesc'][0]) > maxCharacters}"><b>...</b></c:if>
						</c:if>
						<br />
					</c:when>
				</c:choose>
				<br />
			</c:forEach>
			<br />
			
			<jsp:include page="/jsp/search_pages.jsp">
				<jsp:param value="/rest/search" name="searchURLPart"/>
			</jsp:include>
		</c:if>
	</div>
</anu:content>

<jsp:include page="/jsp/footer.jsp" />
