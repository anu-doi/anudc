package au.edu.anu.datacommons.gateway;

import static java.text.MessageFormat.format;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import au.edu.anu.datacommons.config.Config;
import au.edu.anu.datacommons.config.PropertiesFile;
import au.edu.anu.datacommons.db.DaoException;
import au.edu.anu.datacommons.gateway.logging.LogDao;
import au.edu.anu.datacommons.gateway.logging.WebSvcLog;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;
import com.sun.jersey.api.client.filter.ClientFilter;
import com.sun.jersey.api.client.filter.LoggingFilter;

@Path("/")
public class GatewayResource
{
	private static final Logger LOGGER = LoggerFactory.getLogger(GatewayResource.class);
	private static final Client client = Client.create();
	private static final ClientFilter loggingFilter = new LoggingFilter();

	private static Properties redirProps;

	static
	{
		try
		{
			redirProps = new PropertiesFile(new File(Config.DIR, "ws-gateway/redir.properties"));
		}
		catch (IOException e)
		{
			LOGGER.error(e.getMessage(), e);
		}
	}

	@Context
	private UriInfo uriInfo;
	@Context
	private HttpServletRequest request;
	@Context
	private HttpHeaders httpHeaders;

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public Response doGetRequest()
	{
		updateLogging();

		LogDao logDao = null;
		Response resp = null;
		try
		{
			logDao = new LogDao();
			WebSvcLog reqLog = generateWebSvcLog(request, httpHeaders, uriInfo);
			try
			{
				logDao.create(reqLog);
			}
			catch (DaoException e)
			{
				LOGGER.warn("Unable to create a log entry for GET request.", e);
			}
			resp = Response.ok("Test", MediaType.TEXT_PLAIN_TYPE).build();

			updateLog(reqLog, resp);
			try
			{
				logDao.update(reqLog);
			}
			catch (DaoException e)
			{
				LOGGER.warn("Unable to update log entry for GET request.", e);
			}
		}
		finally
		{
			if (logDao != null)
				logDao.close();
		}

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
		updateLogging();

		Response resp = null;
		String function = xmlDoc.getDocumentElement().getAttribute("function");
		WebResource redirRes = client.resource(UriBuilder.fromPath(redirProps.getProperty(function)).build());

		LogDao logDao = null;
		try
		{
			logDao = new LogDao();
			WebSvcLog reqLog = generateWebSvcLog(request, httpHeaders, uriInfo, function, xmlDoc);
			try
			{
				logDao.create(reqLog);
			}
			catch (DaoException e)
			{
				LOGGER.warn("Unable to create a log entry for POST request.", e);
			}

			// Add HTTP headers to the generic service resource object.
			Builder reqBuilder = redirRes.accept(MediaType.APPLICATION_XML_TYPE);
			boolean userAgentExists = false;
			MultivaluedMap<String, String> headersMap = httpHeaders.getRequestHeaders();
			for (String key : headersMap.keySet())
			{
				for (String value : headersMap.get(key))
				{
					if (!key.equalsIgnoreCase("user-agent"))
						reqBuilder = reqBuilder.header(key, value);
					else
					{
						// Append 'DataCommons' in the user agent so the service can recognize that it's not a request from a web browser.
						reqBuilder = reqBuilder.header(key, value + " DataCommons");
						userAgentExists = true;
					}
				}
			}

			// If header doesn't have a user agent key at all, add it.
			if (!userAgentExists)
				reqBuilder = reqBuilder.header("user-agent", "DataCommons");

			ClientResponse respFromRedirRes = reqBuilder.post(ClientResponse.class, xmlDoc);

			// Generate an outbound response object from the inbound response object received from the redirected URL.
			String respBodyFromRedirRes = respFromRedirRes.getEntity(String.class);
			resp = Response.status(respFromRedirRes.getStatus()).type(respFromRedirRes.getType()).entity(respBodyFromRedirRes).build();

			updateLog(reqLog, resp);
			try
			{
				logDao.update(reqLog);
			}
			catch (DaoException e)
			{
				LOGGER.warn("Unable to update log entry for POST request.", e);
			}
		}
		finally
		{
			if (logDao != null)
				logDao.close();
		}

		return resp;
	}

	private void updateLogging()
	{
		if (redirProps.getProperty("http.logging", "false").equalsIgnoreCase("true"))
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

	private WebSvcLog generateWebSvcLog(HttpServletRequest request, HttpHeaders httpHeaders, UriInfo uri)
	{
		StringBuilder requestStr = new StringBuilder();
		requestStr.append(format("{0} {1}", request.getMethod().toUpperCase(), uri.getRequestUri().toString()));
		requestStr.append(Config.NEWLINE);
		for (Entry<String, List<String>> iHeader : httpHeaders.getRequestHeaders().entrySet())
		{
			for (String value : iHeader.getValue())
				requestStr.append(format("{0}: {1}", iHeader.getKey(), iHeader.getKey().equalsIgnoreCase("authorization") ? "" : value));
			requestStr.append(Config.NEWLINE);
		}

		WebSvcLog webSvcLog = new WebSvcLog(requestStr.toString(), request.getRemoteAddr(), null);
		return webSvcLog;
	}

	private WebSvcLog generateWebSvcLog(HttpServletRequest request, HttpHeaders httpHeaders, UriInfo uri, String function, Document xmlDoc)
	{
		WebSvcLog webSvcLog = generateWebSvcLog(request, httpHeaders, uri);
		StringBuilder requestStr = new StringBuilder(webSvcLog.getRequest());
		requestStr.append(Config.NEWLINE);
		requestStr.append(getXmlAsString(xmlDoc));
		webSvcLog.setRequest(requestStr.toString());
		webSvcLog.setFunction(function);
		return webSvcLog;
	}

	private void updateLog(WebSvcLog reqLog, Response resp)
	{
		StringBuilder respStr = new StringBuilder();
		respStr.append(resp.getStatus());
		respStr.append(Config.NEWLINE);
		for (Entry<String, List<Object>> iMd : resp.getMetadata().entrySet())
		{
			for (Object val : iMd.getValue())
				respStr.append(format("{0}: {1}", iMd.getKey(), val.toString()));
		}
		if (resp.getEntity() != null)
		{
			respStr.append(Config.NEWLINE);
			respStr.append(Config.NEWLINE);
			respStr.append(resp.getEntity().toString());
		}
		reqLog.addResponse(respStr.toString());
	}

	public void writeXmlToWriter(Document inDoc, Writer xmlWriter) throws TransformerFactoryConfigurationError, TransformerException
	{
		Transformer transformer;
		transformer = TransformerFactory.newInstance().newTransformer();
		DOMSource source = new DOMSource(inDoc);

		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		transformer.transform(source, new StreamResult(xmlWriter));

		return;
	}

	public String getXmlAsString(Document inDoc)
	{
		StringWriter stringWriter = new StringWriter();
		try
		{
			writeXmlToWriter(inDoc, stringWriter);
		}
		catch (Exception e)
		{
			LOGGER.warn("Unable to convert XML to String.", e);
		}

		return stringWriter.toString();
	}
}
