<#import "../layout/common.ftl" as c/>
<@c.page title="${data.getFirstElementByName('name').value}">
<#if item.reviewReady??>
<div class="container alert alert-info">
This item is in the status ready for review
</div>
</#if>
<#if item.publishReady??>
<div class="container alert alert-info">
This item is in the status ready for publish
</div>
</#if>
<#if item.reviewReject??>
<div class="container alert alert-info">
This item is in the status more work required
</div>
</#if>
<div class="container mb-3">
<div class="row">
<div class="col-9">
<#list data.getElementByName('fullDesc') as description>
	<div>${description.value}</div>
</#list>
<div class="mt-3">
<#list tmplt.templateAttributes as attr>
	<#assign attrName=attr.name>
	<#assign values=data.getElementByName(attrName)>
	<#if values?size != 0>
	<div class="row border-top border-bottom border-primary">
		<div class="col-sm-4 bg-uni25 pt-1 pb-1">${attr.label}</div>
		<div class="col-sm-8 pt-1 pb-1">
			<#list values as element>
				<#if attr.selectCode?has_content>
					<#assign optionValue=options(attr.selectCode, element.value)>
					${optionValue.description}<#if element?has_next>; </#if>
				<#elseif attr.fieldType.name=='Table'>
					<span class="alert-danger">aaa</span>
					<div class="border-bottom border-primary">
					<#list attr.columns as column>
						<div>
						<label class="font-weight-bold">${column.label}</label>
						<#list element.childValues as childVal>
							<#if childVal.name == column.name>
								${childVal.value}
							</#if>
						</#list>
						</div>
					</#list>
					</div>
					<#break>
				<#elseif attr.fieldType.name='Group'>
					<#assign group=groups(element.value)>
					${group.group_name}
					<#break>
				<#else>
					${element.value}<#if element?has_next>; </#if>
				</#if>
			</#list>
		</div>
	</div>
	</#if>
</#list>
</div>
</div>
<div class="col-sm-3">
<#if tmplt.entityType.name == 'collection'>
<div class="bg-uni25 border-top border-bottom border-primary mb-3">
	<p><a href="/DataCommons/rest/records/${item.object_id}/data/" class="text-black">Download data files</a></p>
	<#if rdi??>
	<p>Number of files: ${rdi.recordNumFiles}</p>
	<p>Size: ${rdi.recordFriendlySize}</p>
	<#else>
	<p>No files in collection</p>
	</#if>
	<p>Identifier: ${item.object_id}</p>
</div>
</#if>
<div class="border-top border-bottom border-primary mb-3">
	<#if item.published!false>
	Status: Published
	<ul class="list-unstyled">
	<#list item.publishedLocations as location>
		<li>${location.name}</li>
	</#list>
	</ul>
	<#else>
	Status: Unpublished
	</#if>
</div>
<div class="border-top border-bottom border-primary mb-3">
	<#include "buttons.ftl" />
</div>
<div class="border-top border-bottom border-primary mb-3">
	Related items
	<ul class="list-unstyled">
	<#list links as link>
		<#if link.fields['item'].value?starts_with("info:fedora/")>
		<li>${link.fields['predicate'].value[26..]}:<br/><a href="/DataCommons/rest/display/${link.fields['item'].value[12..]}" class="text-secondary">${link.fields['title'].value}</a></li>
		<#else>
		${link.fields['title'].value}
		</#if>
	</#list>
	</ul>
</div>
</div>
</div>

<#assign itemType=data.getFirstElementByName('type').value>
<input type="hidden" id="itemType" value="${itemType}"/>
</@c.page>