<#import "../layout/common.ftl" as c/>
<@c.page title="Search Data Files">
<div class="container bg-uni25 pb-3 pt-3">
	<div>
		<fmt:bundle basename='global'>
			<fmt:message var="searchItemsPerPage" key='search.resultsPerPage' />
		</fmt:bundle>
		<form name="" action="" method="" class="" >
			<div class="form-row">
			<div class="col">
			<input id="query" class="ui-autocomplete-input form-control" type="text" name="query" autocomplete="off" role="textbox" aria-autocomplete="list" aria-haspopup="true" />
			</div>
			<div class="form-row">
			<button type="submit" class="btn btn-primary">Go</button>
			</div>
			</div>
		</form>
	</div>
</div>
<div class="container">
	<div class="row">
		<div class="col-9">
			<h2>Results</h2>
			<div id="result">
				<div id="navigation">
					<p id="pager-header" class="msg-info"></p>
				</div>
				<div id="docs"></div>
				<ul id="pager" class="list-inline"></ul>
			</div>
		</div>
		<div class="col-3">
			<div>
				<h4>Current Selection</h4>
				<ul id="selection"></ul>
			</div>
			<div class="card mb-3">
				<h5 class="card-header bg-uni25">Titles</h5>
				<div id="title_str" class="card-body">
				</div>
			</div>
			<div class="card mb-3">
				<h5 class="card-header bg-uni25">Authors</h5>
				<div id="author_str" class="card-body">
				</div>
			</div>
			<div class="card mb-3">
				<h5 class="card-header bg-uni25">File extensions</h5>
				<div id="ext" class="card-body">
				</div>
			</div>
		</div>
	</div>
</div>
	<script type="text/javascript" src="/DataCommons/js/solrjs/Core.js"></script>
	<script type="text/javascript" src="/DataCommons/js/solrjs/AbstractManager.js"></script>
	<script type="text/javascript" src="/DataCommons/js/solrjs/Manager.jquery.js"></script>
	<script type="text/javascript" src="/DataCommons/js/solrjs/Parameter.js"></script>
	<script type="text/javascript" src="/DataCommons/js/solrjs/ParameterStore.js"></script>
	<script type="text/javascript" src="/DataCommons/js/solrjs/AbstractWidget.js"></script>
	<script type="text/javascript" src="/DataCommons/js/solrjs/ResultWidget.js"></script>
	<script type="text/javascript" src="/DataCommons/js/solrjs/PagerWidget.js"></script>
	<script type="text/javascript" src="/DataCommons/js/solrjs/AbstractTextWidget.js"></script>
	<script type="text/javascript" src="/DataCommons/js/solrjs/AbstractFacetWidget.js"></script>
	<script type="text/javascript" src="/DataCommons/js/solrjs/TagcloudWidget.js"></script>
	<script type="text/javascript" src="/DataCommons/js/solrjs/AutocompleteWidget.js"></script>
	<script type="text/javascript" src="/DataCommons/js/solrjs/CurrentSearchWidget.js"></script>
	<script type="text/javascript" src="/DataCommons/js/solrjs/init.js"></script>
	<script type="text/javascript">
		solrUrl = "./";
	</script>
</@c.page>