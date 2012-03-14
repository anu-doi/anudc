<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Logout</title>
</head>
<body>
	<% session.invalidate(); %>
	<p>Logged out. Redirecting in 3 seconds...</p>
	<%
	response.sendRedirect(request.getContextPath() + "/");
	%>
</body>
</html>