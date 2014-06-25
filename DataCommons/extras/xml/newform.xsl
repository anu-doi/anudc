<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:datetime="http://exslt.org/dates-and-times"
		xmlns:options="xalan://au.edu.anu.datacommons.xml.transform.SelectExtension">
	<xsl:param name="item" />
	<xsl:param name="tmplt" />
	<xsl:param name="data" />
	<xsl:variable name="mData" select="$data" />
	<xsl:variable name="ucLetters" select="'ABCDEFGHIJKLMNOPQRSTUVWXYZ'" />
	<xsl:variable name="lcLetters" select="'abcdefghijklmnopqrstuvwxyz'" />
	
	<xsl:template match="/">
		<xsl:if test="$data != null"></xsl:if>
		<xsl:variable name="template" select="." />
		<html>
			<body>
				<xsl:choose>
					<xsl:when test="$data = ''">
						<h1><xsl:value-of select="template/name" /></h1>
					</xsl:when>
					<xsl:otherwise>
						<h1><xsl:value-of select="$mData/data/name" /></h1>
					</xsl:otherwise>
				</xsl:choose>
				<form id="form" method="post">
				<xsl:if test="$data = ''">
					<xsl:attribute name="action">new?layout=def:display&amp;tmplt=<xsl:value-of select="$tmplt" />&amp;item=<xsl:value-of select="$item" /></xsl:attribute>
				</xsl:if>
				<input type="submit" class="right" value="Submit" />
				<xsl:if test="$data != ''">
					<input type="button" class="right" value="Return to Record" onclick="window.location='/DataCommons/rest/display/{$item}?layout=def:display'" />
				</xsl:if>
				<br/>
				<div id="tabs" class="pagetabs-nav">
					<ul>
						<xsl:for-each select="template/tab">
							<xsl:sort select="@order" data-type="number" order="ascending" />
							<li>
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
		<xsl:variable name="mName" select="@name" />
		<div class="field">
			<xsl:choose>
				<xsl:when test="@fieldType='TextField'">
					<xsl:call-template name="Label" />
					<xsl:call-template name="TextField">
						<xsl:with-param name="mValue"><xsl:if test="$data != ''"><xsl:value-of select="$mData/data/*[name() = $mName]" /></xsl:if></xsl:with-param>
					</xsl:call-template>
				</xsl:when>
				<xsl:when test="@fieldType='TextFieldMulti'">
					<xsl:call-template name="Label" />
					<xsl:call-template name="TextFieldMulti" />
				</xsl:when>
				<xsl:when test="@fieldType='TextArea'">
					<xsl:call-template name="Label" />
					<xsl:call-template name="TextArea">
						<xsl:with-param name="mValue"><xsl:if test="$data != ''"><xsl:value-of select="$mData/data/*[name() = $mName]" /></xsl:if></xsl:with-param>
					</xsl:call-template>
				</xsl:when>
				<xsl:when test="@fieldType='Combobox'">
					<xsl:call-template name="Label" />
					<xsl:call-template name="ComboBox">
						<xsl:with-param name="mValue"><xsl:if test="$data != ''"><xsl:value-of select="$mData/data/*[name() = $mName]" /></xsl:if></xsl:with-param>
						<xsl:with-param name="mCode"><xsl:if test="$data != ''"><xsl:value-of select="$mData/data/*[name() = $mName]/@code" /></xsl:if></xsl:with-param>
					</xsl:call-template>
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
					<xsl:call-template name="RadioButton">
				 		<xsl:with-param name="mValue"><xsl:if test="$data != ''"><xsl:value-of select="$mData/data/*[name() = $mName]" /></xsl:if></xsl:with-param>
						<xsl:with-param name="mCode"><xsl:if test="$data != ''"><xsl:value-of select="$mData/data/*[name() = $mName]/@code" /></xsl:if></xsl:with-param>
					</xsl:call-template>
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
			<xsl:if test="contains(@class,'needed')">
				<b style="color:blue;">*</b>
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
		<xsl:param name="mValue" />
		<xsl:variable name="mName" select="@name" />
		<input type="text" name="{@name}" class="{@class}" maxlength="{@maxLength}">
			<xsl:if test="@disabled = 'disabled'">
				<xsl:attribute name="disabled"><xsl:value-of select="@disabled" /></xsl:attribute>
			</xsl:if>
			<xsl:if test="@readonly = 'readonly'">
				<xsl:attribute name="readonly"><xsl:value-of select="@readonly" /></xsl:attribute>
			</xsl:if>
			<xsl:choose>
				<xsl:when test="$data != ''"> 
					<xsl:attribute name="value"><xsl:value-of select="$mValue" /></xsl:attribute>
				</xsl:when>
				<xsl:otherwise>
					<xsl:attribute name="value"><xsl:value-of select="@defaultValue" /></xsl:attribute>
				</xsl:otherwise>
			</xsl:choose>
		</input>
	</xsl:template>
	
	<xsl:template name="TextArea">
		<xsl:param name="mValue" />
		<xsl:variable name="mName" select="@name" />
		<textarea name="{@name}" class="{@class}">
			<xsl:value-of select="$mValue" />
		</textarea>
	</xsl:template>
	
	<xsl:template name="TextFieldMulti">
		<xsl:variable name="mName" select="@name" />
		<input type="button" value="Add Row" onClick="addTableRow('{@name}')" />
		<table id="{@name}">
			<xsl:choose>
				<xsl:when test="$data != '' and $mData/data/*[name() = $mName]">
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
	
	<xsl:template name="ComboBox">
		<xsl:param name="mValue" />
		<xsl:param name="mCode" />
		<xsl:variable name="mName" select="@name" />
		<select id="{@name}" name="{@name}" class="{@class}">
			<option value="">
				- No Value Selected -
			</option>
			
			<xsl:choose>
				<xsl:when test="$data = '' and @defaultValue != ''">
					<xsl:value-of disable-output-escaping="yes" select="options:getOptions(@name, ./option, @defaultValue, '')"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of disable-output-escaping="yes" select="options:getOptions(@name, ./option, $mCode, $mValue)"/>
				</xsl:otherwise>
			</xsl:choose>
		</select>
	</xsl:template>
	
	<xsl:template name="ComboBoxMulti">
		<xsl:variable name="mName" select="@name" />
		<xsl:variable name="mCurrent" select="." />
		<select id="{@name}2">
			<option value=""></option>
			<xsl:value-of disable-output-escaping="yes" select="options:getOptions(@name, ./option)"/>
		</select>
		<br />
		<select id="{@name}" name="{@name}" class="{@class}" multiple="multiple">
			<xsl:if test="$data != ''">
				<xsl:for-each select="$mData/data/*[name() = $mName]">
					<xsl:variable name="mCurrCode" select="@code" />
					<xsl:variable name="mCurrValue" select="text()" />
					<xsl:variable name="mDesc" select="options:getOptionValue($mName, $mCurrent/option, $mCurrCode, $mCurrValue)" />
					<xsl:choose>
						<xsl:when test="$mCurrCode != ''">
							<option value="{$mCurrCode}" title="{$mDesc}">
								<xsl:value-of select="$mDesc" />
							</option>
						</xsl:when>
						<xsl:otherwise>
							<option value="{$mCurrValue}" title="{$mDesc}">
								<xsl:value-of select="$mDesc" />
							</option>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:for-each>
			</xsl:if>
			<xsl:if test="$data = '' and @defaultValue != ''">
				<option value="{@defaultValue}">
					<xsl:value-of select="options:getOptionValue($mName, $mCurrent/option, '', @defaultValue)"/>
				</option>
			</xsl:if>
		</select>
		<br />
		<input type="button" value="Remove Selected" onClick="removeSelected('{@name}')" />
	</xsl:template>
	
	<xsl:template name="RadioButton">
		<xsl:param name="mValue" />
		<xsl:param name="mCode" />
		<xsl:variable name="mCurrent" select="." />
		<xsl:variable name="mName" select="@name" />
		<xsl:variable name="lcValue" select="translate($mValue, $ucLetters, $lcLetters)" />
		<xsl:variable name="lcCode" select="translate($mCode, $ucLetters, $lcLetters)" />
		<xsl:for-each select="option">
			<input type="radio" name="{$mName}" value="{@value}">
				<xsl:if test="@value = $mValue or @value = $mCode or @value = $lcValue or @value = $lcCode or $data = '' and @value = $mCurrent/@defaultValue">
					<xsl:attribute name="checked">checked</xsl:attribute>
				</xsl:if>
			</input>
			<xsl:value-of select="@label" />
			<br/>
		</xsl:for-each>
	</xsl:template>
	
	<xsl:template name="Table">
		<xsl:variable name="mName" select="@name" />
		<xsl:variable name="mValue" select="." />
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
				<xsl:choose>
					<xsl:when test="$data != '' and $mData/data/*[name() = $mName]">
						<xsl:for-each select="$mData/data/*[name() = $mName]">
							<xsl:variable name="mRow" select="." />
							<tr>
								<xsl:for-each select="$mValue/column">
									<xsl:variable name="mColName" select="@name" />
									<td>
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
												<xsl:with-param name="mCode"><xsl:value-of select="$mRow/*[name() = $mColName]/@code" /></xsl:with-param>
											</xsl:call-template>
										</xsl:when>
									</xsl:choose>
									</td>
								</xsl:for-each>
								<td>
									<input type="button" value="Remove" onClick="removeTableRow(this)" />
								</td>
							</tr>
						</xsl:for-each>
					</xsl:when>
					<xsl:otherwise>
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
					</xsl:otherwise>
				</xsl:choose>
			</tbody>
		</table>
	</xsl:template>
</xsl:stylesheet>