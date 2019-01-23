<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>

<anu:header id="1998" title="ANU Data Commons - Message" description="description" subject="subject" respOfficer="ANU Library" respOfficerContact="mailto:repository.admin@anu.edu.au"
	ssl="true">

	<link rel="stylesheet" type="text/css" href="<c:url value='/css/default.css' />" />
	<script type="text/javascript" src="<c:url value='/js/page.js' />"></script>
</anu:header>

<jsp:include page="/jsp/header.jsp" />

<anu:content layout="doublewide">
	<h1>Domains</h1>
	<h2>Create Domain</h2>
	<p>
		<form class="anuform" method="POST">
			<fieldset>
			<p>
			<label class="req" for="domainName">New Domain Name</label><input type="text" maxlength="255" name="domainName" />
			<input type="submit" value="Add" />
			</p>
			</fieldset>
		</form>
	</p>
	<h2>Domain List</h2>
	<p>
		<ul class="nobullet">
			<c:forEach items="${it.domains}" var="domain">
				<li>${domain.domain_name} [${domain.id}]</li>
			</c:forEach>
		</ul>
	</p>
</anu:content>

<jsp:include page="/jsp/footer.jsp" />
