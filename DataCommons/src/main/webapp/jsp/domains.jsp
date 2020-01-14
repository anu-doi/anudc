<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>

<anu:header id="1998" title="ANU Data Commons - Message" description="description" subject="subject" respOfficer="ANU Library" respOfficerContact="mailto:repository.admin@anu.edu.au"
	ssl="true">

	<link rel="stylesheet" type="text/css" href="<c:url value='/css/default.css' />" />
	<link rel="stylesheet" type="text/css" href="<c:url value='/css/administration.css' />" />
	<script type="text/javascript" src="<c:url value='/js/page.js' />"></script>
	<script type="text/javascript" src="<c:url value='/js/popup.js' />"></script>
	<script type="text/javascript" src="<c:url value='/js/administration.js' />"></script>
</anu:header>

<jsp:include page="/jsp/header.jsp" />

<anu:content layout="full">
	<h1>Domains</h1>
	<h2>Create Domain</h2>
	<p>
		<form class="anuform" method="POST">
			<fieldset>
			<p>
			<label class="req" for="domainName">New Domain Name</label><input type="text" maxlength="255" name="domainName" class="text tfull" />
			</p>
			</fieldset>
			<p class="text-right"><input type="submit" value="Add" /></p>
		</form>
	</p>
	<h2>Domain List</h2>
	<p>
		<ul class="nobullet">
			<c:forEach items="${it.domains}" var="domain">
				<li>${domain.domain_name} [${domain.id}] <button id="btn-edit-${domain.id}" data-id="${domain.id}" data-name="${domain.domain_name}" class="edit-domain" title="Edit" aria-label="Edit ${domain.domain_name}"><img src="//style.anu.edu.au/_anu/images/icons/web/draw.png" alt="Edit" /></button></li>
			</c:forEach>
		</ul>
	</p>
	<div id="popupEditDomain" class="popup-edit">
		<a id="popupEditDomainClose" class="popup-close">X</a>
		<h1>Edit Domain</h1>
		<div id="popupEditContent">
			<form class="anuform" method="POST">
				<fieldset>
				<p><label for="edit-domain-id">ID</label><input id="edit-domain-id" name="domainId" type="text" class="text tfull" value="" readonly /></p>
				<p><label for="edit-domain-name">Name</label><input id="edit-domain-name" name="domainName" type="text" class="text tfull" value="" /></p>
				</fieldset>
				<p class="right"><input type="submit" value="Edit"/></p>
			</form>
		</div>
	</div>
	<div id="backgroundPopup"></div>
</anu:content>

<jsp:include page="/jsp/footer.jsp" />
