<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<anu:header id="1998" title="Page" description="description" subject="subject" respOfficer="Doug Moncur" respOfficerContact="mailto:doug.moncur@anu.edu.au" ssl="true">
	<link rel="stylesheet" type="text/css" href="<c:url value='/css/default.css' />" />
	<script type="text/javascript" src="<c:url value='/js/global.js' />"></script>
	<script type="text/javascript" src="<c:url value='/js/jquery.validate.min.js' />"></script>
</anu:header>

<jsp:include page="header.jsp" />

<anu:content layout="doublenarrow">
	<h1>Publishing Page</h1>
	<form id="form" method="post" action="">
		<c:forEach items="${it.publishLocations}" var="publishLocation">
			<input type="checkbox" name="publish" value="${publishLocation.id}" />${publishLocation.code} - ${publishLocation.name} <br />
		</c:forEach>
		<input id="publishSubmit" type="submit" value="Publish" />
	</form>
</anu:content>


<jsp:include page="footer.jsp" />