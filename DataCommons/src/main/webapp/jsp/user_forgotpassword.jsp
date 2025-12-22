<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<anu:header id="1998" title="ANU Data Commons - User Information" description="description" subject="subject" respOfficer="ANU Library" respOfficerContact="mailto:repository.admin@anu.edu.au"
	ssl="true">

	<link rel="stylesheet" type="text/css" href="<c:url value='/css/default.css' />" />
	<script type="text/javascript" src="<c:url value='/js/user.js' />"></script>
</anu:header>

<jsp:include page="/jsp/header.jsp" />

<anu:container type="container">
<anu:content layout="doublewide">
	<c:if test="${not empty it.error}">
		<anu:message type="error">${it.error}</anu:message><br/>
	</c:if>
	<form id="form" class="anuform" method="POST">
	<fieldset>
			<div class="field">
				<label for="email">Email:</label>
				<div class="field-span">
					<input class="text tfull" type="text" id="email" name="email" />
				</div>
			<br/>
			</div>
			<p class="float-end">
			<input class="btn btn-primary " type="submit" value="Submit" />
			</p>
	</fieldset>
	</form>
</anu:content>
</anu:container>
<jsp:include page="/jsp/footer.jsp" />