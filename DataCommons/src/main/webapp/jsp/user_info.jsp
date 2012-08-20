<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<anu:header id="1998" title="ANU Data Commons - User Information" description="description" subject="subject" respOfficer="Doug Moncur" respOfficerContact="mailto:doug.moncur@anu.edu.au"
	ssl="true">
	<!-- Possible bug in the ANU taglib. The following CSS should not be referenced here. Should be referenced in the taglib. -->
	<link href="http://styles.anu.edu.au/_anu/3/style/anu-forms.css" rel="stylesheet" type="text/css" />
	<link rel="stylesheet" type="text/css" href="<c:url value='/css/default.css' />" />
	<script type="text/javascript" src="<c:url value='/js/user.js' />"></script>
</anu:header>

<jsp:include page="/jsp/header.jsp" />

<anu:content layout="doublenarrow" title="User Information">
	<label class="user-label">Name:</label><sec:authentication property="principal.displayName" /><br/>
	<label class="user-label">Identification:</label><sec:authentication property="principal.username" /><br/>
	<br/>
	
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
