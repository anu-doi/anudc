package au.edu.anu.datacommons.xml.data;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Data
 * 
 * Australian National University Data Comons
 * 
 * Data item for jaxb
 * 
 * JUnit Coverage:
 * None
 * 
 * Version	Date		Developer				Description
 * 0.1		23/03/2012	Genevieve Turner (GT)	Initial
 * 0.2		29/03/2012	Genevieve Turner (GT)	Added remove elements function and updated to fix issue when unmarshalling
 * 
 */
@XmlRootElement(name="data")
public class Data {
	static final Logger LOGGER = LoggerFactory.getLogger(Data.class);
	
	
	private List<DataItem> items;
	
	/**
	 * Constructor
	 * 
	 * Version	Date		Developer				Description
	 * 0.1		23/03/2012	Genevieve Turner (GT)	Initial
	 * 0.2		29/03/2012	Genevieve Turner (GT)	Updated to fix issue when unmarshalling
	 */
	public Data() {
		items = new ArrayList<DataItem>();
	}
	
	/**
	 * getItems
	 * 
	 * Gets the list of JAXBElements with a string value
	 * 
	 * Version	Date		Developer				Description
	 * 0.1		23/03/2012	Genevieve Turner (GT)	Initial
	 * 0.2		29/03/2012	Genevieve Turner (GT)	Updated to fix issue when unmarshalling
	 * 
	 * @return The list of elements
	 */
	@XmlAnyElement
	public List<DataItem> getItems() {
		return items;
	}
	
	/**
	 * removeElementsByName
	 * 
	 * Removes all the elements with the given name
	 * 
	 * Version	Date		Developer				Description
	 * 0.2		29/03/2012	Genevieve Turner (GT)	Added function
	 * 
	 * @param localpart The name of the fields to remove
	 */
	public void removeElementsByName(String localpart) {
		for (int i = items.size() - 1; i >= 0; i--) {
			DataItem item = items.get(i);
			if(item.getName_().equals(localpart)) {
				items.remove(i);
			}
		}
	}
}
