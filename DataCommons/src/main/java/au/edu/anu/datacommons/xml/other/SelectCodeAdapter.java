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

package au.edu.anu.datacommons.xml.other;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import au.edu.anu.datacommons.data.db.model.SelectCode;
import au.edu.anu.datacommons.data.db.model.SelectCodePK;

/**
 * SelectCodeAdapter
 * 
 * Australian National University Data Commons
 * 
 * Adapter classs for SelectCode
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		22/06/2012	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
public class SelectCodeAdapter extends XmlAdapter<Element, SelectCode> {
	static final Logger LOGGER = LoggerFactory.getLogger(SelectCodeAdapter.class);
	
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
	 * Marshals a SelectCode to an XML Element
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		22/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param selectCode
	 * @return
	 * @throws Exception
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
	 */
	@Override
	public Element marshal(SelectCode selectCode) throws Exception {
		if (selectCode == null) {
			return null;
		}
		
		Document document = getDocumentBuilder().newDocument();
		Element element = document.createElement(selectCode.getId().getSelect_name());
		Element idElement = document.createElement("id");
		idElement.setTextContent(selectCode.getId().getCode());
		element.appendChild(idElement);
		
		Element nameElement = document.createElement("name");
		nameElement.setTextContent(selectCode.getDescription());
		element.appendChild(nameElement);
		
		return element;
	}
	
	/**
	 * unmarshal
	 * 
	 * Unmarhals an element to a select code.
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		22/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param element
	 * @return
	 * @throws Exception
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
	 */
	@Override
	public SelectCode unmarshal(Element element) throws Exception {
		if (element == null) {
			return null;
		}
		SelectCode selectCode = new SelectCode();
		SelectCodePK selectCodePK = new SelectCodePK();
		selectCodePK.setSelect_name(element.getLocalName());
		
		NodeList idNodeList = element.getElementsByTagName("id");
		if (idNodeList.getLength() > 0) {
			Node node = idNodeList.item(0);
			selectCodePK.setCode(node.getTextContent());
		}
		selectCode.setId(selectCodePK);
		
		NodeList nameNodeList = element.getElementsByTagName("name");
		if (nameNodeList.getLength() > 0) {
			Node node = nameNodeList.item(0);
			selectCode.setDescription((node.getTextContent()));
		}
		
		return selectCode;
	}
}
