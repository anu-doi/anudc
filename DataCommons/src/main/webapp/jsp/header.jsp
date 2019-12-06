<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<c:url value="/" var="homeUrl" />

<header>
<anu:banner id="" ssl="true" primaryTitle="Data Commons" secondaryTitle="Library" primaryTitleUrl="${homeUrl}" secondaryTitleUrl="http://anulib.anu.edu.au/">
	<anu:utilitymenu>
		<sec:authorize access="isAnonymous()">
			<li><a href="#">Welcome Guest</a></li>
			<li> <a href='<c:url value="/login-select" />'>Login</a></li>
		</sec:authorize>
		<sec:authorize access="isAuthenticated()">
			<li><a href='<c:url value="/rest/user" />'>Welcome <sec:authentication property="principal.displayName" /> (<sec:authentication property="principal.username" />)</a></li>
			<li><a href='<c:url value="/logout" />'>Logout</a></li>
		</sec:authorize>
	</anu:utilitymenu>
</anu:banner>

<anu:topmenu>
	<anu:topmenulinks>
		<li><a class="tabs-home" href="<c:url value='/' />">Home</a></li>
		<li><a href="#">About</a></li>
		<li><a href="<c:url value='/rest/contribute' />">Contribute</a></li>
		<li><a href='<c:url value="/rest/upload/search"/>'>Data search</a></li>
		<li><a href='<c:url value="/rest/collreq"/>'>Data request</a></li>
		<li><a href="https://openresearch.anu.edu.au/contact">Contact</a></li>
		<sec:authorize access="isAnonymous()">
			<li> <a href='<c:url value="/login-select" />'>Login</a></li>
		</sec:authorize>
		<sec:authorize access="isAuthenticated()">
			<li><a href='<c:url value="/logout" />'>Logout</a></li>
		</sec:authorize>
		<li>
			<a id="gw-mega-tab-3" data-mega-menu-rigger="3" href="<c:url value='/rest/admin' />">Administration</a>
		</li>
		<li><a href="<c:url value='/rest/search/browse?field=keyword' />">Browse</a></li>
	</anu:topmenulinks>
</anu:topmenu>

</header>

<anu:body />
