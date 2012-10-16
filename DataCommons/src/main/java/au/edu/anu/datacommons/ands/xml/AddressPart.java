package au.edu.anu.datacommons.ands.xml;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

/**
 * AddressPart
 * 
 * Australian National University Data Commons
 * 
 * Relates to the Address Parts of the ANDS RIF-CS schema
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		12/10/2012	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
public class AddressPart {
	private String type;
	private String value;
	
	/**
	 * getType
	 *
	 * Get the address part type
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the type
	 */
	@NotNull(message = "Address parts must have a type")
	@Pattern(regexp="^addressLine|text|telephoneNumber|faxNumber$", message="An address part must be of the type addressLine, text, telephoneNumber, or faxNumber")
	@XmlAttribute
	public String getType() {
		return type;
	}
	
	/**
	 * setType
	 *
	 * Set the address part type
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	/**
	 * getValue
	 *
	 * Get the address part value
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the value
	 */
	@NotNull(message="Address Parts must have a value")
	@XmlValue
	public String getValue() {
		return value;
	}
	
	/**
	 * setValue
	 *
	 * Set the address part value
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
}
