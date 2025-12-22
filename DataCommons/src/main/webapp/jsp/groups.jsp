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
	<h1>Groups</h1>
	<h2>Create Group</h2>
	<p>
		<form class="anuform"  method="POST">
			<fieldset>
			<div class="field">
			<label class="req" for="groupName">New Group Name</label>
			<span>
			<input type="text" maxlength="255" name="groupName" id="groupName" class="text tfull" />
			</span>
			</div>
			<div class="field">
			<label class="req" for="domain">Associated Domain</label>
			<span>
			<select id="domain" name="domain">
				<option value="">- No Value Selected -</option>
				<c:forEach items="${it.domains}" var="domain">
					<option value="${domain.id}">${domain.domain_name}</option>
				</c:forEach>
			</select>
			</span>
			</div>
			</fieldset>
			<div class="float-end"><input class="btn btn-primary" type="submit" value="Add" /></div>
		</form>
	</p>
	<h2>Group List</h2>
	<p>
		<ul class="nobullet">
			<c:forEach items="${it.groups}" var="group">
				<li>${group.group_name} [${group.id}] <button id="btn-edit-${group.id}" data-id="${group.id}" data-name="${group.group_name}" title="Edit" class="edit-group" aria-label="Edit ${group.group_name}" data-bs-toggle="modal" data-bs-target="#popupEditGroup"><img src="//style.anu.edu.au/_anu/images/icons/web/draw.png" alt="Edit" /></button></li>
			</c:forEach>
		</ul>
	</p>
	<div id="popupEditGroup" class="modal fade" role="dialog" aria-labelledby="modifyGroup" data-bs-backdrop="static" aria-hidden="true">
		<div class="modal-dialog modal-dialog-centered" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<h1>Edit Group</h1>
					<button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close">
				</button>
				</div>
				<div class="modal-body">
					<form id="modifyGroup" class="anuform" method="POST" name="formModifyGroup">
						<fieldset>
							<div class="field"><label for="edit-group-id">ID</label><span><input id="edit-group-id" name="groupId" type="text" class="text tfull" value="" readonly /></span></div>
							<div class="field"><label for="edit-group-name">Name</label><span><input id="edit-group-name" name="groupName" type="text" class="text tfull" value="" /></span></div>
						</fieldset>
				<p class="float-end"><input class="btn btn-primary" type="submit" value="Edit"/></p>	
					</form>
				</div>

			</div>
		</div>
	</div>
	<div id="backgroundPopup"></div>
</anu:content>
</anu:container>
<jsp:include page="/jsp/footer.jsp" />
