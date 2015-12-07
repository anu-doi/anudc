<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>

<anu:header id="1998" title="ANU Data Commons - Message" description="description" subject="subject" respOfficer="Doug Moncur" respOfficerContact="mailto:doug.moncur@anu.edu.au"
	ssl="true">

	<link rel="stylesheet" type="text/css" href="<c:url value='/css/default.css' />" />
	<script type="text/javascript" src="<c:url value='/js/page.js' />"></script>
</anu:header>

<jsp:include page="/jsp/header.jsp" />

<anu:content layout="doublewide">
	<h1>Groups</h1>
	<h2>Create Group</h2>
	<p>
		<form class="anuform"  method="POST">
			<fieldset>
			<p>
			<label class="req" for="groupName">New Group Name</label>
			<input type="text" maxlength="255" name="groupName" class="text" />
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
				<li>${group.group_name} [${group.id}]</li>
			</c:forEach>
		</ul>
	</p>
</anu:content>

<jsp:include page="/jsp/footer.jsp" />
