<%@ page import="au.edu.anu.datacommons.ldap.LdapPerson"%>
<%@ page import="au.edu.anu.datacommons.ldap.LdapRequest"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>

<jsp:include page="/jsp/constants.jsp" />

<anu:body />
<anu:banner id="" ssl="true" primaryTitle="ANU Data Commons" secondaryTitle="Division of Information" primaryTitleUrl="/" secondaryTitleUrl="/" />

<anu:tabnav>
	<anu:tabmeta>
		<sec:authorize access="isAnonymous()">
			Welcome Guest <a href='<c:url value="/login" />'>Login</a>
			<a
				href="
				<c:url value='${casServer}/login'>
					<c:param name='service' value='${pageContext.request.scheme}://${pageContext.request.serverName}:${pageContext.request.serverPort}/DataCommons/j_spring_cas_security_check' />
				</c:url>">ANU
				Login</a>
		</sec:authorize>
		<sec:authorize access="isAuthenticated()">
			Welcome <sec:authentication property="principal.username" />
			<a href='<c:url value="/j_spring_security_logout" />'>Logout</a>
		</sec:authorize>
	</anu:tabmeta>
</anu:tabnav>

<anu:menu showSearch="true" id="1108" shortTitle="Data Commons" ssl="true">
	<anu:submenu title="Data Commons">
		<li><a href="<c:url value='/jsp/welcome.jsp' />">Home</a></li>
		<li><a href="<c:url value='/rest/search' />">Search</a></li>
		<sec:authorize access="hasRole('ROLE_REGISTERED')">
			<li><a href="<c:url value='/rest/collreq' />">Collection Request</a>
				<ul>
					<li><a href="<c:url value='/rest/collreq/question' />">Question Bank</a></li>
					<li><a href="<c:url value='/rest/collreq/dropbox' />">Dropboxes</a></li>
				</ul></li>
		</sec:authorize>
		<sec:authorize access="hasRole('ROLE_ANU_USER')">
			<li><a href="<c:url value='/rest/list/template' />">New</a></li>
			<li><a href="<c:url value='/rest/upload' />">Upload</a></li>
		</sec:authorize>
	</anu:submenu>

	<anu:submenu title="ANU">
		<li><a href="#">Item</a></li>
		<li><a href="#">Item 2</a></li>
		<li><a href="#">Item 3</a></li>
	</anu:submenu>
</anu:menu>
