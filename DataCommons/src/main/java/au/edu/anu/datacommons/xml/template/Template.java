package au.edu.anu.datacommons.xml.template;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Template
 * 
 * Australian National University Data Commons
 * 
 * The Template class is utilised for marshalling and unmarshalling JAXB objects with the
 * template root element.
 * 
 * JUnit coverage:
 * JAXBTransformTest
 * 
 * Version	Date		Developer			Description
 * 0.1		19/03/2012	Genevieve Turner	Initial build
 * 
 */
@XmlRootElement(name="template")
public class Template {
	private List<TemplateItem> items;
	
	public Template() {
		items = new ArrayList<TemplateItem>();
	}
	
	/**
	 * getItems
	 * 
	 * Get the list of items
	 * 
	 * Version	Date		Developer			Description
	 * 0.1		13/03/2012	Genevieve Turner	Initial build
	 * 
	 * @return The list of items
	 */
	@XmlElement(name="item")
	public List<TemplateItem> getItems() {
		return items;
	}

	/**
	 * setItems
	 * 
	 * Set the list of items
	 * 
	 * Version	Date		Developer			Description
	 * 0.1		13/03/2012	Genevieve Turner	Initial build
	 * 
	 * @param items The list of items
	 */
	public void setItems(List<TemplateItem> items) {
		this.items = items;
	}
}
