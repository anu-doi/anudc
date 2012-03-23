package au.edu.anu.datacommons.xml.transform;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import au.edu.anu.datacommons.connection.fedora.FedoraBroker;
import au.edu.anu.datacommons.properties.GlobalProps;
import au.edu.anu.datacommons.util.Constants;
import au.edu.anu.datacommons.util.Util;
import au.edu.anu.datacommons.xml.data.Data;
import au.edu.anu.datacommons.xml.dc.DublinCore;
import au.edu.anu.datacommons.xml.dc.DublinCoreConstants;
import au.edu.anu.datacommons.xml.template.Template;
import au.edu.anu.datacommons.xml.template.TemplateColumn;
import au.edu.anu.datacommons.xml.template.TemplateItem;

import com.sun.jersey.api.representation.Form;
import com.yourmediashelf.fedora.client.FedoraClientException;

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
 * Version	Date		Developer			Description
 * 0.1		19/03/2012	Genevieve Turner	Initial build
 * 0.2		23/03/2012	Genevieve Turner	Updated to include saving
 * 
 */
public class ViewTransform
{
	static final Logger LOGGER = LoggerFactory.getLogger(ViewTransform.class);
	
	private List<String> processedValues_;
	
	public ViewTransform() {
		processedValues_ = new ArrayList<String>();
	}
	
	/**
	 * getPage
	 * 
	 * Transforms to a document specified by the layout and performs the transformation
	 * on either the template or the item depending on which values are given.  This generally
	 * transforms the given documents to either a html page or an xml document
	 * 
	 * Version	Date		Developer			Description
	 * 0.1		19/03/2012	Genevieve Turner	Initial build
	 * 
	 * @param layout
	 * @param template
	 * @param item
	 * @return
	 * @throws FedoraClientException
	 */
	public String getPage (String layout, String template, String item) throws FedoraClientException
	{
		Map<String, Object> parameters = new HashMap<String, Object>();
		InputStream xmlStream = getXMLInputStream(template, item);

		if (xmlStream == null) {
			LOGGER.warn("XML Stream is empty");
			return "";
		}
		
		InputStream xslStream = getInputStream(layout, Constants.XSL_SOURCE);
		
		if (xslStream == null) {
			LOGGER.warn("XSL Stream is empty");
			return "";
		}
		
		if(Util.isNotEmpty(template)) {
			parameters.put("tmplt", template);
		}
		if(Util.isNotEmpty(item)) {
			parameters.put("item", item);
		}
		if(Util.isNotEmpty(layout)) {
			parameters.put("layout", layout);
		}
		
		if (Util.isNotEmpty(item)) {
			InputStream dataStream = FedoraBroker.getDatastreamAsStream(item, Constants.XML_SOURCE);
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
				return "";
			}
		}
		String result = "";
		try {
			result = transform(xmlStream, xslStream, parameters);
		}
		catch (Exception e) {
			LOGGER.error("Exception transforming page", e);
		}
		return result;
	}
	
	/**
	 * transform
	 * 
	 * Transforms the given streams xsl and xml streams
	 * 
	 * Version	Date		Developer			Description
	 * 0.1		19/03/2012	Genevieve Turner	Initial build
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
	 * Version	Date		Developer			Description
	 * 0.1		19/03/2012	Genevieve Turner	Initial build
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
	 * Version	Date		Developer			Description
	 * 0.2		23/03/2012	Genevieve Turner	Initial creation
	 * 
	 * @param template
	 * @param item
	 * @return
	 * @throws FedoraClientException
	 */
	private InputStream getXMLInputStream (String template, String item) throws FedoraClientException {
		InputStream xmlStream = null;
		
		if(Util.isNotEmpty(template)){
			xmlStream = FedoraBroker.getDatastreamAsStream(template, Constants.XML_TEMPLATE);
		}
		else if(Util.isNotEmpty(item)){
			xmlStream = FedoraBroker.getDatastreamAsStream(item, Constants.XML_TEMPLATE);
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
	 * Version	Date		Developer			Description
	 * 0.2		23/03/2012	Genevieve Turner	Initial creation
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
	 * saveData
	 * 
	 * Saves the data to either the specified object or creates a new object
	 * 
	 * Version	Date		Developer			Description
	 * 0.2		23/03/2012	Genevieve Turner	Initial creation
	 * 
	 * @param tmplt The id of the template
	 * @param item The object id
	 * @param form The form data
	 * @return Returns the object id
	 * @throws FedoraClientException
	 * @throws JAXBException
	 */
	public String saveData (String tmplt, String item, Form form) 
			throws FedoraClientException, JAXBException {
		InputStream templateStream = getXMLInputStream(tmplt, item);
		
		JAXBTransform jaxbTransform = new JAXBTransform();
		Template template = (Template) jaxbTransform.unmarshalStream(templateStream, Template.class);
		
		Data data = null;
		if (Util.isNotEmpty(item)) {
			//TODO need to figure out how to remove objects when adding them if they exist
			InputStream dataStream = getInputStream(item, Constants.XML_SOURCE);
			data = (Data) jaxbTransform.unmarshalStream(dataStream, Data.class);
		}
		else {
			data = new Data();
		}
		
		Map<String, TemplateItem> templateItemMap = createItemMap (template);
		
		for (Entry param : form.entrySet()) {
			String key = (String) param.getKey();
			Object value = param.getValue();
			if (value instanceof List) {
				List values = (List) value;
				TemplateItem someItem = templateItemMap.get(key);
				if(someItem == null) {
					LOGGER.info("Could not find " + key + " in template" + tmplt);
				}
				else if (!processedValues_.contains(key)) {
					processItem(templateItemMap.get(key), key, values, data, form);
				}
			}
		}
		DublinCore dublinCore = getDublinCore(data);
		StringWriter dcSW = new StringWriter();
		
		StringWriter sw = new StringWriter();
		
		//Marshal the data for saving then create/update the appropriate streams
		jaxbTransform.marshalStream(sw, data, Data.class);
		
		// Marshal the dublin core data stream
		if (dublinCore != null) {
			Map<String, Object> dublinCoreProperties = new HashMap<String, Object>();
			dublinCoreProperties.put(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			//dublinCoreProperties.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
			dublinCoreProperties.put(Marshaller.JAXB_SCHEMA_LOCATION, "http://www.openarchives.org/OAI/2.0/oai_dc/ http://www.openarchives.org/OAI/2.0/oai_dc.xsd");
			
			jaxbTransform.marshalStream(dcSW, dublinCore, DublinCore.class, dublinCoreProperties);
			LOGGER.info("DublinCore: " + dcSW.toString());
		}
		
		if (!Util.isNotEmpty(item)) {
			String location = String.format("%s/objects/%s/datastreams/%s/content"
					, GlobalProps.getProperty(GlobalProps.PROP_FEDORA_URI)
					, tmplt
					, Constants.XML_TEMPLATE);
			
			item = FedoraBroker.createNewObject("test");
			
			LOGGER.debug("Created object: " + item);
			
			FedoraBroker.addDatasstreamBySource(item, Constants.XML_SOURCE, "XML Source", sw.toString());
			FedoraBroker.addDatastreamByReference(item, Constants.XML_TEMPLATE, "M", "XML Template", location);
			if(Util.isNotEmpty(dcSW.toString())) {
				FedoraBroker.modifyDatastreamBySource(item, Constants.DC, "Dublin Core Record for this object", dcSW.toString());
			}
		} else {
			//TODO Create edit functionality
			//FedoraBroker.modifyDatastreamBySource(item, Constants.XML_SOURCE, "XML Source", sw.toString());
			
		}
		//LOGGER.info("Data contents: " + sw.toString());
		
		processedValues_.clear();
		
		return item;
	}
	
	/**
	 * getDublinCore
	 * 
	 * Create the dublin core information for the object
	 * 
	 * Version	Date		Developer			Description
	 * 0.2		23/03/2012	Genevieve Turner	Initial creation
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
			if (Util.isNotEmpty(DublinCoreConstants.getFieldName(processedValues_.get(i)))) {
				modifiedDublinCore = true;
				break;
			}
		}
		
		if(!modifiedDublinCore) {
			return null;
		}
		
		List<JAXBElement<String>> dataItems = data.getItems();
		for (int i = 0; i < dataItems.size(); i++) {
			JAXBElement<String> element = dataItems.get(i);
			
			String fieldName = element.getName().getLocalPart();
			String dublinCoreLocalpart = DublinCoreConstants.getFieldName(fieldName);
			LOGGER.info("field Name : " + fieldName + ", DC field name: " + dublinCoreLocalpart);
			if(Util.isNotEmpty(dublinCoreLocalpart)) {
				dublinCore.getItems().add(createJAXBElement(DublinCoreConstants.DC, dublinCoreLocalpart, element.getValue()));
			}
		}
		
		return dublinCore;
	}
	
	/**
	 * createItemMap
	 * 
	 * Creates an item map that contains the fields in the template to process
	 * 
	 * Version	Date		Developer			Description
	 * 0.2		23/03/2012	Genevieve Turner	Initial creation
	 * 
	 * @param template
	 * @return
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
	 * Version	Date		Developer			Description
	 * 0.2		23/03/2012	Genevieve Turner	Initial creation
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
	 * createJAXBElement
	 * 
	 * Creates a JAXBElement
	 * 
	 * Version	Date		Developer			Description
	 * 0.2		23/03/2012	Genevieve Turner	Initial creation
	 * 
	 * @param namespace The namespace of the jaxb element
	 * @param localpart The localpart of the jaxb element
	 * @param value The value to place in the jaxb element
	 * @return The newly created jaxb element
	 */
	private JAXBElement<Data> createJAXBElement(String namespace, String localpart, Data value) {
		QName qname = null;
		if (Util.isNotEmpty(namespace)) {
			qname = new QName(namespace, localpart);
		}
		else {
			qname = new QName(localpart);
		}
		JAXBElement element = new JAXBElement(qname, Data.class, value);
		
		return element;
	}
	
	/**
	 * processItem
	 * 
	 * Processes the values
	 * 
	 * Version	Date		Developer			Description
	 * 0.2		23/03/2012	Genevieve Turner	Initial creation
	 * 
	 * @param item
	 * @param key
	 * @param values
	 * @param data
	 * @param form
	 * @return
	 */
	private boolean processItem(TemplateItem item, String key, List values, Data data, Form form) {
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
	 * Version	Date		Developer			Description
	 * 0.2		23/03/2012	Genevieve Turner	Initial creation
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
					data.getItems().add(createJAXBElement(null, key, strValue));
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
	 * Version	Date		Developer			Description
	 * 0.2		23/03/2012	Genevieve Turner	Initial creation
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
					data.getItems().add(createJAXBElement(null, key, strValue));
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
	 * @param item The template item object
	 * @param data The data object to put data in
	 * @param form The form data
	 * @return If the item has sucessfully been processed
	 */
	private boolean processTableItem(TemplateItem item, Data data, Form form) {
		List<Data> tableData = new ArrayList<Data>();
		String itemName = item.getName();
		if (!Util.isNotEmpty(itemName)) {
			LOGGER.warn("Item has no name");
			return false;
		}
		
		String columnName = null;
		for (TemplateColumn column : item.getTemplateColumns()) {
			columnName = column.getName();
			List<String> values = (List<String>) form.get(columnName);
			for (int i = 0; i < values.size(); i++) {
				if (tableData.size() <= i) {
					tableData.add(new Data());
				}
				if(Util.isNotEmpty(values.get(i))) {
					tableData.get(i).getItems().add(createJAXBElement(null, columnName, values.get(i)));
				}
			}
			processedValues_.add(columnName);
		}
		for (int i = tableData.size() - 1; i >= 0; i--) {
			if(tableData.get(i).getItems().size() == 0) {
				tableData.remove(i);
			}
		}
		for (Data dataRow : tableData) {
			data.getData().add(createJAXBElement(null, item.getName(), dataRow));
		}
		return true;
	}
}
