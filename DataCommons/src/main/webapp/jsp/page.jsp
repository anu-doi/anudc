<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<anu:header id="1998" title="Page" description="description" subject="subject" respOfficer="Doug Moncur" respOfficerContact="mailto:doug.moncur@anu.edu.au" ssl="true">
	<script type="text/javascript" src="<c:url value='/js/global.js' />"></script>
	<script type="text/javascript" src="<c:url value='/js/login.js' />"></script>

</anu:header>

<jsp:include page="header.jsp" />
<jsp:include page="body.jsp" />
<c:import url="${it.sidepage}" />
<jsp:include page="footer.jsp" />