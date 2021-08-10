<#import "../layout/common.ftl" as c/>
<#import "listvalues.ftl" as l/>
<@c.page title="${data.getFirstElementByName('name').value}">
<#assign canEdit=security.checkPermission(2)>
<#assign canReview=security.checkPermission(32)>
<#assign canPublish=security.checkPermission(64)>
<#if canEdit || canReview || canPublish>
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
This item is in the status more work required.<br/>Reason: ${item.reviewReject.reason}
</div>
</#if>
</#if>
<#if errormessage??>
<div class="container alert alert-danger" role="alert">
	${errormessage}
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
	<div class="row border-top border-bottom border-black">
		<div role="rowheader" class="col-sm-4 bg-uni25 pt-1 pb-1">${attr.label}</div>
		<div class="col-sm-8 pt-1 pb-1">
			<@l.listvalues attr values />
		</div>
	</div>
	</#if>
	<#if differenceData??>
		<#assign values=differenceData.getElementByName(attrName)>
		<#if values?size != 0>
			<div class="row border-top border-bottom border-black">
				<div role="rowheader" class="col-sm-4 bg-uni25 pt-1 pb-1">${attr.label} Modified</div>
				<div class="col-sm-8 pt-1 pb-1 alert alert-light">
					<@l.listvalues attr values />
				</div>
			</div>
		</#if>
	</#if>
</#list>
</div>
</div>

<#include "sidebar.ftl" />

<#assign itemType=data.getFirstElementByName('type').value>
<input type="hidden" id="itemType" value="${itemType}"/>
</@c.page>