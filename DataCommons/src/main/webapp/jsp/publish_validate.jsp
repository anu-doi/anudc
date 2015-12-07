<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<anu:header id="1998" title="Search" description="description" subject="subject" respOfficer="Doug Moncur" respOfficerContact="mailto:doug.moncur@anu.edu.au"
	ssl="true">

</anu:header>

<jsp:include page="/jsp/header.jsp" />

<anu:content layout="doublewide">
	<a href='<c:url value="/rest/publish/validate/multiple" />'>Validate Multiple Records</a><br />
	<a href='<c:url value="/rest/publish/multiple" />'>Publish Multiple Records</a><br />
</anu:content>

<jsp:include page="/jsp/footer.jsp" />