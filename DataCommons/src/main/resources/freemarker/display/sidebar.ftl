
<div class="col-sm-3">
<#assign canEdit=security.checkPermission(2)>
<#if tmplt.entityType.name == 'collection'>
<div class="bg-tint border-top border-bottom border-primary mb-3 link-underline">
	<#assign canView=security.checkPermission(1)>
	<#assign filesPublic=item.isFilesPublic()>
	<#if canView || item.filesPublic>
	<p><a href="/DataCommons/rest/records/${item.object_id}/data/" class="text-link"><#if canEdit>Upload/</#if>Download data files</a></p>
	<#else>
	<p><a href="/DataCommons/rest/collreq?pid=${item.object_id}" class="text-link">Request data files</a></p>
	</#if>
	<#if rdi??>
	<p>Number of files: ${rdi.recordNumFiles}</p>
	<p>Size: ${rdi.recordFriendlySize}</p>
	<#else>
	<p>No files in collection</p>
	</#if>
	<p>Identifier: ${item.object_id}</p>
</div>
</#if>
<#if canEdit && created??>
<div class="border-top border-bottom border-primary mb-3">
First Created: ${created}
</div>
</#if>
<div class="border-top border-bottom border-primary mb-3">
	<#if item.published!false>
	Status: Published<br/>
	<#if firstPublished?? && canEdit>
	First Published: ${firstPublished} <br/>
	</#if>
	<#if lastPublished?? && canEdit>
	Last Published: ${lastPublished} <br/>
	</#if>
	Published to:
	<ul id="published-list" class="list-dash">
	<#list item.publishedLocations as location>
		<li>${location.name}</li>
	</#list>
	</ul>
	<#else>
	Status: Unpublished
	</#if>
</div>

<#if security.getUsername()??>
<div class="mb-3">
	<#include "buttons.ftl" />
</div>
</#if>
<div class="border-top border-bottom border-primary mb-3">
	Related items
	<ul class="list-unstyled link-underline">
	<#list links as link>
		<#if link.fields['item'].value?starts_with("info:fedora/")>
		<li>${link.fields['predicate'].value[26..]}:<br/><a href="/DataCommons/rest/display/${link.fields['item'].value[12..]}" class="text-link">${link.fields['title'].value}</a> [${link.fields['item'].value[12..]}]</li>
		<#else>
		<li>${link.fields['predicate'].value[26..]}:<br/>${link.fields['item'].value}</li>
		</#if>
	</#list>
	</ul>
</div>
</div>
</div>
