package au.edu.anu.datacommons.phenomics;

import static java.text.MessageFormat.format;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import au.edu.anu.datacommons.config.Config;
import au.edu.anu.datacommons.config.PropertiesFile;
import au.edu.anu.datacommons.phenomics.bindings.PhenResponse;
import au.edu.anu.datacommons.webservice.AbstractResource;
import au.edu.anu.datacommons.webservice.UnauthorisedException;
import au.edu.anu.datacommons.webservice.bindings.DcRequest;
import au.edu.anu.datacommons.webservice.bindings.FedoraItem;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.filter.ClientFilter;
import com.sun.jersey.api.client.filter.LoggingFilter;
import com.sun.jersey.core.util.MultivaluedMapImpl;

@Path("/")
public class PhenomicsResource extends AbstractResource
{
	public static final Logger LOGGER = LoggerFactory.getLogger(PhenomicsResource.class);

	static
	{
		try
		{
			genericWsProps = new PropertiesFile(new File(Config.DIR, "ws-phenomics/genericws.properties"));
		}
		catch (IOException e)
		{
			LOGGER.error(e.getMessage(), e);
			throw new RuntimeException(e.getMessage(), e);
		}

		try
		{
			packageLookup = new PropertiesFile(new File(Config.DIR, "ws-phenomics/wslookup.properties"));
		}
		catch (IOException e)
		{
			LOGGER.error(e.getMessage(), e);
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public Response doGetRequest()
	{
		Response resp = null;
		resp = Response.ok("Test", MediaType.TEXT_PLAIN_TYPE).build();
		return resp;
	}

	/**
	 * Accepts an HTTP POST request with an application/xml documents in its body. Generates individual requests for the Generic DataCommons web service and
	 * submits for processing. Then generates a response to be sent back.
	 * 
	 * @param xmlDoc
	 *            Request as an XML document.
	 * @return HTTP Response with an XML document with results of requests.
	 */
	public Response doPostRequest(Document xmlDoc)
	{
		Response resp = null;

		updateClientLogging();
		PhenResponse phenRespRootElement = new PhenResponse();
		List<Element> statusElements = new ArrayList<Element>();
		try
		{
			int countSuccess = 0;
			int countTotalRequests = 0;
			JAXBContext context = getJaxbContext(xmlDoc);
			Unmarshaller um = context.createUnmarshaller();

			Processable proc = (Processable) um.unmarshal(xmlDoc);

			authenticateCredentials();

			// Iterate through each DcRequest object, wrap it in an HTTP request and send to generic service.
			Map<DcRequest, Map<String, FedoraItem>> dcReqs = proc.generateDcRequests();
			countTotalRequests = dcReqs.size();
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
						NodeList extIdElements = statusElementFromGenSvc.getElementsByTagName("externalId");
						if (extIdElements.getLength() > 0)
							statusElementFromGenSvc.setAttribute("phid", extIdElements.item(0).getTextContent());
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

			// With the items created, generate requests for relations and send to generic request.
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
								LOGGER.debug("Relation request - HTTP Status {}, body: {}", respFromGenSvc.getStatus(), respStr);
							}
							catch (Exception e)
							{
								LOGGER.error(format("Unable to set relationship between {0} and {1}", sourcePid, targetPid), e);
							}
						}
					}
				}
			}

			if (countSuccess == countTotalRequests)
				phenRespRootElement.setStatus(PhenResponse.Status.SUCCESS);
			else if (countSuccess == 0)
				phenRespRootElement.setStatus(PhenResponse.Status.FAILURE);
			else
				phenRespRootElement.setStatus(PhenResponse.Status.PARTIAL);

			resp = Response.ok(phenRespRootElement, MediaType.APPLICATION_XML_TYPE).build();
		}
		catch (UnauthorisedException e)
		{
			LOGGER.error(e.getMessage());
			phenRespRootElement.setStatus(PhenResponse.Status.FAILURE);
			phenRespRootElement.setMsg(e.getMessage());
			resp = Response.status(Status.UNAUTHORIZED).entity(phenRespRootElement).build();
		}
		catch (Exception e)
		{
			LOGGER.error(e.getMessage(), e);
			phenRespRootElement.setStatus(PhenResponse.Status.FAILURE);
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
