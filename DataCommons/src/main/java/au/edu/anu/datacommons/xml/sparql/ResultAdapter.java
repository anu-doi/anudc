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

package au.edu.anu.datacommons.xml.sparql;

import java.util.HashMap;
import java.util.Map.Entry;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * ResultItemAdapter
 * 
 * Australian National University Data Commons
 * 
 * An XmlAdapter that transforms a w3c Element into a Result.
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		01/08/2012	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
public class ResultAdapter extends XmlAdapter<Element, Result> {
	static final Logger LOGGER = LoggerFactory.getLogger(ResultAdapter.class);
	
	private DocumentBuilder documentBuilder_;
	
	/**
	 * getDocumentBuilder
	 *
	 * Instantiates the document builder
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		01/08/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return Document builder for the function.
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
	 * Marshals the Result into an Element
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		01/08/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param result The result to transform to an element
	 * @return The element
	 * @throws Exception
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
	 */
	@Override
	public Element marshal(Result result) throws Exception {
		if (result == null) {
			return null;
		}
		Document document = getDocumentBuilder().newDocument();
		Element element = document.createElement("result");
		
		// Adds the fields to child nodes
		for (Entry<String, ResultItem> entry : result.getFields().entrySet()) {
			Element subElement = document.createElement(entry.getKey());
			ResultItem resultItem = entry.getValue();

			if (resultItem.getIsLiteral() == Boolean.TRUE) {
				Attr attribute = document.createAttribute("uri");
				attribute.setTextContent(resultItem.getValue());
			}
			else {
				subElement.setTextContent(resultItem.getValue());
			}
			element.appendChild(subElement);
		}
		
		return element;
	}

	/**
	 * unmarshal
	 * 
	 * Unmarshals an element into a Result
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		01/08/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param element The element to make in to a Result
	 * @return The result
	 * @throws Exception
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
	 */
	@Override
	public Result unmarshal(Element element) throws Exception {
		if (element == null) {
			return null;
		}
		
		Result result = new Result();
		HashMap<String, ResultItem> resultItems = new HashMap<String, ResultItem>();
		NodeList nodeList = element.getChildNodes();
		// Make the child nodes ResultItems for the fields
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element subElement = (Element) node;
				String name = node.getLocalName();
				ResultItem resultItem = new ResultItem();
				resultItem.setName(name);

				if (subElement.hasAttribute("uri")) {
					resultItem.setIsLiteral(Boolean.TRUE);
					resultItem.setValue(subElement.getAttribute("uri"));
				}
				else {
					resultItem.setIsLiteral(Boolean.FALSE);
					resultItem.setValue(subElement.getTextContent());
				}
				resultItems.put(name, resultItem);
			}
		}
		result.setFields(resultItems);
		return result;
	}
}
