<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.openarchives.org/OAI/2.0/oai_dc/ http://www.openarchives.org/OAI/2.0/oai_dc.xsd" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#">
	<xsl:param name="key" />
	<xsl:param name="external" />
	<xsl:variable name="anukey">http://anu.edu.au/</xsl:variable>
	<xsl:variable name="anuidentifier">https://datacommons.anu.edu.au:8443/DataCommons/item/</xsl:variable>
	<xsl:template match="/">
		<oai_dc:dc xsi:schemaLocation="http://www.openarchives.org/OAI/2.0/oai_dc/ http://www.openarchives.org/OAI/2.0/oai_dc.xsd">
			<dc:title><xsl:value-of select="data/name" /></dc:title>
			<xsl:if test="data/altLastName or data/altGivenName">
				<dc:title><xsl:value-of select="data/altTitle" /> <xsl:value-of select="data/altGivenName" /> <xsl:value-of select="data/altLastName" /></dc:title>
			</xsl:if>
			<xsl:if test="data/abbrLastName or data/abbrGivenName">
				<dc:title><xsl:value-of select="data/abbrTitle" /> <xsl:value-of select="data/abbrGivenName" /> <xsl:value-of select="data/abbrLastName" /></dc:title>
			</xsl:if>
			<xsl:if test="data/altName">
				<dc:title><xsl:value-of select="data/altName" /></dc:title>
			</xsl:if>
			<xsl:if test="data/abbrName">
				<dc:title><xsl:value-of select="data/abbrName" /></dc:title>
			</xsl:if>
			<!-- Ensure the for subjects are resolved -->
			<xsl:if test="data/anzforSubject">
				<xsl:for-each select="data/anzforSubject">
					<dc:subject><xsl:value-of select="selext:getSelectValue('anzforSubject', text())"  xmlns:selext="java:au.edu.anu.datacommons.xslt.SelectExtension"/></dc:subject>
				</xsl:for-each>
			</xsl:if>
			<xsl:if test="data/locSubject">
				<xsl:for-each select="data/locSubject">
					<dc:subject><xsl:value-of select="text()" /></dc:subject>
				</xsl:for-each>
			</xsl:if>
			<xsl:if test="data/briefDesc">
				<dc:description><xsl:value-of select="data/briefDesc" /></dc:description>
			</xsl:if>
			<xsl:if test="data/fullDesc">
				<dc:description><xsl:value-of select="data/fullDesc" /></dc:description>
			</xsl:if>
			<xsl:if test="data/significanceStatement">
				<dc:description><xsl:value-of select="data/significanceStatement" /></dc:description>
			</xsl:if>
			<xsl:if test="data/citationPublisher">
				<dc:publisher><xsl:value-of select="data/citationPublisher" /></dc:publisher>
			</xsl:if>
			<xsl:if test="data/createdDate">
				<dc:date><xsl:value-of select="data/createdDate" /></dc:date>
			</xsl:if>
			<xsl:if test="data/citationYear">
				<dc:date><xsl:value-of select="data/citationYear" /></dc:date>
			</xsl:if>
			<xsl:if test="data/existenceStart or data/existenceEnd">
				<dc:date>
					<xsl:choose>
						<xsl:when test="data/existenceStart and data/existenceEnd">
							<xsl:value-of select="concat('start=',data/existenceStart,'; end=', data/existenceEnd,';')" />
						</xsl:when>
						<xsl:when test="data/existenceStart">
							<xsl:value-of select="concat('start=',data/existenceStart,';')" />
						</xsl:when>
						<xsl:when test="data/existenceEnd">
							<xsl:value-of select="concat('end=', data/existenceEnd,';')" />
						</xsl:when>
					</xsl:choose>
				</dc:date>
			</xsl:if>
			<dc:type><xsl:value-of select="data/type" /></dc:type>
			<dc:type><xsl:value-of select="data/subType" /></dc:type>
			<dc:identifier><xsl:value-of select="$key" /></dc:identifier>
			<dc:identifier><xsl:value-of select="$anuidentifier" /><xsl:value-of select="$key" /></dc:identifier>
			<xsl:if test="data/nlaIdentifier">
				<dc:identifier><xsl:value-of select="data/nlaIdentifier" /></dc:identifier>
			</xsl:if>
			<xsl:if test="data/doi">
				<dc:identifier><xsl:value-of select="data/doi" /></dc:identifier>
			</xsl:if>
			<xsl:if test="data/coverageDates">
				<xsl:for-each select="data/coverageDates">
					<dc:coverage>
						<xsl:choose>
							<xsl:when test="dateFrom and dateTo">
								<xsl:value-of select="concat('start=', dateFrom, '; end=', dateTo, ';')" />
							</xsl:when>
							<xsl:when test="data/existenceStart">
								<xsl:value-of select="concat('start=', dateFrom, ';')" />
							</xsl:when>
							<xsl:when test="data/existenceEnd">
								<xsl:value-of select="concat('end=', dateTo, ';')" />
							</xsl:when>
						</xsl:choose>
					</dc:coverage>
				</xsl:for-each>
			</xsl:if>
			<xsl:if test="data/citCreator">
				<xsl:for-each select="data/citCreator">
					<dc:creator>
						<xsl:choose>
							<xsl:when test="citCreatorSurname and citCreatorGiven">
								<xsl:value-of select="citCreatorSurname" />, <xsl:value-of select="citCreatorGiven" />
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="citCreatorSurname" /><xsl:value-of select="citCreatorGiven" />
							</xsl:otherwise>
						</xsl:choose>
					</dc:creator>
				</xsl:for-each>
			</xsl:if>
			<xsl:if test="$external">
				<xsl:for-each select="document($external)//*[namespace-uri()='http://anu.edu.au/related/']">
					<dc:relation>
						<xsl:choose>
							<xsl:when test="contains(@rdf:resource,'info:fedora')">
								<xsl:value-of select="concat(selext:getRelationValue(name()), ' ', $anuidentifier, substring-after(@rdf:resource, 'info:fedora/'))" xmlns:selext="java:au.edu.anu.datacommons.xslt.SelectExtension" />
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="concat(selext:getRelationValue(name()), ' ', @rdf:resource)" xmlns:selext="java:au.edu.anu.datacommons.xslt.SelectExtension" />
							</xsl:otherwise>
						</xsl:choose>
					</dc:relation>
				</xsl:for-each>
			</xsl:if>
			<xsl:if test="data/coverageArea">
				<xsl:for-each select="data/coverageArea">
					<dc:coverage>
						<xsl:value-of select="covAreaValue" />
					</dc:coverage>
				</xsl:for-each>
			</xsl:if>
			<xsl:if test="data/rightsStatement">
				<dc:rights><xsl:value-of select="data/rightsStatement" /></dc:rights>
			</xsl:if>
			<xsl:if test="data/licenceType">
				<dc:rights><xsl:value-of select="data/licenceType" /></dc:rights>
			</xsl:if>
			<xsl:if test="data/licence">
				<dc:rights><xsl:value-of select="data/licence" /></dc:rights>
			</xsl:if>
			<xsl:if test="data/accessRights">
				<dc:rights><xsl:value-of select="data/accessRights" /></dc:rights>
			</xsl:if>
			<xsl:if test="data/accessPolicy">
				<dc:rights><xsl:value-of select="data/accessPolicy" /></dc:rights>
			</xsl:if>
		</oai_dc:dc>
	</xsl:template>
</xsl:stylesheet>