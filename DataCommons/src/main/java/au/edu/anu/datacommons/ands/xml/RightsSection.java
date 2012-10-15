package au.edu.anu.datacommons.ands.xml;

import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

import au.edu.anu.datacommons.validator.AtLeastOneOf;

/**
 * RightsSection
 * 
 * Australian National University Data Commons
 * 
 * Class for setting the rightsStatement, licence and accessRights information
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		15/10/2012	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
@AtLeastOneOf(fieldNames={"rightsUri", "type", "value"}, message="Rights fields require either a uri, licence type or text value")
public class RightsSection {
	private String rightsUri;
	private String type;
	private String value;
	
	/**
	 * getRightsUri
	 *
	 * Get the rights uri
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the rightsUri
	 */
	@XmlAttribute
	public String getRightsUri() {
		return rightsUri;
	}
	
	/**
	 * setRightsUri
	 *
	 * Set the rights uri
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param rightsUri the rightsUri to set
	 */
	public void setRightsUri(String rightsUri) {
		this.rightsUri = rightsUri;
	}
	
	/**
	 * getType
	 *
	 * Get the rights type
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the type
	 */
	//TODO - fix this regex so its more efficient
	@Pattern(regexp="^CC-BY|CC-BY-SA|CC-BY-ND|CC-BY-NC|CC-BY-NC-SA|CC-BY-NC-ND|GPL|AusGoalRestrictive|NoLicence|Unknown/Other$", message="")
	@XmlAttribute
	public String getType() {
		return type;
	}
	
	/**
	 * setType
	 *
	 * Set the rights type
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
	 * Get the rights value
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the value
	 */
	@XmlValue
	public String getValue() {
		return value;
	}
	
	/**
	 * setValue
	 *
	 * Set the rights value
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
