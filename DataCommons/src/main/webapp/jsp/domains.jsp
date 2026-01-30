<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>

<anu:header id="1998" title="ANU Data Commons - Message" description="description" subject="subject" respOfficer="ANU Library" respOfficerContact="mailto:repository.admin@anu.edu.au"
	ssl="true">

	<link rel="stylesheet" type="text/css" href="<c:url value='/css/default.css' />" />
	<link href="/DataCommons/static/css/anu-bootstrap.css" rel="stylesheet" type="text/css" />
	<script type="text/javascript" src="<c:url value='/js/page.js' />"></script>
	<script type="text/javascript" src="<c:url value='/js/administration.js' />"></script>
</anu:header>

<jsp:include page="/jsp/header.jsp" />

<anu:container type="container">
<anu:content layout="full">
	<h1>Domains</h1>
	<h2>Create Domain</h2>
	<p>
		<form class="anuform" method="POST">
			<fieldset>
			<div class="field">
			<label class="req" for="domainName">New Domain Name</label>
			<span><input type="text" maxlength="255" name="domainName" id="domainName" class="text tfull" /></span>
			</div>
			</fieldset>
			<p class="float-end"><input class="btn btn-primary" type="submit" value="Add" /></p>
		</form>
	</p>
	<h2>Domain List</h2>
	<p>
		<ul class="nobullet">
			<c:forEach items="${it.domains}" var="domain">
				<li>${domain.domain_name} [${domain.id}] <button id="btn-edit-${domain.id}" data-id="${domain.id}" data-name="${domain.domain_name}" class="edit-domain" title="Edit" aria-label="Edit ${domain.domain_name}" data-bs-toggle="modal" data-bs-target="#popupEditDomain"><img src="//style.anu.edu.au/_anu/images/icons/web/draw.png" alt="Edit" /></button></li>
			</c:forEach>
		</ul>
	</p>
<!-- 	<div id="popupEditDomain" class="popup-edit">
		<a id="popupEditDomainClose" class="popup-close">X</a>
		<h1>Edit Domain</h1>
		<div id="popupEditContent">
			<form class="anuform" method="POST">
				<fieldset>
				<div class="field"><label for="edit-domain-id">ID</label><span><input id="edit-domain-id" name="domainId" type="text" class="text tfull" value="" readonly /></span></div>
				<div class="field"><label for="edit-domain-name">Name</label><span><input id="edit-domain-name" name="domainName" type="text" class="text tfull" value="" /></span></div>
				</fieldset>
				<p class="right"><input class="btn btn-primary float-end" type="submit" value="Edit"/></p>
			</form>
		</div>
	</div> -->
	<div id="popupEditDomain" class="modal fade" role="dialog" aria-labelledby="modifyDomain" data-bs-backdrop="static" aria-hidden="true">
		<div class="modal-dialog modal-dialog-centered" role="document">
		<div class="modal-content">
			<div class="modal-header">
				<h1>Edit Domains</h1>
					<button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close">
					</button>
			</div>
			<div class="modal-body">
				<form id="modifyDomain" class="anuform" method="POST">
					<fieldset>
						<div class="field"><label for="edit-domain-id">ID</label><span><input id="edit-domain-id" name="domainId" type="text" class="text tfull" value="" readonly /></span></div>
						<div class="field"><label for="edit-domain-name">Name</label><span><input id="edit-domain-name" name="domainName" type="text" class="text tfull" value="" /></span></div>
					</fieldset>
					<p class="float-end"><input class="btn btn-primary float-end" type="submit" value="Edit"/></p>
				</form>
			</div>
			</div>
		</div>
	</div>
	<div id="backgroundPopup"></div>
</anu:content>
</anu:container>
<jsp:include page="/jsp/footer.jsp" />
