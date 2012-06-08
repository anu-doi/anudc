<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<anu:header id="1998" title="Welcome" description="description" subject="subject" respOfficer="Doug Moncur" respOfficerContact="mailto:doug.moncur@anu.edu.au"
	ssl="true">
	<!-- Possible bug in the ANU taglib. The following CSS should not be referenced here. Should be referenced in the taglib. -->
	<link href="http://styles.anu.edu.au/_anu/3/style/anu-forms.css" rel="stylesheet" type="text/css" />
</anu:header>

<jsp:include page="/jsp/header.jsp" />

<anu:content layout="doublenarrow" title="ANU Data Commons">
	<p>Welcome to the ANU Data Commons.</p>
	<p>This project will allow people to add information about their datasets, catalogues etc.</p>
	<p><jsp:include page="searchbox.jsp"></jsp:include> </p>
	<sec:authorize access="hasRole('ROLE_ANU_USER')">
		
	<p>Create new object: <a href="<c:url value='/rest/list/template' />">New</a></p>
	</sec:authorize>
			
</anu:content>

<!-- Section for changelogs, updates, news and announcements etc. for users to see. -->
<anu:content layout="narrow">
	<anu:boxheader text="Updates" />
	<anu:box style="solid">Welcome to the Closed Beta of ANU Data Commons</anu:box>
</anu:content>

<jsp:include page="/jsp/footer.jsp" />
