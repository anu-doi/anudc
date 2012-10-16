package au.edu.anu.datacommons.ands.xml;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

/**
 * Description
 * 
 * Australian National University Data Commons
 * 
 * Class for the description element in the ANDS RIF-CS schema
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
public class Description {
	private String type;
	private String lang;
	private String value;
	
	/**
	 * getType
	 *
	 * Get the description type
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the type
	 */
	//TODO filter by type e.g. significanceStatement only for collections
	@NotNull(message="The description type may not be null")
	@Pattern(regexp="^brief|full|logo|note|deliveryMethod|significanceStatement$", 
			message="The description type must be one of brief, full, logo, note,deliveryMethod, or significanceStatement")
	@XmlAttribute
	public String getType() {
		return type;
	}
	
	/**
	 * setType
	 *
	 * Set the description type
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
	 * Get the description language
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
	 * Set the description language
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
	 * Get the description value
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the value
	 */
	@NotNull(message="The description value may not be null")
	@Size(max=4000, message="The description can only be a maximum of 4000 characters")
	@XmlValue
	public String getValue() {
		return value;
	}

	/**
	 * setValue
	 *
	 * Set the description value
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
