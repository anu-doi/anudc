<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<anu:header id="1998" title="ANU Data Commons" description="description" subject="subject" respOfficer="Doug Moncur" respOfficerContact="mailto:doug.moncur@anu.edu.au"
	ssl="true">
	<!-- Possible bug in the ANU taglib. The following CSS should not be referenced here. Should be referenced in the taglib. -->
	<link href="//styles.anu.edu.au/_anu/3/style/anu-forms.css" rel="stylesheet" type="text/css" />
</anu:header>

<jsp:include page="/jsp/header.jsp" />

<anu:content layout="doublenarrow" title="ANU Data Commons">
	<p>Welcome to the ANU Data Commons.</p>
	<p>This project will allow people to add information about their datasets, catalogues etc.</p>
	<p><jsp:include page="searchbox.jsp"></jsp:include> </p>
	<div id="divSearchResults">
		<c:set var="maxCharacters" value="100" />
		<c:if test="${it.resultSet != null and it.resultSet.numFound > 0}">
			<hr />
			<h2>Recently Updated Records</h2>
			<c:forEach items="${it.resultSet.documentList}" var="row">
				<a href="<c:url value="/rest/display/${row['id']}?layout=def:display" />"><c:out value="${row['unpublished.name']}" /></a>&nbsp;&nbsp;<span class="text-grey50">[${row['id']}]</span><br />
				<c:set var="desc" value="${fn:substring(row['unpublished.briefDesc'][0],0,maxCharacters) }" />
				${desc}<c:if test="${fn:length(row['unpublished.briefDesc'][0]) > maxCharacters}"><b>...</b></c:if>
				<c:if test="${empty row['unpublished.briefDesc'][0]}">
					<c:set var="desc" value="${fn:substring(row['unpublished.fullDesc'][0],0,maxCharacters) }" />
					${desc}<c:if test="${fn:length(row['unpublished.fullDesc'][0]) > maxCharacters}"><b>...</b></c:if>
				</c:if>
				<br/>
				<br />
			</c:forEach>
			<br />
			<c:url value="/rest/search" var="viewAll">
				<c:param name="q">*</c:param>
				<c:param name="limit">5000</c:param>
				<c:param name="filter">team</c:param>
				<c:param name="offset">0</c:param>
				<c:param name="sort">_docid_</c:param>
				<c:param name="order">desc</c:param>
			</c:url>
			<p class="right"><a href="${viewAll}">Show All My Records</a></p>
		</c:if>
	</div>
</anu:content>

<!-- Section for changelogs, updates, news and announcements etc. for users to see. -->
<anu:content layout="narrow">
	<anu:boxheader text="Updates" />
	<anu:box style="solid">Welcome to The Australian National University Data Commons</anu:box>
</anu:content>

<jsp:include page="/jsp/footer.jsp" />
