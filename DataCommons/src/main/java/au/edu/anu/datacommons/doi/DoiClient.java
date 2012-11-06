package au.edu.anu.datacommons.doi;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Properties;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.datacite.schema.kernel_2.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.config.Config;
import au.edu.anu.datacommons.config.PropertiesFile;
import au.edu.anu.datacommons.doi.logging.ExtWebResourceLog;
import au.edu.anu.datacommons.doi.logging.ExtWebResourceLogDao;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.LoggingFilter;
import com.sun.jersey.client.apache.ApacheHttpClient;
import com.sun.jersey.client.apache.config.DefaultApacheHttpClientConfig;
import com.sun.jersey.core.util.MultivaluedMapImpl;

public class DoiClient
{
	private static final Logger LOGGER = LoggerFactory.getLogger(DoiClient.class);
	private static Client noProxyClient = null;
	private static Client proxyClient = null;
	private static Client client = null;

	private static JAXBContext resourceContext;
	private static JAXBContext doiResponseContext;

	private Marshaller resourceMarshaller;
	private Unmarshaller resourceUnmarshaller;
	private Unmarshaller doiResponseUnmarshaller;

	private DoiConfig doiConfig;

	static
	{
		try
		{
			resourceContext = JAXBContext.newInstance(Resource.class);
			doiResponseContext = JAXBContext.newInstance(DoiResponse.class);
		}
		catch (JAXBException e)
		{
			// Exception thrown only when class DoiResponse isn't properly coded. NPE expected in that case.
			LOGGER.warn(e.getMessage(), e);
		}
	}

	private ResponseFormat respFmt = ResponseFormat.XML;
	private DoiResponse doiResponse = null;
	private String doiResponseAsString = null;

	public enum ResponseFormat
	{
		XML, JSON, STRING;

		@Override
		public String toString()
		{
			return super.toString().toLowerCase();
		}
	}

	public DoiClient()
	{
		Properties doiProps;
		try
		{
			doiProps = new PropertiesFile(new File(Config.DIR, "datacommons/doi.properties"));
		}
		catch (IOException e)
		{
			throw new RuntimeException("doi.properties not found or unreadable.");
		}

		this.doiConfig = new DoiConfigImpl(doiProps);
		setupClients(this.doiConfig);
		setupMarshallers();
	}

	public DoiClient(DoiConfig doiConfig)
	{
		this.doiConfig = doiConfig;
		setupClients(this.doiConfig);
		setupMarshallers();
	}

	public ResponseFormat getRespFmt()
	{
		return respFmt;
	}

	public void setRespFmt(ResponseFormat respFmt)
	{
		this.respFmt = respFmt;
	}

	public DoiResponse getDoiResponse()
	{
		return doiResponse;
	}

	public String getDoiResponseAsString()
	{
		return doiResponseAsString;
	}

	public void mint(String pid, Resource metadata) throws DoiException
	{
		if (pid == null || pid.trim().length() <= 0)
			throw new DoiException("URL not specified.");
		if (metadata == null)
			throw new DoiException("Metadata for DOI not provided.");

		ExtWebResourceLogDao logDao = null;
		try
		{
			String url = generateLandingUri(pid).toString();
			String xml = getMetadataAsStr(metadata);

			LOGGER.trace("Minting url={}, xml={}.", new Object[] { url, xml });

			// Build URI.
			UriBuilder doiUriBuilder = UriBuilder.fromUri(doiConfig.getBaseUri()).path("mint." + this.respFmt.toString() + "/");
			doiUriBuilder = appendAppId(doiUriBuilder);
			doiUriBuilder = appendDebug(doiUriBuilder);
			doiUriBuilder = doiUriBuilder.queryParam("url", URLEncoder.encode(url, "UTF-8"));
			URI doiUri = doiUriBuilder.build();

			LOGGER.debug("Minting DOI using {}", doiUri.toString());
			WebResource mintDoiResource = client.resource(doiUri);
			MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
			formData.add("xml", xml);

			logDao = new ExtWebResourceLogDao();
			ExtWebResourceLog extResLog = null;
			try
			{
				extResLog = generateExtWebResourceLog(doiUri, pid, xml);
				logDao.create(extResLog);
			}
			catch (Exception e)
			{
				LOGGER.warn("Unable to create log record to external service.");
			}
			
			ClientResponse resp = mintDoiResource.accept(getMediaTypeForResp()).type(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
					.post(ClientResponse.class, formData);
			processResponse(resp);
			
			try
			{
				updateExtWebResourceLog(extResLog, this.doiResponseAsString);
				logDao.update(extResLog);
			}
			catch (Exception e)
			{
				LOGGER.warn("Unable to update log record to external service.");
			}
			
			if (!doiResponse.getType().equalsIgnoreCase("success"))
				throw new DoiException("DOI Service request failed. Server response: " + doiResponseAsString);
		}
		catch (DoiException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new DoiException("Unable to mint DOI", e);
		}
		finally
		{
			if (logDao != null)
				logDao.close();
		}
	}

	public void update(String doi, String pid, Resource metadata) throws DoiException
	{
		ExtWebResourceLogDao logDao = null;
		try
		{
			String url = generateLandingUri(pid).toString();
			String xml = getMetadataAsStr(metadata);
			LOGGER.trace("Updating doi={}, url={}, xml={}.", new Object[] { doi, url, xml });

			// Build URI.
			UriBuilder doiUriBuilder = UriBuilder.fromUri(doiConfig.getBaseUri()).path("update." + this.respFmt.toString() + "/");
			doiUriBuilder = appendAppId(doiUriBuilder);
			doiUriBuilder = appendDoi(doiUriBuilder, doi);
			doiUriBuilder = appendDebug(doiUriBuilder);
			if (url != null && url.trim().length() > 0)
				doiUriBuilder = doiUriBuilder.queryParam("url", URLEncoder.encode(url, "UTF-8"));
			URI doiUri = doiUriBuilder.build();

			LOGGER.debug("Updating DOI using {}", doiUri.toString());
			WebResource updateDoiResource = client.resource(doiUri);
			ClientResponse resp;
			
			logDao = new ExtWebResourceLogDao();
			ExtWebResourceLog extResLog = null;
			try
			{
				extResLog = generateExtWebResourceLog(doiUri, pid, xml);
				logDao.create(extResLog);
			}
			catch (Exception e)
			{
				LOGGER.warn("Unable to create log record to external service.");
			}

			
			if (xml != null && xml.trim().length() > 0)
			{
				MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
				formData.add("xml", xml);
				resp = updateDoiResource.accept(getMediaTypeForResp()).type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).post(ClientResponse.class, formData);
			}
			else
				resp = updateDoiResource.accept(getMediaTypeForResp()).get(ClientResponse.class);
			processResponse(resp);
			if (!doiResponse.getType().equalsIgnoreCase("success"))
				throw new DoiException("DOI Service request failed. Server response: " + doiResponseAsString);
		}
		catch (Exception e)
		{
			throw new DoiException("Unable to update DOI", e);
		}
		finally
		{
			if (logDao != null)
				logDao.close();
		}
	}

	public void deactivate(String doi) throws DoiException
	{
		ExtWebResourceLogDao logDao = null;
		try
		{
			LOGGER.trace("Deactivating doi={}", doi);

			// Build URI.
			UriBuilder doiUriBuilder = UriBuilder.fromUri(doiConfig.getBaseUri()).path("deactivate." + this.respFmt.toString() + "/");
			doiUriBuilder = appendAppId(doiUriBuilder);
			doiUriBuilder = appendDoi(doiUriBuilder, doi);
			doiUriBuilder = appendDebug(doiUriBuilder);
			URI doiUri = doiUriBuilder.build();

			logDao = new ExtWebResourceLogDao();
			ExtWebResourceLog extResLog = null;
			try
			{
				extResLog = generateExtWebResourceLog(doiUri);
				logDao.create(extResLog);
			}
			catch (Exception e)
			{
				LOGGER.warn("Unable to create log record to external service.");
			}
			
			LOGGER.debug("Deactivating DOI using {}", doiUri.toString());
			WebResource deactivateDoiResouce = client.resource(doiUri);
			ClientResponse resp = deactivateDoiResouce.accept(getMediaTypeForResp()).get(ClientResponse.class);
			processResponse(resp);
		}
		catch (Exception e)
		{
			throw new DoiException("Unable to deactivate DOI", e);
		}
		finally
		{
			if (logDao != null)
				logDao.close();
		}
	}

	public void activate(String doi) throws DoiException
	{
		ExtWebResourceLogDao logDao = null;
		try
		{
			LOGGER.trace("Activating doi={}", doi);

			// Build URI.
			UriBuilder doiUriBuilder = UriBuilder.fromUri(doiConfig.getBaseUri()).path("activate." + this.respFmt.toString() + "/");
			doiUriBuilder = appendAppId(doiUriBuilder);
			doiUriBuilder = appendDoi(doiUriBuilder, doi);
			doiUriBuilder = appendDebug(doiUriBuilder);
			URI doiUri = doiUriBuilder.build();

			ExtWebResourceLog extResLog = null;
			logDao = new ExtWebResourceLogDao();
			try
			{
				extResLog = generateExtWebResourceLog(doiUri);
				logDao.create(extResLog);
			}
			catch (Exception e)
			{
				LOGGER.warn("Unable to create log record to external service.");
			}

			LOGGER.debug("Activating DOI using {}", doiUri.toString());
			WebResource activateDoiResource = client.resource(doiUri);
			ClientResponse resp = activateDoiResource.accept(getMediaTypeForResp()).get(ClientResponse.class);
			processResponse(resp);
		}
		catch (Exception e)
		{
			throw new DoiException("Unable to activate DOI", e);
		}
		finally
		{
			if (logDao != null)
				logDao.close();
		}
	}

	public Resource getMetadata(String doi) throws DoiException
	{
		Resource res;
		String respStr;

		ExtWebResourceLogDao logDao = null;
		try
		{
			LOGGER.trace("Getting metadata for doi={}", doi);

			// Build URI.
			UriBuilder doiUriBuilder = UriBuilder.fromUri(doiConfig.getBaseUri()).path("xml." + this.respFmt.toString() + "/");
			doiUriBuilder = appendDoi(doiUriBuilder, doi);
			doiUriBuilder = appendDebug(doiUriBuilder);
			URI doiUri = doiUriBuilder.build();

			ExtWebResourceLog extResLog = null;
			logDao = new ExtWebResourceLogDao();
			try
			{
				extResLog = generateExtWebResourceLog(doiUri);
				logDao.create(extResLog);
			}
			catch (Exception e)
			{
				LOGGER.warn("Unable to create log record to external service.");
			}

			LOGGER.debug("Getting DOI Metadata using {}", doiUri.toString());
			WebResource getDoiMetadataResource = client.resource(doiUri);
			ClientResponse resp = getDoiMetadataResource.accept(getMediaTypeForResp()).get(ClientResponse.class);
			respStr = resp.getEntity(String.class);
			LOGGER.trace("Response from server: {}", respStr);
			res = (Resource) resourceUnmarshaller.unmarshal(new StringReader(respStr));
		}
		catch (Exception e)
		{
			throw new DoiException("Unable to retrieve DOI metadata.", e);
		}
		finally
		{
			if (logDao != null)
				logDao.close();
		}

		return res;
	}

	private UriBuilder appendAppId(UriBuilder ub)
	{
		// App Id is alphanumeric so no need to URLEncode.
		return ub.queryParam("app_id", (doiConfig.useTestPrefix() ? "TEST" : "") + doiConfig.getAppId());
	}

	private UriBuilder appendDoi(UriBuilder ub, String doi)
	{
		String encodedDoi;
		try
		{
			encodedDoi = URLEncoder.encode(doi, "UTF-8");
			return ub.queryParam("doi", encodedDoi);
		}
		catch (UnsupportedEncodingException e)
		{
			// This exception should never be thrown if the charset is a valid one.
			LOGGER.error(e.getMessage(), e);
			return null;
		}
	}

	private UriBuilder appendDebug(UriBuilder ub)
	{
		if (doiConfig.isDebug())
			return ub.queryParam("debug", true);
		else
			return ub;
	}

	private MediaType getMediaTypeForResp()
	{
		if (this.respFmt == ResponseFormat.XML)
			return MediaType.APPLICATION_XML_TYPE;
		else if (this.respFmt == ResponseFormat.JSON)
			return MediaType.APPLICATION_JSON_TYPE;
		else if (this.respFmt == ResponseFormat.STRING)
			return MediaType.TEXT_HTML_TYPE;
		else
			throw new IllegalArgumentException("Invalid response type.");
	}

	private void processResponse(ClientResponse resp)
	{
		this.doiResponseAsString = resp.getEntity(String.class);
		LOGGER.trace("Server response: {}", doiResponseAsString);
		if (this.getRespFmt() == ResponseFormat.XML)
		{
			try
			{
				this.doiResponse = (DoiResponse) doiResponseUnmarshaller.unmarshal(new StreamSource(new StringReader(this.doiResponseAsString
						.substring(this.doiResponseAsString.indexOf("<?xml")))));
			}
			catch (StringIndexOutOfBoundsException e)
			{
				this.doiResponse = null;
			}
			catch (JAXBException e)
			{
				LOGGER.warn(e.getMessage(), e);
				this.doiResponse = null;
			}
		}
		LOGGER.debug("Response from server: ({}) {}", resp.getStatus(), this.doiResponseAsString);
	}

	private String getMetadataAsStr(Resource metadata) throws JAXBException
	{
		StringWriter xmlSw = new StringWriter();
		resourceMarshaller.marshal(metadata, xmlSw);
		return xmlSw.toString();
	}

	private URI generateLandingUri(String pid)
	{
		return doiConfig.getLandingUri().build(pid);
	}
	
	private void setupMarshallers()
	{
		try
		{
			resourceMarshaller = resourceContext.createMarshaller();
			resourceMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			resourceMarshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION,
					"http://datacite.org/schema/kernel-2.2 http://schema.datacite.org/meta/kernel-2.2/metadata.xsd");
		}
		catch (JAXBException e)
		{
			LOGGER.error(e.getMessage(), e);
			resourceMarshaller = null;
		}
		
		try
		{
			resourceUnmarshaller = resourceContext.createUnmarshaller();
		}
		catch (JAXBException e)
		{
			LOGGER.error(e.getMessage(), e);
			resourceUnmarshaller = null;
		}

		try
		{
			doiResponseUnmarshaller = doiResponseContext.createUnmarshaller();
		}
		catch (JAXBException e)
		{
			LOGGER.error(e.getMessage(), e);
			doiResponseUnmarshaller = null;
		}
	}

	private void updateExtWebResourceLog(ExtWebResourceLog extResLog, String respAsStr)
	{
		extResLog.addResponse(respAsStr);
	}

	private ExtWebResourceLog generateExtWebResourceLog(URI doiUri, String pid, String xml)
	{
		StringBuilder reqStr = new StringBuilder();
		reqStr.append(doiUri.toString());
		reqStr.append(Config.NEWLINE);
		reqStr.append(Config.NEWLINE);
		if (xml != null)
			reqStr.append(xml);
		ExtWebResourceLog extResLog = new ExtWebResourceLog(pid, reqStr.toString());
		return extResLog;
	}

	private ExtWebResourceLog generateExtWebResourceLog(URI doiUri)
	{
		return generateExtWebResourceLog(doiUri, null, null);
	}

	private static void setupClients(DoiConfig doiConfig)
	{
		if (noProxyClient == null)
		{
			noProxyClient = Client.create();
			noProxyClient.addFilter(new LoggingFilter());
		}

		if (proxyClient == null)
		{
			final String proxyHost = doiConfig.getProxyServer();
			final String proxyPort = doiConfig.getProxyPort();

			final DefaultApacheHttpClientConfig config = new DefaultApacheHttpClientConfig();
			if (proxyHost != null && proxyHost.length() > 0 && proxyPort != null && proxyPort.length() > 0)
			{
				config.getProperties().put(DefaultApacheHttpClientConfig.PROPERTY_PROXY_URI, "http://" + proxyHost + ":" + proxyPort);
			}

			proxyClient = ApacheHttpClient.create(config);
			proxyClient.addFilter(new LoggingFilter());
		}

		if (doiConfig.useProxy())
			client = proxyClient;
		else
			client = noProxyClient;
	}
}
