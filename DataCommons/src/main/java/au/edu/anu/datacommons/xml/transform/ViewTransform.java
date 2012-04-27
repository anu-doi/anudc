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
import au.edu.anu.datacommons.xml.data.DataItem;
import au.edu.anu.datacommons.xml.dc.DublinCore;
import au.edu.anu.datacommons.xml.dc.DublinCoreConstants;
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
 * </pre>
 * 
 */
public class ViewTransform
{
	static final Logger LOGGER = LoggerFactory.getLogger(ViewTransform.class);
	
	private List<String> processedValues_;
	
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
	 * </pre>
	 * 
	 * @param layout The layout to use with display (i.e. the xsl stylesheet)
	 * @param template The template that determines the fields on the screen
	 * @param item The item to retrieve data for
	 * @param fieldName The field to retrieve data for
	 * @param editMode Whether the request is in edit mode or not
	 * @return Returns a string representation of the page
	 * @throws FedoraClientException
	 */
	public String getPage (String layout, String template, String item, String fieldName, boolean editMode) throws FedoraClientException
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
		if(Util.isNotEmpty(fieldName)){
			parameters.put("fieldName", fieldName);
		}
		
		if (Util.isNotEmpty(item)) {
			InputStream dataStream = null;
		/*	InputStream dataStream2 = null;
			InputStream modifiedDatastream = null; */
			if(editMode) {
				dataStream = FedoraBroker.getDatastreamAsStream(item, Constants.XML_SOURCE);
			} else {
				List<DatastreamType> datastreamList = FedoraBroker.getDatastreamList(item); //FedoraBroker.getDatastreamAsStream(pid, streamId)
				boolean hasXMLSource = false;
				boolean hasXMLPublished = false;
				for (DatastreamType datastream : datastreamList) {
					String dsId = datastream.getDsid();
					if (dsId.equals(Constants.XML_SOURCE)) {
						hasXMLSource = true;
					}
					else if(dsId.equals(Constants.XML_PUBLISHED)) {
						hasXMLPublished = true;
					}
				}
				if(hasXMLPublished) {
					dataStream = FedoraBroker.getDatastreamAsStream(item, Constants.XML_PUBLISHED);
				}
				else if (hasXMLSource) {
					dataStream = FedoraBroker.getDatastreamAsStream(item, Constants.XML_SOURCE);
				}
				else {
					LOGGER.warn("item specified does not exist");
					return "";
				}
			/*	if(hasXMLPublished && hasXMLSource) {
					dataStream2 = FedoraBroker.getDatastreamAsStream(item, Constants.XML_PUBLISHED);
					modifiedDatastream = FedoraBroker.getDatastreamAsStream(item, Constants.XML_SOURCE);
					String modifiedData = compareXML(dataStream2, modifiedDatastream);
					LOGGER.info("Modified Data: " + modifiedData);
				}*/
			}
			if (dataStream != null) {
				try {
					// Xalan appears to have issues tranforming when a stream is sent to the document so making
					// it a w3c Document 
					Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(dataStream);
					parameters.put("data", doc);
				/*	if (modifiedDatastream != null) {
						Document modifiedDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(modifiedDatastream);
						parameters.put("modifiedData", modifiedDoc);
					}*/
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
	
//	private String compareXML(InputStream publishedValue, InputStream modifiedValue) {
		/*JAXBTransform jaxbTransform = new JAXBTransform();
		try {
			Object publishedObject = jaxbTransform.unmarshalStream(publishedValue, Data.class);
			Object modifiedObject = jaxbTransform.unmarshalStream(modifiedValue, Data.class);
			
		}
		catch(JAXBException e) {
			LOGGER.error("Error comparing strings");
		}
		*/
		//Document document = new Document
		/*String publishedDocument = convertStreamToString(publishedValue);
		LOGGER.info("Published Document: " + publishedDocument);
		String modifiedDocument = convertStreamToString(modifiedValue);
		LOGGER.info("Modified Document: " + modifiedDocument);*/
	/*	List<String> diffs = new ArrayList<String>();
		XmlDiff aDiff = new XmlDiff();
		try {
			boolean hasDifference = aDiff.diff(publishedValue, modifiedValue, diffs);
			LOGGER.info("Has difference: " + hasDifference);
			LOGGER.info("Differences: " + diffs.toString());
		}
		catch (Exception e) {
			LOGGER.error("Error comparing xml documents");
		}
		
		//aDiff.di
		
		return null;
	}*/
	
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
	 * </pre>
	 * 
	 * @param layout The layout to use with display (i.e. the xsl stylesheet)
	 * @param template The template that determines the fields on the screen
	 * @param item The item to retrieve data for
	 * @param fieldName The field to retrieve data for
	 * @return Returns a string representation of the page
	 * @throws FedoraClientException
	 */
	public String getPublishedPage(String layout, String template, String item, String fieldName) throws FedoraClientException
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
		if(Util.isNotEmpty(fieldName)){
			parameters.put("fieldName", fieldName);
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
	 * </pre>
	 * 
	 * @param template The template that determines the fields on the screen
	 * @param item The item to retrieve data for
	 * @return The stream for the xml template
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
	 * </pre>
	 * 
	 * @param template To retrieve the object for
	 * @param item The item to retrieve the template for
	 * @return The object representation of the template 
	 * @throws FedoraClientException
	 * @throws JAXBException
	 */
	public Template getTemplateObject(String tmplt, String item)
			throws FedoraClientException, JAXBException {
		InputStream xmlStream = getXMLInputStream(tmplt, item);
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
	 * </pre>
	 * 
	 * @param tmplt The id of the template
	 * @param item The object id
	 * @param form The form data
	 * @return Returns the object id
	 * @throws FedoraClientException
	 * @throws JAXBException
	 */
	public String saveData (String tmplt, String item, Map<String, List<String>> form) 
			throws FedoraClientException, JAXBException {
		InputStream templateStream = getXMLInputStream(tmplt, item);
		
		JAXBTransform jaxbTransform = new JAXBTransform();
		Template template = (Template) jaxbTransform.unmarshalStream(templateStream, Template.class);
		
		Data data = null;
		if (Util.isNotEmpty(item)) {
			InputStream dataStream = getInputStream(item, Constants.XML_SOURCE);
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
					LOGGER.warn("Could not find " + key + " in template" + tmplt);
				}
				else if (!processedValues_.contains(key)) {
					processItem(templateItemMap.get(key), key, values, data, form);
				}
			}
		}
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
		
		if (!Util.isNotEmpty(item)) {
			String location = String.format("%s/objects/%s/datastreams/%s/content"
					, GlobalProps.getProperty(GlobalProps.PROP_FEDORA_URI)
					, tmplt
					, Constants.XML_TEMPLATE);
			
			item = FedoraBroker.createNewObject("test");
			
			FedoraBroker.addDatasstreamBySource(item, Constants.XML_SOURCE, "XML Source", sw.toString());
			FedoraBroker.addDatastreamByReference(item, Constants.XML_TEMPLATE, "M", "XML Template", location);
			if(Util.isNotEmpty(dcSW.toString())) {
				FedoraBroker.modifyDatastreamBySource(item, Constants.DC, "Dublin Core Record for this object", dcSW.toString());
			}
		} else {
			FedoraBroker.modifyDatastreamBySource(item, Constants.XML_SOURCE, "XML Source", sw.toString());
			if(Util.isNotEmpty(dcSW.toString())) {
				FedoraBroker.modifyDatastreamBySource(item, Constants.DC, "Dublin Core Record for this object", dcSW.toString());
			}
		}
		
		processedValues_.clear();
		
		return item;
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
		data.removeElementsByName(item.getName());
		
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
			for (int i = 0; i < values.size(); i++) {
				if (tableData.size() <= i) {
					DataItem dataItem = new DataItem();
					dataItem.setName_(itemName);
					tableData.add(dataItem);
				}
				if(Util.isNotEmpty(values.get(i))) {
					tableData.get(i).getChildValues_().put(columnName, values.get(i));
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
