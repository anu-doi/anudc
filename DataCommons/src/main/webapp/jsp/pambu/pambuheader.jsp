<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>

<anu:header id="226" title="Microfilm catalogue - PAMBU - ANU" description="description" subject="subject" respOfficer="Doug Moncur" respOfficerContact="mailto:doug.moncur@anu.edu.au"
	ssl="true">
	<c:set var="pambusite" value="http://asiapacific.anu.edu.au/pambu" scope="page" />
	<!-- Possible bug in the ANU taglib. The following CSS should not be referenced here. Should be referenced in the taglib. -->
	<link href="http://styles.anu.edu.au/_anu/3/style/anu-forms.css" rel="stylesheet" type="text/css" />
</anu:header>

<anu:body />
<anu:banner id="226" ssl="true" primaryTitle="Pacific Manuscripts Bureau" secondaryTitle="ANU College of Asia & the Pacific" primaryTitleUrl="/" secondaryTitleUrl="/" />

<anu:menu showSearch="true" id="1108" shortTitle="PAMBU" ssl="true">
	<anu:submenu title="">
		<li><a href="<c:url value='${pambusite}' />">Home</a></li>
		<li><a href="<c:url value='${pambusite}/about.php' />">About</a></li>
		<li><a href="<c:url value='/rest/search/pambu' />">Catalogue</a></li>
		<li><a href="<c:url value='${pambusite}/current-projects.php' />">Current Projects</a></li>
		<li><a href="<c:url value='${pambusite}/newsletters.php' />">Pambu newsletters</a></li>
		<li><a href="<c:url value='${pambusite}/accessing.php' />">Accessing PMB collections</a></li>
		<li><a href="<c:url value='${pambusite}/online.php' />">Online resources</a></li>
		<li><a href="<c:url value='${pambusite}/links.php' />">Links</a></li>
		<li><a href="<c:url value='${pambusite}/collections/' />">Collections</a></li>
		<ul>
			<li><a href="<c:url value='${pambusite}/microfilm.php' />">Microfilm</a></li>
			<li><a href="<c:url value='${pambusite}/audio.php' />">Audio recordings</a></li>
			<li><a href="<c:url value='${pambusite}/photo.php' />">Photographic</a></li>
			<li><a href="<c:url value='${pambusite}/print.php' />">Printed publications</a></li>
		</ul>
		<li><a href="<c:url value='${pambusite}/contacts.php' />">Contacts</a></li>
		<ul>
			<li><a href="<c:url value='${pambusite}/contacts/committee.php' />">Management committee</a></li>
		</ul>
	</anu:submenu>
</anu:menu>