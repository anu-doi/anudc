<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="anu" uri="http://www.anu.edu.au/taglib"%>

<anu:header id="1998" title="Storage Search" description="DESCRIPTION" subject="SUBJECT" respOfficer="ANU Library" respOfficerContact="mailto:repository.admin@anu.edu.au" ssl="true">

	<link href="<c:url value='/css/ajaxsolr.css' />" rel="stylesheet" type="text/css"></link>
	<link href="<c:url value='/css/default.css' />" rel="stylesheet" type="text/css"></link>
	<script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.12.1/jquery-ui.min.js"></script>
	<link rel="stylesheet" href="//ajax.googleapis.com/ajax/libs/jqueryui/1.12.1/themes/smoothness/jquery-ui.css">
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
	<script type="text/javascript" src="<c:url value='/js/storage-search.js' />"></script>
</anu:header>

<jsp:include page="/jsp/header.jsp" />

<anu:container type="container">
<anu:content layout="full">
<anu:container type="container">
<div class="bg-tint p-1 rounded">
<div class="welcome-search-box overlap-child">
		<div id="search">
			<input id="query" aria-label="search data" class="ui-autocomplete-input text w70" type="text" name="query" autocomplete="off" role="textbox" aria-autocomplete="list" aria-haspopup="true" style="border: 3px solid white;padding: 12px;border-radius: 4px;"/>
		</div>
	</div>
	<div>Include: <input id="show-titles" name="show-titles" type="checkbox" data-section="title-section" class="show-section" checked aria-label="show titles"/> Titles <input id="show-authors" name="show-authors" type="checkbox" aria-label="show authors" data-section="author-section" class="show-section" checked /> Authors <input id="show-extension" name="show-extension" aria-label="show extensions" type="checkbox" data-section="extension-section" class="show-section" checked /> File extensions</div>
</div>
</anu:container>
</anu:content>

<anu:container type="container">
<div class="row">
<anu:content layout="doublewide" title="Search results">
	<div id="result">
		<div id="navigation">
			<p id="pager-header" class="msg-info"></p>
		</div>
		<div id="docs"></div>
		<ul id="pager"></ul>
	</div>
</anu:content>

<anu:content layout="one-third">
	<div>
		<h4>Current search options</h4>
		<ul id="selection" class="nobullet"></ul>
	</div>

	<div id="title-section">
	<anu:boxheader borderColour="tint" text="Titles"/>
	<anu:box style="bdr" styleColour="gold">
		<div id="title_str"></div>
	</anu:box>
	</div>
	
	<div id="author-section">
	<anu:boxheader borderColour="tint" text="Authors"/>
	<anu:box style="bdr" styleColour="gold">
		<div id="author_str"></div>
	</anu:box>
	</div>
	
	<div id="extension-section">
	<anu:boxheader  borderColour="tint"  text="File Extensions"/>
	<anu:box style="bdr" styleColour="gold">
		<div id="ext"></div>
	</anu:box>
	</div>
</anu:content>
</div>
</anu:container>
</anu:container>
<jsp:include page="/jsp/footer.jsp" />