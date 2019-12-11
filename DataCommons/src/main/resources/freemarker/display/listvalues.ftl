<#macro listvalues attr values>
	<#list values as element>
		<#if attr.selectCode?has_content>
			<#assign optionValue=options(attr.selectCode, element.value)>
			${optionValue.description}<#if element?has_next>; </#if>
		<#elseif attr.fieldType.name=='Table'>
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
		<#elseif attr.fieldType.name='Group'>
			<#assign group=groups(element.value)>
			${group.group_name}
		<#else>
			${element.value}<#if element?has_next>; </#if>
		</#if>
	</#list>
</#macro>