package au.edu.anu.datacommons.xml.transform;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
import au.edu.anu.datacommons.util.Constants;
import au.edu.anu.datacommons.util.Util;

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
 * 
 */
public class ViewTransform
{
	static final Logger LOGGER = LoggerFactory.getLogger(ViewTransform.class);
	
	/**
	 * getPage
	 * 
	 * Transforms to a document specified by the layout and performs the transformation
	 * on either the template or the item depending on which values are given.  This generally
	 * transforms the given documents to either a html page or an xml document
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
		InputStream xmlStream = null;
		if(Util.isNotEmpty(template)){
			xmlStream = FedoraBroker.getDatastreamAsStream(template, Constants.XML_TEMPLATE);
			
			parameters.put("tmplt", template);
		}
		else if(Util.isNotEmpty(item)){
			xmlStream = FedoraBroker.getDatastreamAsStream(item, Constants.XML_TEMPLATE);
		}
		else {
			LOGGER.warn("No Template or Item to retrieve the datastream from specified");
			return "";
		}
		
		InputStream xslStream = null;
		
		if(Util.isNotEmpty(layout)){
			xslStream = FedoraBroker.getDatastreamAsStream(layout, Constants.XSL_SOURCE);
			parameters.put("layout", layout);
		}
		else {
			LOGGER.warn("No layout specified");
			return "";
		}
		if (xmlStream == null) {
			LOGGER.warn("XML Stream is empty");
			return "";
		}
		if (xslStream == null) {
			LOGGER.warn("XSL Stream is empty");
			return "";
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
}
