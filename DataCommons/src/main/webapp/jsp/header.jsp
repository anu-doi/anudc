<%@ page import="au.edu.anu.datacommons.ldap.LdapPerson"%>
<%@ page import="au.edu.anu.datacommons.ldap.LdapRequest"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>

<anu:body />
<anu:banner id="" ssl="true" primaryTitle="ANU Data Commons" secondaryTitle="Division of Information" primaryTitleUrl="/" secondaryTitleUrl="/" />

<anu:tabnav>
	<anu:tabmeta>Welcome <c:out value="${pageContext.request.remoteUser}">Guest</c:out>.
	<c:choose>
			<c:when test="${empty pageContext.request.remoteUser}">
				<a
					href="<c:url value='https://login-test.anu.edu.au/login'><c:param name='service' value='http://l3a5006.uds.anu.edu.au:9081${pageContext.request.contextPath}${pageContext.request.servletPath}' /></c:url>">Login</a>
			</c:when>
			<c:otherwise>
				<c:if test="${empty sessionScope.user}">
					<%
						pageContext.getSession().setAttribute("user", new LdapRequest().searchUniId(request.getRemoteUser()));
					%>
				</c:if>
				<c:out value="${sessionScope.user.givenName}" />
				<a href="<c:url value='/login/login.do'><c:param name="cmd" value="logout" /></c:url>">Logout</a>
			</c:otherwise>
		</c:choose>
	</anu:tabmeta>
</anu:tabnav>

<anu:menu showSearch="true" id="1108" shortTitle="Data Commons" ssl="true">
	<anu:submenu title="Data Commons">
		<li><a href="<c:url value='/jsp/welcome.jsp' />">Home</a></li>
		<li><a href="<c:url value='/jsp/search.jsp' />">Search</a></li>
	</anu:submenu>

	<anu:submenu title="ANU">
		<li><a href="#">Item</a></li>
		<li><a href="#">Item 2</a></li>
		<li><a href="#">Item 3</a></li>
	</anu:submenu>
</anu:menu>
