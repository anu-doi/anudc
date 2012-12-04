package au.edu.anu.datacommons.ands.xml;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;

/**
 * Format
 * 
 * Australian National University Data Commons
 * 
 * Class for the format element in the ANDS RIF-CS schema
 *
 * JUnit Coverage:
 * RegistryObjectsTest
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		04/12/2012	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
public class Format {
	private Identifier identifier;

	/**
	 * getIdentifier
	 *
	 * Get the identifier for the format
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		04/12/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the identifier
	 */
	@Valid
	@NotNull(message="Related Info format requires an identifier")
	@XmlElement(namespace=Constants.ANDS_RIF_CS_NS)
	public Identifier getIdentifier() {
		return identifier;
	}

	/**
	 * setIdentifier
	 *
	 * Set the identifier for the format
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		04/12/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param identifier the identifier to set
	 */
	public void setIdentifier(Identifier identifier) {
		this.identifier = identifier;
	}
}
