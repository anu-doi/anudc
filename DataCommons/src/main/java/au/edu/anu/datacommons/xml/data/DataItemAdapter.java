/*******************************************************************************
 * Australian National University Data Commons
 * Copyright (C) 2013  The Australian National University
 * 
 * This file is part of Australian National University Data Commons.
 * 
 * Australian National University Data Commons is free software: you
 * can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package au.edu.anu.datacommons.xml.data;

import java.util.ArrayList;
import java.util.List;

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
		Element element = document.createElement(dataItem.getName());
		if (dataItem.getDescription() != null) {
			element.setAttribute("code", dataItem.getValue());
			element.setTextContent(dataItem.getDescription());
		}
		else {
			element.setTextContent(dataItem.getValue());
		}
		
		for (DataItem item : dataItem.getChildValues()) {
			Element childElement = marshal(item);
			Node childNode = document.importNode(childElement, true);
			
			element.appendChild(childNode);
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
		
		dataItem.setName(element.getLocalName());
		NodeList childNodes = element.getChildNodes();
		
		// Essentially check if it contains just a text node or not
		if (childNodes.getLength() > 1) {
			// Map the child nodes
			List<DataItem> childValues = new ArrayList<DataItem>();
			for (int i = 0; i < childNodes.getLength(); i++) {
				Node node = childNodes.item(i);
				if(node.getNodeType() == Node.ELEMENT_NODE) {
					Element childElement = (Element) node;
					DataItem childItem = unmarshal(childElement);
					childValues.add(childItem);
				}
			}
			dataItem.setChildValues(childValues);
		}
		else {
			if (element.hasAttribute("code")) {
				dataItem.setValue(element.getAttribute("code"));
				dataItem.setDescription(element.getTextContent());
			}
			else {
				dataItem.setValue(element.getTextContent());
			}
		}
		
		return dataItem;
	}
}
