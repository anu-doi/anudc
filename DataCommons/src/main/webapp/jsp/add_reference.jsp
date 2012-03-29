<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<html>
	<body>
		Please enter the reference to add
		<form method="post" onsubmit="return validateForm()"  action="/DataCommons/rest/display/addLink?item=test:57">
			<p><label for="txtType">Link Type</label><input type="text" name="txtType" /></p>
			<p><label for="txtItem">Item Id</label><input type="text" name="txtItem" /></p>
			<p><input type="submit" value="Submit" /></p>
		</form>
	</body>
</html>