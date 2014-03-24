<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<anu:header id="1998" title="ANU Data Commons - User Information" description="description" subject="subject" respOfficer="Doug Moncur" respOfficerContact="mailto:doug.moncur@anu.edu.au"
	ssl="true">
	<!-- Possible bug in the ANU taglib. The following CSS should not be referenced here. Should be referenced in the taglib. -->
	<link href="//styles.anu.edu.au/_anu/3/style/anu-forms.css" rel="stylesheet" type="text/css" />
	<link rel="stylesheet" type="text/css" href="<c:url value='/css/default.css' />" />
	<script type="text/javascript" src="<c:url value='/js/user.js' />"></script>
</anu:header>

<jsp:include page="/jsp/header.jsp" />

<anu:content layout="doublenarrow" title="User Information">
	<div>
		<table width="100%">
			<tr>
				<th valign="top"><label>Name</label></th>
				<td><sec:authentication property="principal.displayName" /></td>
			</tr>
			<tr>
				<th valign="top"><label>Identification</label></th>
				<td><sec:authentication property="principal.username" /></td>
			</tr>
			<tr>
				<th valign="top"><label>Institution Affiliation</label></th>
				<td>
					<c:choose>
						<c:when test="${it.user.user_type == 1}">
							The Australian National University
						</c:when>
						<c:otherwise>
							${it.user.user_registered.institution}
						</c:otherwise>
					</c:choose>
				</td>
			</tr>
			<tr>
				<th valign="top"><label>Phone</label></th>
				<td>${it.user.user_registered.phone}</td>
			</tr>
			<tr>
				<th valign="top"><label>Address</label></th>
				<% pageContext.setAttribute("newLineChar", "\n"); %>
				<td>${fn:replace(it.user.user_registered.address, newLineChar,"<br/>")}</td>
			</tr>
		</table>
	</div>
	<c:if test="${it.user.user_type == 2}">
		<c:url value="/rest/user/update" var="updateUserLink" />
		<p>
		<input class="right" type="button" id="updateuser" name="updateuser" value="Update Details" onclick="window.location='${updateUserLink}'" />
		</p>
	</c:if>
</anu:content>

<anu:content layout="narrow">
		<anu:boxheader text="Groups you belong to"/>
	<anu:box style="solid">
		<select id="groups" size="5" style="width:170px;">
			<c:forEach items="${it.groups}" var="group">
				<option value="${group.id}">${group.group_name}</option>
			</c:forEach>
		</select>
	</anu:box>
	<c:if test="${fn:length(it.groups) > 0}">
		<anu:boxheader text="Selected Group Permissions"/>
		<anu:box style="solid">
			<input type="checkbox" disabled="disabled" name="group_perm" class="chk_perm" value="1"/>READ<br/>
			<input type="checkbox" disabled="disabled" name="group_perm" class="chk_perm" value="2"/>WRITE<br/>
			<input type="checkbox" disabled="disabled" name="group_perm" class="chk_perm" value="16"/>ADMINISTRATION<br/>
			<input type="checkbox" disabled="disabled" name="group_perm" class="chk_perm" value="32"/>REVIEW<br/>
			<input type="checkbox" disabled="disabled" name="group_perm" class="chk_perm" value="64"/>PUBLISH<br/>
		</anu:box>
	</c:if>
</anu:content>

<jsp:include page="/jsp/footer.jsp" />
