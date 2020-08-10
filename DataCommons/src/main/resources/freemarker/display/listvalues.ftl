<#macro listvalues attr values>
	<#list values as element>
		<#if attr.selectCode?has_content>
			<#assign optionValue=options(attr.selectCode, element.value)!"">
			<#if optionValue?has_content>${optionValue.description}<#else>${element.value}</#if><#if element?has_next>; </#if>
		<#elseif attr.fieldType.name=='Table'>
			<div class="border-bottom border-primary">
			<#list attr.columns as column>
				<div>
				<label class="font-weight-bold">${column.label}</label>
				<#list element.childValues as childVal>
					<#if childVal.name == column.name>
						<#if column.extra?has_content && column.extra?contains("link")><a href="${childVal.value}" class="text-link"></#if>${childVal.value}<#if column.extra?has_content && column.extra?contains("link")></a></#if>
					</#if>
				</#list>
				</div>
			</#list>
			</div>
		<#elseif attr.fieldType.name='Group'>
			<#assign group=groups(element.value)>
			${group.group_name}
		<#else>
			<#if attr.extra?has_content && attr.extra?contains("link")><a href="${element.value}" class="text-link"></#if>${element.value}<#if attr.extra?has_content && attr.extra?contains("link")></a></#if><#if element?has_next>; </#if>
		</#if>
	</#list>
</#macro>