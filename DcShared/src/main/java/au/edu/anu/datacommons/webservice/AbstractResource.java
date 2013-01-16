package au.edu.anu.datacommons.webservice;

import static java.text.MessageFormat.format;

import java.io.IOException;
import java.io.StringReader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import au.edu.anu.datacommons.webservice.bindings.CombinedStatusResponse;
import au.edu.anu.datacommons.webservice.bindings.DcRequest;
import au.edu.anu.datacommons.webservice.bindings.FedoraItem;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;
import com.sun.jersey.api.client.filter.ClientFilter;
import com.sun.jersey.api.client.filter.LoggingFilter;
import com.sun.jersey.core.util.MultivaluedMapImpl;

/**
 * Provides common functionality for individual Machine 2 Machine implementations. Includes the primary Restful endpoint for XML requests from instruments and
 * other systems.
 * 
 * This class is intended to act as the middle layer between the web service gateway that dispatches requests to one of the Service modules that implement this
 * class.
 */
public abstract class AbstractResource
{
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractResource.class);
	
	protected static final Client client = Client.create();
	protected static final ClientFilter loggingFilter = new LoggingFilter();
	protected static DocumentBuilder docBuilder;

	protected Properties genericWsProps;
	protected Properties packageLookup;

	static
	{
		try
		{
			docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		}
		catch (ParserConfigurationException e)
		{
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	@Context
	protected UriInfo uriInfo;
	@Context
	protected Request request;
	@Context
	protected HttpHeaders httpHeaders;

	
	/**
	 * Accepts an HTTP POST request with an application/xml documents in its body. Generates individual requests for the Generic DataCommons web service and
	 * submits for processing. Then generates a response to be sent back.
	 * 
	 * @param xmlDoc
	 *            Request as an XML document.
	 * @return HTTP Response with an XML document with results of requests.
	 */
	@POST
	@Produces(MediaType.APPLICATION_XML)
	public Response doPostRequest(Document xmlDoc)
	{
		Response resp = null;
		resp = processXmlRequest(xmlDoc);
		return resp;
	}
	
	abstract protected Element processRespElement(Element statusElementFromGenSvc);

	/**
	 * Updates logging from the Client object as specified in the configuration file. All HTTP requests and responses are sent and received through the Client
	 * object. If http.logging is set to true then a logging filter is added to the Client object.
	 */
	protected void updateClientLogging()
	{
		if (genericWsProps.getProperty("http.logging", "false").equalsIgnoreCase("true"))
		{
			if (!client.isFilterPreset(loggingFilter))
				client.addFilter(loggingFilter);
		}
		else
		{
			if (client.isFilterPreset(loggingFilter))
				client.removeFilter(loggingFilter);
		}
	}

	/**
	 * Reads the <code>version</code> attribute in the document element, finds the package name containing the class files for the version specified and creates
	 * a {@link JAXBContext} using the package name as the context path.
	 * 
	 * @param xmlDoc
	 *            XML document from which version attribute will be read from the root element
	 * @return Generated JAXBContext based on the version attribute
	 * @throws JAXBException
	 *             when unable to generate a JAXBContext object
	 */
	protected JAXBContext getJaxbContext(Document xmlDoc) throws JAXBException
	{
		String version = xmlDoc.getDocumentElement().getAttribute("version");
		if (!packageLookup.containsKey(version))
			throw new JAXBException(MessageFormat.format("Unrecognised schema version - {0}", version));
		String packageName = packageLookup.getProperty(version);
		LOGGER.debug("Using package '{}' for version '{}'", packageName, version);
		JAXBContext context = JAXBContext.newInstance(packageName);
		return context;
	}

	/**
	 * Generates a WebResource.Builder object containing template parameters for an HTTP request to Data Commons Web Service.
	 * 
	 * @return Generated Builder object
	 */
	protected Builder generateHttpRequestBuilder()
	{
		UriBuilder uriBuilder = UriBuilder.fromPath(genericWsProps.getProperty("dc.baseUrl")).path(genericWsProps.getProperty("dc.wsPath"));
		uriBuilder = addQueryParams(uriBuilder);
		LOGGER.debug("Generated URI {}", uriBuilder.build().toString());
		Builder reqBuilder = client.resource(uriBuilder.build()).accept(MediaType.APPLICATION_XML_TYPE);
		reqBuilder = addHttpHeaders(reqBuilder);
		return reqBuilder;
	}

	/**
	 * Adds query parameters in the context for the current HTTP request to a UriBuilder. All query parameters present in the original HTTP request are included
	 * in all HTTP requests generated by this class.
	 * 
	 * @param uriBuilder
	 *            UriBuilder object to which query parameters will be added
	 * @return UriBuilder with the added query parameters
	 */
	protected UriBuilder addQueryParams(UriBuilder uriBuilder)
	{
		Set<Entry<String, List<String>>> queryParamsSet = uriInfo.getQueryParameters().entrySet();
		for (Entry<String, List<String>> queryParam : queryParamsSet)
			for (String queryParamValue : queryParam.getValue())
				uriBuilder = uriBuilder.queryParam(queryParam.getKey(), queryParamValue);
		
		return uriBuilder;
	}

	/**
	 * Generates a WebResource.Builder object containing template parameters for an HTTP request to Data Commons to add a link to a record.
	 * 
	 * @param pid
	 *            Pid of the record to which another record will be linked
	 * @return Generated Builder object
	 */
	protected Builder generateAddLinkBuilder(String pid)
	{
		Builder reqBuilder = client
				.resource(UriBuilder.fromPath(genericWsProps.getProperty("dc.baseUrl")).path(genericWsProps.getProperty("dc.addLinkPath")).path(pid).build())
				.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).accept(MediaType.TEXT_PLAIN_TYPE);
		reqBuilder = addHttpHeaders(reqBuilder);
		return reqBuilder;
	}

	/**
	 * Sends an HTTP request to authenticate the credentials provided in the original HTTP request.
	 * 
	 * @return ClientResponse with the body containing username and display name.
	 * @throws UnauthorisedException
	 *             when the credentials provided are invalid
	 */
	protected ClientResponse authenticateCredentials() throws UnauthorisedException
	{
		ClientResponse respUserInfo = generateUserInfoBuilder().get(ClientResponse.class);
		if (respUserInfo.getClientResponseStatus() == Status.UNAUTHORIZED)
			throw new UnauthorisedException("Invalid username and/or password.");
		return respUserInfo;
	}
	
	/**
	 * Generates an HTTP Request Builder for getting the details of the user sending the request or the user associated with an M2M request.
	 * 
	 * @return Generated Builder object
	 */
	protected Builder generateUserInfoBuilder()
	{
		UriBuilder authUserUriBuilder = UriBuilder.fromPath(genericWsProps.getProperty("dc.baseUrl")).path(genericWsProps.getProperty("dc.userInfo"));
		authUserUriBuilder = addQueryParams(authUserUriBuilder);
		Builder reqBuilder = client.resource(authUserUriBuilder.build()).type(MediaType.TEXT_PLAIN_TYPE).accept(MediaType.TEXT_PLAIN_TYPE);
		reqBuilder = addHttpHeaders(reqBuilder);
		return reqBuilder;
	}

	/**
	 * Adds the HTTP headers present in the original HTTP request to a Builder object that will subsequently be used to send a request to Data Commons.
	 * 
	 * @param b
	 *            Builder to which HTTP headers will be added
	 * @return Builder object with HTTP headers added to it
	 */
	protected Builder addHttpHeaders(Builder b)
	{
		MultivaluedMap<String, String> headersMap = httpHeaders.getRequestHeaders();
		String headersToCopyLine = genericWsProps.getProperty("http.headers");
		if (headersToCopyLine != null)
		{
			String[] headersToCopy = headersToCopyLine.split(";");
			for (int i = 0; i < headersToCopy.length; i++)
			{
				if (headersMap.containsKey(headersToCopy[i]))
				{
					for (String value : headersMap.get(headersToCopy[i]))
						b = b.header(headersToCopy[i], value);
				}
			}
		}
	
		return b;
	}
	
	/**
	 * Creates a relation between records created/updated as a result of sending a DcRequest and a list of PIDs of records that already existed in Data Commons.
	 * 
	 * @param dcReqs
	 *            Map of records and their type of link to another record as <code>Map&lt;DcRequest, Map&lt;String, FedoraItem&gt;&gt;</code>
	 */
	protected void createRelations(Map<DcRequest, Map<String, FedoraItem>> dcReqs)
	{
		for (Entry<DcRequest, Map<String, FedoraItem>> dcReqEntry : dcReqs.entrySet())
		{
			// If the value is null instead of Map<String, FedoraItem, item doesn't need to be related.
			if (dcReqEntry.getValue() != null)
			{
				for (Entry<String, FedoraItem> relEntry : dcReqEntry.getValue().entrySet())
				{
					String sourcePid = dcReqEntry.getKey().getFedoraItem().getPid();
					String targetPid = relEntry.getValue().getPid();
					if (sourcePid != null && sourcePid.length() > 0 && targetPid != null && targetPid.length() > 0)
					{
						MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
						formData.add("linkType", relEntry.getKey());
						formData.add("itemId", targetPid);
	
						try
						{
							ClientResponse respFromGenSvc = generateAddLinkBuilder(sourcePid).post(ClientResponse.class, formData);
							String respStr = respFromGenSvc.getEntity(String.class);
							LOGGER.debug("Relation request for {} {} {} - HTTP Status {}, body: {}", sourcePid, relEntry.getKey(), targetPid,
									respFromGenSvc.getStatus(), respStr);
							if (respFromGenSvc.getClientResponseStatus() == Status.OK)
								LOGGER.info("Created relation {} {} {}...", sourcePid, relEntry.getKey(), targetPid);
						}
						catch (Exception e)
						{
							LOGGER.error(format("Unable to set relationship {0} {1} {2}", sourcePid, relEntry.getKey(), targetPid), e);
						}
					}
				}
			}
		}
	}

	/**
	 * Executes the collection of DcRequests to create/update records. Then links those records with other records as specified.
	 * 
	 * @param dcReqs
	 *            Map of DcRequests with the relations to other records
	 * @param statusElements
	 *            Collection of status elements returned from the Data Commons Web Service for each DcRequest sent
	 * @return Number of requests successfully processed
	 */
	public int executeDcRequests(Map<DcRequest, Map<String, FedoraItem>> dcReqs, List<Element> statusElements)
	{
		int countSuccess = 0;
		for (DcRequest iDcRequest : dcReqs.keySet())
		{
			ClientResponse respFromGenService = generateHttpRequestBuilder().post(ClientResponse.class, iDcRequest);
	
			String respStr = respFromGenService.getEntity(String.class);
			try
			{
				Document doc = docBuilder.parse(new InputSource(new StringReader(respStr)));
				Node n = doc.getDocumentElement().getFirstChild();
				while (n != null && n.getNodeType() != Node.ELEMENT_NODE)
					n = n.getNextSibling();
				if (n != null)
				{
					Element statusElementFromGenSvc = (Element) n;
					statusElementFromGenSvc = processRespElement(statusElementFromGenSvc);
					statusElements.add(statusElementFromGenSvc);
					String pid = statusElementFromGenSvc.getAttribute("anudcid");
					LOGGER.trace("Generic service returned Pid: {}", pid);
					iDcRequest.getFedoraItem().setPid(pid);
				}
	
				// If it reaches this point without exception then the response was a valid XML
				if (respFromGenService.getClientResponseStatus() == Status.OK)
					countSuccess++;
			}
			catch (IOException e)
			{
				LOGGER.error(e.getMessage());
			}
			catch (SAXException e)
			{
				LOGGER.error(e.getMessage());
			}
		}
		return countSuccess;
	}

	/**
	 * Processes a specified XML document by:
	 * 
	 * <ol>
	 * <li>Authenticates the sender</li>
	 * <li>Generates a list of DcRequests and sends to the Data Commons Web Service to create/update records</li>
	 * <li>Creates/updates the relations between records as specified constants properties file</li>
	 * <li>Generates an HTTP response to be sent back to the client</li>
	 * </ol>
	 * 
	 * @param xmlDoc
	 *            Document object to be processed
	 * @return HTTP response as Response
	 */
	public Response processXmlRequest(Document xmlDoc)
	{
		Response resp;
		updateClientLogging();
		CombinedStatusResponse phenRespRootElement = new CombinedStatusResponse();
		List<Element> statusElements = new ArrayList<Element>();
		try
		{
			authenticateCredentials();
			
			JAXBContext context = getJaxbContext(xmlDoc);
			Unmarshaller um = context.createUnmarshaller();
	
			Processable proc = (Processable) um.unmarshal(xmlDoc);
	
			// Iterate through each DcRequest object, wrap it in an HTTP request and send to generic service.
			Map<DcRequest, Map<String, FedoraItem>> dcReqs = proc.generateDcRequests();
			int countTotalRequests = dcReqs.size();
			int countSuccess = executeDcRequests(dcReqs, statusElements);
	
			createRelations(dcReqs);
	
			if (countSuccess == countTotalRequests)
				phenRespRootElement.setStatus(CombinedStatusResponse.Status.SUCCESS);
			else if (countSuccess == 0)
				phenRespRootElement.setStatus(CombinedStatusResponse.Status.FAILURE);
			else
				phenRespRootElement.setStatus(CombinedStatusResponse.Status.PARTIAL);
	
			resp = Response.ok(phenRespRootElement, MediaType.APPLICATION_XML_TYPE).build();
		}
		catch (UnauthorisedException e)
		{
			LOGGER.error(e.getMessage());
			phenRespRootElement.setStatus(CombinedStatusResponse.Status.FAILURE);
			phenRespRootElement.setMsg(e.getMessage());
			resp = Response.status(Status.UNAUTHORIZED).entity(phenRespRootElement).build();
		}
		catch (Exception e)
		{
			LOGGER.error(e.getMessage(), e);
			phenRespRootElement.setStatus(CombinedStatusResponse.Status.FAILURE);
			phenRespRootElement.setMsg(e.getMessage());
			resp = Response.serverError().entity(phenRespRootElement).build();
		}
		finally
		{
			phenRespRootElement.setNodes(statusElements);
		}
		return resp;
	}
}
