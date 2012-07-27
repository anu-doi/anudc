<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<anu:header id="1998" title="Search" description="description" subject="subject" respOfficer="Doug Moncur" respOfficerContact="mailto:doug.moncur@anu.edu.au"
	ssl="true">
	<!-- Possible bug in the ANU taglib. The following CSS should not be referenced here. Should be referenced in the taglib. -->
	<link href="http://styles.anu.edu.au/_anu/3/style/anu-forms.css" rel="stylesheet" type="text/css" />
</anu:header>

<jsp:include page="/jsp/header.jsp" />

<anu:content layout="doublewide" title="Review Lists Available">
	<a href='<c:url value="/rest/ready/list/rejected" />'>Rejected</a><br />
	<a href='<c:url value="/rest/ready/list/review" />'>Ready for Review</a><br />
	<a href='<c:url value="/rest/ready/list/publish" />'>Ready for Publish</a><br />
	
</anu:content>

<jsp:include page="/jsp/footer.jsp" />