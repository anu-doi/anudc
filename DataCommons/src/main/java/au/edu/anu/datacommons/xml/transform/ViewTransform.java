package au.edu.anu.datacommons.xml.transform;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import au.edu.anu.datacommons.data.db.dao.FedoraObjectDAOImpl;
import au.edu.anu.datacommons.data.db.dao.GenericDAO;
import au.edu.anu.datacommons.data.db.dao.GenericDAOImpl;
import au.edu.anu.datacommons.data.db.model.AuditObject;
import au.edu.anu.datacommons.data.db.model.FedoraObject;
import au.edu.anu.datacommons.data.db.model.Groups;
import au.edu.anu.datacommons.data.fedora.FedoraBroker;
import au.edu.anu.datacommons.properties.GlobalProps;
import au.edu.anu.datacommons.security.CustomUser;
import au.edu.anu.datacommons.security.service.GroupService;
import au.edu.anu.datacommons.security.service.GroupServiceImpl;
import au.edu.anu.datacommons.util.Constants;
import au.edu.anu.datacommons.util.Util;
import au.edu.anu.datacommons.xml.data.Data;
import au.edu.anu.datacommons.xml.data.DataItem;
import au.edu.anu.datacommons.xml.dc.DublinCore;
import au.edu.anu.datacommons.xml.dc.DublinCoreConstants;
import au.edu.anu.datacommons.xml.other.OptionList;
import au.edu.anu.datacommons.xml.template.Template;
import au.edu.anu.datacommons.xml.template.TemplateColumn;
import au.edu.anu.datacommons.xml.template.TemplateItem;

import com.yourmediashelf.fedora.client.FedoraClientException;
import com.yourmediashelf.fedora.generated.access.DatastreamType;

/**
 * ViewTransform
 * 
 * Australian National University Data Commons
 * 
 * The View Transform class transforms xml documents to something more useable
 * 
 * JUnit coverage:
 * JAXBTransformTest
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		19/03/2012	Genevieve Turner (GT)	Initial build
 * 0.2		23/03/2012	Genevieve Turner (GT)	Updated to include saving
 * 0.3		29/03/2012	Genevieve Turner (GT)	Updated for editing
 * 0.4		26/04/2012	Genevieve Turner (GT)	Some updates for differences between published and non-published records
 * 0.5		03/05/2012	Genevieve Turner (GT)	Updated to use fedora objects instead of a string for fedora items
 * 0.6		14/05/2012	Genevieve Turner (GT)	Updated to use namespace from a property
 * 0.7		15/05/2012	Genevieve Turner (GT)	Updated to fix issue with the dublin core title field
 * 0.8		28/05/2012	Genevieve Turner (GT)	Added groups to code
 * 0.9		20/06/2012	Genevieve Turner (GT)	Updated to allow the display of the object type
 * 0.10		20/06/2012	Genevieve Turner (GT)	Updated to perform additions to the audit object table
 * </pre>
 * 
 */
public class ViewTransform
{
	static final Logger LOGGER = LoggerFactory.getLogger(ViewTransform.class);
	
	private List<String> processedValues_;
	
	private List<DataItem> removedItems_;
	private List<DataItem> addedItems_;
	
	/**
	 * Constructor
	 * 
	 * Initialises variables when object is created
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.3		29/03/2012	Genevieve Turner (GT)	Updated for editing
	 * </pre>
	 */
	public ViewTransform() {
		processedValues_ = new ArrayList<String>();
		removedItems_ = new ArrayList<DataItem>();
		addedItems_ = new ArrayList<DataItem>();
	}
	
	/**
	 * getPage
	 * 
	 * Transforms to a document specified by the layout and performs the transformation
	 * on either the template or the item depending on which values are given.  This generally
	 * transforms the given documents to either a html page or an xml document
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		19/03/2012	Genevieve Turner (GT)	Initial build
	 * 0.4		26/04/2012	Genevieve Turner (GT)	Some updates for differences between published and non-published records
	 * 0.5		02/05/2012	Genevieve Turner (GT)	Updates to display differences between published and non-published records
	 * 0.8		28/05/2012	Genevieve Turner (GT)	Updated for retrieving data from the database
	 * 0.9		20/06/2012	Genevieve Turner (GT)	Updated to allow the display of the object type
	 * </pre>
	 * 
	 * @param layout The layout to use with display (i.e. the xsl stylesheet)
	 * @param template The template that determines the fields on the screen
	 * @param fedoraObject The item to retrieve data for
	 * @param fieldName The field to retrieve data for
	 * @param editMode Whether the request is in edit mode or not
	 * @return Returns a map containing the page information and what type the object is
	 * @throws FedoraClientException
	 */
	public Map<String, Object> getPage (String layout, String template, FedoraObject fedoraObject, String fieldName, boolean editMode) throws FedoraClientException
	{
		Map<String, Object> values = new HashMap<String, Object>();
		LOGGER.debug("In getPage");
		Map<String, Object> parameters = new HashMap<String, Object>();
		InputStream xmlStream = getXMLInputStream(template, fedoraObject);

		if (xmlStream == null) {
			LOGGER.warn("XML Stream is empty");
			return values;
		}
		
		InputStream xslStream = getInputStream(layout, Constants.XSL_SOURCE);
		
		if (xslStream == null) {
			LOGGER.warn("XSL Stream is empty");
			return values;
		}
		
		if(Util.isNotEmpty(template)) {
			parameters.put("tmplt", template);
		}
		if(fedoraObject != null) {
			parameters.put("item", fedoraObject.getObject_id());
		}
		if(Util.isNotEmpty(layout)) {
			parameters.put("layout", layout);
		}
		if(Util.isNotEmpty(fieldName)){
			parameters.put("fieldName", fieldName);
		}
		
		if (fedoraObject != null) {
			InputStream dataStream = null;
			Document modifiedDocument = null;
			
			if(editMode) {
				dataStream = FedoraBroker.getDatastreamAsStream(fedoraObject.getObject_id(), Constants.XML_SOURCE);
			} else {
				List<DatastreamType> datastreamList = FedoraBroker.getDatastreamList(fedoraObject.getObject_id()); //FedoraBroker.getDatastreamAsStream(pid, streamId)
				boolean hasXMLSource = false;
				boolean hasXMLPublished = false;
				for (DatastreamType datastream : datastreamList) {
					String dsId = datastream.getDsid();
					LOGGER.debug("Data Source id: {}", dsId);
					if (dsId.equals(Constants.XML_SOURCE)) {
						hasXMLSource = true;
					}
					else if(dsId.equals(Constants.XML_PUBLISHED)) {
						hasXMLPublished = true;
					}
				}
				if(hasXMLPublished) {
					dataStream = FedoraBroker.getDatastreamAsStream(fedoraObject.getObject_id(), Constants.XML_PUBLISHED);
				}
				else if (hasXMLSource) {
					dataStream = FedoraBroker.getDatastreamAsStream(fedoraObject.getObject_id(), Constants.XML_SOURCE);
				}
				else {
					LOGGER.warn("item specified does not exist");
					return values;
				}
				if(hasXMLPublished && hasXMLSource) {
					InputStream dataStream2 = FedoraBroker.getDatastreamAsStream(fedoraObject.getObject_id(), Constants.XML_PUBLISHED);
					InputStream modifiedDatastream = FedoraBroker.getDatastreamAsStream(fedoraObject.getObject_id(), Constants.XML_SOURCE);
					try {
						modifiedDocument = getXMLDifference(dataStream2, modifiedDatastream);
					}
					catch (Exception e) {
						LOGGER.warn("Exception retrieving differences between documents");
					}
				}
			}
			if (dataStream != null) {
				try {
					// Xalan appears to have issues tranforming when a stream is sent to the document so making
					// it a w3c Document 
					Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(dataStream);
					values.put("itemType", getObjectType(doc));
					parameters.put("data", doc);
					if (modifiedDocument != null) {
						LOGGER.debug("Adding modified data");
						parameters.put("modifiedData", modifiedDocument);
					}
				}
				catch (SAXException e){
					LOGGER.error("Issue with document", e);
				}
				catch (ParserConfigurationException e) {
					LOGGER.error("Issue with document", e);
				}
				catch (IOException e) {
					LOGGER.error("Issue with document", e);
				}
			}
			else {
				LOGGER.warn("item specified does not exist");
				return values;
			}
		}
		
		try {
			Document options = getOptionsXML();
			if (options != null) {
				parameters.put("options", options);
			}
			else {
				LOGGER.info("Options are null");
			}
			String result = transform(xmlStream, xslStream, parameters);
			values.put("page", result);
		}
		catch (Exception e) {
			LOGGER.error("Exception transforming page", e);
		}
		return values;
	}
	
	/**
	 * getPublishedPage
	 * 
	 * Transforms to a document specified by the layout and performs the transformation
	 * on either the template or the item depending on which values are given.  This generally
	 * transforms the given documents to either a html page or an xml document.
	 * 
	 * This function specifically gets published documents.
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		19/03/2012	Genevieve Turner (GT)	Initial build
	 * 0.4		26/04/2012	Genevieve Turner (GT)	Some updates for differences between published and non-published records
	 * 0.5		03/05/2012	Genevieve Turner (GT)	Updated so the fedora object is used to get the input stream
	 * 0.9		20/06/2012	Genevieve Turner (GT)	Updated to allow the display of the object type
	 * </pre>
	 * 
	 * @param layout The layout to use with display (i.e. the xsl stylesheet)
	 * @param template The template that determines the fields on the screen
	 * @param fedoraObject The item to retrieve data for
	 * @param fieldName The field to retrieve data for
	 * @return Returns a map containing the page information and what type the object is
	 * @throws FedoraClientException
	 */
	public Map<String, Object> getPublishedPage(String layout, String template, FedoraObject fedoraObject, String fieldName) throws FedoraClientException
	{
		Map<String, Object> values = new HashMap<String, Object>();
		
		LOGGER.debug("In getPublishedPage");
		Map<String, Object> parameters = new HashMap<String, Object>();
		InputStream xmlStream = getXMLInputStream(template, fedoraObject);

		if (xmlStream == null) {
			LOGGER.warn("XML Stream is empty");
			return values;
		}
		
		InputStream xslStream = getInputStream(layout, Constants.XSL_SOURCE);
		
		if (xslStream == null) {
			LOGGER.warn("XSL Stream is empty");
			return values;
		}
		
		if(Util.isNotEmpty(template)) {
			parameters.put("tmplt", template);
		}
		if(fedoraObject != null) {
			parameters.put("item", fedoraObject.getObject_id());
		}
		if(Util.isNotEmpty(layout)) {
			parameters.put("layout", layout);
		}
		if(Util.isNotEmpty(fieldName)){
			parameters.put("fieldName", fieldName);
		}
		
		if (fedoraObject != null) {
			InputStream dataStream = FedoraBroker.getDatastreamAsStream(fedoraObject.getObject_id(), Constants.XML_SOURCE);
			if (dataStream != null) {
				try {
					// Xalan appears to have issues tranforming when a stream is sent to the document so making
					// it a w3c Document 
					Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(dataStream);
					parameters.put("data", doc);
				}
				catch (SAXException e){
					LOGGER.error("Issue with document", e);
				}
				catch (ParserConfigurationException e) {
					LOGGER.error("Issue with document", e);
				}
				catch (IOException e) {
					LOGGER.error("Issue with document", e);
				}
			}
			else {
				LOGGER.warn("item specified does not exist");
				return values;
			}
		}
		
		try {
			String result = transform(xmlStream, xslStream, parameters);
			values.put("page", result);
		}
		catch (Exception e) {
			LOGGER.error("Exception transforming page", e);
		}
		return values;
	}
	
	/**
	 * getOptionsXML
	 * 
	 * Gets an xml representation of of options for select boxes.
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.8		28/05/2012	Genevieve Turner (GT)	Updated for retrieving data from the database
	 * </pre>
	 * 
	 * @return Returns an xml document that contains values for drop down lists
	 */
	public Document getOptionsXML() {
		Document doc = null;
		try {
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		}
		catch (ParserConfigurationException e) {
			LOGGER.error("Error creating document", e);
		}
		OptionList optionList = new OptionList();
		GroupService groupService = new GroupServiceImpl();
		List<Groups> groups = groupService.getCreateGroups();
		if (groups.size() > 0) {
			optionList.getGroups().addAll(groups);
		}
		
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(OptionList.class);
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.marshal(optionList, doc);
		}
		catch(JAXBException e) {
			LOGGER.error("Exception transforming document", e);
		}
		return doc;
	}
	
	/**
	 * getObjectType
	 *
	 * Returns the type of object i.e. whether it is a Collection, Activity, Party or Service
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.9		20/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param doc The document with the saved information
	 * @return The object type
	 */
	private String getObjectType(Document doc) {
		NodeList nodeList = doc.getElementsByTagName("type");
		if (nodeList.getLength() > 0) {
			Node node = nodeList.item(0);
			String objectType = node.getTextContent();
			LOGGER.debug("Template Type {}", objectType);
			return objectType;
		}
		return "";
	}
	
	/**
	 * getXMLDifference
	 * 
	 * Retrieves the differences between two xml documents
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.5		02/05/2012	Genevieve Turner (GT)	Allows the display of between published and non-published records
	 * </pre>
	 * 
	 * @param xml1 InputStream of the base document to compare
	 * @param xml2 InputStream of the modified document to compare
	 * @return The document of the differences from the second document
	 * @throws Exception
	 */
	public Document getXMLDifference (InputStream xml1, InputStream xml2) throws Exception {
		Document doc1 = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xml1);
		Document doc2 = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xml2);
		
		//Skip the first level of items in the document and compare them
		return getXMLDifference (doc1.getFirstChild(), doc2.getFirstChild());
	}
	
	/**
	 * getXMLDifference
	 * 
	 * Retrieves the differences between two xml documents
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.5		02/05/2012	Genevieve Turner (GT)	Allows the display of between published and non-published records
	 * </pre>
	 * 
	 * @param node1 Node of the base document to compare
	 * @param node2 Node of the modified docuemtn to compare
	 * @return The document of the differences from the second document
	 * @throws Exception
	 */
	public Document getXMLDifference (Node node1, Node node2) throws Exception {
		if (node1.isEqualNode(node2)) {
			return null;
		}
		
		Map<String, List<Element>> nodeMap1 = getDocumentMap(node1);
		Map<String, List<Element>> nodeMap2 = getDocumentMap(node2);
		
		List<String> differences = getDifferences(nodeMap1, nodeMap2);
		
		Document newDoc = null;
		if (differences.size() > 0) {
			try {
				newDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
				Element rootElement = newDoc.createElement("data");
				newDoc.appendChild(rootElement);
				for (String difference : differences) {
					List<Element> elems = nodeMap2.get(difference);
					for (Element element : elems) {
						Node node = element.cloneNode(true);
						rootElement.appendChild(newDoc.importNode(node, true));
					}
				}
			}
			catch (ParserConfigurationException e) {
				LOGGER.error("Error configuring parser: ", e);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return newDoc;
	}
	
	/**
	 * getDifferences
	 * 
	 * Returns hte differences between the map of nodes given
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.5		02/05/2012	Genevieve Turner (GT)	Allows the display of between published and non-published records
	 * </pre>
	 * 
	 * @param nodeMap1 Node of the base document to compare
	 * @param nodeMap2 Node of the modified docuemtn to compare
	 * @return
	 */
	private List<String> getDifferences(Map<String, List<Element>> nodeMap1, Map<String, List<Element>> nodeMap2) {
		List<String> keySet1 = new ArrayList<String>(nodeMap1.keySet());
		List<String> keySet2 = new ArrayList<String>(nodeMap2.keySet());
		List<String> diffs = new ArrayList<String>();
		
		int i = 0;
		int j = 0;
		while (i < keySet1.size() && j < keySet2.size()) {
			int result = keySet1.get(i).compareTo(keySet2.get(j));
			if (result == 0) {

				List<Element> list1 = nodeMap1.get(keySet1.get(i));
				List<Element> list2 = nodeMap2.get(keySet2.get(j));
				
				if (list1.size() == list2.size()) {
					for (int k = 0; k < list1.size(); k++) {
						Element elem1 = list1.get(k);
						Element elem2 = list2.get(k);

						if (!elem1.isEqualNode(elem2)) {
							diffs.add(keySet2.get(j));
							break;
						}
					}
				}
				else {
					diffs.add(keySet2.get(j));
				}
				i++;
				j++;
			}
			else if (result < 0) {
				i++;
			}
			else if (result > 0) {
				diffs.add(keySet2.get(j));
				j++;
			}
		}
		if (j < keySet2.size()) {
			diffs.addAll(keySet2.subList(j, keySet2.size()));
		}
		
		return diffs;
	}
	
	/**
	 * getDocumentMap
	 * 
	 * Returns a map of the child nodes.
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.5		02/05/2012	Genevieve Turner (GT)	Allows the display of between published and non-published records
	 * </pre>
	 * 
	 * Creates a map of elements
	 * @param node The node to map
	 * @return
	 */
	private Map<String, List<Element>> getDocumentMap(Node node) {
		Map<String, List<Element>> nodeMap = new TreeMap<String, List<Element>>();
		
		NodeList nodeList = node.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node childNode = nodeList.item(i);
			if (childNode.getNodeType() == Node.ELEMENT_NODE) {
				Element elem = (Element) childNode;
				if (nodeMap.containsKey(elem.getNodeName())) {
					nodeMap.get(elem.getNodeName()).add(elem);
				}
				else {
					List<Element> list = new ArrayList<Element>();
					list.add(elem);
					nodeMap.put(elem.getNodeName(), list);
				}
			}
		}
		
		return nodeMap;
	}
	
	/**
	 * transform
	 * 
	 * Transforms the given streams xsl and xml streams
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		19/03/2012	Genevieve Turner (GT)	Initial build
	 * </pre>
	 * 
	 * @param xmlStream The xml document to transform
	 * @param xslStream The xsl stylesheet to use in the transformation
	 * @return The result of the transformation
	 * @throws TransformerConfigurationException
	 * @throws TransformerException
	 */
	public String transform(InputStream xmlStream, InputStream xslStream)
			throws TransformerConfigurationException, TransformerException {
		return transform(xmlStream, xslStream, null);
	}

	/**
	 * transform
	 * 
	 * Transforms the given streams xsl and xml streams with the specified parameters
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		19/03/2012	Genevieve Turner (GT)	Initial build
	 * </pre>
	 * 
	 * @param xmlStream The xml document to transform
	 * @param xslStream The xsl stylesheet to use in the transformation
	 * @param parameters Additional parameters to use in xsl document during the transformation
	 * @return The result of the transformation
	 * @throws TransformerConfigurationException
	 * @throws TransformerException
	 */
	public String transform(InputStream xmlStream, InputStream xslStream, Map<String, Object> parameters)
			throws TransformerConfigurationException, TransformerException {
		StringWriter sw = new StringWriter();
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Source xmlSource = new StreamSource (xmlStream);
		Source xslSource = new StreamSource (xslStream);
		Transformer transformer = transformerFactory.newTransformer(xslSource);
		if (parameters != null) {
			for ( Entry param : parameters.entrySet() ) {
				transformer.setParameter(param.getKey().toString(), param.getValue());
			}
		}
		transformer.transform(xmlSource, new StreamResult(sw));
		return sw.toString();
	}
	
	/**
	 * getXMLInputStream
	 * 
	 * Get the input stream for the xml document
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.2		23/03/2012	Genevieve Turner (GT)	Initial creation
	 * 0.5		03/05/2012	Genevieve Turner (GT)	Updated so the fedora object is used to get the input stream
	 * </pre>
	 * 
	 * @param template The template that determines the fields on the screen
	 * @param fedoraObject The item to retrieve data for
	 * @return The stream for the xml template
	 * @throws FedoraClientException
	 */
	private InputStream getXMLInputStream (String template, FedoraObject fedoraObject) throws FedoraClientException {
		InputStream xmlStream = null;
		
		if(Util.isNotEmpty(template)){
			xmlStream = FedoraBroker.getDatastreamAsStream(template, Constants.XML_TEMPLATE);
		}
		else if(fedoraObject != null){
			xmlStream = FedoraBroker.getDatastreamAsStream(fedoraObject.getObject_id(), Constants.XML_TEMPLATE);
		}
		else {
			LOGGER.warn("No Template or Item to retrieve the datastream from specified");
		}
		
		return xmlStream;
	}
	
	/**
	 * getInputStream
	 * 
	 * Get the input stream with the specified pid and datastream id
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.2		23/03/2012	Genevieve Turner (GT)	Initial creation
	 * </pre>
	 * 
	 * @param pid Id of the object to fetch
	 * @param dsId The id of the  datastream to get
	 * @return The input stream containing the contents of the datastream
	 * @throws FedoraClientException
	 */
	private InputStream getInputStream (String pid, String dsId) throws FedoraClientException {
		InputStream xslStream = null;
		if(Util.isNotEmpty(pid)) {
			xslStream = FedoraBroker.getDatastreamAsStream(pid, dsId);
		}
		else {
			LOGGER.warn("No layout specified");
		}
		
		return xslStream;
	}
	
	/**
	 * getTemplateObject
	 * 
	 * Returns the Template object given either the template or the object id
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.3		29/03/2012	Genevieve Turner (GT)	Initial creation
	 * 0.5		03/05/2012	Genevieve Turner (GT)	Updated so the fedora object is used to get the input stream
	 * </pre>
	 * 
	 * @param template To retrieve the object for
	 * @param fedoraObject The item to retrieve the template for
	 * @return The object representation of the template 
	 * @throws FedoraClientException
	 * @throws JAXBException
	 */
	public Template getTemplateObject(String tmplt, FedoraObject fedoraObject)
			throws FedoraClientException, JAXBException {
		InputStream xmlStream = getXMLInputStream(tmplt, fedoraObject);
		JAXBTransform jaxbTransform = new JAXBTransform();
		Template template = (Template) jaxbTransform.unmarshalStream(xmlStream, Template.class);
		return template;
	}
	
	/**
	 * saveData
	 * 
	 * Saves the data to either the specified object or creates a new object
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.2		23/03/2012	Genevieve Turner (GT	Initial creation
	 * 0.4		26/04/2012	Genevieve Turner (GT)	Updated to fix an issue with the Form class when introducing security
	 * 0.5		03/05/2012	Genevieve Turner (GT)	Updated so the fedora object is used to get the input stream
	 * 0.6		14/05/2012	Genevieve Turner (GT)	Updated to retrieve the pid namespace from a global property
	 * 0.10		20/06/2012	Genevieve Turner (GT)	Updated to perform additions to the audit object table
	 * </pre>
	 * 
	 * @param tmplt The id of the template
	 * @param fedoraObject The object to save data for
	 * @param form The form data
	 * @return Returns the object id
	 * @throws FedoraClientException
	 * @throws JAXBException
	 */
	public FedoraObject saveData (String tmplt, FedoraObject fedoraObject, Map<String, List<String>> form) 
			throws FedoraClientException, JAXBException {
		InputStream templateStream = null;
		if (fedoraObject == null) {
			templateStream = getXMLInputStream(tmplt, null);
		}
		else {
			templateStream = getXMLInputStream(tmplt, fedoraObject);
		}
		
		JAXBTransform jaxbTransform = new JAXBTransform();
		Template template = (Template) jaxbTransform.unmarshalStream(templateStream, Template.class);
		
		Data data = null;
		if (fedoraObject != null) {
			InputStream dataStream = getInputStream(fedoraObject.getObject_id(), Constants.XML_SOURCE);
			data = (Data) jaxbTransform.unmarshalStream(dataStream, Data.class);
		}
		else {
			data = new Data();
		}
		
		Map<String, TemplateItem> templateItemMap = createItemMap (template);
		for (Entry<String, List<String>> param : form.entrySet()) {
			String key = param.getKey();
			List<String> values = param.getValue();
			if (values != null) {
				TemplateItem templateItem = templateItemMap.get(key);
				if(templateItem == null) {
					LOGGER.debug("Could not find " + key + " in template" + tmplt);
				}
				else if (!processedValues_.contains(key)) {
					processItem(templateItemMap.get(key), key, values, data, form);
				}
			}
		}
		//Added because for some types the name is separated fields
		setName(data);
		DublinCore dublinCore = getDublinCore(data);
		StringWriter dcSW = new StringWriter();
		
		StringWriter sw = new StringWriter();
		
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		
		//Marshal the data for saving then create/update the appropriate streams
		jaxbTransform.marshalStream(sw, data, Data.class, properties);
		
		// Marshal the dublin core data stream
		if (dublinCore != null) {
			Map<String, Object> dublinCoreProperties = new HashMap<String, Object>();
			dublinCoreProperties.put(Marshaller.JAXB_SCHEMA_LOCATION, "http://www.openarchives.org/OAI/2.0/oai_dc/ http://www.openarchives.org/OAI/2.0/oai_dc.xsd");
			dublinCoreProperties.put(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			jaxbTransform.marshalStream(dcSW, dublinCore, DublinCore.class, dublinCoreProperties);
		}
		
		if (fedoraObject == null) {
			String location = String.format("%s/objects/%s/datastreams/%s/content"
					, GlobalProps.getProperty(GlobalProps.PROP_FEDORA_URI)
					, tmplt
					, Constants.XML_TEMPLATE);
			
			String item = FedoraBroker.createNewObject(GlobalProps.getProperty(GlobalProps.PROP_FEDORA_SAVENAMESPACE));
			
			FedoraBroker.addDatastreamBySource(item, Constants.XML_SOURCE, "XML Source", sw.toString());
			FedoraBroker.addDatastreamByReference(item, Constants.XML_TEMPLATE, "M", "XML Template", location);
			if(Util.isNotEmpty(dcSW.toString())) {
				FedoraBroker.modifyDatastreamBySource(item, Constants.DC, "Dublin Core Record for this object", dcSW.toString());
			}
			String group_id = "1";
			if (form.get("ownerGroup") != null) {
				LOGGER.debug("There is an ownerGroup");
				group_id = form.get("ownerGroup").get(0);
			}
			else {
				LOGGER.debug("There is no ownerGroup");
			}
			
			fedoraObject = new FedoraObject();
			fedoraObject.setObject_id(item);
			
			//TODO Update so this is not a hard coded value
			fedoraObject.setGroup_id(new Long(group_id));
			fedoraObject.setPublished(Boolean.FALSE);

			FedoraObjectDAOImpl fedoraObjectDAO = new FedoraObjectDAOImpl(FedoraObject.class);
			fedoraObjectDAO.create(fedoraObject);
			LOGGER.debug("fedora object id: {}", fedoraObject.getId());
			saveAuditModifyRow(fedoraObject);
		} else {
			FedoraBroker.modifyDatastreamBySource(fedoraObject.getObject_id(), Constants.XML_SOURCE, "XML Source", sw.toString());
			if(Util.isNotEmpty(dcSW.toString())) {
				FedoraBroker.modifyDatastreamBySource(fedoraObject.getObject_id(), Constants.DC, "Dublin Core Record for this object", dcSW.toString());
			}
			saveAuditModifyRow(fedoraObject);
		}
		
		processedValues_.clear();
		
		return fedoraObject;
	}
	
	/**
	 * saveAuditModifyRow
	 *
	 * Saves the changed information for the fedora object
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.10		20/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param fedoraObject The fedora object to add an audit row to
	 */
	private void saveAuditModifyRow(FedoraObject fedoraObject) {
		CustomUser customUser = (CustomUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		AuditObject auditObject = new AuditObject();
		auditObject.setLog_date(new java.util.Date());
		auditObject.setLog_type("MODIFIED");
		auditObject.setObject_id(fedoraObject.getId());
		auditObject.setUser_id(customUser.getId());
		
		JAXBTransform jaxbTransform = new JAXBTransform();
		
		Data removeData = new Data();
		removeData.setItems(removedItems_);
		StringWriter removedString = new StringWriter();
		
		Data addData = new Data();
		addData.setItems(addedItems_);
		StringWriter addedString = new StringWriter();
		
		try {
			jaxbTransform.marshalStream(removedString, removeData, Data.class);
			jaxbTransform.marshalStream(addedString, addData, Data.class);
		}
		catch (JAXBException e) {
			LOGGER.error("Exception creating audit information", e);
		}
		auditObject.setBefore(removedString.toString());
		auditObject.setAfter(addedString.toString());
		
		GenericDAO<AuditObject,Long> auditDao = new GenericDAOImpl<AuditObject,Long>(AuditObject.class);
		auditDao.create(auditObject);
	}
	
	/**
	 * setName
	 * 
	 * Sets the value of the name field, this in particular is used if there are
	 * multiple values to the name
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.7		15/05/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @param data The form data to save
	 */
	private void setName(Data data) {
		// If there is already a field of type 'name' there is no need to create another
		if (processedValues_.contains("name")) {
			return;
		}
		String nameFields = GlobalProps.getProperty(GlobalProps.PROP_FEDORA_NAMEFIELDS);
		String[] splitName = nameFields.split(",");
		Map<String, String> nameMap = new HashMap<String, String>();
		
		// Get the values of the fields
		for (String nameField : splitName) {
			if (processedValues_.contains(nameField)) {
				data.removeElementsByName("name");
				processedValues_.add("name");
				for (DataItem dataItem : data.getItems()) {
					for (String field : splitName) {
						if (dataItem.getName_().equals(field)) {
							nameMap.put(field, dataItem.getValue_());
							break;
						}
					}
				}
				break;
			}
		}
		
		// Ensure that the fields are placed in the correct order
		StringBuffer sb = new StringBuffer();
		for (String nameField : splitName) {
			if (nameMap.containsKey(nameField)) {
				sb.append(nameMap.get(nameField));
				sb.append(" ");
			}
		}
		
		// Add the name to the saved data
		if (Util.isNotEmpty(sb.toString())) {
			DataItem dataItem = new DataItem();
			dataItem.setName_("name");
			dataItem.setValue_(sb.toString().trim());
			data.getItems().add(dataItem);
		}	
		
		return;
	}
	
	/**
	 * getDublinCore
	 * 
	 * Create the dublin core information for the object
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.2		23/03/2012	Genevieve Turner (GT)	Initial creation
	 * </pre>
	 * 
	 * @param data The data to create the dublin core from
	 * @return The dublin core data
	 */
	private DublinCore getDublinCore(Data data) {
		DublinCore dublinCore = new DublinCore();
		
		// Check if any of the dublin core fields have been modified.  If not then we do
		// not want to update the  dublin core
		boolean modifiedDublinCore = false;
		for (int i = 0; !modifiedDublinCore && i < processedValues_.size(); i++) {
			// GT - 20120515 - Updated to perform partial match so that multivalued fields can execute
			if (Util.isNotEmpty(DublinCoreConstants.getFieldName(processedValues_.get(i)))) {
				modifiedDublinCore = true;
				break;
			}
		}
		
		if(!modifiedDublinCore) {
			return null;
		}
		
		List<DataItem> dataItems = data.getItems();
		for (int i = 0; i < dataItems.size(); i++) {
			DataItem dataItem = dataItems.get(i);
			
			String fieldName = dataItem.getName_();
			String dublinCoreLocalpart = DublinCoreConstants.getFieldName(fieldName);
			if(Util.isNotEmpty(dublinCoreLocalpart)) {
				dublinCore.getItems_().add(createJAXBElement(DublinCoreConstants.DC, dublinCoreLocalpart, dataItem.getValue_()));
			}
		}
		
		return dublinCore;
	}
	
	/**
	 * createItemMap
	 * 
	 * Creates an item map that contains the fields in the template to process
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.2		23/03/2012	Genevieve Turner (GT)	Initial creation
	 * </pre>
	 * 
	 * @param template The template to create an item map from
	 * @return The map of items
	 */
	private Map<String, TemplateItem> createItemMap(Template template){
		Map<String, TemplateItem> itemMap = new HashMap<String, TemplateItem>();
		List<TemplateItem> templateItems = template.getItems();
		for (TemplateItem templateItem : templateItems) {
			itemMap.put(templateItem.getName(), templateItem);
			if (templateItem.getTemplateColumns() != null) {
				for(TemplateColumn column : templateItem.getTemplateColumns()) {
					itemMap.put(column.getName(), templateItem);
				}
			}
		}
		return itemMap;
	}
	
	/**
	 * createJAXBElement
	 * 
	 * Creates a JAXBElement
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.2		23/03/2012	Genevieve Turner (GT)	Initial creation
	 * </pre>
	 * 
	 * @param namespace The namespace of the jaxb element
	 * @param localpart The localpart of the jaxb element
	 * @param value The value to place in the jaxb element
	 * @return The newly created jaxb element
	 */
	private JAXBElement<String> createJAXBElement(String namespace, String localpart, String value) {
		QName qname = null;
		if (Util.isNotEmpty(namespace)) {
			qname = new QName(namespace, localpart);
		}
		else {
			qname = new QName(localpart);
		}
		JAXBElement element = new JAXBElement(qname, String.class, value);
		
		return element;
	}
	
	/**
	 * processItem
	 * 
	 * Processes the values
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.2		23/03/2012	Genevieve Turner (GT)	Initial creation
	 * 0.4		26/04/2012	Genevieve Turner (GT)	UUpdated to fix an issue with the Form class when introducing security
	 * </pre>
	 * 
	 * @param item The name of the field to process
	 * @param key The key of the field to process
	 * @param values The list of values sent to the system for these values
	 * @param data The data object to put data in
	 * @param form The form data to process
	 * @return Whether the processing has completed
	 */
	private boolean processItem(TemplateItem item, String key, List values, Data data, Map<String, List<String>> form) {
		removedItems_.addAll(data.removeElementsByName(item.getName()));
		
		if (item.getSaveType() == null) {
			return true;
		}
		if (item.getSaveType().equals("single")) {
			processSingleItem(key, values, data);
		}
		else if (item.getSaveType().equals("multiple")) {
			processMultipleItem(key, values, data);
		}
		else if (item.getSaveType().equals("table")) {
			processTableItem(item, data, form);
		}
		
		return true;
	}
	
	/**
	 * processSingleItem
	 * 
	 * Process an item with a single value
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.2		23/03/2012	Genevieve Turner (GT)	Initial creation
	 * </pre>
	 * 
	 * @param key The name of the field to process
	 * @param values The list of values sent to the system for these values
	 * @param data The data object to put data in
	 * @return if the item has sucessfully been processed
	 */
	private boolean processSingleItem(String key, List values, Data data) {
		for (Object value : values) {
			if (value instanceof String) {
				String strValue = (String) value;
				if (Util.isNotEmpty(strValue)) {
					DataItem dataItem = new DataItem();
					dataItem.setName_(key);
					dataItem.setValue_(strValue);
					data.getItems().add(dataItem);
					addedItems_.add(dataItem);
				}
			}
		}
		processedValues_.add(key);
		return true;
	}
	
	/**
	 * processMultipleItem
	 * 
	 * Process an item with multiple values
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.2		23/03/2012	Genevieve Turner (GT)	Initial creation
	 * </pre>
	 * 
	 * @param key The name of the field to process
	 * @param values The list of values sent to the system for these values
	 * @param data The data object to put data in
	 * @return If the item has sucessfully been processed
	 */
	private boolean processMultipleItem(String key, List values, Data data) {
		for (Object value : values) {
			if (value instanceof String) {
				String strValue = (String) value;
				if (Util.isNotEmpty(strValue)) {
					DataItem dataItem = new DataItem();
					dataItem.setName_(key);
					dataItem.setValue_(strValue);
					data.getItems().add(dataItem);
					addedItems_.add(dataItem);
				}
			}
		}
		processedValues_.add(key);
		return true;
	}
	
	/**
	 * processTableItem
	 * 
	 * Process an item for a more table like structure
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.2		23/03/2012	Genevieve Turner (GT)	Initial creation
	 * 0.3		23/03/2012	Genevieve Turner (GT)	Updated to cater for changes to the 'Data' class
	 * 0.4		26/04/2012	Genevieve Turner (GT)	Updated to fix an issue with the Form class when introducing security
	 * </pre>
	 * 
	 * @param item The template item object
	 * @param data The data object to put data in
	 * @param form The form data
	 * @return If the item has sucessfully been processed
	 */
	private boolean processTableItem(TemplateItem item, Data data, Map<String, List<String>> form) {
		List<DataItem> tableData = new ArrayList<DataItem>();
		
		String itemName = item.getName();
		if (!Util.isNotEmpty(itemName)) {
			LOGGER.warn("Item has no name");
			return false;
		}
		
		// process all the table values
		String columnName = null;
		for (TemplateColumn column : item.getTemplateColumns()) {
			columnName = column.getName();
			List<String> values = (List<String>) form.get(columnName);
			if (values != null) {
				for (int i = 0; i < values.size(); i++) {
					if (tableData.size() <= i) {
						DataItem dataItem = new DataItem();
						dataItem.setName_(itemName);
						tableData.add(dataItem);
						addedItems_.add(dataItem);
					}
					if(Util.isNotEmpty(values.get(i))) {
						tableData.get(i).getChildValues_().put(columnName, values.get(i));
					}
				}
			}
		}
		
		// remove all the rows without any values in them
		for (int i = tableData.size() - 1; i >= 0; i--) {
			if(tableData.get(i).getChildValues_().size() == 0) {
				tableData.remove(i);
			}
		}
		
		// add all the values to the data object
		data.getItems().addAll(tableData);
		
		return true;
	}
}
