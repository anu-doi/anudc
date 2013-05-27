package au.edu.anu.datacommons.metadatastores;

import javax.xml.bind.annotation.XmlElement;

/**
 * Subject
 * 
 * Australian National University Data Commons
 * 
 * Class that contains subjects from grants
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		24/05/2013	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
public class Subject {
	private String code_;
	private String value_;
	private String percentage_;
	
	/**
	 * getCode
	 *
	 * Get the subject code
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		20/05/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the code
	 */
	@XmlElement(name="code")
	public String getCode() {
		return code_;
	}
	
	/**
	 * setCode
	 *
	 * Set the subject code
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		20/05/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code_ = code;
	}
	
	/**
	 * getValue
	 *
	 * Get the subject value
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		20/05/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the value
	 */
	@XmlElement(name="value")
	public String getValue() {
		return value_;
	}
	
	/**
	 * setValue
	 *
	 * Set the subject value
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		20/05/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value_ = value;
	}
	
	/**
	 * getPercentage
	 *
	 * Get the subject percentage
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		20/05/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the percentage
	 */
	@XmlElement(name="percentage")
	public String getPercentage() {
		return percentage_;
	}
	
	/**
	 * setPercentage
	 *
	 * Set the subject percentage
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		20/05/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param percentage the percentage to set
	 */
	public void setPercentage(String percentage) {
		this.percentage_ = percentage;
	}
	
}
