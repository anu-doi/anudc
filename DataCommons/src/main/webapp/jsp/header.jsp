<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<anu:body />
<anu:banner id="" ssl="true" primaryTitle="ANU Data Commons" secondaryTitle="Division of Information" primaryTitleUrl="/" secondaryTitleUrl="/" />

<anu:tabnav>
	<anu:tabmeta>
	<div class="right right">
		<ul>
		<sec:authorize access="isAnonymous()">
			<fmt:bundle basename='global'>
				<fmt:message var="casserver" key='cas.server' />
				<fmt:message var="appserver" key='app.server' />
			</fmt:bundle>
			<li><a>Welcome Guest</a></li>
			<li> <a href='<c:url value="/login" />'>Login</a></li>
			<li><a href="<c:url value='${casserver}/login'><c:param name='service' value='${appserver}/DataCommons/j_spring_cas_security_check' /></c:url>">ANU Login</a></li>
		</sec:authorize>
		<sec:authorize access="isAuthenticated()">
			<li><a>Welcome <sec:authentication property="principal.displayName" /> (<sec:authentication property="principal.username" />)</a></li>
			<li><a href='<c:url value="/j_spring_security_logout" />'>Logout</a></li>
			<li><a href="<c:url value="https://login-test.anu.edu.au/logout" />">CAS Logout</a></li>
		</sec:authorize>
		</ul>
	</div>
	</anu:tabmeta>
</anu:tabnav>

<anu:menu showSearch="true" id="1108" shortTitle="Public Data" ssl="true">
	<anu:submenu title="Data Commons">
		<li><a href="<c:url value='/jsp/welcome.jsp' />">Home (Search)</a></li>
		<sec:authorize access="hasRole('ROLE_REGISTERED')">
			<li><a href="<c:url value='/rest/collreq' />">Collection Request</a>
				<ul>
					<li><a href="<c:url value='/rest/collreq/dropbox' />">Dropboxes</a></li>
				</ul></li>
		</sec:authorize>
		<sec:authorize access="hasRole('ROLE_ANU_USER')">
			<li><a href="<c:url value='/rest/list/template' />">New</a></li>
			<li><a href="<c:url value='/rest/upload' />">Upload</a></li>
			<li><a href="<c:url value='/jsp/review_lists.jsp' />">Review</a>
				<ul>
					<li><a href="<c:url value='/rest/ready/list/rejected' />">Rejected</a></li>
					<li><a href="<c:url value='/rest/ready/list/review' />">Ready for Review</a></li>
					<li><a href="<c:url value='/rest/ready/list/publish' />">Ready for Publish</a></li>
				</ul>
			</li>
		</sec:authorize>
		<li><a href="<c:url value='/rest/admin/anupublished' />">Site Map</a></li>
	</anu:submenu>
	<sec:authorize access="hasRole('ROLE_ADMIN')">
		<anu:submenu title="Admin">
			<li><a href="<c:url value='/rest/search/admin' />">Update Index</a></li>
			<li><a href="<c:url value='/rest/pambu/admin' />">Pambu Administration</a></li>
		</anu:submenu>
	</sec:authorize>

	<anu:submenu title="External Links">
		<li><a href="<c:url value='http://ands.org.au/' />">ANDS</a></li>
		<li><a href="<c:url value='http://services.ands.org.au/home/orca/rda/' />">Research Data Australia</a></li>
		<li><a href="<c:url value='/rest/pambu/search' />">PAMBU Catalogue Search</a>
	</anu:submenu>
</anu:menu>
