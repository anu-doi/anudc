<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>

<anu:header id="1998" title="Welcome" description="description" subject="subject" respOfficer="Doug Moncur" respOfficerContact="mailto:doug.moncur@anu.edu.au"
	ssl="true">
</anu:header>

<jsp:include page="/jsp/header.jsp" />

<anu:content layout="doublenarrow" title="ANU Data Commons">
	<p>Welcome to the ANU Data Commons.</p>
	<p>This project will allow people to add information about their datasets, catalogues etc.</p>
</anu:content>

<anu:content layout="narrow">
	<anu:boxheader text="Updates" />
	<anu:box style="solid">Updates, changelog etc.</anu:box>
</anu:content>
<jsp:include page="/jsp/footer.jsp" />