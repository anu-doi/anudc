<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<anu:header id="1998" title="Login" description="description" subject="subject" respOfficer="Doug Moncur" respOfficerContact="mailto:doug.moncur@anu.edu.au"
	ssl="true">
	<script type="text/javascript" src="<c:url value='/js/global.js' />"></script>
	<script type="text/javascript" src="<c:url value='/js/aaf.js' />"></script>

</anu:header>

<jsp:include page="/jsp/header.jsp" />

<anu:content layout="doublewide" extraClass="nopadbottom" title="Select Login Method">

	<p class="center"><a href="http://www.aaf.edu.au"><img alt="AAF Logo" src='<c:url value="/images/aaf_logo.png" />'></a></p>
	<p class="text-center bg-uni50 padtop padbottom large">
		<strong>
			<a href='<c:url value="/login?method=aaf" />'>
				Login using Australian Access Federation (AAF) credentials
			</a>
		</strong>
	</p>
	<p class="right"><a href='<c:url value="/login?method=registered" />'>Alternative Login</a></p>
</anu:content>

<anu:content layout="doublewide" extraClass="nopadbottom">
	<p>
		Note: The Australian Access Federation (AAF) is a federated login utilized by research institutions in Australia.
		If you belong to one of the member institutions please login via AAF, otherwise use the alternative login.
	</p>
	<p><a href="#" id="aaflist" onclick="return false;">Click to view a list of AAF member institutions</a></p>
	<div id="idplist"></div>
</anu:content>

<jsp:include page="/jsp/footer.jsp" />
