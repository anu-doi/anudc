<#import "../layout/common.ftl" as l/>
<#import "controls.ftl" as c />

<@l.page title="New ${tmplt.name}">
<form id="form" method="post">
<div class="float-right">
	<input class="btn btn-outline-secondary" type="submit" value="Submit"/>
</div>
<ul class="nav nav-tabs" id="form-tab" role="tablist">
<#list tmplt.templateTabs as tab>
	<li class="nav-item"><a class="nav-link <#if tab?index == 0>active</#if>" id="nav-${tab.name}-tab" data-toggle="tab" href="#nav-${tab.name}" role="tab" aria-controls="nav-${tab.name}" aria-selected="true">${tab.label}</a></li>
</#list>
</ul>
<#assign currentTab=tmplt.templateTabs?first>
<div class="tab-content" id="nav-tabContent">
<div class="tab-pane fade show active" id="nav-${currentTab.name}" role="tabpanel" aria-labelledby="nav-${currentTab.name}-tab">
<#list tmplt.templateAttributes as attr>
<#if attr.tab != currentTab>
	<#assign currentTab=attr.tab>
</div>
<div class="tab-pane fade show" id="nav-${currentTab.name}" role="tabpanel" aria-labelledby="nav-${currentTab.name}-tab">
</#if>
	<#-- <div>${attr.name} - ${attr.label} - ${attr.fieldType.name} - Tab ${attr.tab.name}</div> -->
	<div class="form-group">
		<label class="font-weight-bold<#if attr.required> required</#if>" for="${attr.name}">${attr.label}</label>
		<#if attr.tooltip??>
			<div><small id="${attr.name}.tooltip">${attr.tooltip}</small></div>
		</#if>
		<@c.renderField attr data.getElementByName(attr.name)/>
	</div>
</#list>
</div>
</div>
</form>
</@l.page>