<#import "../layout/common.ftl" as c/>
<@c.page title="Administration">
<h2>Review Functions</h2>
<ul>
	<li><a href="/DataCommons/rest/ready/list/rejected">More Work Required</a></li>
	<li><a href="/DataCommons/rest/ready/list/review">Ready for Review</a></li>
	<li><a href="/DataCommons/rest/ready/list/publish">Ready for Publish</a></li>
</ul>
<h2>Validate/Publish</h2>
<ul>
	<li><a href="/DataCommons/rest/publish/validate/multiple">Validate Multiple Records</a></li>
	<li><a href="/DataCommons/rest/publish/multiple">Publish Multiple Records</a></li>
</ul>
<h2>Administration Functions</h2>
<ul>
	<li><a href="/DataCommons/rest/admin/domains">Domain Administration</a></li>
	<li><a href="/DataCommons/rest/admin/groups">Group Administration</a></li>
	<li><a href="/DataCommons/rest/user/permissions">User Administration</a></li>
	<li><a href="/DataCommons/rest/search/admin">Update Index</a></li>
	<li><a href="/DataCommons/rest/reload">Reload</a></li>
	<li><a href="/DataCommons/rest/report">Report</a></li>
</ul>
</@c.page>