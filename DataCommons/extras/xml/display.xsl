<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:param name="data" />
	<xsl:param name="modifiedData" />
	<xsl:param name="options" />
	<xsl:variable name="mData" select="$data" />
	<xsl:variable name="mModifiedData" select="$modifiedData" />
	<xsl:variable name="mOptions" select="$options" />
	
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
								<xsl:if test="$mData/data/*[name() = $name]">
									<tr>
										<th><xsl:value-of select="@label" /></th>
										<td>
											<xsl:choose>
												<xsl:when test="@saveType='table'">
													<xsl:call-template name="Table">
														<xsl:with-param name="tableVal" select="$mData" />
													</xsl:call-template>
												</xsl:when>
													<xsl:when test="@fieldType='Combobox' or @fieldType='ComboBoxMulti'">
														<xsl:call-template name="Combobox">
															<xsl:with-param name="comboVal" select="$mData" />
														</xsl:call-template>
													</xsl:when>
												<xsl:otherwise>
													<xsl:for-each select="$mData/data/*[name() = $name]">
														<xsl:value-of disable-output-escaping="yes" select="text()" /><br />
													</xsl:for-each>
												</xsl:otherwise>
											</xsl:choose>
										</td>
									</tr>
								</xsl:if>
								<xsl:if test="$modifiedData != ''">
									<xsl:if test="$mModifiedData/data/*[name() = $name]">
										<tr>			
											<th><xsl:value-of select="@label" /> Modified</th>
											<td>
												<xsl:choose>
													<xsl:when test="@saveType='table'">
														<xsl:call-template name="Table">
															<xsl:with-param name="tableVal" select="$mModifiedData" />
														</xsl:call-template>
													</xsl:when>
													<xsl:otherwise>
														<xsl:for-each select="$mModifiedData/data/*[name() = $name]">
															<xsl:value-of disable-output-escaping="yes" select="text()" /><br />
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
		<xsl:variable name="something" select="." />
		<xsl:for-each select="$comboVal/data/*[name() = $something/@name]">
			<xsl:variable name="comboContent" select="." />
			<xsl:choose>
				<xsl:when test="$mOptions/options/*[name() = $something/@name]">
					<xsl:value-of select="$mOptions/options/*[name() = $something/@name][id = $comboContent/text()]/name" />
				</xsl:when>
				<xsl:when test="$something/option[@value = $comboContent/text()]">
					<xsl:value-of select="$something/option[@value = $comboContent/text()]/@label" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of disable-output-escaping="yes" select="text()" /><br />
				</xsl:otherwise>
			</xsl:choose>
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
							<xsl:value-of disable-output-escaping="yes" select="$tabledata/*[name() = $colname]/text()" />
						</td>
					</xsl:for-each>
				</tr>
			</xsl:for-each>
		</table>
	</xsl:template>
</xsl:stylesheet>