package au.edu.anu.dcclient.shibboleth.auth;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.NewCookie;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.opensaml.saml2.ecp.RelayState;
import org.opensaml.saml2.ecp.Response;
import org.opensaml.ws.soap.soap11.Body;
import org.opensaml.ws.soap.soap11.Envelope;
import org.opensaml.ws.soap.soap11.Header;
import org.opensaml.xml.Configuration;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.XMLObjectBuilderFactory;
import org.opensaml.xml.io.Marshaller;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.io.Unmarshaller;
import org.opensaml.xml.io.UnmarshallerFactory;
import org.opensaml.xml.io.UnmarshallingException;
import org.opensaml.xml.parse.BasicParserPool;
import org.opensaml.xml.parse.XMLParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import au.edu.anu.dcclient.Global;
import au.edu.anu.dcclient.shibboleth.ecp.liberty.paos.Request;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.ClientFilter;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

public class ShibbolethAuthenticator {
	static final Logger LOGGER = LoggerFactory.getLogger(ShibbolethAuthenticator.class);
	
	private String username_;
	private String password_;
	private String idp_;
	
	private static Client spClient_;
	private static Client idpClient_;
	
	private static final String paosVersion = "ver=\"urn:liberty:paos:2003-08\"";
	private static final String supportsService = "\"urn:oasis:names:tc:SAML:2.0:profiles:SSO:ecp\"";
	
	public ShibbolethAuthenticator(String username, String password, String idp) {
		this.username_ = username;
		this.password_ = password;
		this.idp_ = idp;
	}
	
	public List<Cookie> authenticate() throws ShibbolethAuthenticationException {
		String loginUrl = Global.getShibbolethLoginUriAsString();
		if (idp_ == null || "".equals(idp_.trim())) {
			throw new ShibbolethAuthenticationException("No Identity Provider selected");
		}
		LOGGER.info("IdP: {}", idp_);
		if (idp_.endsWith("/profile/SAML2/SOAP/ECP")) {
			//TODO figure out how to know what the ECP url is!
			//Do nothing as this is the url we want
		}
		else if (idp_.endsWith("shibboleth")) {
			idp_ = idp_.replace("shibboleth", "profile/SAML2/SOAP/ECP");
		}
		
		AuthenticationInformation spAuthInfo = getServiceProviderInformation(loginUrl);
		AuthenticationInformation idpAuthInfo = requestIdpAuthentication(spAuthInfo, idp_, username_, password_);
		List<NewCookie> sessionCookies = establishSpSession(spAuthInfo, idpAuthInfo, loginUrl);
		List<Cookie> cookies = new ArrayList<Cookie>();
		for (NewCookie cookie : sessionCookies) {
			cookies.add(cookie);
		}
		
		return cookies;
	}
	
	private AuthenticationInformation getServiceProviderInformation(String targetURL) throws ShibbolethAuthenticationException
	{
		LOGGER.info("In getServiceProviderInformation");
		if (spClient_ == null) {
			spClient_ = Client.create();
		}
		
		WebResource resource = spClient_.resource(targetURL);
		ClientResponse clientResponse = resource.accept("text/html, application/vnd.paos+xml").header("PAOS", paosVersion + ";" + supportsService).get(ClientResponse.class);
		if (clientResponse.getStatus() == 200) {
			//LOGGER.info("Response: {}", clientResponse.getEntity(String.class));
			
			AuthenticationInformation authInfo = storeAuthenticationInfo(clientResponse);
			if  (authInfo.getAssertionConsumerServiceURL() == null) {
				throw new ShibbolethAuthenticationException("No Paos request header found");
			}
			if (authInfo.getRelayState() == null) {
				throw new ShibbolethAuthenticationException("No ECP relay state header found");
			}
			
			return authInfo;
		}
		else {
			LOGGER.error("Http Error, status {} from {}", clientResponse.getStatus(), resource.getURI());
			LOGGER.info("Error response was: {}", clientResponse.getEntity(String.class));
		}
		
		return null;
	}
	
	private AuthenticationInformation requestIdpAuthentication(AuthenticationInformation spAuthInfo, String idp, String username, String password)  throws ShibbolethAuthenticationException
	{
		if (idpClient_ == null) {
			idpClient_ = Client.create();
		}
		ClientFilter clientFilter = new HTTPBasicAuthFilter(username, password);
		idpClient_.addFilter(clientFilter);
		
		LOGGER.info("Idp URL: {}", idp);
		WebResource resource = idpClient_.resource(idp);
		WebResource.Builder builder = resource.getRequestBuilder();
		
		for (NewCookie cookie : spAuthInfo.getCookies()) {
			builder = builder.cookie(cookie);
		}
		Envelope newEnvelope = (Envelope) buildObject(Envelope.DEFAULT_ELEMENT_NAME);
		newEnvelope.setBody(spAuthInfo.getBody());;
		
		InputStream envelopeInputStream = envelopeToInputStream(newEnvelope);
		ClientResponse clientResponse = builder.post(ClientResponse.class, envelopeInputStream);
		if (clientResponse.getStatus() == 200) {
			AuthenticationInformation authInfo = storeAuthenticationInfo(clientResponse);
			if (authInfo.getAssertionConsumerServiceURL() == null) {
				throw new ShibbolethAuthenticationException("No ECP Response header found from the IdP");
			}
			if (!authInfo.getAssertionConsumerServiceURL().equals(spAuthInfo.getAssertionConsumerServiceURL())) {
				throw new ShibbolethAuthenticationException("The ResponseConsumerURL (" + spAuthInfo.getAssertionConsumerServiceURL() + ") and AssertionConsumerServiceURL (" + authInfo.getAssertionConsumerServiceURL() + ") do not match.");
			}
			return authInfo;
		}
		else {
			LOGGER.error("Exception received response of {} when {}", clientResponse.getStatus(), clientResponse.getEntity(String.class));
			throw new ShibbolethAuthenticationException("Error retrieving information from IdP Provider");
		}
		
	}
	
	private AuthenticationInformation storeAuthenticationInfo(ClientResponse clientResponse)  throws ShibbolethAuthenticationException
	{
		LOGGER.info("In storeAuthenticationInfo");
		AuthenticationInformation authInfo = new AuthenticationInformation();
		
		InputStream is = clientResponse.getEntityInputStream();
		Envelope envelope = getEnvelope(is);
		
		Header header = envelope.getHeader();
		
		List<XMLObject> requests = header.getUnknownXMLObjects(Request.DEFAULT_ELEMENT_NAME);
		
		if (requests.size() > 0) {
			Request request = (Request) requests.get(0);
			authInfo.setAssertionConsumerServiceURL(request.getResponseConsumerURL());
		}
		
		List<XMLObject> ecpResponses = header.getUnknownXMLObjects(Response.DEFAULT_ELEMENT_NAME);
		if (ecpResponses.size() > 0) {
			Response response = (Response) ecpResponses.get(0);
			authInfo.setAssertionConsumerServiceURL(response.getAssertionConsumerServiceURL());
		}
		
		List<XMLObject> relayStates = header.getUnknownXMLObjects(RelayState.DEFAULT_ELEMENT_NAME);

		if (relayStates.size() > 0) {
			RelayState relayState = (RelayState) relayStates.get(0);
			relayState.detach();
			authInfo.setRelayState(relayState);
		}
		
		Body bodyInfo = envelope.getBody();
		bodyInfo.detach();
		authInfo.setBody(bodyInfo);
		
		List<NewCookie> cookies = clientResponse.getCookies();
		authInfo.setCookies(cookies);
		
		return authInfo;
	}
	
	private InputStream envelopeToInputStream(Envelope envelope) throws ShibbolethAuthenticationException {
		try {
			Document doc = asDOMDocument(envelope);
			return documentToInputStream(doc);
		}
		catch (MarshallingException e) {
			throw new ShibbolethAuthenticationException("Exception creating envelope", e);
		}
		catch (ParserConfigurationException e) {
			throw new ShibbolethAuthenticationException("Exception creating envelope", e);
		}
		catch (TransformerException e) {
			throw new ShibbolethAuthenticationException("Exception transforming document to inputstream", e);
		}
	}
	
	private List<NewCookie> establishSpSession(AuthenticationInformation spAuthInfo, AuthenticationInformation idpAuthInfo, String targetURL)
			throws ShibbolethAuthenticationException {
		Envelope spEnvelope = (Envelope) buildObject(Envelope.DEFAULT_ELEMENT_NAME);
		Header spHeader = (Header) buildObject(Header.DEFAULT_ELEMENT_NAME);
		spHeader.getUnknownXMLObjects().add(spAuthInfo.getRelayState());
		spEnvelope.setHeader(spHeader);;
		spEnvelope.setBody(idpAuthInfo.getBody());
		
		WebResource resource = spClient_.resource(idpAuthInfo.getAssertionConsumerServiceURL());
		
		WebResource.Builder builder = resource.getRequestBuilder();
		
		for (NewCookie cookie : spAuthInfo.getCookies()) {
			builder = builder.cookie(cookie);
		}
		
		InputStream envelopeInputStream = envelopeToInputStream(spEnvelope);
		ClientResponse clientResponse = builder.type("application/vnd.paos+xml").post(ClientResponse.class, envelopeInputStream);
		if (clientResponse.getStatus() == 200 || clientResponse.getStatus() == 302) {
			return clientResponse.getCookies();
		}
		else {
			LOGGER.error("Http Error, status {} from {}", clientResponse.getStatus(), idpAuthInfo.getAssertionConsumerServiceURL());
			LOGGER.info("Error response was: {}", clientResponse.getEntity(String.class));
		}
		
		return null;
	}
	
	private Envelope getEnvelope(InputStream is) throws ShibbolethAuthenticationException 
	{
		XMLObject xmlObject = inputStreamToXMLObject(is);
		LOGGER.info("{}, {}", xmlObject.getElementQName().getNamespaceURI(), xmlObject.getElementQName().getLocalPart());
		
		return (Envelope) xmlObject;
	}
	
	private XMLObject buildObject(QName qName) {
		XMLObjectBuilderFactory builderFactory = Configuration.getBuilderFactory();
		XMLObject object = builderFactory.getBuilder(qName).buildObject(qName);
		return object;
	}
	
	private XMLObject inputStreamToXMLObject(InputStream is) throws ShibbolethAuthenticationException 
	{
		BasicParserPool ppMgr = new BasicParserPool();
		ppMgr.setNamespaceAware(true);
		XMLObject xmlObject = null;
		
		try {
			Document doc = ppMgr.parse(is);
			Element metadataRoot = doc.getDocumentElement();
			LOGGER.info("Node: {}", metadataRoot.getNodeName());
			UnmarshallerFactory unmarshallerFactory = Configuration.getUnmarshallerFactory();
			Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(metadataRoot);
			if (unmarshaller == null) {
				throw new ShibbolethAuthenticationException("Unable to unmarshall message");
			}
			xmlObject = unmarshaller.unmarshall(metadataRoot);
		}
		catch (XMLParserException e) {
			throw new ShibbolethAuthenticationException("Exception parsing document", e);
		}
		catch (UnmarshallingException e) {
			throw new ShibbolethAuthenticationException("Exception unmarshalling document", e);
		}
		
		return xmlObject;
	}
	
	public Document asDOMDocument(XMLObject object) throws ParserConfigurationException, MarshallingException
	{
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		
		Document doc = builder.newDocument();
		Marshaller marshaller = Configuration.getMarshallerFactory().getMarshaller(Envelope.DEFAULT_ELEMENT_NAME);
		marshaller.marshall(object, doc);
		return doc;
	}
	
	public InputStream documentToInputStream(Document doc) throws TransformerConfigurationException, TransformerException
	{
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		Source xmlSource = new DOMSource(doc);
		Result outputTarget = new StreamResult(outputStream);
		TransformerFactory.newInstance().newTransformer().transform(xmlSource, outputTarget);
		
		return new ByteArrayInputStream(outputStream.toByteArray());
	}
}
