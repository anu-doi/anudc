<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0" exclude-result-prefixes="exts" xmlns:audit="info:fedora/fedora-system:def/audit#" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:exts="xalan://dk.defxws.fedoragsearch.server.GenericOperationsImpl" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:dtu_meta="http://www.dtu.dk/dtu_meta/" xmlns:foxml="info:fedora/fedora-system:def/foxml#" xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:meta="http://www.dtu.dk/dtu_meta/meta/" xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#">
	<xsl:output method="xml" indent="yes" encoding="UTF-8"/>
	<xsl:param name="REPOSITORYNAME" select="'FgsRepos'"/>
	<xsl:param name="REPOSBASEURL" select="'http://localhost:8380/fedora'"/>
	<xsl:param name="FEDORASOAP" select="'http://localhost:8380/fedora/services'"/>
	<xsl:param name="FEDORAUSER" select="'fedoraAdmin'"/>
	<xsl:param name="FEDORAPASS" select="'fedoraAdmin'"/>
	<xsl:param name="TRUSTSTOREPATH" select="'trustStorePath'"/>
	<xsl:param name="TRUSTSTOREPASS" select="'trustStorePass'"/>
	<xsl:variable name="PID" select="/foxml:digitalObject/@PID"/>
	<xsl:template match="/">
		<!--The following allows only active FedoraObjects to be indexed.-->
		<xsl:if test="foxml:digitalObject/foxml:objectProperties/foxml:property[@NAME='info:fedora/fedora-system:def/model#state' and @VALUE='Active']">
			<xsl:if test="not(foxml:digitalObject/foxml:datastream[@ID='METHODMAP'] or foxml:digitalObject/foxml:datastream[@ID='DS-COMPOSITE-MODEL'])">
				<xsl:if test="starts-with($PID,'')">
					<xsl:apply-templates mode="activeFedoraObject"/>
				</xsl:if>
			</xsl:if>
		</xsl:if>
		<!--The following allows inactive FedoraObjects to be deleted from the index.-->
		<xsl:if test="foxml:digitalObject/foxml:objectProperties/foxml:property[@NAME='info:fedora/fedora-system:def/model#state' and @VALUE='Inactive']">
			<xsl:if test="not(foxml:digitalObject/foxml:datastream[@ID='METHODMAP'] or foxml:digitalObject/foxml:datastream[@ID='DS-COMPOSITE-MODEL'])">
				<xsl:if test="starts-with($PID,'')">
					<xsl:apply-templates mode="inactiveFedoraObject"/>
				</xsl:if>
			</xsl:if>
		</xsl:if>
	</xsl:template>
	<xsl:template match="/foxml:digitalObject" mode="activeFedoraObject">
		<add overwrite="true" commitWithin="1000" allowDups="false">
			<doc>
				<field name="id">
					<xsl:value-of select="$PID"/>
				</field>
				<field name="PID">
					<xsl:value-of select="$PID"/>
				</field>
				<field name="REPOSITORYNAME">
					<xsl:value-of select="$REPOSITORYNAME"/>
				</field>
				<field name="REPOSBASEURL">
					<xsl:value-of select="substring($FEDORASOAP, 1, string-length($FEDORASOAP)-9)"/>
				</field>
				<field name="TITLE_UNTOK">
					<xsl:value-of select="foxml:datastream/foxml:datastreamVersion[last()]/foxml:xmlContent/oai_dc:dc/dc:title"/>
				</field>
				<field name="AUTHOR_UNTOK">
					<xsl:value-of select="foxml:datastream/foxml:datastreamVersion[last()]/foxml:xmlContent/oai_dc:dc/dc:creator"/>
				</field>
				<!--indexing foxml property fields-->
				<xsl:for-each select="foxml:objectProperties/foxml:property">
					<field>
						<xsl:attribute name="name">
							<xsl:value-of select="concat('fgs.', substring-after(@NAME,'#'))"/>
						</xsl:attribute>
						<xsl:value-of select="@VALUE"/>
					</field>
				</xsl:for-each>
				<!--indexing foxml fields-->
				
				<xsl:for-each select="foxml:datastream[@ID='XML_SOURCE']/foxml:datastreamVersion[last()]/foxml:xmlContent/data/*">
					<xsl:call-template name="fields">
						<xsl:with-param name="fieldname">unpublished</xsl:with-param>
					</xsl:call-template>
				</xsl:for-each>
				
				<xsl:for-each select="foxml:datastream[@ID='RELS-EXT']/foxml:datastreamVersion[last()]/foxml:xmlContent/rdf:RDF/rdf:Description/*">
					<xsl:choose>
						<xsl:when test="namespace-uri()='http://anu.edu.au/related/'">
							<field>
								<xsl:attribute name="name">published.related.<xsl:value-of select="name()" /></xsl:attribute>
								<xsl:value-of select="substring-after(@rdf:resource, 'info:fedora/')" />
							</field>
						</xsl:when>
					</xsl:choose>
				</xsl:for-each>
				
				<xsl:for-each select="foxml:datastream[@ID='RELS-EXT']/foxml:datastreamVersion[last()]/foxml:xmlContent/rdf:RDF/rdf:Description/*[namespace-uri()='http://anu.edu.au/' and local-name()='publish']">
					<field name="location.published">
						<xsl:value-of select="text()" />
					</field>
				</xsl:for-each>
				
				<xsl:for-each select="foxml:datastream[@ID='XML_TEMPLATE' and @CONTROL_GROUP='X']/foxml:datastreamVersion[last()]/foxml:xmlContent/*">
					<field name="template.name">
						<xsl:value-of select="//name" />
					</field>
					<field name="template.briefDesc">
						<xsl:value-of select="//briefDesc" />
					</field>
					<field name="template.type">
						<xsl:text>Template</xsl:text>
					</field>
					<field name="template.subType">
						<xsl:value-of select="//item[@name='type']/@defaultValue" />
					</field>
				</xsl:for-each>
				
				<field name="published.combinedAuthors">
					<xsl:for-each select="foxml:datastream[@ID='RELS-EXT']/foxml:datastreamVersion[last()]/foxml:xmlContent/rdf:RDF/rdf:Description/*[namespace-uri()='http://anu.edu.au/related/' and local-name()='isOutputOf']">
						<xsl:variable name="docstr"><xsl:value-of select="$REPOSBASEURL" />/get/<xsl:value-of select="substring-after(@rdf:resource, 'info:fedora/')" />/XML_PUBLISHED</xsl:variable>
						<xsl:value-of select="document($docstr)/data/name" />
						<xsl:if test="not(position() = last())"> &amp; </xsl:if>
					</xsl:for-each>
				</field>
				
				<xsl:for-each select="foxml:datastream[@CONTROL_GROUP='M' and @ID='XML_PUBLISHED']">
					<xsl:variable name="docstr"><xsl:value-of select="$REPOSBASEURL" />/get/<xsl:value-of select="$PID" />/XML_PUBLISHED</xsl:variable>
					<xsl:for-each select="document($docstr)/data/*">
						<xsl:call-template name="fields">
							<xsl:with-param name="fieldname">published</xsl:with-param>
						</xsl:call-template>
					</xsl:for-each>
					<xsl:if test="document($docstr)/data/coverageDates">
						<field name="published.combinedDates">
							<xsl:for-each select="document($docstr)/data/coverageDates">
								<xsl:sort select="concat(dateFrom,'_', dateTo,'_', dateText)" />
								<xsl:choose>
									<xsl:when test="dateFrom != '' and dateTo != ''">
										<xsl:value-of select="concat(dateFrom,' - ',dateTo)" />
									</xsl:when>
									<xsl:when test="dateFrom != '' or dateTo != ''">
										<xsl:value-of select="concat(dateFrom,dateTo)" />
									</xsl:when>
								</xsl:choose>
								<xsl:if test="dateText != ''">
									<xsl:value-of select="dateText" />
								</xsl:if>
								<xsl:if test="not(position() = last())">,</xsl:if>
							</xsl:for-each>
						</field>
					</xsl:if>
				</xsl:for-each>
				
				<field name="unpublished.all">
					<xsl:for-each select="foxml:datastream[@ID='XML_SOURCE']/foxml:datastreamVersion[last()]/foxml:xmlContent//text()">
						<xsl:value-of select="."/>
						<xsl:text> </xsl:text>
					</xsl:for-each>
				</field>
				<field name="published.all">
					<xsl:for-each select="foxml:datastream[@CONTROL_GROUP='M' and @ID='XML_PUBLISHED']">
						<xsl:value-of select="exts:getDatastreamText($PID, $REPOSITORYNAME, @ID, $FEDORASOAP, $FEDORAUSER, $FEDORAPASS, $TRUSTSTOREPATH, $TRUSTSTOREPASS)"/>
						<xsl:text> </xsl:text>
					</xsl:for-each> 
				</field>
			</doc>
		</add>
	</xsl:template>
	<xsl:template match="/foxml:digitalObject" mode="inactiveFedoraObject">
		<delete>
			<id>
				<xsl:value-of select="$PID"/>
			</id>
		</delete>
	</xsl:template>
	<xsl:template name="fields">
		<xsl:param name="fieldname" />
		<xsl:variable name="name" select="name()" />
		
		<xsl:choose>
			<xsl:when test="count(./*) > 0"> 
				<xsl:for-each select="./*">
					<xsl:call-template name="fields">
						<xsl:with-param name="fieldname" select="concat($fieldname,'.',$name)" />
					</xsl:call-template>
				</xsl:for-each>
			</xsl:when>
			<xsl:otherwise>
				<field>
					<xsl:attribute name="name">
						<xsl:value-of select="concat($fieldname,'.',name())" />
					</xsl:attribute>
					<xsl:value-of select="text()" />
				</field>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
