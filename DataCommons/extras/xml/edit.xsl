<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:options="xalan://au.edu.anu.datacommons.xml.transform.SelectExtension">
	<xsl:param name="data" />
	<xsl:param name="fieldName" />
	<xsl:variable name="mData" select="$data" />
	<xsl:template match="/">
		<xsl:if test="$fieldName != ''">
			<xsl:for-each select="template/item[@name=$fieldName]">
				<xsl:value-of select="@label" /> 
				<xsl:if test="tooltip">
					<img src="http://styles.anu.edu.au/_anu/images/icons/silk/information.png" alt="information" title="{tooltip}" />
				</xsl:if>
				<br/><br/>
				<xsl:choose>
					<xsl:when test="@fieldType = 'TextFieldMulti'">
						<xsl:call-template name="TextFieldMulti" />
					</xsl:when>
					<xsl:when test="@fieldType = 'TextField'">
						<xsl:call-template name="TextField">
							<xsl:with-param name="mValue"><xsl:value-of select="$mData/data/*[name() = $fieldName]" /></xsl:with-param>
						</xsl:call-template>
					</xsl:when>
					<xsl:when test="@fieldType = 'TextArea'">
						<xsl:call-template name="TextArea">
							<xsl:with-param name="mValue"><xsl:value-of select="$mData/data/*[name() = $fieldName]" /></xsl:with-param>
						</xsl:call-template>
					</xsl:when>
					<xsl:when test="@fieldType = 'Combobox'">
						<xsl:call-template name="ComboBox">
							<xsl:with-param name="mValue"><xsl:value-of select="$mData/data/*[name() = $fieldName]" /></xsl:with-param>
						</xsl:call-template>
					</xsl:when>
					<xsl:when test="@fieldType = 'ComboBoxMulti'">
						<xsl:call-template name="ComboBoxMulti">
							<xsl:with-param name="mValue"><xsl:value-of select="$mData/data/*[name() = $fieldName]" /></xsl:with-param>
						</xsl:call-template>
					</xsl:when>
					<xsl:when test="@fieldType = 'Table'">
						<xsl:call-template name="Table" />
					</xsl:when>
					<xsl:when test="@fieldType = 'TableVertical'">
						<xsl:call-template name="Table" />
					</xsl:when>
					<xsl:when test="@fieldType = 'RadioButton'">
						<xsl:call-template name="RadioButton">
							<xsl:with-param name="mValue"><xsl:value-of select="$mData/data/*[name() = $fieldName]" /></xsl:with-param>
						</xsl:call-template>
					</xsl:when>
				</xsl:choose>
			</xsl:for-each>
		</xsl:if>
	</xsl:template>
	
	<xsl:template name="TextField">
		<xsl:param name="mValue" />
		<xsl:variable name="mName" select="@name" />
		<input type="text" name="{@name}" class="{@class}" maxlength="{@maxLength}">
			<xsl:attribute name="value"><xsl:value-of select="$mValue" /></xsl:attribute>
		</input>
	</xsl:template>
	
	<xsl:template name="TextFieldMulti">
		<xsl:variable name="mName" select="@name" />
		<input type="button" value="Add Row" onClick="addTableRow('{@name}')" />
		<table id="{@name}">
			<xsl:choose>
				<xsl:when test="$mData/data/*[name() = $mName]">
					<xsl:variable name="mCurrField" select="." />
					<xsl:for-each select="$mData/data/*[name() = $mName]">
						<tr>
							<td>
								<input type="text" name="{$mCurrField/@name}" class="{$mCurrField/@class}" maxlength="{$mCurrField/@maxLength}">
									<xsl:attribute name="value"><xsl:value-of select="text()" /></xsl:attribute>
								</input>
							</td>
							<td>
								<input type="button" value="Remove" onClick="removeTableRow(this)" />
							</td>
						</tr>
					</xsl:for-each>
				</xsl:when>
				<xsl:otherwise>
					<tr>
						<td>
							<input type="text" name="{@name}" class="{@class}" maxlength="{@maxLength}" />
						</td>
						<td>
							<input type="button" value="Remove" onClick="removeTableRow(this)" />
						</td>
					</tr>
				</xsl:otherwise>
			</xsl:choose>
		</table>
	</xsl:template>
	
	<xsl:template name="TextArea">
		<xsl:param name="mValue" />
		<xsl:variable name="mName" select="@name" />
		<textarea name="{@name}" class="{@class}">
			<xsl:value-of select="$mValue"></xsl:value-of>
		</textarea>
	</xsl:template>
	
	<xsl:template name="ComboBox">
		<xsl:param name="mValue" />
		<xsl:variable name="mName" select="@name" />
		<select name="{@name}">
			<option value="">
				- No Value Selected -
			</option>
			<xsl:value-of disable-output-escaping="yes" select="options:getOptions(@name, ./option, $mValue)"/>
		</select>
	</xsl:template>
	
	<xsl:template name="RadioButton">
		<xsl:param name="mValue" />
		<xsl:variable name="mName" select="@name" />
		<xsl:for-each select="option">
			<input type="radio" name="{$mName}" value="{@value}">
				<xsl:if test="@value = $mValue">
					<xsl:attribute name="checked">checked</xsl:attribute>
				</xsl:if>
			</input>
			<xsl:value-of select="@label" />
			<br/>
		</xsl:for-each>
	</xsl:template>
	
	<xsl:template name="ComboBoxMulti">
		<xsl:param name="mValue" />
		<xsl:variable name="mName" select="@name" />
		<xsl:variable name="item" select="." />
		<select id="{@name}2">
			<option value=""></option>
			<xsl:value-of disable-output-escaping="yes" select="options:getOptions(@name, ./option)"/>
		</select>
		<select id="{@name}" name="{@name}" class="{@class}" multiple="multiple">
			<xsl:for-each select="$mData/data/*[name() = $mName]">
				<xsl:variable name="mCurrText" select="text()" />
				<option value="{$mCurrText}">
					<xsl:value-of select="options:getOptionValue(name(), $item/option, $mCurrText)"/>
				</option>
			</xsl:for-each>
		</select>
		<input type="button" value="Remove Selected" onClick="removeSelected('{@name}')" />
	</xsl:template>
	
	<xsl:template name="Table">
		<xsl:variable name="mName" select="@name" />
		<xsl:variable name="mValue" select="." />
		
		<input type="button" value="Add Row" onClick="addTableRow('{@name}')" /><br />
		<table id="{@name}">
			<xsl:choose>
				<xsl:when test="$mData/data/*[name() = $mName]">
					<xsl:for-each select="$mData/data/*[name() = $mName]">
						<xsl:variable name="mRow" select="." />
						<tr><td>
							<xsl:for-each select="$mValue/column">
								<label for="{@name}"><xsl:value-of select="@label" /></label><br />
								<xsl:variable name="mColName" select="@name" />
								<xsl:choose>
									<xsl:when test="@fieldType='TextField'">
										<xsl:call-template name="TextField">
											<xsl:with-param name="mValue"><xsl:value-of select="$mRow/*[name() = $mColName]" /></xsl:with-param>
										</xsl:call-template>
									</xsl:when>
									<xsl:when test="@fieldType='TextArea'">
										<xsl:call-template name="TextArea">
											<xsl:with-param name="mValue"><xsl:value-of select="$mRow/*[name() = $mColName]" /></xsl:with-param>
										</xsl:call-template>
									</xsl:when>
									<xsl:when test="@fieldType='Combobox'">
										<xsl:call-template name="ComboBox">
											<xsl:with-param name="mValue"><xsl:value-of select="$mRow/*[name() = $mColName]" /></xsl:with-param>
										</xsl:call-template>
									</xsl:when>
								</xsl:choose>
								<br />
								<br />
							</xsl:for-each>
							<input type="button" value="Remove" onClick="removeTableRow(this)" />
						</td></tr>
					</xsl:for-each>
				</xsl:when>
				<xsl:otherwise>
					<tr>
						<td>
						<xsl:for-each select="column">
								<label for="{@name}"><xsl:value-of select="@label" /></label><br />
								<xsl:choose>
									<xsl:when test="@fieldType='TextField'">
										<xsl:call-template name="TextField" />
									</xsl:when>
									<xsl:when test="@fieldType='TextArea'">
										<xsl:call-template name="TextArea" />
									</xsl:when>
									<xsl:when test="@fieldType='Combobox'">
										<xsl:call-template name="ComboBox" />
									</xsl:when>
								</xsl:choose>
							<br />
							<br />
						</xsl:for-each>
						<input type="button" value="Remove" onClick="removeTableRow(this)" />
						</td>
					</tr>
				</xsl:otherwise>
			</xsl:choose>
		</table>
	</xsl:template>
	
</xsl:stylesheet>