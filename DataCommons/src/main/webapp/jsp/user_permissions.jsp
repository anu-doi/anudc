<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<anu:header id="1998" title="ANU Data Commons - User Information" description="description" subject="subject" respOfficer="ANU Library" respOfficerContact="mailto:repository.admin@anu.edu.au" ssl="true">

<!-- <link rel="stylesheet" type="text/css" href="<c:url value='/css/default.css' />" /> -->
	<link href="/DataCommons/static/css/anu-bootstrap.css" rel="stylesheet" type="text/css" />
	<script type="text/javascript" src="<c:url value='/js/user.js' />"></script>
</anu:header>

<jsp:include page="/jsp/header.jsp" />

<anu:container type="container">
<anu:content layout="full" title="Update User Permissions" extraClass="pb-0">
	<p id="message"></p>
	<form id="form" class="anuform" onsubmit="return false;">
		<fieldset>
			<legend>Search User</legend>
			<div class="field">
				<label for="registered-false">ANU User</label><span><input id="registered-false" type="radio" name="registered" value="false" checked="checked"></span>
			</div>
			<div class="field">
				<label for="registered-true">Registered User</label><span><input id="registered-true" type="radio" name="registered" value="true"></span>
			</div>
			<div class="field">
				<label for="firstname">First Name</label><span><input id="firstname" name="firstname" type="text" class="text tfull" /></span>
			</div>
			<div class="field">
				<label for="lastname">Last Name</label><span><input id="lastname" name="lastname" type="text" class="text tfull" /></span>
			</div>
			<div class="field">
				<label for="uniId">Uni Id</label><span><input id="uniId" name="uniId" type="text" class="text tfull" class="text tfull" /></span>
			</div>
			<div class="field">
				<label for="email">Email</label><span><input id="email" name="email" type="text" class="text tfull"/></span>
			</div>
			<p>
				<button type="submit" id="findPeople" value="Search" class="btn btn-primary">Search</button>
			<p>
		</fieldset>
	</form>
	<div id="peopleList" class="pb-2"></div>
</anu:content>
<div class="row">
<div class="col">
<anu:content layout="two-third">
	<div id="updateGroups">
		<label for="groups">Allowable groups to modify permissions for:</label>
		<p>
			<select id="groups" size="10" style="width: 100%">
				<c:forEach items="${it.groups}" var="group">
					<option value="${group.id}">${group.group_name} [${group.id}]</option>
				</c:forEach>
			</select>
		</p>
	</div>
	<div id="permissions">
	<anu:box style="bdr" styleColour="gold">
		<fieldset>
		<ul class="nobullet bdr">
			<li><label><input type="checkbox" name="group_perm" class="chk_perm" value="1" />READ</label></li>
			<li><label><input type="checkbox" name="group_perm" class="chk_perm" value="2" />WRITE</label></li>
			<li><label><input type="checkbox" name="group_perm" class="chk_perm" value="8" />DELETE</label></li>
			<li><label><input type="checkbox" name="group_perm" class="chk_perm" value="16" />ADMINISTRATION</label></li>
			<li><label><input type="checkbox" name="group_perm" class="chk_perm" value="32" />REVIEW</label></li>
			<li><label><input type="checkbox" name="group_perm" class="chk_perm" value="64" />PUBLISH</label></li>
			<li><label><input type="checkbox" name="group_perm" class="chk_perm" value="128" />PUBLISH MULTIPLE</label></li>
			<li><label><input type="checkbox" name="group_perm" class="chk_perm" value="256" />ASSIGN PERMISSIONS</label></li>
		</ul>
		</fieldset>
	</anu:box>
		<p>
			<input class="btn btn-primary" type="button" name="updatePerm" id="updatePerm" value="Update" />
		</p>
	</div>
</anu:content>
</div>
<anu:content layout="one-third">
	<sec:authorize access="hasRole('ROLE_ADMIN')">
		<div id="permissions2" class="">
			<label>
			Allow publishing to locations:
			<anu:box style="bdr" styleColour="gold">
			<ul class="nobullet">
				<c:forEach items="${it.publishLocations}" var="location">
					<li><input type="checkbox" name="publish_location" class="chk_location" value="${location.id}" />${location.name}</li>
				</c:forEach>
			</ul>
			</anu:box>
			</label>
			<p>
			Allow the use of templates:
			<anu:box style="bdr" styleColour="gold">
			<ul class="nobullet">
				<c:forEach items="${it.templates}" var="template">
					<li><label><input type="checkbox" name="template" class="chk_template" value="${template.id}" />${template.name}</label></li>
				</c:forEach>
			</ul>
			</anu:box>
			</p>
		</div>
	</sec:authorize>

</anu:content>
</div>
</anu:container>
<jsp:include page="/jsp/footer.jsp" />
