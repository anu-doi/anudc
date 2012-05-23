<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://ands.org.au/standards/rif-cs/registryObjects" xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#">
	<xsl:param name="key" />
	<xsl:param name="external" />
	<xsl:variable name="anukey">http://anu.edu.au/</xsl:variable>
	<xsl:template match="/">
		<registryObjects xmlns="http://ands.org.au/standards/rif-cs/registryObjects" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
			xsi:schemaLocation="http://ands.org.au/standards/rif-cs/registryObjects http://services.ands.org.au/documentation/rifcs/schema/registryObjects.xsd">
			<registryObject group="The Australian National University">
				<key><xsl:value-of select="$anukey" /><xsl:value-of select="$key" /></key>
				<originatingSource>http://anu.edu.au</originatingSource>
				<xsl:choose>
					<xsl:when test="data/type/text() = 'Collection'">
						<collection>
							<xsl:attribute name="type">
								<xsl:value-of select="data/subType" />
							</xsl:attribute>
							<xsl:call-template name="process" />
						</collection>
					</xsl:when>
					<xsl:when test="data/type/text() = 'Activity'">
						<activity>
							<xsl:attribute name="type">
								<xsl:value-of select="data/subType/text()" />
							</xsl:attribute>
							<xsl:call-template name="process" />
						</activity>
					</xsl:when>
					<xsl:when test="data/type/text() = 'Party'">
						<party>
							<xsl:attribute name="type">
								<xsl:value-of select="data/subType/text()" />
							</xsl:attribute>
							<xsl:call-template name="process" />
						</party>
					</xsl:when>
					<xsl:when test="data/type/text() = 'Service'">
						<service>
							<xsl:attribute name="type">
								<xsl:value-of select="data/subType/text()" />
							</xsl:attribute>
							<xsl:call-template name="process" />
						</service>
					</xsl:when>
				</xsl:choose>
			</registryObject>
		</registryObjects>
	</xsl:template>
	<xsl:template name="process">
		<xsl:if test="data/arcNumber">
			<identifier type="arc">http://purl.org/au-research/grants/arc/<xsl:value-of select="data/arcNumber" /></identifier>
		</xsl:if>
		<xsl:if test="data/nlaIdentifier">
			<identifier type="AU-ANL:PEAU"><xsl:value-of select="data/nlaIdentifier" /></identifier>
		</xsl:if>
		<xsl:choose>
			<xsl:when test="data/lastName or data/givenName">
				<name type="primary">
					<xsl:if test="data/title">
						<namePart type="title">
							<xsl:value-of select="data/title" />
						</namePart>
					</xsl:if>
					<xsl:if test="data/lastName">
						<namePart type="family">
							<xsl:value-of select="data/lastName" />
						</namePart>
					</xsl:if>
					<xsl:if test="data/givenName">
						<namePart type="given">
							<xsl:value-of select="data/givenName" />
						</namePart>
					</xsl:if>
				</name>
			</xsl:when>
			<xsl:when test="data/name">
				<name type="primary">
					<namePart>
						<xsl:value-of select="data/name" />
					</namePart>
				</name>
			</xsl:when>
		</xsl:choose>
		<xsl:if test="data/altLastName or data/altGivenName"> 
			<name type="primary">
				<xsl:if test="data/altTitle">
					<namePart type="title">
						<xsl:value-of select="data/altTitle" />
					</namePart>
				</xsl:if>
				<xsl:if test="data/altLastName">
					<namePart type="family">
						<xsl:value-of select="data/altLastName" />
					</namePart>
				</xsl:if>
				<xsl:if test="data/altGivenName">
					<namePart type="given">
						<xsl:value-of select="data/altGivenName" />
					</namePart>
				</xsl:if>
			</name>
		</xsl:if>
		<xsl:if test="data/abbrLastName or data/abbrGivenName">
			<name type="primary">
				<xsl:if test="data/altTitle">
					<namePart type="title">
						<xsl:value-of select="data/altTitle" />
					</namePart>
				</xsl:if>
				<xsl:if test="data/abbrLastName">
					<namePart type="family">
						<xsl:value-of select="data/abbrLastName" />
					</namePart>
				</xsl:if>
				<xsl:if test="data/abbrGivenName">
					<namePart type="given">
						<xsl:value-of select="data/abbrGivenName" />
					</namePart>
				</xsl:if>
			</name>
		</xsl:if>
		<xsl:if test="data/abbrName">
			<xsl:for-each select="data/abbrName">
				<name type="abbreviated">
					<namePart>
						<xsl:value-of select="text()" />
					</namePart>
				</name>
			</xsl:for-each>
		</xsl:if>
		<xsl:if test="data/altName">
			<xsl:for-each select="data/altName">
				<name type="alternative">
					<namePart>
						<xsl:value-of select="text()" />
					</namePart>
				</name>
			</xsl:for-each>
		</xsl:if>
		<xsl:if test="data/email">
			<xsl:for-each select="data/email">
				<location>
					<address>
						<electronic type="email">
							<value><xsl:value-of select="text()" /></value>
						</electronic>
					</address>
				</location>
			</xsl:for-each>
		</xsl:if>
		<xsl:if test="data/postalAddress">
			<xsl:for-each select="data/postalAddress">
				<location>
					<address>
						<physical type="postalAddress">
							<addressPart type="addressLine">
								<xsl:value-of select="text()" />
							</addressPart>
						</physical>
					</address>
				</location>
			</xsl:for-each>
		</xsl:if>
		<xsl:if test="data/phone">
			<xsl:for-each select="data/phone">
				<location>
					<address>
						<physical type="streetAddress">
							<addressPart type="telephoneNumber">
								<xsl:value-of select="text()" />
							</addressPart>
						</physical>
					</address>
				</location>
			</xsl:for-each>
		</xsl:if>
		<xsl:if test="data/fax">
			<xsl:for-each select="data/fax">
				<location>
					<address>
						<physical type="streetAddress">
							<addressPart type="faxNumber">
								<xsl:value-of select="text()" />
							</addressPart>
						</physical>
					</address>
				</location>
			</xsl:for-each>
		</xsl:if>
		<xsl:if test="data/websiteAddress">
			<xsl:for-each select="data/websiteAddress">
				<location>
					<address>
						<electronic type="url">
							<value><xsl:value-of select="text()" /></value>
						</electronic>
					</address>
				</location>
			</xsl:for-each>
		</xsl:if>
		<xsl:if test="$external">
			<xsl:for-each select="document($external)//*[namespace-uri()='http://anu.edu.au/related/']">
				<relatedObject>
					<key><xsl:value-of select="$anukey" /><xsl:value-of select="substring-after(@rdf:resource, 'info:fedora/')" /></key>
					<relation type="{name()}" />
				</relatedObject>
			</xsl:for-each>
		</xsl:if>
		<xsl:if test="data/anzforSubject">
			<xsl:for-each select="data/anzforSubject">
				<subject type="anzsrc-for">
					<xsl:value-of select="text()" />
				</subject>
			</xsl:for-each>
		</xsl:if>
		<xsl:if test="data/locSubject">
			<xsl:for-each select="data/locSubject">
				<subject type="local">
					<xsl:value-of select="text()" />
				</subject>
			</xsl:for-each>
		</xsl:if>
		<xsl:if test="data/briefDesc">
			<description type="brief">
				<xsl:value-of select="data/briefDesc" />
			</description>
		</xsl:if>
		<xsl:if test="data/fullDesc">
			<description type="full">
				<xsl:value-of select="data/fullDesc" />
			</description>
		</xsl:if>
		<xsl:if test="data/significanceStatement">
			<description type="significanceStatement">
				<xsl:value-of select="data/significanceStatement" />
			</description>
		</xsl:if>
		<xsl:if test="data/deliveryMethod">
			<description type="deliveryMethod">
				<xsl:value-of select="data/deliveryMethod" />
			</description>
		</xsl:if>
		<xsl:if test="data/coverageDates">
			<xsl:for-each select="date/coverageDates">
				<coverage>
					<temporal>
						<xsl:if test="dateFrom">
							<date type="dateFrom" dateFormat="W3CDTF"><xsl:value-of select="dateFrom" /></date>
						</xsl:if>
						<xsl:if test="dateTo">
							<date type="dateFrom" dateFormat="W3CDTF"><xsl:value-of select="dateTo" /></date>
						</xsl:if>
					</temporal>
				</coverage>
			</xsl:for-each>
		</xsl:if>
		<xsl:if test="data/coverageArea">
			<xsl:for-each select="data/coverageArea">
				<coverage>
					<spatial>
						<xsl:if test="covAreaType">
							<xsl:attribute name="type"><xsl:value-of select="covAreaType" /></xsl:attribute>
						</xsl:if>
						<xsl:if test="covAreaValue">
							<xsl:value-of select="covAreaValue" />
						</xsl:if>
					</spatial>
				</coverage>
			</xsl:for-each>
		</xsl:if>
		<xsl:if test="data/publication">
			<xsl:for-each select="data/publication">
				<relatedInfo type="publication">
					<identifier>
						<xsl:attribute name="type"><xsl:value-of select="pubType" /></xsl:attribute>
						<xsl:value-of select="pubValue" />
					</identifier>
					<title><xsl:value-of select="pubTitle" /></title>
				</relatedInfo>
			</xsl:for-each>
		</xsl:if>
		<xsl:if test="data/relatedURL">
			<relatedInfo type="website">
				<identifier type="uri">
					<xsl:value-of select="data/relatedURL" />
				</identifier>
			</relatedInfo>
		</xsl:if>
		<xsl:if test="data/rightsStatement">
			<xsl:for-each select="data/rightsStatement">
				<rights>
					<rightsStatement>
						<xsl:value-of select="text()" />
					</rightsStatement>
				</rights>
			</xsl:for-each>
		</xsl:if>
		<xsl:if test="data/licence">
			<xsl:for-each select="data/licence">
				<rights>
					<license>
						<xsl:value-of select="text()" />
					</license>
				</rights>
			</xsl:for-each>
		</xsl:if>
		<xsl:if test="data/accessRights">
			<xsl:for-each select="data/accessRights">
				<rights>
					<accessRights>
						<xsl:value-of select="text()" />
					</accessRights>
				</rights>
			</xsl:for-each>
		</xsl:if>
		<xsl:if test="data/accessPolicy">
			<accessPolicy>
				<xsl:value-of select="data/accessPolicy" />
			</accessPolicy>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>