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
	
	private List<DataItem> items_;
	
	/**
	 * Constructor
	 * 
	 * Version	Date		Developer				Description
	 * 0.1		23/03/2012	Genevieve Turner (GT)	Initial
	 * 0.2		29/03/2012	Genevieve Turner (GT)	Updated to fix issue when unmarshalling
	 */
	public Data() {
		items_ = new ArrayList<DataItem>();
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
		return items_;
	}
	
	/**
	 * setItems
	 *
	 * Sets the list of items
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.3		20/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param items The list of elements
	 */
	public void setItems(List<DataItem> items) {
		this.items_ = items;
	}
	
	/**
	 * removeElementsByName
	 * 
	 * Removes all the elements with the given name
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.2		29/03/2012	Genevieve Turner (GT)	Added function
	 * 0.3		20/06/2012	Genevieve Turner (GT)	Amended to return a list of removed items
	 * </pre>
	 * 
	 * @param localpart The name of the fields to remove
	 */
	public List<DataItem> removeElementsByName(String localpart) {
		List<DataItem> removedItems = new ArrayList<DataItem>();
		for (int i = items_.size() - 1; i >= 0; i--) {
			DataItem item = items_.get(i);
			if(item.getName().equals(localpart)) {
				removedItems.add(items_.remove(i));
			}
		}
		return removedItems;
	}
	
	/**
	 * hasElement
	 * 
	 * Checks if there is an element with the specified name
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.3		15/10/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @param localpart The name of the fields to check if it exists
	 */
	public boolean hasElement(String localpart) {
		for (int i = items_.size() - 1; i >= 0; i--) {
			DataItem item = items_.get(i);
			if (item.getName().equals(localpart)) {
				return true;
			}
		}
		return false;
	}
}
