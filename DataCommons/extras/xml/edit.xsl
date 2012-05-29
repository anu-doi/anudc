<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:param name="data" />
	<xsl:param name="fieldName" />
	<xsl:param name="options" />
	<xsl:variable name="mData" select="$data" />
	<xsl:variable name="mOptions" select="$options" />
	<xsl:template match="/">
		<xsl:if test="$fieldName != ''">
			<xsl:for-each select="template/item[@name=$fieldName]">
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
					<xsl:when test="@fieldType = 'Table'">
						<xsl:call-template name="Table" />
					</xsl:when>
					<xsl:when test="@fieldType = 'TableVertical'">
						<xsl:call-template name="Table" />
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
			<xsl:choose>
				<xsl:when test="$options != '' and $mOptions/options/*[name() = $mName]">
					<option value="">
						- No Value Selected -
					</option>
					<xsl:for-each select="$mOptions/options/*[name() = $mName]">
						<option value="{id}">
							<xsl:value-of select="name" />
						</option>
					</xsl:for-each>
				</xsl:when>
				<xsl:otherwise>
					<xsl:for-each select="option">
						<option value="{@value}">
							<xsl:if test="$mValue = @value">
								<xsl:attribute name="selected" />
							</xsl:if>
							<xsl:value-of select="@label" />
						</option>
					</xsl:for-each>
				</xsl:otherwise>
			</xsl:choose>
		</select>
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