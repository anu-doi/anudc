<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<anu:header id="1998" title="Import" description="DESCRIPTION" subject="SUBJECT" respOfficer="Doug Moncur" respOfficerContact="doug.moncur@anu.edu.au" ssl="true">

	<script type="text/javascript" src="<c:url value='/js/collreq.js' />"></script>
</anu:header>

<jsp:include page="/jsp/header.jsp" />

<anu:content layout="full" title="Import your data">
	<!-- Create a form for each metadata provider. -->
	<c:forEach var="iProvider" items="${it.providers}">
		<form class="anuform">
			<fieldset>
				<legend><c:out value="${iProvider.friendlyName}" /></legend>
					<!-- Create a textfield for each required parameter for the metadata provider. -->
					<c:forEach var="iParam" items="${iProvider.requiredParams}">
						<p>
							<label><c:out value="${iParam.friendlyName}" /></label>
							<input type="text" class="text tfull" size="60" name="${iParam.name}">
						</p>
					</c:forEach>
				<input type="hidden" name="provider" value="${iProvider.fqClassName}">
				<p class="text-center"><input type="submit" value="Submit"></p>
			</fieldset>
		</form>
	</c:forEach>
</anu:content>

<jsp:include page="/jsp/footer.jsp" />