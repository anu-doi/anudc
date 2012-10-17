package au.edu.anu.datacommons.phenomics;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
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
import org.xml.sax.SAXException;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;
import com.sun.jersey.api.client.filter.LoggingFilter;

import au.edu.anu.datacommons.config.Config;
import au.edu.anu.datacommons.config.PropertiesFile;
import au.edu.anu.datacommons.phenomics.bindings.PhenResponse;
import au.edu.anu.datacommons.webservice.bindings.DcRequest;

@Path("/")
public class PhenomicsResource
{
	private static final Logger LOGGER = LoggerFactory.getLogger(PhenomicsResource.class);
	private static final Client client = Client.create();
	private static Properties genericWsProps;

	private static DocumentBuilder docBuilder;

	static
	{
		try
		{
			docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		}
		catch (ParserConfigurationException e)
		{
			LOGGER.error(e.getMessage(), e);
			docBuilder = null;
		}

		client.addFilter(new LoggingFilter());

		try
		{
			genericWsProps = new PropertiesFile(new File(Config.DIR, "phenomics-ws/genericws.properties"));
		}
		catch (IOException e)
		{
			LOGGER.error(e.getMessage(), e);
		}
	}

	@Context
	private UriInfo uriInfo;
	@Context
	private Request request;
	@Context
	private HttpHeaders httpHeaders;

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
	@POST
	@Produces(MediaType.APPLICATION_XML)
	public Response doPostRequest(Document xmlDoc)
	{
		Response resp = null;
		PhenResponse phenRespDoc = new PhenResponse();
		try
		{
			List<Element> statusElements = new ArrayList<Element>();
			int countSuccess = 0;
			JAXBContext context = getJaxbContext(xmlDoc);
			Unmarshaller um = context.createUnmarshaller();

			Processable proc = (Processable) um.unmarshal(xmlDoc);
			WebResource genericRes = client.resource(UriBuilder.fromPath(genericWsProps.getProperty("dc.baseUrl"))
					.path(genericWsProps.getProperty("dc.wsPath")).build());

			// Add HTTP headers to the generic service resource object.
			Builder reqBuilder = genericRes.accept(MediaType.APPLICATION_XML_TYPE);
			MultivaluedMap<String, String> headersMap = httpHeaders.getRequestHeaders();
			for (String key : headersMap.keySet())
			{
				for (String value : headersMap.get(key))
					reqBuilder = reqBuilder.header(key, value);
			}
			
			// Iterate through each DcRequest object, wrap it in an HTTP request and send to generic service.
			Map<DcRequest, Map<String, DcRequest>> dcReqs = proc.generateDcRequests();
			for (Entry<DcRequest, Map<String, DcRequest>> entry : dcReqs.entrySet())
			{
				ClientResponse respFromGenService = reqBuilder.post(ClientResponse.class, entry.getKey());
				if (respFromGenService.getClientResponseStatus() == Status.OK)
					countSuccess++;

				try
				{
					Document doc = docBuilder.parse(respFromGenService.getEntityInputStream());
					Node n = doc.getDocumentElement().getFirstChild();
					while (n != null && n.getNodeType() != Node.ELEMENT_NODE)
						n = n.getNextSibling();
					if (n != null)
						statusElements.add((Element) n);
				}
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (SAXException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			phenRespDoc.setNodes(statusElements);
			if (countSuccess == dcReqs.size())
				phenRespDoc.setStatus(PhenResponse.Status.SUCCESS);
			else if (countSuccess == 0)
				phenRespDoc.setStatus(PhenResponse.Status.FAILURE);
			else
				phenRespDoc.setStatus(PhenResponse.Status.PARTIAL);

			resp = Response.ok(phenRespDoc, MediaType.APPLICATION_XML_TYPE).build();
		}
		catch (JAXBException e)
		{
			LOGGER.error(e.getMessage(), e);
			phenRespDoc.setStatus(PhenResponse.Status.FAILURE);
			phenRespDoc.setMsg(e.toString());
			resp = Response.serverError().entity(phenRespDoc).build();
		}
		catch (IOException e)
		{
			LOGGER.error(e.getMessage(), e);
			phenRespDoc.setStatus(PhenResponse.Status.FAILURE);
			phenRespDoc.setMsg(e.toString());
			resp = Response.serverError().entity(phenRespDoc).build();
		}

		return resp;
	}

	private JAXBContext getJaxbContext(Document xmlDoc) throws JAXBException, IOException
	{
		Properties lookup = getWsLookup();
		String version = xmlDoc.getDocumentElement().getAttribute("version");
		if (!lookup.containsKey(version))
			throw new JAXBException(MessageFormat.format("Unrecognised schema version - {0}", version));
		String packageName = lookup.getProperty(version);
		LOGGER.debug("Using package '{}' for version '{}'", packageName, version);
		JAXBContext context = JAXBContext.newInstance(packageName);
		return context;
	}

	private Properties getWsLookup() throws IOException
	{
		Properties lookupTbl = new Properties();
		InputStream fileStream = null;
		try
		{
			File wsLookupFile = new File(Config.DIR, "phenomics-ws/wslookup.properties");
			fileStream = new FileInputStream(wsLookupFile);
			lookupTbl.load(fileStream);
		}
		finally
		{
			try
			{
				fileStream.close();
			}
			catch (Exception e)
			{
			}
		}

		return lookupTbl;
	}
}
