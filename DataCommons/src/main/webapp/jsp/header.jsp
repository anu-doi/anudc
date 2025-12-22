<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<c:url value="/" var="homeUrl" />

<body>
<anu:banner id="" ssl="true" primaryTitle="Data Commons" secondaryTitle="Library" primaryTitleUrl="${homeUrl}" secondaryTitleUrl="http://anulib.anu.edu.au/">
	<sec:authorize access="isAuthenticated()">
		<anu:utilitymenu>
			<li class="nav-item"><a href='<c:url value="/rest/user" />'><sec:authentication property="principal.displayName" /> (<sec:authentication property="principal.username" />)</a></li>
	</anu:utilitymenu>
	</sec:authorize>
</anu:banner>

<anu:topmenu>
	<anu:topmenulinks>
		<div class='anu-wf-mobile-menu react-none d-block d-lg-none' data-showauthlinks="true">
			<ul>
				<li><a href="/DataCommons/rest/about">About</a></li>
				<li><a href="/DataCommons/rest/contribute">Contribute</a></li>
				<li><a href="/DataCommons/rest/upload/search">Data search</a></li>
				<li><a href="/DataCommons/rest/collreq">Data request</a></li>
				<li><a href="/DataCommons/rest/contact">Contact</a></li>
				<li><a href="/DataCommons/login-select">Login</a></li>
			</ul>
		</div>
		<div class='anu-wf-mega-menu d-none d-lg-block'>
		<div class="anu-wf-mega-menu-item"><a href="<c:url value='/rest/contribute' />">Contribute</a></div>
		<div class="anu-wf-mega-menu-item"><a href="<c:url value='/rest/about' />">About</a></div>
<!--		<div class="anu-wf-mega-menu-item"><a href="<c:url value='/rest/contribute' />">Contribute</a></div> -->
		<div class="anu-wf-mega-menu-item"><a href='<c:url value="/rest/upload/search"/>'>Data search</a></div>
		<div class="anu-wf-mega-menu-item"><a href='<c:url value="/rest/collreq"/>'>Data request</a></div>
		<div class="anu-wf-mega-menu-item"><a href='<c:url value="/rest/contact"/>'>Contact</a></div>
		<sec:authorize access="isAnonymous()">
			<div class="anu-wf-mega-menu-item"><a href='<c:url value="/login-select" />'>Login</a></div>
		</sec:authorize>
		<sec:authorize access="isAuthenticated()">
			<div class="anu-wf-mega-menu-item"><a href='<c:url value="/logout" />'>Logout</a></div>
		<div class="anu-wf-mega-menu-item">
			<a href="<c:url value='/rest/admin' />">Administration</a>
		</div>
		</sec:authorize>
		</div>
	</anu:topmenulinks>
</anu:topmenu>

<!-- </header>  -->

<anu:body />
