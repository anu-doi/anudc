package au.edu.anu.datacommons.xml.data;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * Data
 * 
 * Australian National University Data Comons
 * 
 * Data item class for jaxb processing of the 'data' type xml objects.
 * 
 * JUnit Coverage:
 * None
 * 
 * Version	Date		Developer				Description
 * 0.1		29/03/2012	Genevieve Turner (GT)	Initial
 * 
 */
@XmlJavaTypeAdapter(DataItemAdapter.class)
public class DataItem {
	private String name_;
	private String value_;
	private Map<String, String> childValues_;
	
	/**
	 * Constructor class.
	 */
	public DataItem() {
		childValues_ = new HashMap<String, String>();
	}
	
	/**
	 * getName_
	 * 
	 * Gets name of the object
	 * 
	 * Version	Date		Developer				Description
	 * 0.1		29/03/2012	Genevieve Turner (GT)	Initial
	 * 
	 * @return The name of the object
	 */
	public String getName() {
		return name_;
	}
	
	/**
	 * setName_
	 * 
	 * Sets the name of the xml object
	 * 
	 * Version	Date		Developer				Description
	 * 0.1		29/03/2012	Genevieve Turner (GT)	Initial
	 * 
	 * @param name_ The name of the object
	 */
	public void setName(String name_) {
		this.name_ = name_;
	}
	
	/**
	 * getValue_
	 * 
	 * Gets the text node value of the xml object
	 * 
	 * Version	Date		Developer				Description
	 * 0.1		29/03/2012	Genevieve Turner (GT)	Initial
	 * 
	 * @return The text node value of the object
	 */
	public String getValue() {
		return value_;
	}
	
	/**
	 * setValue_
	 * 
	 * Sets the text node value of the object
	 * 
	 * Version	Date		Developer				Description
	 * 0.1		29/03/2012	Genevieve Turner (GT)	Initial
	 * 
	 * @param value_ The text node value of the object
	 */
	public void setValue(String value_) {
		this.value_ = value_;
	}

	/**
	 * getChildValues_
	 * 
	 * Gets the element name and text value of the child nodes.
	 * 
	 * Version	Date		Developer				Description
	 * 0.1		29/03/2012	Genevieve Turner (GT)	Initial
	 * 
	 * @return A map containing the name and values of the child nodes
	 */
	public Map<String, String> getChildValues() {
		return childValues_;
	}

	/**
	 * setChildValues_
	 * 
	 * Sets the element name and text value of the child nodes.
	 * 
	 * Version	Date		Developer				Description
	 * 0.1		29/03/2012	Genevieve Turner (GT)	Initial
	 * 
	 * @param childValues_ A map containing the name and values of the child nodes
	 */
	public void setChildValues(Map<String, String> childValues_) {
		this.childValues_ = childValues_;
	}
}
