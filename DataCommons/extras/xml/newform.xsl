<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
		xmlns:xalan="http://xml.apache.org/xalan" exclude-result-prefixes="xalan">
	<xsl:param name="item" />
	<xsl:param name="tmplt" />
	<xsl:param name="data" />
	<xsl:param name="options" />
	<xsl:variable name="mData" select="$data" />
	<xsl:variable name="mOptions" select="$options" />
	<xsl:template match="/">
		<html>
			<body>
				<h1><xsl:value-of select="template/name" /></h1>
				<form id="form" method="post" action="new?layout=def:display&amp;tmplt={$tmplt}&amp;item={$item}">
					<table>
						<xsl:for-each select="template/item">
							<tr>
								<xsl:choose>
									<xsl:when test="@fieldType='TextField'">
										<xsl:call-template name="Label" />
										<xsl:call-template name="TextField" />
									</xsl:when>
									<xsl:when test="@fieldType='TextFieldMulti'">
										<xsl:call-template name="Label" />
										<xsl:call-template name="TextFieldMulti" />
									</xsl:when>
									<xsl:when test="@fieldType='TextArea'">
										<xsl:call-template name="Label" />
										<xsl:call-template name="TextArea" />
									</xsl:when>
									<xsl:when test="@fieldType='Combobox'">
										<xsl:call-template name="Label" />
										<xsl:call-template name="ComboBox" />
									</xsl:when>
									<xsl:when test="@fieldType='ComboBoxMulti'">
										<xsl:call-template name="Label" />
										<xsl:call-template name="ComboBoxMulti" />
									</xsl:when>
									<xsl:when test="@fieldType='Table'">
										<xsl:call-template name="Label" />
										<xsl:call-template name="Table" />
									</xsl:when>
									<xsl:when test="@fieldType='TableVertical'">
										<xsl:call-template name="Label" />
										<xsl:call-template name="Table" />
									</xsl:when>
								</xsl:choose>
							</tr>
						</xsl:for-each>
					</table>
					<input type="submit" value="Submit" />
				</form>
			</body>
		</html>
	</xsl:template>
	
	<xsl:template name="Label">
		<th>
			<label for="{@name}">
				<xsl:value-of select="@label" />
				<xsl:if test="contains(@class,'required')">
					<b style="color:red;">*</b>
				</xsl:if>
			</label>
		</th>
	</xsl:template>
	
	<xsl:template name="TextField">
		<td>
			<input type="text" name="{@name}" class="{@class}" maxlength="{@maxLength}">
				<xsl:if test="@disabled = 'disabled'">
					<xsl:attribute name="disabled"><xsl:value-of select="@disabled" /></xsl:attribute>
					<xsl:attribute name="value"><xsl:value-of select="@defaultValue" /></xsl:attribute>
				</xsl:if>
				<xsl:if test="@readonly = 'readonly'">
					<xsl:attribute name="readonly"><xsl:value-of select="@readonly" /></xsl:attribute>
					<xsl:attribute name="value"><xsl:value-of select="@defaultValue" /></xsl:attribute>
				</xsl:if>
			</input>
		</td>
	</xsl:template>
	
	<xsl:template name="TextArea">
		<td>
			<textarea name="{@name}" class="{@class}">
				
			</textarea>
		</td>
	</xsl:template>
	
	<xsl:template name="TextFieldMulti">
		<td>
			<input type="button" value="Add Row" onClick="addTableRow('{@name}')" />
			<table id="{@name}">
				<xsl:choose>
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
		</td>
	</xsl:template>
	
	<xsl:template name="ComboBox">
		<td>
			<xsl:variable name="mName" select="@name" />
			<select id="{@name}" name="{@name}" class="{@class}">
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
								<xsl:value-of select="@label" />
							</option>
						</xsl:for-each>
					</xsl:otherwise>
				</xsl:choose>
			</select>
		</td>
	</xsl:template>
	
	<xsl:template name="ComboBoxMulti">
		<td>
			<xsl:variable name="mName" select="@name" />
			<select id="{@name}2">
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
								<xsl:value-of select="@label" />
							</option>
						</xsl:for-each>
					</xsl:otherwise>
				</xsl:choose>
			</select>
			<br />
			<select id="{@name}" name="{@name}" class="{@class}" multiple="multiple">
				
			</select>
			<br />
			<input type="button" value="Remove Selected" onClick="removeSelected('{@name}')" />
		</td>
	</xsl:template>
	
	<xsl:template name="Table">
		<td>
			<input type="button" value="Add Row" onClick="addTableRow('{@name}')" />
			<table id="{@name}">
				<thead>
					<tr>
						<xsl:for-each select="column">
							<th><xsl:value-of select="@label" /></th>
						</xsl:for-each>
					</tr>
				</thead>
				<tbody>
					<tr>
						<xsl:for-each select="column">
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
						</xsl:for-each>
						<td>
							<input type="button" value="Remove" onClick="removeTableRow(this)" />
						</td>
					</tr>
				</tbody>
			</table>
		</td>
	</xsl:template>
</xsl:stylesheet>