package au.edu.anu.datacommons.xml.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Data
 * 
 * Australian National University Data Comons
 * 
 * An XML Adapter for the DataItem class utilising in marshalling and unmarshalling objects.
 * 
 * JUnit Coverage:
 * None
 * 
 * Version	Date		Developer				Description
 * 0.1		29/03/2012	Genevieve Turner (GT)	Initial
 * 
 */
public class DataItemAdapter extends XmlAdapter<Element, DataItem> {
	static final Logger LOGGER = LoggerFactory.getLogger(DataItemAdapter.class);
	
	private DocumentBuilder documentBuilder_;
	
	/**
	 * getDocumentBuilder
	 * 
	 * If uninitialised creates the document builder object, then it 
	 * 
	 * @return document builder object
	 * @throws Exception
	 */
	private DocumentBuilder getDocumentBuilder() throws Exception {
		if(documentBuilder_ == null) {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			documentBuilder_ = documentBuilderFactory.newDocumentBuilder();
		}
		return documentBuilder_;
	}
	
	/**
	 * marshal
	 * 
	 * Marshals a DataItem object into a w3c dom object to utilise in JAXB processing
	 * 
	 * Version	Date		Developer				Description
	 * 0.1		29/03/2012	Genevieve Turner (GT)	Initial
	 * 
	 * @param dateItem The item to marshal into the xml document
	 * @return The w3c dom object representation of the dataItem
	 */
	@Override
	public Element marshal(DataItem dataItem) throws Exception {
		if (dataItem == null) {
			return null;
		}
		
		Document document = getDocumentBuilder().newDocument();
		Element element = document.createElement(dataItem.getName_());
		element.setTextContent(dataItem.getValue_());
		
		// Populate the child nodes
		for (Entry<String, String> entry : dataItem.getChildValues_().entrySet()) {
			Element childElement = document.createElement(entry.getKey());
			childElement.setTextContent(entry.getValue());
			element.appendChild(childElement);
		}
		return element;
	}

	/**
	 * marshal
	 * 
	 * Unmarshals a w3c dom object into a DataItem object.
	 * 
	 * Version	Date		Developer				Description
	 * 0.1		29/03/2012	Genevieve Turner (GT)	Initial
	 * 
	 * @param element The w3c dom object to unmarshal
	 * @return The unmarshalled DataItem object
	 */
	@Override
	public DataItem unmarshal(Element element) throws Exception {
		if (element == null) {
			return null;
		}
		DataItem dataItem = new DataItem();
		
		dataItem.setName_(element.getLocalName());
		NodeList childNodes = element.getChildNodes();
		
		// Essentially check if it contains just a text node or not
		if (childNodes.getLength() > 1) {
			String key;
			String value;
			
			// Map the child nodes
			Map<String, String> childValues = new HashMap<String, String>();
			for(int i = 0; i < childNodes.getLength(); i++) {
				Node node = childNodes.item(i);
				if(node.getNodeType() == Node.ELEMENT_NODE) {
					Element childElement = (Element) node;
					key = childElement.getLocalName();
					value = childElement.getTextContent();
					
					childValues.put(key, value);
				}
			}
			dataItem.setChildValues_(childValues);
		}
		else {
			dataItem.setValue_(element.getTextContent());
		}
		
		return dataItem;
	}
}
