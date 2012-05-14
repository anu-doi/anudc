<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/">
		<xsl:param name="key" />
		
		<registryObjects xmlns="http://ands.org.au/standards/rif-cs/registryObjects" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
			xsi:schemaLocation="http://ands.org.au/standards/rif-cs/registryObjects http://services.ands.org.au/documentation/rifcs/schema/registryObjects.xsd">
			<registryObject group="The Australian National University">
				<key>http://anu.edu.au/<xsl:value-of select="$key" /></key>
				<something>${key}</something>
				<originatingSource>http://anu.edu.au</originatingSource>
				<xsl:choose>
					<xsl:when test="data/type/text() = 'Collection'">
						<collection>
							<xsl:attribute name="type">
								<xsl:value-of select="data/subType/text()" />
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
		<xsl:if test="data/name">
			<name type="primary">
				<namePart>
					<xsl:value-of select="data/name" />
				</namePart>
			</name>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>