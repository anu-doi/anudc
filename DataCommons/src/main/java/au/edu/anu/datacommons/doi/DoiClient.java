/*******************************************************************************
 * Australian National University Data Commons
 * Copyright (C) 2013  The Australian National University
 * 
 * This file is part of Australian National University Data Commons.
 * 
 * Australian National University Data Commons is free software: you
 * can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package au.edu.anu.datacommons.doi;

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

/**
 * A Client that sends Digital Object Identifier (DOI) requests to a relevant service. Refer to <a
 * href="http://ands.org.au/resource/r9-cite-my-data-v1.1-tech-doco.pdf">Cite My Data M2M Service</a>
 * 
 * @author Rahul Khanna
 * 
 */
public class DoiClient {
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

	static {
		try {
			resourceContext = JAXBContext.newInstance(Resource.class);
			doiResponseContext = JAXBContext.newInstance(DoiResponse.class);
		} catch (JAXBException e) {
			// Exception thrown only when class DoiResponse isn't properly coded. NPE expected in that case.
			LOGGER.warn(e.getMessage(), e);
		}
	}

	private ResponseFormat respFmt = ResponseFormat.XML;
	private DoiResponse doiResponse = null;
	private String doiResponseAsString = null;

	public enum ResponseFormat {
		XML, JSON, STRING;

		@Override
		public String toString() {
			return super.toString().toLowerCase();
		}
	}

	/**
	 * Constructor for DoiClient. A DoiConfig object is created from the doi.properties file in the default conf
	 * location.
	 */
	public DoiClient() {
		Properties doiProps;
		try {
			doiProps = new PropertiesFile(new File(Config.DIR, "datacommons/doi.properties"));
		} catch (IOException e) {
			throw new RuntimeException("doi.properties not found or unreadable.");
		}

		this.doiConfig = new DoiConfigImpl(doiProps);
		setupClients(this.doiConfig);
		setupMarshallers();
	}

	/**
	 * Constructor for when a DoiConfig object is provided
	 * 
	 * @param doiConfig
	 *            DoiConfig object
	 */
	public DoiClient(DoiConfig doiConfig) {
		this.doiConfig = doiConfig;
		setupClients(this.doiConfig);
		setupMarshallers();
	}

	public ResponseFormat getRespFmt() {
		return respFmt;
	}

	public void setRespFmt(ResponseFormat respFmt) {
		this.respFmt = respFmt;
	}

	public DoiResponse getDoiResponse() {
		return doiResponse;
	}

	public String getDoiResponseAsString() {
		return doiResponseAsString;
	}

	/**
	 * Sends a request to mint a DOI. Refer to section 3.9 of Cite My Data Technical Documentation. URL request is in
	 * format: <code>https://services.ands.org.au/doi/1.1/mint.{response_type}/?app_id={app_id}&url={url}</code>
	 * 
	 * @param pid
	 *            Pid of the record for which DOI is to be minted.
	 * @param metadata
	 *            DataCite Resource object containing metadata about the record.
	 * @throws DoiException
	 *             If unable to mint a DOI.
	 */
	public void mint(String pid, Resource metadata) throws DoiException {
		if (pid == null || pid.trim().length() <= 0)
			throw new DoiException("URL not specified.");
		if (metadata == null)
			throw new DoiException("Metadata for DOI not provided.");

		ExtWebResourceLogDao logDao = null;
		try {
			String url = generateLandingUri(pid).toString();
			String xml = getMetadataAsStr(metadata);

			LOGGER.trace("Minting url={}, xml={}.", new Object[] { url, xml });

			// Build URI.
			UriBuilder doiUriBuilder = UriBuilder.fromUri(doiConfig.getBaseUri()).path(
					"mint." + this.respFmt.toString() + "/");
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
			try {
				extResLog = generateExtWebResourceLog(doiUri, pid, xml);
				logDao.create(extResLog);
			} catch (Exception e) {
				LOGGER.warn("Unable to create log record to external service.");
			}

			ClientResponse resp = mintDoiResource.accept(getMediaTypeForResp())
					.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).post(ClientResponse.class, formData);
			processResponse(resp);

			try {
				updateExtWebResourceLog(extResLog, this.doiResponseAsString);
				logDao.update(extResLog);
			} catch (Exception e) {
				LOGGER.warn("Unable to update log record to external service.");
			}

			if (!doiResponse.getType().equalsIgnoreCase("success"))
				throw new DoiException("DOI Service request failed. Server response: " + doiResponseAsString);
		} catch (DoiException e) {
			throw e;
		} catch (Exception e) {
			throw new DoiException("Unable to mint DOI", e);
		} finally {
			if (logDao != null)
				logDao.close();
		}
	}

	/**
	 * Updates the metadata associated with a DOI. Refer to section 3.9 of Cite My Data Technical Documentation. URL is
	 * in format <code>https://services.ands.org.au/doi/1.1/mint.{response_type}/?app_id={app_id}&url={url}</code>
	 * 
	 * @param doi
	 *            DOI whose metadata to be updated
	 * @param pid
	 *            Pid of the record to which the DOI belongs
	 * @param metadata
	 *            Updated metadata as DataCite Resource object
	 * @throws DoiException
	 *             If unable to update DOI
	 */
	public void update(String doi, String pid, Resource metadata) throws DoiException {
		ExtWebResourceLogDao logDao = null;
		try {
			String url = generateLandingUri(pid).toString();
			String xml = getMetadataAsStr(metadata);
			LOGGER.trace("Updating doi={}, url={}, xml={}.", new Object[] { doi, url, xml });

			// Build URI.
			UriBuilder doiUriBuilder = UriBuilder.fromUri(doiConfig.getBaseUri()).path(
					"update." + this.respFmt.toString() + "/");
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
			try {
				extResLog = generateExtWebResourceLog(doiUri, pid, xml);
				logDao.create(extResLog);
			} catch (Exception e) {
				LOGGER.warn("Unable to create log record to external service.");
			}

			if (xml != null && xml.trim().length() > 0) {
				MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
				formData.add("xml", xml);
				resp = updateDoiResource.accept(getMediaTypeForResp()).type(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
						.post(ClientResponse.class, formData);
			} else
				resp = updateDoiResource.accept(getMediaTypeForResp()).get(ClientResponse.class);
			processResponse(resp);
			if (!doiResponse.getType().equalsIgnoreCase("success"))
				throw new DoiException("DOI Service request failed. Server response: " + doiResponseAsString);
		} catch (Exception e) {
			throw new DoiException("Unable to update DOI", e);
		} finally {
			if (logDao != null)
				logDao.close();
		}
	}

	/**
	 * Deactivates a DOI. Refer to section 3.9 of Cite My Data Technical Documentation. URL is in format
	 * <code>https://services.ands.org.au/doi/1.1/mint.{response_type}/?app_id={app_id}&url={url}</code>
	 * 
	 * @param doi
	 *            DOI to deactivate.
	 * 
	 * @throws DoiException
	 *             If unable to deactivate DOI
	 */
	public void deactivate(String doi) throws DoiException {
		ExtWebResourceLogDao logDao = null;
		try {
			LOGGER.trace("Deactivating doi={}", doi);

			// Build URI.
			UriBuilder doiUriBuilder = UriBuilder.fromUri(doiConfig.getBaseUri()).path(
					"deactivate." + this.respFmt.toString() + "/");
			doiUriBuilder = appendAppId(doiUriBuilder);
			doiUriBuilder = appendDoi(doiUriBuilder, doi);
			doiUriBuilder = appendDebug(doiUriBuilder);
			URI doiUri = doiUriBuilder.build();

			logDao = new ExtWebResourceLogDao();
			ExtWebResourceLog extResLog = null;
			try {
				extResLog = generateExtWebResourceLog(doiUri);
				logDao.create(extResLog);
			} catch (Exception e) {
				LOGGER.warn("Unable to create log record to external service.");
			}

			LOGGER.debug("Deactivating DOI using {}", doiUri.toString());
			WebResource deactivateDoiResouce = client.resource(doiUri);
			ClientResponse resp = deactivateDoiResouce.accept(getMediaTypeForResp()).get(ClientResponse.class);
			processResponse(resp);
		} catch (Exception e) {
			throw new DoiException("Unable to deactivate DOI", e);
		} finally {
			if (logDao != null)
				logDao.close();
		}
	}

	/**
	 * Activates a deactivated DOI. Refer to section 3.9 of Cite My Data Technical Documentation. URL is in format
	 * <code>https://services.ands.org.au/doi/1.1/deactivate.{response_type}/?app_id={app_id}&doi={doi}</code>
	 * 
	 * @param doi
	 *            DOI to activate
	 * @throws DoiException
	 *             If unable to activate a DOI
	 */
	public void activate(String doi) throws DoiException {
		ExtWebResourceLogDao logDao = null;
		try {
			LOGGER.trace("Activating doi={}", doi);

			// Build URI.
			UriBuilder doiUriBuilder = UriBuilder.fromUri(doiConfig.getBaseUri()).path(
					"activate." + this.respFmt.toString() + "/");
			doiUriBuilder = appendAppId(doiUriBuilder);
			doiUriBuilder = appendDoi(doiUriBuilder, doi);
			doiUriBuilder = appendDebug(doiUriBuilder);
			URI doiUri = doiUriBuilder.build();

			ExtWebResourceLog extResLog = null;
			logDao = new ExtWebResourceLogDao();
			try {
				extResLog = generateExtWebResourceLog(doiUri);
				logDao.create(extResLog);
			} catch (Exception e) {
				LOGGER.warn("Unable to create log record to external service.");
			}

			LOGGER.debug("Activating DOI using {}", doiUri.toString());
			WebResource activateDoiResource = client.resource(doiUri);
			ClientResponse resp = activateDoiResource.accept(getMediaTypeForResp()).get(ClientResponse.class);
			processResponse(resp);
		} catch (Exception e) {
			throw new DoiException("Unable to activate DOI", e);
		} finally {
			if (logDao != null)
				logDao.close();
		}
	}

	/**
	 * Get the metadata associated with a DOI.
	 * 
	 * @param doi
	 *            DOI whose metadata to receive.
	 * @return Metadata as a DataCite Resource Object
	 * @throws DoiException
	 *             If unable to retrieve DOI metadata.
	 */
	public Resource getMetadata(String doi) throws DoiException {
		Resource res;
		String respStr;

		ExtWebResourceLogDao logDao = null;
		try {
			LOGGER.trace("Getting metadata for doi={}", doi);

			// Build URI.
			UriBuilder doiUriBuilder = UriBuilder.fromUri(doiConfig.getBaseUri()).path(
					"xml." + this.respFmt.toString() + "/");
			doiUriBuilder = appendDoi(doiUriBuilder, doi);
			doiUriBuilder = appendDebug(doiUriBuilder);
			URI doiUri = doiUriBuilder.build();

			ExtWebResourceLog extResLog = null;
			logDao = new ExtWebResourceLogDao();
			try {
				extResLog = generateExtWebResourceLog(doiUri);
				logDao.create(extResLog);
			} catch (Exception e) {
				LOGGER.warn("Unable to create log record to external service.");
			}

			LOGGER.debug("Getting DOI Metadata using {}", doiUri.toString());
			WebResource getDoiMetadataResource = client.resource(doiUri);
			ClientResponse resp = getDoiMetadataResource.accept(getMediaTypeForResp()).get(ClientResponse.class);
			respStr = resp.getEntity(String.class);
			LOGGER.trace("Response from server: {}", respStr);
			res = (Resource) resourceUnmarshaller.unmarshal(new StringReader(respStr));
		} catch (Exception e) {
			throw new DoiException("Unable to retrieve DOI metadata.", e);
		} finally {
			if (logDao != null)
				logDao.close();
		}

		return res;
	}

	/**
	 * Appends the app ID to a URIBuilder object as query parameter. App ID is required to make a DOI change request.
	 * 
	 * @param ub
	 *            UriBuilder to which app ID will be appended.
	 * 
	 * @return UriBuilder with app ID appended as a query parameter.
	 */
	private UriBuilder appendAppId(UriBuilder ub) {
		// App Id is alphanumeric so no need to URLEncode.
		return ub.queryParam("app_id", (doiConfig.useTestPrefix() ? "TEST" : "") + doiConfig.getAppId());
	}

	/**
	 * Appends a DOI to a URIBuilder object as a query parameter.
	 * 
	 * @param ub
	 *            UriBuilder object to which DOI is to be appended.
	 * @param doi
	 *            DOI to be appended.
	 * @return UriBuilder
	 */
	private UriBuilder appendDoi(UriBuilder ub, String doi) {
		String encodedDoi;
		try {
			encodedDoi = URLEncoder.encode(doi, "UTF-8");
			return ub.queryParam("doi", encodedDoi);
		} catch (UnsupportedEncodingException e) {
			// This exception should never be thrown if the charset is a valid one.
			LOGGER.error(e.getMessage(), e);
			return null;
		}
	}

	/**
	 * Appends debug query parameter to a URIBuilder.
	 * 
	 * @param ub
	 *            UriBuilder to which debug query parameter will be appended.
	 * 
	 * @return UriBuilder with appended query parameter.
	 */
	private UriBuilder appendDebug(UriBuilder ub) {
		if (doiConfig.isDebug())
			return ub.queryParam("debug", true);
		else
			return ub;
	}

	private MediaType getMediaTypeForResp() {
		if (this.respFmt == ResponseFormat.XML)
			return MediaType.APPLICATION_XML_TYPE;
		else if (this.respFmt == ResponseFormat.JSON)
			return MediaType.APPLICATION_JSON_TYPE;
		else if (this.respFmt == ResponseFormat.STRING)
			return MediaType.TEXT_HTML_TYPE;
		else
			throw new IllegalArgumentException("Invalid response type.");
	}

	/**
	 * Processes the response received from the DOI service.
	 * 
	 * @param resp
	 *            Response received from DOI service as ClientResponse.
	 */
	private void processResponse(ClientResponse resp) {
		this.doiResponseAsString = resp.getEntity(String.class);
		LOGGER.trace("Server response: {}", doiResponseAsString);
		if (this.getRespFmt() == ResponseFormat.XML) {
			try {
				this.doiResponse = (DoiResponse) doiResponseUnmarshaller.unmarshal(new StreamSource(new StringReader(
						this.doiResponseAsString.substring(this.doiResponseAsString.indexOf("<?xml")))));
			} catch (StringIndexOutOfBoundsException e) {
				this.doiResponse = null;
			} catch (JAXBException e) {
				LOGGER.warn(e.getMessage(), e);
				this.doiResponse = null;
			}
		}
		LOGGER.debug("Response from server: ({}) {}", resp.getStatus(), this.doiResponseAsString);
	}

	/**
	 * Marshals a DataCite Resource object containing metadata about a record into a String.
	 * 
	 * @param metadata
	 *            DataCite Resource object
	 * @return Returns DataCite Resource Object XML as String
	 * 
	 * @throws JAXBException
	 *             If unable to marshall DataCite Resource into XML
	 */
	private String getMetadataAsStr(Resource metadata) throws JAXBException {
		StringWriter xmlSw = new StringWriter();
		resourceMarshaller.marshal(metadata, xmlSw);
		return xmlSw.toString();
	}

	/**
	 * Generates the landing page URI for a record.
	 * 
	 * @param pid
	 *            Pid of the record for which a URI is to be created.
	 * @return URI of the record
	 */
	private URI generateLandingUri(String pid) {
		return doiConfig.getLandingUri().build(pid);
	}

	/**
	 * Sets up the marshallers and unmarshallers required to marshal/unmarshall requests to and responses from the DOI
	 * service.
	 */
	private void setupMarshallers() {
		try {
			resourceMarshaller = resourceContext.createMarshaller();
			resourceMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			resourceMarshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION,
					"http://datacite.org/schema/kernel-2.2 http://schema.datacite.org/meta/kernel-2.2/metadata.xsd");
		} catch (JAXBException e) {
			LOGGER.error(e.getMessage(), e);
			resourceMarshaller = null;
		}

		try {
			resourceUnmarshaller = resourceContext.createUnmarshaller();
		} catch (JAXBException e) {
			LOGGER.error(e.getMessage(), e);
			resourceUnmarshaller = null;
		}

		try {
			doiResponseUnmarshaller = doiResponseContext.createUnmarshaller();
		} catch (JAXBException e) {
			LOGGER.error(e.getMessage(), e);
			doiResponseUnmarshaller = null;
		}
	}

	/**
	 * Adds the response object to the External Resource Log object. The log object would already contain the request
	 * when this method is called.
	 * 
	 * @param extResLog
	 * @param respAsStr
	 */
	private void updateExtWebResourceLog(ExtWebResourceLog extResLog, String respAsStr) {
		extResLog.addResponse(respAsStr);
	}

	private ExtWebResourceLog generateExtWebResourceLog(URI doiUri, String pid, String xml) {
		StringBuilder reqStr = new StringBuilder();
		reqStr.append(doiUri.toString());
		reqStr.append(Config.NEWLINE);
		reqStr.append(Config.NEWLINE);
		if (xml != null)
			reqStr.append(xml);
		ExtWebResourceLog extResLog = new ExtWebResourceLog(pid, reqStr.toString());
		return extResLog;
	}

	private ExtWebResourceLog generateExtWebResourceLog(URI doiUri) {
		return generateExtWebResourceLog(doiUri, null, null);
	}

	/**
	 * Sets up two client objects that will be used for HTTP requests - one without a proxy server specified, and
	 * another without. As DOI requests can be sent from machines with specific IP addresses for testing purposes, the
	 * DOI requests will be routed through an authorised server if initiated from a test machine.
	 * 
	 * @param doiConfig
	 *            Config object containing details of proxy server
	 */
	private static void setupClients(DoiConfig doiConfig) {
		if (noProxyClient == null) {
			noProxyClient = Client.create();
			noProxyClient.addFilter(new LoggingFilter());
		}

		if (proxyClient == null) {
			final String proxyHost = doiConfig.getProxyServer();
			final String proxyPort = doiConfig.getProxyPort();

			final DefaultApacheHttpClientConfig config = new DefaultApacheHttpClientConfig();
			if (proxyHost != null && proxyHost.length() > 0 && proxyPort != null && proxyPort.length() > 0) {
				config.getProperties().put(DefaultApacheHttpClientConfig.PROPERTY_PROXY_URI,
						"http://" + proxyHost + ":" + proxyPort);
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
