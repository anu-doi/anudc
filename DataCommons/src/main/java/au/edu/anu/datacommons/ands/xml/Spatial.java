package au.edu.anu.datacommons.ands.xml;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

/**
 * Spatial
 * 
 * Australian National University Data Commons
 * 
 * Class for the spatial element in the ANDS RIF-CS schema
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
public class Spatial {
	private String type;
	private String lang;
	private String value;
	
	/**
	 * getType
	 *
	 * Get the spatial type
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the type
	 */
	@NotNull(message="A spatial record is missing a type")
	@XmlAttribute
	public String getType() {
		return type;
	}
	
	/**
	 * setType
	 *
	 * Set the spatial type
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
	 * getLang
	 *
	 * Get the spatial language
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the lang
	 */
	@XmlAttribute
	public String getLang() {
		return lang;
	}
	
	/**
	 * setLang
	 *
	 * Set the spatial language
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param lang the lang to set
	 */
	public void setLang(String lang) {
		this.lang = lang;
	}
	
	/**
	 * getValue
	 *
	 * Get the spatial language
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the value
	 */
	@NotNull(message="A spatial record is missing a value")
	@XmlValue
	public String getValue() {
		return value;
	}
	
	/**
	 * setValue
	 *
	 * Set the spatial language
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
