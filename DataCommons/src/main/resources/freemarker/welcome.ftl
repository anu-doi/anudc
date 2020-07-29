<#import "layout/common.ftl" as c/>
<@c.page title="Welcome to ANU Data Commons">
<p>Welcome to the ANU Data Commons.</p>
<p>This project will allow people to add information about their datasets, catalogues etc.</p>
<div id="search">
	<#include "search/search-box.ftl"/>
</div>
<div id="divSearchResults">
	<#if resultSet??>
		<hr />
		<h2>Recently Updated Records</h2>
		<#list resultSet.documentList as row>
			<article class="media border-top border-primary">
				<div class="media-body">
					<h5 class="mt-0 text-primary"><a href="/DataCommons/rest/display/${row['id']}?layout=def:display">${row['unpublished.name']}</a></h5>
					<#if row['unpublished.briefDesc']??>
					<p>
						${row['unpublished.briefDesc'][0][0..500]}
					</p>
					</#if>
				</div>
			</article>
		</#list>
		<br/>
		<a href="/DataCommons/rest/search?q=*&limit=5000&filter=team&offset=0&sort=unpublished.lastModified&order=desc">Show All My Records</a>
	</#if>
</div>
</@c.page>