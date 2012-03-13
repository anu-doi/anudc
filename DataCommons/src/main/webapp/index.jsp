<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="au.edu.anu.datacommons.ldap.LdapPerson" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<body>
<h2>Hello World!</h2>
<%
	if (session.getAttribute("user") != null)
	{
		String displayName = ((LdapPerson) session.getAttribute("user")).getDisplayName();
%>
<p>Hello, <%= displayName %></p>
<%
	}
%>
</body>
</html>
