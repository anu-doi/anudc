package au.edu.anu.datacommons.xml.solr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
 * SolrMapAdapter
 * 
 * Australian National University Data Commons
 * 
 * Adaptor to create a map for the solr doc
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		08/06/2012	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
public class SolrMapAdapter extends XmlAdapter<Element, SolrDoc> {
	private static final Logger LOGGER = LoggerFactory.getLogger(SolrMapAdapter.class);
	
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
	 * Marshals a SolrDoc to an Element
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		08/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param doc The SolrDoc to transform
	 * @return An xml element
	 * @throws Exception
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
	 */
	@Override
	public Element marshal(SolrDoc doc) throws Exception {
		if (doc == null) {
			return null;
		}
		Map<String, List<String>> map = doc.getReturnVals();
		Document document = getDocumentBuilder().newDocument();
		Element element = document.createElement("doc");
		for (Entry<String, List<String>> entry : map.entrySet()) {
			Element arrElement = document.createElement("arr");
			arrElement.setAttribute("name", entry.getKey());
			for (String item : entry.getValue()) {
				Element childElement = document.createElement("str");
				childElement.setTextContent(item);
				arrElement.appendChild(childElement);
			}
			element.appendChild(arrElement);
		}
		return element;
	}
	
	/**
	 * unmarshal
	 * 
	 * Unmarshals an Element to a SolrDoc
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		08/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param element An xml element to transform
	 * @return A SolrDoc
	 * @throws Exception
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
	 */
	@Override
	public SolrDoc unmarshal(Element element) throws Exception {
		if (element == null) {
			return null;
		}
		SolrDoc doc = new SolrDoc();
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		
		NodeList nodeList = element.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element nodeElement = (Element) node;
				String key = nodeElement.getAttribute("name");
				List<String> values = new ArrayList<String>();
				NodeList childNodeList = nodeElement.getChildNodes();
				//values.add(node.getTextContent());
				for (int j = 0; j < childNodeList.getLength(); j++) {
					Node childNode = childNodeList.item(j);
					values.add(childNode.getTextContent());
				}
				map.put(key, values);
			}
		}
		doc.setReturnVals(map);
		return doc;
	}
}
