package au.edu.anu.datacommons.gateway;

import java.io.File;
import java.io.IOException;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;
import com.sun.jersey.api.client.filter.LoggingFilter;

import au.edu.anu.datacommons.config.Config;
import au.edu.anu.datacommons.config.PropertiesFile;

@Path("/")
public class GatewayResource
{
	private static final Logger LOGGER = LoggerFactory.getLogger(GatewayResource.class);
	private static final Client client = Client.create();
	private static Properties redirProps;

	static
	{
		try
		{
			redirProps = new PropertiesFile(new File(Config.DIR, "gateway-ws/redir.properties"));
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
	 * Accepts HTTP POST requests with XML data as body, looks up the function name in the lookup properties file, recreates the inbound request as an outbound
	 * request, sends it to the looked up URL. Then accepts the in bound response, creates an outbound response from the inbound response and forwards the
	 * outbound response to the client.
	 * 
	 * @param xmlDoc
	 *            XML document as HTTP request body.
	 * @return Recreated Response object from ClientResponse.
	 */
	@POST
	@Produces(MediaType.APPLICATION_XML)
	public Response doPostRequest(Document xmlDoc)
	{
		Response resp = null;
		String function = xmlDoc.getDocumentElement().getAttribute("function");
		WebResource redirRes = client.resource(UriBuilder.fromPath(redirProps.getProperty(function)).build());

		// Add HTTP headers to the generic service resource object.
		Builder reqBuilder = redirRes.accept(MediaType.APPLICATION_XML_TYPE);
		MultivaluedMap<String, String> headersMap = httpHeaders.getRequestHeaders();
		for (String key : headersMap.keySet())
		{
			for (String value : headersMap.get(key))
				reqBuilder = reqBuilder.header(key, value);
		}

		ClientResponse respFromRedirRes = reqBuilder.post(ClientResponse.class, xmlDoc);

		// Generate an outbound response object from the inbound response object received from the redirected URL.
		resp = Response.status(respFromRedirRes.getStatus()).type(respFromRedirRes.getType()).entity(respFromRedirRes.getEntityInputStream()).build();

		return resp;
	}
}
