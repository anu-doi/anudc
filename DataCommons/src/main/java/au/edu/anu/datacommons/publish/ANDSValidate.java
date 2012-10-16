package au.edu.anu.datacommons.publish;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import au.edu.anu.datacommons.ands.check.ActivityCheck;
import au.edu.anu.datacommons.ands.check.CollectionCheck;
import au.edu.anu.datacommons.ands.check.PartyCheck;
import au.edu.anu.datacommons.ands.check.ServiceCheck;
import au.edu.anu.datacommons.ands.xml.RegistryObjects;
import au.edu.anu.datacommons.data.fedora.FedoraBroker;
import au.edu.anu.datacommons.properties.GlobalProps;
import au.edu.anu.datacommons.search.ExternalPoster;
import au.edu.anu.datacommons.search.SparqlQuery;
import au.edu.anu.datacommons.util.Constants;
import au.edu.anu.datacommons.xml.transform.JAXBTransform;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.yourmediashelf.fedora.client.FedoraClientException;

/**
 * ANDSValidate
 * 
 * Australian National University Data Commons
 * 
 * Validates records to send to ANDS.
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		17/07/2012	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
public class ANDSValidate implements Validate{
	static final Logger LOGGER = LoggerFactory.getLogger(ANDSValidate.class);
	
	private List<String> errorMessages_;
	
	/**
	 * Constructor
	 * 
	 * Performs initialisation for some fields
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		17/07/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 *
	 */
	public ANDSValidate() {
		errorMessages_ = new ArrayList<String>();
	}

	/**
	 * isValid
	 * 
	 * Checks if the pid is valid
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * X.X		17/07/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param pid
	 * @return
	 * @see au.edu.anu.datacommons.publish.Validate#isValid(java.lang.String)
	 */
	@Override
	public boolean isValid(String pid) {
		boolean isValid = false;
		try {
			InputStream xmlSource = FedoraBroker.getDatastreamAsStream(pid, Constants.XML_SOURCE);
			Document doc = getStreamAsDocument(xmlSource);
			NodeList typeNodes = doc.getElementsByTagName("type");
			
			if (typeNodes.getLength() > 0) {
				Node typeNode = typeNodes.item(0);
				
				if ("collection".equals(typeNode.getTextContent().toLowerCase())) {
					LOGGER.info("Is collection");
					isValid = isValidCollection(pid, doc);
				}
				else if ("activity".equals(typeNode.getTextContent().toLowerCase())) {
					LOGGER.info("Is activity");
					isValid = isValidActivity(pid, doc);
				}
				else if ("party".equals(typeNode.getTextContent().toLowerCase())) {
					LOGGER.info("Is party");
					isValid = isValidParty(pid, doc);
				}
				else if ("service".equals(typeNode.getTextContent().toLowerCase())) {
					LOGGER.info("Is service");
					isValid = isValidService(pid, doc);
				}
				else {
					LOGGER.error("This type of field should not go to ANDS, Type: {}", typeNode.getTextContent());
					isValid = false;
				}
			}
			else {
				LOGGER.error("Element has no type");
				isValid = false;
			}
		}
		catch (FedoraClientException e) {
			isValid = false;
		}
		
		return isValid;
	}
	
	/**
	 * getStreamAsDocument
	 *
	 * Gets the inputstream as a Document
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		17/07/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param inputStream An inputstream to transform to a XML Document
	 * @return The xml document
	 */
	private Document getStreamAsDocument(InputStream inputStream) {
		Document doc = null;
		try {
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream);
		}
		catch (ParserConfigurationException e) {
			
		}
		catch (IOException e) {
			
		}
		catch (SAXException e) {
			
		}
		return doc;
	}

	/**
	 * getErrorMessages
	 * 
	 * Returns a list of error messages from the validation
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		17/07/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return A list of error messages
	 * @see au.edu.anu.datacommons.publish.Validate#getErrorMessages()
	 */
	@Override
	public List<String> getErrorMessages() {
		return errorMessages_;
	}
	
	/**
	 * isValidCollection
	 *
	 * Determins if it is a valid collection
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		17/07/2012	Genevieve Turner(GT)	Initial
	 * 0.2		15/10/2012	Genevieve Turner (GT)	Updated to perform RIF-CS validation
	 * </pre>
	 * 
	 * @param pid The pid of the object to check if it is valid
	 * @param doc The xml document to validate
	 * @return true if it is valid otherwise false
	 */
	private boolean isValidCollection(String pid, Document doc) {
		// Required Assocation Types
		// Party
		// Activity
		
		boolean isValid = true;
		if(!hasAssociatedType(pid, "party", 2)) {
			isValid = false;
		}
		if(!hasAssociatedType(pid, "activity", 3)) {
			isValid = false;
		}
		boolean validationErrors = xmlValidate(pid, CollectionCheck.class);
		/*if (!validationErrors) {
			isValid = false;
		}*/
		
		return isValid;
	}
	
	/**
	 * isValidParty
	 *
	 * Placeholder
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		17/07/2012	Genevieve Turner(GT)	Initial
	 * 0.2		15/10/2012	Genevieve Turner (GT)	Updated to perform RIF-CS validation
	 * </pre>
	 * 
	 * @param pid The pid of the object to check if it is valid
	 * @param doc The xml document to validate
	 * @return true if it is valid otherwise false
	 */
	private boolean isValidParty(String pid, Document doc) {
		// Required Assocation Types
		// Collection
		// Recommended Association Types - Nothing is done with recommended at this point
		// Activity
		
		boolean isValid = true;
		if(!hasAssociatedType(pid, "collection", 2)) {
			isValid = false;
		}
		// Relationship not require but recommended
		hasAssociatedType(pid,"activity", 3);
		boolean validationErrors = xmlValidate(pid, PartyCheck.class);
		/*if (!validationErrors) {
			isValid = false;
		}*/
		
		return isValid;
	}
	
	/**
	 * isValidActivity
	 *
	 * Determines if it is a valid Activity
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		17/07/2012	Genevieve Turner(GT)	Initial
	 * 0.2		15/10/2012	Genevieve Turner (GT)	Updated to perform RIF-CS validation
	 * </pre>
	 * 
	 * @param pid The pid of the object to check if it is valid
	 * @param doc The xml document to validate
	 * @return true if it is valid otherwise false
	 */
	private boolean isValidActivity(String pid, Document doc) {
		// Required Assocation Types
		// Party
		// Collection
		
		boolean isValid = true;
		if(!hasAssociatedType(pid, "collection", 2)) {
			isValid = false;
		}
		if(!hasAssociatedType(pid, "party", 3)) {
			isValid = false;
		}
		boolean validationErrors = xmlValidate(pid, ActivityCheck.class);
		/*if (!validationErrors) {
			isValid = false;
		}*/

		return isValid;
	}
	
	/**
	 * isValidService
	 *
	 * Determines if it is a valid service
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		17/07/2012	Genevieve Turner(GT)	Initial
	 * 0.2		15/10/2012	Genevieve Turner (GT)	Updated to perform RIF-CS validation
	 * </pre>
	 * 
	 * @param pid The pid of the object to check if it is valid
	 * @param doc The xml document to validate
	 * @return true if it is valid otherwise false
	 */
	private boolean isValidService(String pid, Document doc) {
		// Required Assocation Types
		// Collection
		// Recommended Association Types - Nothing is done with recommended at this point
		// Party
		
		boolean isValid = true;
		if(!hasAssociatedType(pid, "collection", 2)) {
			isValid = false;
		}
		hasAssociatedType(pid, "party", 3);
		boolean validationErrors = xmlValidate(pid, ServiceCheck.class);
		/*if (!validationErrors) {
			isValid = false;
		}*/

		return isValid;
	}
	
	/**
	 * hasAssociatedType
	 *
	 * Checks if there is an assocation type for the record
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		17/07/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param pid The pid check the associations for
	 * @param type The type of association to check
	 * @return If there is an associated type
	 */
	private boolean hasAssociatedType(String pid, String type, int qualityLevel) {
		boolean isValid = false;
		SparqlQuery sparqlQuery = new SparqlQuery();
		sparqlQuery.addVar("?item");
		sparqlQuery.addVar("?type");
		sparqlQuery.addVar("?predicate");
		
		StringBuffer tripleString = new StringBuffer();
		
		tripleString.append("{ <info:fedora/");
		tripleString.append(pid);
		tripleString.append("> ?predicate ?item . } ");
		tripleString.append("UNION ");
		tripleString.append("{ ?item ?predicate <info:fedora/");
		tripleString.append(pid);
		tripleString.append("> } ");
		
		sparqlQuery.addTripleSet(tripleString.toString());
		sparqlQuery.addTriple("?item", "<dc:type>", "?type", false);
		StringBuffer filterString = new StringBuffer();
		
		// Add the predicate filter
		filterString.append("regex(str(?predicate), '");
		filterString.append(GlobalProps.getProperty(GlobalProps.PROP_FEDORA_RELATEDURI));
		filterString.append("', 'i') ");
		filterString.append("&& ");
		// Add the type filter
		filterString.append("regex(?type , '");
		filterString.append(type);
		filterString.append("', 'i') ");
		
		sparqlQuery.addFilter(filterString.toString(), "");
		String queryString = sparqlQuery.generateQuery();
		
		//TODO see if there is an easier way to get this information
		ExternalPoster poster = new ExternalPoster();
		poster.setUrl(GlobalProps.getProperty(GlobalProps.PROP_FEDORA_URI) + GlobalProps.getProperty(GlobalProps.PROP_FEDORA_RISEARCHURL));
		poster.setUsername(GlobalProps.getProperty(GlobalProps.PROP_FEDORA_USERNAME));
		poster.setPassword(GlobalProps.getProperty(GlobalProps.PROP_FEDORA_PASSWORD));
		poster.setType(MediaType.APPLICATION_FORM_URLENCODED);
		poster.setAcceptType(MediaType.TEXT_XML);
		MultivaluedMapImpl parameters = new MultivaluedMapImpl();
		parameters.add("dt", "on");
		parameters.add("format", "Sparql");
		parameters.add("lang", "sparql");
		parameters.add("limit", "1");
		parameters.add("type", "tuples");
		poster.setParameters(parameters);
		ClientResponse response = poster.post("query", queryString.toString());
		Document responseDoc = response.getEntity(Document.class);
		
		NodeList resultNodes = responseDoc.getElementsByTagName("result");
		if (resultNodes.getLength() > 0) {
			LOGGER.info("Number of nodes: {}", resultNodes.getLength());
			isValid = true;
		}
		else {
			LOGGER.info("No Results returned");
			errorMessages_.add("Quality Level " + qualityLevel + " - Link with item type " + type);
			isValid = false;
		}
		return isValid;
	}
	
	private boolean xmlValidate(String pid, Class clazz) {
		boolean isValid = false;
		try {
			InputStream xmlStream = FedoraBroker.getDatastreamAsStream(pid, Constants.XML_SOURCE);
			InputStream xslStream = FedoraBroker.getDatastreamAsStream("def:rif-cs", Constants.XSL_SOURCE);
		
			StringWriter sw = new StringWriter();
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Source xmlSource = new StreamSource (xmlStream);
			Source xslSource = new StreamSource (xslStream);
			Transformer transformer = transformerFactory.newTransformer(xslSource);
			transformer.transform(xmlSource, new StreamResult(sw));
			//transformer.tra
			LOGGER.info("page: {}", sw.toString());
			JAXBTransform jaxbTransform = new JAXBTransform();
			
			InputStream rifcsStream = new ByteArrayInputStream(sw.toString().getBytes("UTF-8"));
			RegistryObjects registryObjects = (RegistryObjects) jaxbTransform.unmarshalStream(rifcsStream, RegistryObjects.class);
			
			ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
			Validator validator = factory.getValidator();
			
			Set<ConstraintViolation<RegistryObjects>> constraintViolations = validator.validate(registryObjects, clazz);
			
			if (constraintViolations.size() > 0) {
				Iterator<ConstraintViolation<RegistryObjects>> it = constraintViolations.iterator();
				while (it.hasNext()) {
					ConstraintViolation<RegistryObjects> violation = it.next();
					//LOGGER.info("Violation: {}", violation.getMessage());
					errorMessages_.add(violation.getMessage());
				}
			}
			else {
				isValid = true;
			}
			
		}
		catch (FedoraClientException e) {
			LOGGER.error("Exception retrieving stream", e);
		}
		catch (TransformerException e) {
			LOGGER.error("Exception executing transform", e);
		}
		catch (UnsupportedEncodingException e) {
			LOGGER.error("Exception getting string as utf-8", e);
		}
		catch (JAXBException e) {
			LOGGER.error("Exception transforming document to JAXB", e);
		}
		return isValid;
	}
}
