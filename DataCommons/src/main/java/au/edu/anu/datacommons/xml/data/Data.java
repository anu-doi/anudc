package au.edu.anu.datacommons.xml.data;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Data
 * 
 * Australian National University Data Comons
 * 
 * Data item for jaxb
 * 
 * Version	Date		Developer				Description
 * 0.1		23/03/2012	Genevieve Turner (GT)	Initial
 * 
 */
@XmlRootElement(name="data")
public class Data {
	private List<JAXBElement<String>> items;
	private List<JAXBElement<Data>> data;
	
	/**
	 * Constructor
	 * 
	 * Version	Date		Developer				Description
	 * 0.1		23/03/2012	Genevieve Turner (GT)	Initial
	 */
	public Data() {
		items = new ArrayList<JAXBElement<String>>();
		data = new ArrayList<JAXBElement<Data>>();
	}
	
	/**
	 * getItems
	 * 
	 * Gets the list of JAXBElements with a string value
	 * 
	 * Version	Date		Developer				Description
	 * 0.1		23/03/2012	Genevieve Turner (GT)	Initial
	 * 
	 * @return The list of elements
	 */
	@XmlAnyElement
	public List<JAXBElement<String>> getItems() {
		return items;
	}
	
	/**
	 * setItems
	 * 
	 * Sets the list of JAXBElements with a string value
	 * 
	 * Version	Date		Developer				Description
	 * 0.1		23/03/2012	Genevieve Turner (GT)	Initial
	 * 
	 * @param items
	 */
	public void setItems(List<JAXBElement<String>> items) {
		this.items = items;
	}

	/**
	 * getData
	 * 
	 * Gets the list of JAXBElements with child nodes
	 * 
	 * Version	Date		Developer				Description
	 * 0.1		23/03/2012	Genevieve Turner (GT)	Initial
	 * 
	 * @return
	 */
	@XmlAnyElement
	public List<JAXBElement<Data>> getData() {
		return data;
	}
	
	/**
	 * setData
	 * 
	 * Sets the list of JAXBElements with child nodes
	 * 
	 * Version	Date		Developer				Description
	 * 0.1		23/03/2012	Genevieve Turner (GT)	Initial
	 * 
	 * @param data
	 */
	public void setData(List<JAXBElement<Data>> data) {
		this.data = data;
	}
}
