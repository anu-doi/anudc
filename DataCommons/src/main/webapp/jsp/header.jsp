<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>

<anu:body />
<anu:banner id="" ssl="true" primaryTitle="ANU Data Commons" secondaryTitle="Division of Information" primaryTitleUrl="/" secondaryTitleUrl="/" />

<anu:tabnav>
	<anu:tabmeta>Welcome <c:out value="${sessionScope.user.givenName}">Guest</c:out>.
	<c:choose>
			<c:when test="${empty sessionScope.user}">
				<a href="<c:url value='/jsp/login.jsp' />">Login</a>
			</c:when>
			<c:otherwise>
				<a href="<c:url value='/login/login.do'><c:param name="cmd" value="logout" /></c:url>">Logout</a>
			</c:otherwise>
		</c:choose>
	</anu:tabmeta>
</anu:tabnav>

<anu:menu showSearch="true" id="1108" shortTitle="Data Commons" ssl="true">
	<anu:submenu title="Data Commons">
		<li><a href="<c:url value='/jsp/welcome.jsp' />">Home</a></li>
		<li><a href="<c:url value='/jsp/login.jsp' />">Login</a></li>
		<li><a href="<c:url value='/jsp/search.jsp' />">Search</a></li>
	</anu:submenu>

	<anu:submenu title="ANU">
		<li><a href="#">Item</a></li>
		<li><a href="#">Item 2</a></li>
		<li><a href="#">Item 3</a></li>
	</anu:submenu>
</anu:menu>
