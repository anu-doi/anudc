package au.edu.anu.datacommons.xml.template;

import javax.xml.bind.annotation.XmlAttribute;

/**
 * TemplateOption
 * 
 * Australian National University Data Commons
 * 
 * The TemplateOption class is utilised for marshalling and unmarshalling JAXB objects with the
 * template root element.
 * 
 * JUnit coverage:
 * JAXBTransformTest
 * 
 * Version	Date		Developer			Description
 * 0.1		19/03/2012	Genevieve Turner	Initial build
 * 
 */
public class TemplateOption {
	private String label;
	private String value;

	/**
	 * getLabel
	 * 
	 * Returns the label attribute of the option
	 * 
	 * Version	Date		Developer			Description
	 * 0.1		13/03/2012	Genevieve Turner	Initial build
	 * 
	 * @return The label of the option
	 */
	@XmlAttribute
	public String getLabel() {
		return label;
	}
	
	/**
	 * setLabel
	 * 
	 * Sets the label attribute of the option
	 * 
	 * Version	Date		Developer			Description
	 * 0.1		13/03/2012	Genevieve Turner	Initial build
	 * 
	 * @param label The label of the option
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * getValue
	 * 
	 * Returns the value attribute of the item
	 * 
	 * Version	Date		Developer			Description
	 * 0.1		13/03/2012	Genevieve Turner	Initial build
	 * 
	 * @return The value of the option
	 */
	@XmlAttribute
	public String getValue() {
		return value;
	}

	/**
	 * setValue
	 * 
	 * Sets the value attribute of the option
	 * 
	 * Version	Date		Developer			Description
	 * 0.1		13/03/2012	Genevieve Turner	Initial build
	 * 
	 * @param value The value of the option
	 */
	public void setValue(String value) {
		this.value = value;
	}
}
