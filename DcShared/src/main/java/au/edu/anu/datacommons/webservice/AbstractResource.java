package au.edu.anu.datacommons.webservice;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
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
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.WebResource.Builder;
import com.sun.jersey.api.client.filter.ClientFilter;
import com.sun.jersey.api.client.filter.LoggingFilter;

public abstract class AbstractResource
{
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractResource.class);
	
	protected static final Client client = Client.create();
	protected static final ClientFilter loggingFilter = new LoggingFilter();
	protected static Properties genericWsProps;
	protected static Properties packageLookup;
	protected static DocumentBuilder docBuilder;

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
	}
	
	@Context
	protected UriInfo uriInfo;
	@Context
	protected Request request;
	@Context
	protected HttpHeaders httpHeaders;

	
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

	protected JAXBContext getJaxbContext(Document xmlDoc) throws JAXBException, IOException
	{
		String version = xmlDoc.getDocumentElement().getAttribute("version");
		if (!packageLookup.containsKey(version))
			throw new JAXBException(MessageFormat.format("Unrecognised schema version - {0}", version));
		String packageName = packageLookup.getProperty(version);
		LOGGER.debug("Using package '{}' for version '{}'", packageName, version);
		JAXBContext context = JAXBContext.newInstance(packageName);
		return context;
	}

	protected Builder generateHttpRequestBuilder()
	{
		UriBuilder uriBuilder = UriBuilder.fromPath(genericWsProps.getProperty("dc.baseUrl")).path(genericWsProps.getProperty("dc.wsPath"));
		uriBuilder = addQueryParams(uriBuilder);
		LOGGER.debug("Generated URI {}", uriBuilder.build().toString());
		Builder reqBuilder = client.resource(uriBuilder.build()).accept(MediaType.APPLICATION_XML_TYPE);
		reqBuilder = addHttpHeaders(reqBuilder);
		return reqBuilder;
	}

	protected UriBuilder addQueryParams(UriBuilder uriBuilder)
	{
		Set<Entry<String, List<String>>> queryParamsSet = uriInfo.getQueryParameters().entrySet();
		for (Entry<String, List<String>> queryParam : queryParamsSet)
			for (String queryParamValue : queryParam.getValue())
				uriBuilder = uriBuilder.queryParam(queryParam.getKey(), queryParamValue);
		
		return uriBuilder;
	}

	protected Builder generateAddLinkBuilder(String pid)
	{
		Builder reqBuilder = client
				.resource(UriBuilder.fromPath(genericWsProps.getProperty("dc.baseUrl")).path(genericWsProps.getProperty("dc.addLinkPath")).path(pid).build())
				.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).accept(MediaType.TEXT_PLAIN_TYPE);
		reqBuilder = addHttpHeaders(reqBuilder);
		return reqBuilder;
	}

	protected ClientResponse authenticateCredentials() throws UnauthorisedException
	{
		ClientResponse respUserInfo = generateUserInfoBuilder().get(ClientResponse.class);
		if (respUserInfo.getClientResponseStatus() == Status.UNAUTHORIZED)
			throw new UnauthorisedException("Invalid username and/or password.");
		return respUserInfo;
	}
	
	protected Builder generateUserInfoBuilder()
	{
		UriBuilder authUserUriBuilder = UriBuilder.fromPath(genericWsProps.getProperty("dc.baseUrl")).path(genericWsProps.getProperty("dc.userInfo"));
		authUserUriBuilder = addQueryParams(authUserUriBuilder);
		Builder reqBuilder = client
				.resource(authUserUriBuilder.build())
				.type(MediaType.TEXT_PLAIN_TYPE).accept(MediaType.TEXT_PLAIN_TYPE);
		reqBuilder = addHttpHeaders(reqBuilder);
		return reqBuilder;
	}

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
	
	@POST
	@Produces(MediaType.APPLICATION_XML)
	abstract public Response doPostRequest(Document xmlDoc);
}
