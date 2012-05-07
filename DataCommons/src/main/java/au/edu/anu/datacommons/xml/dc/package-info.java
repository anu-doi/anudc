/**
 * package-info
 * 
 * Australian National University Data Commons
 * 
 * This package is used in the creation of dublin core information
 * 
 * JUnit coverage:
 * 
 * Version	Date		Developer			Description
 * 0.1		23/03/2012	Genevieve Turner	Initial build
 * 
 */

@XmlSchema (
	xmlns = {
			@XmlNs(prefix="dc", namespaceURI="http://purl.org/dc/elements/1.1/"),
			@XmlNs(prefix="oai_dc", namespaceURI="http://www.openarchives.org/OAI/2.0/oai_dc/")
	}
)

package au.edu.anu.datacommons.xml.dc;

import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlSchema;

