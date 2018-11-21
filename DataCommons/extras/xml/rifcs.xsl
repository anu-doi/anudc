<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://ands.org.au/standards/rif-cs/registryObjects" xmlns:fn="http://www.w3.org/2005/xpath-functions" xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#">
	<xsl:param name="key" />
	<xsl:param name="external" />
	<xsl:variable name="anukey">http://anu.edu.au/</xsl:variable>
	<xsl:variable name="anuidentifier">https://datacommons.anu.edu.au/DataCommons/item/</xsl:variable>
	<xsl:variable name="ucLetters" select="'ABCDEFGHIJKLMNOPQRSTUVWXYZ'" />
	<xsl:variable name="lcLetters" select="'abcdefghijklmnopqrstuvwxyz'" />
	<xsl:template match="/">
		<registryObjects xmlns="http://ands.org.au/standards/rif-cs/registryObjects" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
			xsi:schemaLocation="http://ands.org.au/standards/rif-cs/registryObjects http://services.ands.org.au/documentation/rifcs/schema/registryObjects.xsd">
			<registryObject group="The Australian National University">
				<key><xsl:value-of select="$anukey" /><xsl:value-of select="$key" /></key>
				<originatingSource>http://anu.edu.au</originatingSource>
				<xsl:variable name="mSubTypeCode" select="data/subType/@code" />
				<xsl:variable name="mSubType" select="data/subType" />
				<xsl:choose>
					<xsl:when test="translate(data/type/text(), $ucLetters, $lcLetters) = 'collection'">
						<collection>
							<xsl:attribute name="type">
								<xsl:choose>
									<xsl:when test="$mSubTypeCode">
										<xsl:value-of select="$mSubTypeCode" />
									</xsl:when>
									<xsl:otherwise>
										<xsl:value-of select="$mSubType" />
									</xsl:otherwise>
								</xsl:choose>
							</xsl:attribute>
							<xsl:call-template name="process" />
						</collection>
					</xsl:when>
					<xsl:when test="translate(data/type/text(), $ucLetters, $lcLetters) = 'activity'">
						<activity>
							<xsl:attribute name="type">
								<xsl:choose>
									<xsl:when test="$mSubTypeCode">
										<xsl:value-of select="$mSubTypeCode" />
									</xsl:when>
									<xsl:otherwise>
										<xsl:value-of select="$mSubType" />
									</xsl:otherwise>
								</xsl:choose>
							<!-- 	<xsl:value-of select="data/subType/text()" /> -->
							</xsl:attribute>
							<xsl:call-template name="process" />
						</activity>
					</xsl:when>
					<xsl:when test="translate(data/type/text(), $ucLetters, $lcLetters) = 'party'">
						<party>
							<xsl:attribute name="type">
								<xsl:value-of select="data/subType/text()" />
							</xsl:attribute>
							<xsl:call-template name="process" />
						</party>
					</xsl:when>
					<xsl:when test="translate(data/type/text(), $ucLetters, $lcLetters) = 'service'">
						<service>
							<xsl:attribute name="type">
								<xsl:choose>
									<xsl:when test="$mSubTypeCode">
										<xsl:value-of select="$mSubTypeCode" />
									</xsl:when>
									<xsl:otherwise>
										<xsl:value-of select="$mSubType" />
									</xsl:otherwise>
								</xsl:choose>
							<!-- 	<xsl:value-of select="data/subType/text()" /> -->
							</xsl:attribute>
							<xsl:call-template name="process" />
						</service>
					</xsl:when>
				</xsl:choose>
			</registryObject>
		</registryObjects>
	</xsl:template>
	<xsl:template name="process">
		<identifier type="uri"><xsl:value-of select="$anuidentifier" /><xsl:value-of select="$key" /></identifier>
		<xsl:if test="data/arcNumber">
			<identifier type="arc">http://purl.org/au-research/grants/arc/<xsl:value-of select="data/arcNumber" /></identifier>
		</xsl:if>
		<xsl:if test="data/nlaIdentifier">
			<identifier type="AU-ANL:PEAU"><xsl:value-of select="data/nlaIdentifier" /></identifier>
		</xsl:if>
		<xsl:if test="data/doi">
			<identifier type="doi"><xsl:value-of select="data/doi" /></identifier>
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
			<name type="alternative">
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
			<name type="abbreviated">
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
		<xsl:if test="data/createdDate">
			<dates type="created">
				<date type="dateFrom" dateFormat="W3CDTF">
					<xsl:value-of select="data/createdDate" />
				</date>
			</dates>
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
							<addressPart type="text">
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
		<location>
			<address>
				<electronic type="url">
					<value><xsl:value-of select="$anuidentifier" /><xsl:value-of select="$key" /></value>
				</electronic>
			</address>
		</location>
		<xsl:if test="data/coverageDates">
			<xsl:for-each select="data/coverageDates">
				<coverage>
					<temporal>
						<xsl:if test="dateFrom">
							<date type="dateFrom" dateFormat="W3CDTF"><xsl:value-of select="dateFrom" /></date>
						</xsl:if>
						<xsl:if test="dateTo">
							<date type="dateTo" dateFormat="W3CDTF"><xsl:value-of select="dateTo" /></date>
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
							<xsl:choose>
								<xsl:when test="covAreaType/@code">
									<xsl:attribute name="type"><xsl:value-of select="covAreaType/@code" /></xsl:attribute>
								</xsl:when>
								<xsl:otherwise>
									<xsl:attribute name="type"><xsl:value-of select="covAreaType" /></xsl:attribute>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:if>
						<xsl:if test="covAreaValue">
							<xsl:value-of select="covAreaValue" />
						</xsl:if>
					</spatial>
				</coverage>
			</xsl:for-each>
		</xsl:if>
		<xsl:if test="$external">
			<xsl:for-each select="document($external)//*[namespace-uri()='http://anu.edu.au/related/']">
				<relatedObject>
					<xsl:choose>
						<xsl:when test="contains(@rdf:resource,'info:fedora')">
							<key><xsl:value-of select="$anukey" /><xsl:value-of select="substring-after(@rdf:resource, 'info:fedora/')" /></key>
						</xsl:when>
						<xsl:otherwise>
							<key><xsl:value-of select="@rdf:resource" /></key>
						</xsl:otherwise>
					</xsl:choose>
					<relation type="{name()}" />
				</relatedObject>
			</xsl:for-each>
		</xsl:if>
		<xsl:if test="data/anzforSubject">
			<xsl:for-each select="data/anzforSubject">
				<subject type="anzsrc-for">
					<xsl:choose>
						<xsl:when test="@code">
							<xsl:value-of select="@code" />
						</xsl:when>
						<xsl:otherwise>
								<xsl:value-of select="text()" />
						</xsl:otherwise>
					</xsl:choose>
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
		<xsl:if test="data/dataExtent or data/dataSize">
			<description type="note">
				<xsl:if test="data/dataExtent">
					<xsl:value-of select="data/dataExtent" />
					<xsl:if test="not(substring(data/dataExtent, string-length(data/dataExtent)) = '.')">.</xsl:if>
					&lt;br/&gt;
				</xsl:if>
				<xsl:if test="data/dataSize">
					<xsl:value-of select="data/dataSize" />
					<xsl:if test="not(substring(data/dataSize, string-length(data/dataSize)) = '.')">.</xsl:if>
				</xsl:if>
			</description>
		</xsl:if>
		<xsl:if test="data/existenceStart or data/existenceEnd">
			<existenceDates>
				<xsl:if test="data/existenceStart">
					<startDate dateFormat="W3CDTF"><xsl:value-of select="data/existenceStart" /></startDate>
				</xsl:if>
				<xsl:if test="data/existenceEnd">
					<endDate dateFormat="W3CDTF"><xsl:value-of select="data/existenceEnd" /></endDate>
				</xsl:if>
			</existenceDates>
		</xsl:if>
		<xsl:choose>
			<xsl:when test="data/rightsStatement">
				<xsl:for-each select="data/rightsStatement">
					<rights>
						<rightsStatement>
							<xsl:value-of select="text()" />
						</rightsStatement>
					</rights>
				</xsl:for-each>
			</xsl:when>
			<xsl:when test="translate(data/type/text(), $ucLetters, $lcLetters) = 'collection'">
				<rights>
					<rightsStatement rightsUri="http://legaloffice.weblogs.anu.edu.au/content/copyright/" />
				</rights>
			</xsl:when>
		</xsl:choose>
		<xsl:if test="data/licenceType or data/licence">
				<rights>
					<licence>
						<!-- <xsl:if test="data/licenceType/@code">
							<xsl:attribute name="type"><xsl:value-of select="data/licenceType/@code"/></xsl:attribute>
						</xsl:if> -->
						<xsl:choose>
							<xsl:when test="data/licenceType/@code = 'CC-BY'">
								<xsl:attribute name="type"><xsl:value-of select="data/licenceType/@code"/></xsl:attribute>
								<xsl:attribute name="rightsUri">http://creativecommons.org/licenses/by/3.0/au/deed.en</xsl:attribute>
							</xsl:when>
							<xsl:when test="data/licenceType/@code = 'CC-BY-4_0'">
								<xsl:attribute name="type">CC-BY</xsl:attribute>
								<xsl:attribute name="rightsUri">http://creativecommons.org/licenses/by/4.0/</xsl:attribute>
							</xsl:when>
							<xsl:when test="data/licenceType/@code = 'CC-BY-SA'">
								<xsl:attribute name="type"><xsl:value-of select="data/licenceType/@code"/></xsl:attribute>
								<xsl:attribute name="rightsUri">http://creativecommons.org/licenses/by-sa/3.0/au/deed.en</xsl:attribute>
							</xsl:when>
							<xsl:when test="data/licenceType/@code = 'CC-BY-SA-4_0'">
								<xsl:attribute name="type">CC-BY-SA</xsl:attribute>
								<xsl:attribute name="rightsUri">http://creativecommons.org/licenses/by-sa/4.0/</xsl:attribute>
							</xsl:when>
							<xsl:when test="data/licenceType/@code = 'CC-BY-ND'">
								<xsl:attribute name="type"><xsl:value-of select="data/licenceType/@code"/></xsl:attribute>
								<xsl:attribute name="rightsUri">http://creativecommons.org/licenses/by-nd/3.0/au/deed.en</xsl:attribute>
							</xsl:when>
							<xsl:when test="data/licenceType/@code = 'CC-BY-ND-4_0'">
								<xsl:attribute name="type">CC-BY-ND</xsl:attribute>
								<xsl:attribute name="rightsUri">http://creativecommons.org/licenses/by-nd/4.0/</xsl:attribute>
							</xsl:when>
							<xsl:when test="data/licenceType/@code = 'CC-BY-NC'">
								<xsl:attribute name="type"><xsl:value-of select="data/licenceType/@code"/></xsl:attribute>
								<xsl:attribute name="rightsUri">http://creativecommons.org/licenses/by-nc/3.0/au/deed.en</xsl:attribute>
							</xsl:when>
							<xsl:when test="data/licenceType/@code = 'CC-BY-NC-4_0'">
								<xsl:attribute name="type">CC-BY-NC</xsl:attribute>
								<xsl:attribute name="rightsUri">http://creativecommons.org/licenses/by-nc/4.0/</xsl:attribute>
							</xsl:when>
							<xsl:when test="data/licenceType/@code = 'CC-BY-NC-SA'">
								<xsl:attribute name="type"><xsl:value-of select="data/licenceType/@code"/></xsl:attribute>
								<xsl:attribute name="rightsUri">http://creativecommons.org/licenses/by-nc-sa/3.0/au/deed.en</xsl:attribute>
							</xsl:when>
							<xsl:when test="data/licenceType/@code = 'CC-BY-NC-SA-4_0'">
								<xsl:attribute name="type">CC-BY-NC-SA</xsl:attribute>
								<xsl:attribute name="rightsUri">http://creativecommons.org/licenses/by-nc-sa/4.0/</xsl:attribute>
							</xsl:when>
							<xsl:when test="data/licenceType/@code = 'CC-BY-NC-ND'">
								<xsl:attribute name="type"><xsl:value-of select="data/licenceType/@code"/></xsl:attribute>
								<xsl:attribute name="rightsUri">http://creativecommons.org/licenses/by-nc-nd/3.0/au/deed.en</xsl:attribute>
							</xsl:when>
							<xsl:when test="data/licenceType/@code = 'CC-BY-NC-ND-4_0'">
								<xsl:attribute name="type">CC-BY-NC-ND</xsl:attribute>
								<xsl:attribute name="rightsUri">http://creativecommons.org/licenses/by-nc-nd/4.0/</xsl:attribute>
							</xsl:when>
							<xsl:when test="data/licenceType/@code = 'GPL'"> 
								<xsl:attribute name="rightsUri">http://www.gnu.org/licenses/gpl.html</xsl:attribute>
							</xsl:when>
							<xsl:when test="data/licenceType/@code = 'AusGoalRestrictive'">
								<xsl:attribute name="rightsUri">http://www.ausgoal.gov.au/restrictive-licence-template</xsl:attribute>
							</xsl:when>
							<xsl:when test="data/licenceType/@code = 'NoLicence' or data/licenceType/@code = 'Unknown/Other'">
								<xsl:attribute name="type"><xsl:value-of select="data/licenceType/@code"/></xsl:attribute>
							</xsl:when>
						</xsl:choose>
						<xsl:value-of select="data/licence" />
					</licence>
				</rights>
		</xsl:if>
		<xsl:if test="data/accessRights or data/accessRightsType">
			<rights>
				<accessRights>
					<xsl:if test="data/accessRightsType/@code">
						<xsl:attribute name="type"><xsl:value-of select="data/accessRightsType/@code"/></xsl:attribute>
					</xsl:if>
					<xsl:value-of select="data/accessRights" />
				</accessRights>
			</rights>
		</xsl:if>
		<xsl:if test="data/accessPolicy">
			<accessPolicy>
				<xsl:value-of select="data/accessPolicy" />
			</accessPolicy>
		</xsl:if>
		<xsl:if test="data/publication">
			<xsl:for-each select="data/publication">
				<relatedInfo type="publication">
					<identifier>
						<xsl:choose>
							<xsl:when test="pubType/@code">
								<xsl:attribute name="type"><xsl:value-of select="pubType/@code" /></xsl:attribute>
							</xsl:when>
							<xsl:otherwise>
								<xsl:attribute name="type"><xsl:value-of select="pubType" /></xsl:attribute>
							</xsl:otherwise>
						</xsl:choose>
						<xsl:value-of select="pubValue" />
					</identifier>
					<title><xsl:value-of select="pubTitle" /></title>
					<notes><xsl:value-of select="pubNotes" /></notes>
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
		<xsl:if test="data/relatedWebsites">
			<xsl:for-each select="data/relatedWebsites">
				<relatedInfo type="website">
					<identifier type="uri">
						<xsl:value-of select="relatedWebURL" />
					</identifier>
					<title>
						<xsl:value-of select="relatedWebTitle" />
					</title>
				</relatedInfo>
			</xsl:for-each>
		</xsl:if>
		<xsl:if test="data/type/text() = 'Collection'">
			<xsl:if test="data/name">
				<citationInfo>
					<citationMetadata>
						<xsl:choose>
							<xsl:when test="data/doi">
								<identifier type="doi">
									<xsl:value-of select="data/doi" />
								</identifier>
							</xsl:when>
							<xsl:otherwise>
								<identifier type="uri">
									<xsl:value-of select="$anuidentifier" /><xsl:value-of select="$key" />
								</identifier>
							</xsl:otherwise>
						</xsl:choose>
						<xsl:choose>
							<xsl:when test="data/citCreator">
								<xsl:for-each select="data/citCreator">
									<contributor seq="{position()}">
										<xsl:choose>
											<xsl:when test="citCreatorSurname and citCreatorGiven">
												<namePart type="family"><xsl:value-of select="citCreatorSurname" /></namePart>
												<namePart type="given"><xsl:value-of select="citCreatorGiven" /></namePart>
											</xsl:when>
											<xsl:when test="citCreatorSurname">
												<namePart type="superior"><xsl:value-of select="citCreatorSurname" /></namePart>
											</xsl:when>
											<xsl:when test="citCreatorGiven">
												<namePart type="superior"><xsl:value-of select="citCreatorGiven" /></namePart>
											</xsl:when>
										</xsl:choose>
									</contributor>
								</xsl:for-each>
							</xsl:when>
							<xsl:otherwise>
								<contributor seq="1">
									<namePart type="superior">The Australian National University</namePart>
								</contributor>
							</xsl:otherwise>
						</xsl:choose>
						<title><xsl:value-of select="data/name" /></title>
						<xsl:choose>
							<xsl:when test="data/citationPublisher">
								<publisher><xsl:value-of select="data/citationPublisher" /></publisher>
							</xsl:when>
							<xsl:otherwise>
								<publisher>The Australian National University Data Commons</publisher>
							</xsl:otherwise>
						</xsl:choose>
						<xsl:choose>
							<xsl:when test="data/citationYear">
								<date type="publicationDate"><xsl:value-of select="data/citationYear" /></date>
							</xsl:when>
							<xsl:otherwise>
								<date type="publicationDate"><xsl:value-of select="fn:year-from-date(fn:current-date())" /></date>
							</xsl:otherwise>
						</xsl:choose>
					</citationMetadata>
				</citationInfo>
			</xsl:if>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>