<#macro renderField templateAttribute dataValues={}>
	<#switch templateAttribute.fieldType.name>
		<#case "TextField">
			<#if templateAttribute.multiValued>
				<#if dataValues?has_content>
					<#list dataValues as val>
					<div class="input-group">
					<input type="input" class="form-control" id="${templateAttribute.name}" name="${templateAttribute.name}" aria-describedby="${templateAttribute.name}.tooltip" value="${val.value?html}"<#if templateAttribute.required> required</#if> />
					<div class="input-group-append"><button type="button" class="btn btn-danger btn-remove">-</button></div>
					</div>
					</#list>
				<#else>
					<div class="input-group">
					<input type="input" class="form-control" id="${templateAttribute.name}" name="${templateAttribute.name}" aria-describedby="${templateAttribute.name}.tooltip"<#if templateAttribute.required> required</#if> />
					<div class="input-group-append"><button type="button" class="btn btn-danger btn-remove">-</button></div>
					</div>
				</#if>
				<div><button type="button" class="btn btn-primary btn-add">+</button></div>
			<#else>
				<input type="input" class="form-control" id="${templateAttribute.name}" name="${templateAttribute.name}" aria-describedby="${templateAttribute.name}.tooltip"<#if templateAttribute.required> required</#if> <#if dataValues?has_content>value="${dataValues?first.value?html}"</#if> <#if templateAttribute.name == "type">readonly="readonly"</#if>/>
			</#if>
			<#break>
		<#case "TextArea">
			<#if templateAttribute.multiValued>
				<div class="input-group">
				<#if dataValues?has_content>
					<#list dataValues as val>
						<div class="input-group">
							<textarea class="form-control" id="${templateAttribute.name}" name="${templateAttribute.name}" aria-describedby="${templateAttribute.name}.tooltip" rows="3" <#if templateAttribute.required>required</#if>${val.value}</textarea>
						</div>
						<div class="input-group-append"><button type="button" class="btn btn-danger btn-remove">-</button></div>
					</#list>
				<#else>
				<textarea class="form-control" id="${templateAttribute.name}" name="${templateAttribute.name}" aria-describedby="${templateAttribute.name}.tooltip" rows="3" <#if templateAttribute.required>required</#if>><#if dataValues?has_content>${dataValues?first.value}</#if></textarea>
				<div class="input-group-append"><button type="button" class="btn btn-danger btn-remove">-</button></div>
				</#if>
				</div>
				<div><button type="button" class="btn btn-primary btn-add">+</button></div>
			<#else>
				<textarea class="form-control" id="${templateAttribute.name}" name="${templateAttribute.name}" aria-describedby="${templateAttribute.name}.tooltip" rows="3" <#if templateAttribute.required>required</#if>><#if dataValues?has_content>${dataValues?first.value}</#if></textarea>
			</#if>
			<#break>
		<#case "RadioButton">
			<#assign radioOptions=options(templateAttribute.selectCode)>
			<#list radioOptions as value>
				<#assign found = "false">
				<#if dataValues?has_content && dataValues?first.value == value.id.code>
				        <#assign found = "true">
				</#if>
				<#if value.deprecated?c != "true" || found = "true"><div><input value="${value.id.code}" name="${templateAttribute.name}" type="radio"<#if templateAttribute.required> required</#if> aria-label="${value.description}" <#if found = "true">checked</#if>> ${value.description}</div></#if>
			</#list>
			<#break>
		<#case "ComboBox">
			<#assign comboOptions=options(templateAttribute.selectCode)>
			<#if templateAttribute.multiValued>
				<select id="${templateAttribute.name}" name="${templateAttribute.name}" multiple="multiple"<#if templateAttribute.required> required</#if>>
				<#list comboOptions as value>
					<#assign found = "false">
					<#list dataValues as dataItem>
					<#if dataItem.value == value.id.code><#assign found = "true"></#if>
					</#list>
					<#if value.deprecated?c != "true" || found = "true"><option value="${value.id.code}" <#if found = "true">selected</#if>>${value.description}</option></#if>
				</#list>
				</select>
				<script type="text/javascript">
					$("#${templateAttribute.name}").searchableOptionList();
				</script>
			<#else>
				<select class="form-control" name="${templateAttribute.name}" <#if templateAttribute.required>required</#if>>
					<option value="">-- No Value Selected --</option>
				<#list comboOptions as value>
					<#assign found = "false">
					<#if dataValues?has_content && dataValues?first.value == value.id.code>
						<#assign found = "true">
					</#if>
					<#if value.deprecated?c != "true" || found = "true"><option value="${value.id.code}" <#if found = "true">selected</#if>>${value.description}</option></#if>
				</#list>
				</select>
			</#if>
			<#break>
		<#case "Group">
			<#assign groupOptions=groups()>
			<select class="form-control" name="${templateAttribute.name}" <#if templateAttribute.required>required</#if>>
				<option value="">-- No Value Selected --</option>
			<#list groupOptions as value>
				<option value="${value.id}" <#if dataValues?has_content && dataValues?first.value == value.id?string>selected</#if>>${value.group_name}</option>
			</#list>
			</select>
			<#break>
		<#case "Table">
			<#if dataValues?has_content>
			<@renderTableWithRows templateAttribute dataValues/>
			<#else>
			<@renderTable templateAttribute/>
			</#if>
			<#break>
		<#default>
			<div class="alert alert-danger">Field type '${templateAttribute.fieldType.name}' not found</div>
	</#switch>
</#macro>

<#macro renderTable templateAttribute>
	<div class="form-row">
	<#list templateAttribute.columns as column>
		<div class="col">${column.label}</div>
	</#list>
	<div class="col-1"></div>
	</div>
	<div class="form-row input-group table-group">
	
	<#list templateAttribute.columns as column>
		<#switch column.fieldType.name>
			<#case "TextField">
				<div class="col"><input type="input" class="form-control" name="${column.name}" aria-label="${column.label}" placeholder="${column.label}" title="${column.label}" value=""/></div>
				<#break>
			<#case "TextArea">
				<div class="col"><textarea type="input" class="form-control" name="${column.name}" aria-label="${column.label}" placeholder="${column.label}" title="${column.label}" value=""></textarea></div>
				<#break>
			<#case "ComboBox">
				<div class="col">
				<#assign comboOptions=options(column.selectCode)>
				<select class="form-control" name="${column.name} title="${column.label}"">
					<option value="">-- No Value Selected --</option>
				<#list comboOptions as value>
					<#if value.deprecated?c != "true"><option value="${value.id.code}">${value.description}</option></#if>
				</#list>
				</select>
				</div>
				<#break>
			<#case "Date">
				<div class="col"><input type="text" pattern="[0-9]{4}(-[0-9]{2}(-[0-9]{2}(T[0-9]{2}:[0-9]{2}:[0-9]{2}Z)?)?)?" class="form-control" name="${column.name}" aria-label="${column.label}" placeholder="${column.label}" title="${column.label}" value=""/></div>
				<#break>
			<#default>
				<div class="col alert alert-danger">Field type '${column.fieldType.name}' not found</div>
		</#switch>
	</#list>
	<div class="col-1"><button class="btn btn-danger btn-remove" type="button">-</button></div>
	</div>
	<div>
		<button class="btn btn-primary btn-add" type="button">+</button>
	</div>
</#macro>

<#macro renderTableWithRows templateAttribute dataValues={}>
	<div class="row">
	<#list templateAttribute.columns as column>
		<div class="col-sm">${column.label}</div>
	</#list>
	<div class="col-1"></div>
	</div>
	<#list dataValues as val>
	<div class="form-row input-group">
	
	<#list templateAttribute.columns as column>
		<#assign childValue=val.getChildElementByName('${column.name}') >
		<#switch column.fieldType.name>
			<#case "TextField">
				<div class="col"><input type="input" class="form-control" name="${column.name}" aria-label="${column.label}" title="${column.label}" value="<#if childValue?has_content>${childValue?first.value?html}</#if>"/></div>
				<#break>
			<#case "TextArea">
				<div class="col"><textarea type="input" class="form-control" name="${column.name}" aria-label="${column.label}" title="${column.label}"><#if childValue?has_content>${childValue?first.value}</#if></textarea></div>
				<#break>
			<#case "ComboBox">
				<div class="col">
				<#assign comboOptions=options(column.selectCode)>
				<select class="form-control" name="${column.name}" title="${column.label}">
					<option value="">-- No Value Selected --</option>
				<#list comboOptions as value>
					<#assign found = "false">
					<#if childValue?has_content && value.id.code = childValue?first.value>
						<#assign found = "true">
					</#if>
					<#if value.deprecated?c != "true" || found = "true"><option value="${value.id.code}" <#if found = "true">selected</#if>>${value.description}</option></#if>
				</#list>
				</select>
				</div>
				<#break>
			<#case "Date">
				<div class="col"><input type="text" pattern="[0-9]{4}(-[0-9]{2}(-[0-9]{2}(T[0-9]{2}:[0-9]{2}:[0-9]{2}Z)?)?)?" class="form-control" name="${column.name}" aria-label="${column.label}" placeholder="${column.label}" title="${column.label}" value="<#if childValue?has_content>${childValue?first.value}</#if>"/></div>
				<#break>
			<#default>
				<div class="col alert alert-danger">Field type '${column.fieldType.name}' not found</div>
		</#switch>
	</#list>
	<div class="col-1"><button class="btn btn-danger btn-remove" type="button">-</button></div>
	</div>
	</#list>
	<div>
		<button class="btn btn-primary btn-add" type="button">+</button>
	</div>
</#macro>