<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<script type="text/javascript" src="<%=request.getContextPath()%>/js/global.js"></script>
<title>Search</title>
</head>
<body>
<h1>Search</h1>
	<div id="divBasicSearch">
		<form name="frmBasicSearch" action="<%=request.getContextPath()%>/search/search.do" method="get">
			<label for="basicSearchTerms">Search for: </label><input type="text" name="terms" id="idBasicSearchTerms" size="30"  />&nbsp;
			<input type="hidden" name="dt" value="on" />
			<input type="hidden" name="format" value="Sparql" />
			<input type="hidden" name="lang" value="sparql" />
			<input type="hidden" name="limit" value="1000" />
			<input type="hidden" name="type" value="tuples" />
			<input type="hidden" name="resultsfmt" value="doc" />
			<input type="submit" value="Search" />
		</form>
	</div>
	<div id="divSearchResults"></div>
</body>
</html>