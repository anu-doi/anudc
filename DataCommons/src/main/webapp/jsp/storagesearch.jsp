<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>

<anu:header id="1998" title="Storage Search" description="DESCRIPTION" subject="SUBJECT" respOfficer="ANU Library" respOfficerContact="mailto:repository.admin@anu.edu.au" ssl="true">

	<link href="<c:url value='/css/ajaxsolr.css' />" rel="stylesheet" type="text/css"></link>
	<script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.8.24/jquery-ui.min.js"></script>
	<link rel="stylesheet" href="//ajax.googleapis.com/ajax/libs/jqueryui/1.8.24/themes/smoothness/jquery-ui.css">
	<script type="text/javascript" src="<c:url value='/js/solrjs/Core.js' />"></script>
	<script type="text/javascript" src="<c:url value='/js/solrjs/AbstractManager.js' />"></script>
	<script type="text/javascript" src="<c:url value='/js/solrjs/Manager.jquery.js' />"></script>
	<script type="text/javascript" src="<c:url value='/js/solrjs/Parameter.js' />"></script>
	<script type="text/javascript" src="<c:url value='/js/solrjs/ParameterStore.js' />"></script>
	<script type="text/javascript" src="<c:url value='/js/solrjs/AbstractWidget.js' />"></script>
	<script type="text/javascript" src="<c:url value='/js/solrjs/ResultWidget.js' />"></script>
	<script type="text/javascript" src="<c:url value='/js/solrjs/PagerWidget.js' />"></script>
	<script type="text/javascript" src="<c:url value='/js/solrjs/AbstractTextWidget.js' />"></script>
	<script type="text/javascript" src="<c:url value='/js/solrjs/AbstractFacetWidget.js' />"></script>
	<script type="text/javascript" src="<c:url value='/js/solrjs/TagcloudWidget.js' />"></script>
	<script type="text/javascript" src="<c:url value='/js/solrjs/AutocompleteWidget.js' />"></script>
	<script type="text/javascript" src="<c:url value='/js/solrjs/CurrentSearchWidget.js' />"></script>
	<script type="text/javascript" src="<c:url value='/js/solrjs/init.js' />"></script>
	<script type="text/javascript">
		solrUrl = "./";
	</script>
</anu:header>

<jsp:include page="/jsp/header.jsp" />

<anu:content layout="narrow" title="Search">
	<div class="left">
		<h4>Current Selection</h4>
		<ul id="selection"></ul>

		<h4>Search</h4>
		<span id="search_help">(press ESC to close suggestions)</span>
		<div id="search">
			<input type="text" id="query" name="query" autocomplete="off" size="30" />
		</div>

		<div class="w-narrow margintop nomarginbottom nomarginleft">
			<div class="box-header"><h4>Titles</h4></div>
			<div id="title_str" class="box-solid"></div>
		</div>

		<div class="w-narrow nomarginbottom">
			<div class="box-header"><h4>Authors</h4></div>
			<div id="author_str" class="box-solid"></div>
		</div>
		
		<div class="w-narrow nomarginbottom">
			<div class="box-header"><h4>File Extensions</h4></div>
			<div id="ext" class="box-solid"></div>
		</div>
	</div>
</anu:content>

<anu:content layout="doublenarrow" title="Results">
	<div id="result">
		<div id="navigation">
			<p id="pager-header" class="msg-info"></p>
		</div>
		<div id="docs"></div>
		<ul id="pager"></ul>
	</div>
</anu:content>

<jsp:include page="/jsp/footer.jsp" />