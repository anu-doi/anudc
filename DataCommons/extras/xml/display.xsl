<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:options="xalan://au.edu.anu.datacommons.xml.transform.SelectExtension">
	<xsl:param name="data" />
	<xsl:param name="modifiedData" />
	<xsl:variable name="mData" select="$data" />
	<xsl:variable name="mModifiedData" select="$modifiedData" />
	
	<xsl:template match="/">
		<xsl:if test="$data != null"></xsl:if>
		<html>
			<body>
				<xsl:choose>
					<xsl:when test="$data != ''">
						<h1><xsl:value-of select="$mData/data/name" /></h1>
						<table>
							<xsl:for-each select="template/item">
								<xsl:variable name="name" select="@name" />
								<xsl:if test="$mData/data/*[name() = $name] and $name != 'uid'">
									<tr>
										<th valign="top"><xsl:value-of select="@label" /></th>
										<td>
											<xsl:choose>
												<xsl:when test="@fieldType='Table'">
													<xsl:call-template name="Table">
														<xsl:with-param name="tableVal" select="$mData" />
													</xsl:call-template>
												</xsl:when>
												<xsl:when test="@fieldType='TableVertical'">
													<xsl:call-template name="TableVertical">
														<xsl:with-param name="tableVal" select="$mData" />
													</xsl:call-template>
												</xsl:when>
												<xsl:when test="@fieldType='Combobox' or @fieldType='ComboBoxMulti' or @fieldType='RadioButton'">
													<xsl:call-template name="Combobox">
														<xsl:with-param name="comboVal" select="$mData" />
													</xsl:call-template>
												</xsl:when>
												<xsl:otherwise>
													<xsl:for-each select="$mData/data/*[name() = $name]">
														<xsl:variable name="textvalue" select="text()"/>
														<xsl:choose>
															<xsl:when test="$name = 'websiteAddress'"><a href="{$textvalue}"><xsl:value-of disable-output-escaping="yes" select="$textvalue" /></a></xsl:when>
															<xsl:otherwise><xsl:value-of disable-output-escaping="yes" select="options:replaceNewlineWithBr(text())" /></xsl:otherwise>
														</xsl:choose><br />
													</xsl:for-each>
												</xsl:otherwise>
											</xsl:choose>
										</td>
									</tr>
								</xsl:if>
								<xsl:if test="$modifiedData != ''">
									<xsl:if test="$mModifiedData/data/*[name() = $name] and $name != 'uid'">
										<tr>			
											<th valign="top"><xsl:value-of select="@label" /> Modified</th>
											<td>
												<xsl:choose>
												<xsl:when test="@fieldType='Table'">
														<xsl:call-template name="Table">
															<xsl:with-param name="tableVal" select="$mModifiedData" />
														</xsl:call-template>
													</xsl:when>
													<xsl:when test="@fieldType='TableVertical'">
														<xsl:call-template name="TableVertical">
															<xsl:with-param name="tableVal" select="$mModifiedData" />
														</xsl:call-template>
													</xsl:when>
													<xsl:when test="@fieldType='Combobox' or @fieldType='ComboBoxMulti' or @fieldType='RadioButton'">
														<xsl:call-template name="Combobox">
															<xsl:with-param name="comboVal" select="$mModifiedData" />
														</xsl:call-template>
													</xsl:when>
													<xsl:otherwise>
														<xsl:for-each select="$mModifiedData/data/*[name() = $name]">
															<xsl:variable name="textvalue" select="text()"/>
															<xsl:choose>
																<xsl:when test="$name = 'websiteAddress'"><a href="{$textvalue}"><xsl:value-of disable-output-escaping="yes" select="$textvalue" /></a></xsl:when>
																<xsl:otherwise><xsl:value-of disable-output-escaping="yes" select="options:replaceNewlineWithBr(text())" /></xsl:otherwise>
															</xsl:choose><br />
														</xsl:for-each>
													</xsl:otherwise>
												</xsl:choose>
											</td>
										</tr>
									</xsl:if>
								</xsl:if>
							</xsl:for-each>
						</table>
					</xsl:when>
					<xsl:otherwise>
						Unable to find item.
					</xsl:otherwise>
				</xsl:choose>
			</body>
		</html>
	</xsl:template>
	
	<xsl:template name="Combobox">
		<xsl:param name="comboVal" />
		<xsl:variable name="item" select="." />
		<xsl:for-each select="$comboVal/data/*[name() = $item/@name]">
			<xsl:value-of select="options:getOptionValue(name(), $item/option, @code, text())"/>
			<br />
		</xsl:for-each>
	</xsl:template>
	
	<xsl:template name='Table'>
		<xsl:param name="tableVal" />
		<xsl:variable name="mTableVal" select="$tableVal" />
		<xsl:variable name="table" select="." />
		<table>
			<tr>
				<xsl:for-each select="column">
					<th>
						<xsl:value-of select="@label" />
					</th>
				</xsl:for-each>
			</tr>
			<xsl:for-each select="$mTableVal/data/*[name() = $table/@name]">
				<xsl:variable name="tabledata" select="." />
				<tr>
					<xsl:for-each select="$table/column">
						<td>
							<xsl:variable name="colname" select="@name" />
							<xsl:variable name="colvalue" select="$tabledata/*[name() = $colname]/text()"/>
							<xsl:choose>
								<xsl:when test="$colname = 'relatedWebURL'"><a href="{$colvalue}"><xsl:value-of disable-output-escaping="yes" select="$colvalue" /></a></xsl:when>
								<xsl:otherwise><xsl:value-of disable-output-escaping="yes" select="$colvalue" /></xsl:otherwise>
							</xsl:choose>
						</td>
					</xsl:for-each>
				</tr>
			</xsl:for-each>
		</table>
	</xsl:template>
	
	<xsl:template name='TableVertical'>
		<xsl:param name="tableVal" />
		<xsl:variable name="mTableVal" select="$tableVal" />
		<xsl:variable name="table" select="." />
		<xsl:for-each select="$mTableVal/data/*[name() = $table/@name]">
			<xsl:variable name="tabledata" select="." />
			<p>
				<xsl:for-each select="$table/column">
					<strong class="text-uni"><xsl:value-of select="@label" /></strong><br />
					<xsl:variable name="colname" select="@name" />
					<xsl:variable name="colvalue" select="$tabledata/*[name() = $colname]/text()"/>
					<xsl:choose>
						<xsl:when test="$colname = 'relatedWebURL'"><a href="{$colvalue}"><xsl:value-of disable-output-escaping="yes" select="$colvalue" /></a></xsl:when>
						<xsl:otherwise><xsl:value-of disable-output-escaping="yes" select="$colvalue" /></xsl:otherwise>
					</xsl:choose>
					<br/>
				</xsl:for-each>
			</p><br />
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>