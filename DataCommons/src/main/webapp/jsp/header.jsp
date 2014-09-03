<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<anu:body />
<anu:banner id="" ssl="true" primaryTitle="ANU Data Commons" secondaryTitle="Information Technology Services" primaryTitleUrl="/" secondaryTitleUrl="/" />

<anu:tabnav>
	<anu:tabmeta>
	<div class="right right">
		<ul>
		<sec:authorize access="isAnonymous()">
			<li><a>Welcome Guest</a></li>
			<li> <a href='<c:url value="/login" />'>Login</a></li>
		</sec:authorize>
		<sec:authorize access="isAuthenticated()">
			<li><a href='<c:url value="/rest/user" />'>Welcome <sec:authentication property="principal.displayName" /> (<sec:authentication property="principal.username" />)</a></li>
			<li><a href='<c:url value="/j_spring_security_logout" />'>Logout</a></li>
			<li><a href="<c:url value="/rest/user/caslogout" />">CAS Logout</a></li>
		</sec:authorize>
		</ul>
	</div>
	</anu:tabmeta>
</anu:tabnav>

<anu:menu showSearch="true" id="1108" shortTitle="Public Data" ssl="true">
	<anu:submenu title="Data Commons">
		<li><a href="<c:url value='/' />">Home (Search)</a></li>
		<li><a href="<c:url value='/rest/search/browse?field=keyword' />">Browse</a></li>
		<li><a href="<c:url value='/rest/upload/search' />">Data Search</a>
		<sec:authorize access="hasRole('ROLE_REGISTERED')">
			<li><a href="<c:url value='/rest/collreq' />">Collection Request</a>
				<ul>
					<li><a href="<c:url value='/rest/collreq/dropbox' />">Dropboxes</a></li>
				</ul></li>
		</sec:authorize>
		<sec:authorize access="hasRole('ROLE_ANU_USER')">
			<li><a href="<c:url value='/rest/list/template' />">New</a></li>
			<fmt:bundle basename='global'>
				<fmt:message var="rejectedTitle" key="review.rejected.title" />
				<fmt:message var="reviewReadyTitle" key="review.reviewready.title" />
				<fmt:message var="publishReadyTitle" key="review.publishready.title" />
			</fmt:bundle>
			<li><a href="<c:url value='/jsp/review_lists.jsp' />">Review</a>
				<ul>
					<li><a href="<c:url value='/rest/ready/list/rejected' />">${rejectedTitle}</a></li>
					<li><a href="<c:url value='/rest/ready/list/review' />">${reviewReadyTitle}</a></li>
					<li><a href="<c:url value='/rest/ready/list/publish' />">${publishReadyTitle}</a></li>
				</ul>
			</li>
			<li><a href="<c:url value='/jsp/publish_validate.jsp' />">Validate/Publish</a>
				<ul>
					<li><a href='<c:url value="/rest/publish/validate/multiple" />'>Validate Multiple Records</a></li>
					<li><a href='<c:url value="/rest/publish/multiple" />'>Publish Multiple Records</a></li>
				</ul>
			</li>
		</sec:authorize>
		<li><a href="<c:url value='/rest/admin/anupublished' />">Site Map</a></li>
	</anu:submenu>
	<sec:authorize access="hasRole('ROLE_ADMIN')">
		<anu:submenu title="Admin">
			<li><a href="<c:url value='/rest/admin/domains' />">Domain Administration</a></li>
			<li><a href="<c:url value='/rest/admin/groups' />">Group Administration</a></li>
			<li><a href="<c:url value='/rest/user/permissions' />">User Administration</a></li>
			<li><a href="<c:url value='/rest/search/admin' />">Update Index</a></li>
			<li><a href="<c:url value='/rest/reload' />">Reload</a></li>
			<li><a href="<c:url value='/rest/report' />">Report</a></li>
		</anu:submenu>
	</sec:authorize>

	<anu:submenu title="External Links">
		<li><a href="<c:url value='http://ands.org.au/' />">ANDS</a></li>
		<li><a href="<c:url value='http://services.ands.org.au/home/orca/rda/' />">Research Data Australia</a></li>
	</anu:submenu>
</anu:menu>
