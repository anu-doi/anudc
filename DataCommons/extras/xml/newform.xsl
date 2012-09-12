<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:datetime="http://exslt.org/dates-and-times"
		xmlns:options="xalan://au.edu.anu.datacommons.xml.transform.SelectExtension">
	<xsl:param name="item" />
	<xsl:param name="tmplt" />
	<xsl:param name="data" />
	<xsl:variable name="mData" select="$data" />
	
	<xsl:template match="/">
		<xsl:if test="$data != null"></xsl:if>
		<xsl:variable name="template" select="." />
		<html>
			<body>
				<h1><xsl:value-of select="template/name" /></h1>
				<form id="form" method="post" action="new?layout=def:display&amp;tmplt={$tmplt}&amp;item={$item}">
				<input type="submit" class="right" value="Submit" /><br/>
				<div id="tabs" class="pagetabs-nav">
					<ul>
						<xsl:for-each select="template/tab">
							<xsl:sort select="@order" data-type="number" order="ascending" />
							<li>
								<xsl:if test="position() = 1">
									<xsl:attribute name="class">pagetabs-select</xsl:attribute>
								</xsl:if>
								<a href="#{@name}" id="tab-{@name}"><xsl:attribute name="title"><xsl:value-of disable-output-escaping="yes" select="tooltip"></xsl:value-of></xsl:attribute><xsl:value-of select="@label" /></a>
							</li>
						</xsl:for-each>
						
						<xsl:if test="$template/template/item[not(@tab)]">
							<li>
								<xsl:if test="not($template/template/item/@tab)">
									<xsl:attribute name="class">pagetabs-select</xsl:attribute>
								</xsl:if>
								<a href="#other" id="tab-other">Other</a>
							</li>
						</xsl:if>
					</ul>
				</div>
				<div id="tab-content-container">
					<xsl:for-each select="template/tab">
						<div id="{@name}" class="tab-content">
							<xsl:variable name="tabid" select="@name" />
							<xsl:for-each select="$template/template/item[@tab = $tabid]">
								<xsl:call-template name="field" />
							</xsl:for-each>
						</div>
					</xsl:for-each>
					<div id="other" class="tab-content">
						<xsl:for-each select="$template/template/item[not(@tab)]">
							<xsl:call-template name="field" />
						</xsl:for-each>
					</div>
				</div>
				<div class="divline-solid"></div>
				</form>
			</body>
		</html>
	</xsl:template>
	
	<xsl:template name="field">
		<div class="field">
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
				<xsl:when test="@fieldType='RadioButton'">
					<xsl:call-template name="Label" />
					<xsl:call-template name="RadioButton" />
				</xsl:when>
			</xsl:choose>
		</div>
	</xsl:template>
	
	<xsl:template name="Label">
		<strong>
		<label for="{@name}">
			<xsl:value-of select="@label" />
			<xsl:if test="contains(@class,'required')">
				<b style="color:red;">*</b>
			</xsl:if>
		</label>
		</strong>
		<br/>
		<xsl:if test="tooltip">
			<div class="small">
				<xsl:value-of disable-output-escaping="yes" select="options:replaceNewlineWithBr(tooltip)" />
			</div>
		</xsl:if>
	</xsl:template>
	
	<xsl:template name="TextField">
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
	</xsl:template>
	
	<xsl:template name="TextArea">
		<textarea name="{@name}" class="{@class}">
			
		</textarea>
	</xsl:template>
	
	<xsl:template name="TextFieldMulti">
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
	</xsl:template>
	
	<xsl:template name="ComboBox">
		<xsl:variable name="mName" select="@name" />
		<select id="{@name}" name="{@name}" class="{@class}">
			<option value="">
				- No Value Selected -
			</option>
			<xsl:value-of disable-output-escaping="yes" select="options:getOptions(@name, ./option)"/>
		</select>
	</xsl:template>
	
	<xsl:template name="ComboBoxMulti">
		<xsl:variable name="mName" select="@name" />
		<select id="{@name}2">
			<option value=""></option>
			<xsl:value-of disable-output-escaping="yes" select="options:getOptions(@name, ./option)"/>
		</select>
		<br />
		<select id="{@name}" name="{@name}" class="{@class}" multiple="multiple">
			
		</select>
		<br />
		<input type="button" value="Remove Selected" onClick="removeSelected('{@name}')" />
	</xsl:template>
	
	<xsl:template name="RadioButton">
		<xsl:variable name="mName" select="@name" />
		<xsl:for-each select="option">
			<input type="radio" name="{$mName}" value="{@value}" /><xsl:value-of select="@label" />
			<br/>
		</xsl:for-each>
	</xsl:template>
	
	<xsl:template name="Table">
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
						<td>
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
						</td>
					</xsl:for-each>
					<td>
						<input type="button" value="Remove" onClick="removeTableRow(this)" />
					</td>
				</tr>
			</tbody>
		</table>
	</xsl:template>
</xsl:stylesheet>