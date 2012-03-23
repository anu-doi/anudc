package au.edu.anu.datacommons.xml.dc;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * DublinCore
 * 
 * Australian National University Data Comons
 * 
 * Dublin Core JAXB class
 * 
 * Version	Date		Developer				Description
 * 0.1		08/03/2012	Genevieve Turner (GT)	Initial
 * 
 */
@XmlRootElement(name="dc", namespace=DublinCoreConstants.OAI_DC)
public class DublinCore {
	List<JAXBElement<String>> items;
	
	/**
	 * Constructor
	 * 
	 * Version	Date		Developer				Description
	 * 0.1		08/03/2012	Genevieve Turner (GT)	Initial
	 */
	public DublinCore() {
		items = new ArrayList<JAXBElement<String>>();
	}
	
	/**
	 * getItems
	 * 
	 * Returns a list of dublin core items
	 * 
	 * Version	Date		Developer				Description
	 * 0.1		08/03/2012	Genevieve Turner (GT)	Initial
	 * 
	 * @return The list of dublin core items
	 */
	//TODO potentially add in a spefic vocab that can be used
	@XmlAnyElement
	public List<JAXBElement<String>> getItems() {
		return items;
	}
	
	/**
	 * setItems
	 * 
	 * Sets the list of dublin core items
	 * 
	 * Version	Date		Developer				Description
	 * 0.1		08/03/2012	Genevieve Turner (GT)	Initial
	 * 
	 * @param items The dublin core items
	 */
	public void setItems(List<JAXBElement<String>> items) {
		this.items = items;
	}
	
}
