<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/">
		<xsl:param name="key" />
		
		<registryObjects xmlns="http://ands.org.au/standards/rif-cs/registryObjects" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
			xsi:schemaLocation="http://ands.org.au/standards/rif-cs/registryObjects http://services.ands.org.au/documentation/rifcs/schema/registryObjects.xsd">
			<registryObject group="The Australian National University">
				<key>http://anu.edu.au/<xsl:value-of select="$key" /></key>
				<originatingSource>http://anu.edu.au</originatingSource>
				
			</registryObject>
		</registryObjects>
	</xsl:template>
</xsl:stylesheet>