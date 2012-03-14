<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<body>
<div class="menu">
	<p>ANU Data Commons</p>
	<c:if test="${sessionScope.user} != null">Welcome ${sessionScope.user}</c:if>
	<ul>
		<c:choose>
			<c:when test="${sessionScope.user} != null">
				<li>Welcome ${sessionScope.user}</li>
				<li>Logout</li>
			</c:when>
			<c:otherwise>
				<li>Login</li>
			</c:otherwise>
		</c:choose>
		
		<li><a href="/DataCommons/index.jsp">Home</a></li>
		<li><a href="/DataCommons/jsp/search.jsp">Search</a></li>
	</ul>
</div>
</body>
</html>