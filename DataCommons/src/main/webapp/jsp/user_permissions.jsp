<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<anu:header id="1998" title="ANU Data Commons - User Information" description="description" subject="subject" respOfficer="Doug Moncur"
	respOfficerContact="mailto:doug.moncur@anu.edu.au" ssl="true">
	<!-- Possible bug in the ANU taglib. The following CSS should not be referenced here. Should be referenced in the taglib. -->
	<link href="//styles.anu.edu.au/_anu/3/style/anu-forms.css" rel="stylesheet" type="text/css" />
	<link rel="stylesheet" type="text/css" href="<c:url value='/css/default.css' />" />
	<script type="text/javascript" src="<c:url value='/js/user.js' />"></script>
</anu:header>

<jsp:include page="/jsp/header.jsp" />

<anu:content layout="doublewide" title="Update User Permissions">
	<p id="message"></p>
	<form id="form" class="anuform" onsubmit="return false;">
		<fieldset>
			<legend>Search User</legend>
			<p>
				<label for="firstname">First Name</label><input id="firstname" name="firstname" type="text" size="50" />
			</p>
			<p>
				<label for="lastname">Last Name</label><input id="lastname" name="lastname" type="text" size="50" />
			</p>
			<p>
				<label for="uniId">Uni Id</label><input id="uniId" name="uniId" type="text" size="50" />
			</p>
			<p>
				<input type="submit" id="findPeople" value="Search" class="btn-uni-grad" />
			<p>
		</fieldset>
	</form>
	<div id="peopleList" class="w-doublewide"></div>
	<sec:authorize ifAnyGranted="ROLE_ADMIN">
		<div id="permissions2" class="w-wide right">
			<p>
			Allow publishing to locations:
			<ul class="nobullet bdr-solid">
				<c:forEach items="${it.publishLocations}" var="location">
					<li><input type="checkbox" name="publish_location" class="chk_location" value="${location.id}" />${location.name}</li>
				</c:forEach>
			</ul>
			</p>
			<p>
			Allow the use of templates:
			<ul class="nobullet bdr-solid">
				<c:forEach items="${it.templates}" var="template">
					<li><input type="checkbox" name="template" class="chk_template" value="${template.id}" />${template.name}</li>
				</c:forEach>
			</ul>
			</p>
		</div>
	</sec:authorize>
	<div id="updateGroups" class="w-wide">
		<p>Allowable groups to modify permissions for:</p>
		<p>
			<select id="groups" size="10" style="width: 100%">
				<c:forEach items="${it.groups}" var="group">
					<option value="${group.id}">${group.group_name} [${group.id}]</option>
				</c:forEach>
			</select>
		</p>
	</div>
	<div id="permissions" class="w-wide">
		<ul class="nobullet bdr-solid">
			<li><input type="checkbox" name="group_perm" class="chk_perm" value="1" />READ</li>
			<li><input type="checkbox" name="group_perm" class="chk_perm" value="2" />WRITE</li>
			<li><input type="checkbox" name="group_perm" class="chk_perm" value="8" />DELETE</li>
			<li><input type="checkbox" name="group_perm" class="chk_perm" value="16" />ADMINISTRATION</li>
			<li><input type="checkbox" name="group_perm" class="chk_perm" value="32" />REVIEW</li>
			<li><input type="checkbox" name="group_perm" class="chk_perm" value="64" />PUBLISH</li>
			<li><input type="checkbox" name="group_perm" class="chk_perm" value="128" />PUBLISH MULTIPLE</li>
			<li><input type="checkbox" name="group_perm" class="chk_perm" value="256" />ASSIGN PERMISSIONS</li>
		</ul>
		<p>
			<input type="button" name="updatePerm" id="updatePerm" value="Update" />
		</p>
	</div>
</anu:content>

<jsp:include page="/jsp/footer.jsp" />
