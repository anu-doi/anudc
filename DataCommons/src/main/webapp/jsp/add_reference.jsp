<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<html>
	<head>
		<link rel="stylesheet" type="text/css" href="<c:url value='/css/linkItem.css' />" />
		<script type="text/javascript" src="<c:url value='/js/linkItem.js' />"></script>
	</head>
	<body>
		<p><input type="button" id="itemLinkButton" name="itemLinkButton" value="Link to Item" /></p>
		<div id="popupLink">
			<a id="popupLinkClose">x</a>
			<h1>Add Reference</h1>
			
			<form id="formAddLink" method="post" onsubmit="return false;"  action="">
				<p><label for="txtType">Link Type</label><input type="text" id="txtType" name="txtType" /></p>
				<p><label for="txtItem">Item Id</label><input type="text" id="txtItem" name="txtItem" /></p>
				<p><input id="btnAddLink" type="submit" value="Submit" /></p>
			</form>
		</div>
		<div id="backgroundPopup"></div>
	</body>
</html>