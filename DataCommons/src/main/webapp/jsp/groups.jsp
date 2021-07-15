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
	<h1>Groups</h1>
	<h2>Create Group</h2>
	<p>
		<form class="anuform"  method="POST">
			<fieldset>
			<p>
			<label class="req" for="groupName">New Group Name</label>
			<input type="text" maxlength="255" name="groupName" class="text tfull" />
			</p>
			<p>
			<label class="req" for="domain">Associated Domain</label>
			<select name="domain">
				<option value="">- No Value Selected -</option>
				<c:forEach items="${it.domains}" var="domain">
					<option value="${domain.id}">${domain.domain_name}</option>
				</c:forEach>
			</select>
			</p>
			</fieldset>
			<p class="text-right"><input type="submit" value="Add" /></p>
		</form>
	</p>
	<h2>Group List</h2>
	<p>
		<ul class="nobullet">
			<c:forEach items="${it.groups}" var="group">
				<li>${group.group_name} [${group.id}] <button id="btn-edit-${group.id}" data-id="${group.id}" data-name="${group.group_name}" title="Edit" class="edit-group" aria-label="Edit ${group.group_name}"><img src="//marketing-pages.anu.edu.au/_anu/images/icons/web/draw.png" alt="Edit" /></button></li>
			</c:forEach>
		</ul>
	</p>
	<div id="popupEditGroup" class="popup-edit">
		<a id="popupEditGroupClose" class="popup-close">X</a>
		<h1>Edit Group</h1>
		<div id="popupEditContent">
			<form class="anuform" method="POST">
				<fieldset>
				<p><label for="edit-group-id">ID</label><input id="edit-group-id" name="groupId" type="text" class="text tfull" value="" readonly /></p>
				<p><label for="edit-group-name">Name</label><input id="edit-group-name" name="groupName" type="text" class="text tfull" value="" /></p>
				</fieldset>
				<p class="right"><input type="submit" value="Edit"/></p>
			</form>
		</div>
	</div>
	<div id="backgroundPopup"></div>
</anu:content>

<jsp:include page="/jsp/footer.jsp" />
