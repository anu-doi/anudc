<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<script type="text/javascript" src="<%=request.getContextPath()%>/js/global.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/login.js"></script>
<title>Login</title>
</head>
<body>
	<h1>Login</h1>
	<form method="post" action="<%=request.getContextPath()%>/login/login.do">
		<table>
			<tr>
				<td><label for="idUsername">Username: </label></td>
				<td><input type="text" name="username" id="idUsername" onblur="getPersonName(this.value)" /> </td>
			</tr>
			<tr>
				<td><label for="idPassword">Password: </label></td>
				<td><input type="password" name="password" /></td>
			</tr>
			<tr>
				<td></td>
				<td><input type="submit" value="Login" />&nbsp;&nbsp;<input type="reset" value="Reset" /></td>
			</tr>
		</table>
	</form>
</body>
</html>