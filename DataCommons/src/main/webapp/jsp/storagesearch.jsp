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

<anu:content layout="full">
<div class="clear box20 bg-uni25 bdr-top-solid bdr-white bdr-medium nomargin nomarginbottom">
	<div class="bigsearch nopadtop padbottom">
		<div id="search">
			<input id="query" class="ui-autocomplete-input" type="text" name="query" autocomplete="off" role="textbox" aria-autocomplete="list" aria-haspopup="true" />
		</div>
	</div>
</div>
</anu:content>

<anu:content layout="doublewide" title="Search results">
	<div id="result">
		<div id="navigation">
			<p id="pager-header" class="msg-info"></p>
		</div>
		<div id="docs"></div>
		<ul id="pager"></ul>
	</div>
</anu:content>

<anu:content layout="narrow">
	<div>
		<h4>Current Selection</h4>
		<ul id="selection"></ul>
	</div>

	<anu:boxheader text="Titles"/>
	<anu:box style="solid">
		<div id="title_str"></div>
	</anu:box>
	
	<anu:boxheader text="Authors"/>
	<anu:box style="solid">
		<div id="author_str"></div>
	</anu:box>
	
	<anu:boxheader text="File Extensions"/>
	<anu:box style="solid">
		<div id="ext"></div>
	</anu:box>
</anu:content>

<jsp:include page="/jsp/footer.jsp" />