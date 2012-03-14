<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<body>
<div align="right">
<c:choose>
	<c:when test="${empty user}">
		<a href="<%=request.getContextPath()%>/login/">Login</a>
	</c:when>
	<c:otherwise>
		<a href="<%=request.getContextPath()%>/login/logout.jsp">Logout</a>
	</c:otherwise>
</c:choose>
</div>
<h2>Welcome Page</h2>
<p>
<p>Hello, <c:out value="${user.givenName}">Guest</c:out></p>
</body>
</html>
