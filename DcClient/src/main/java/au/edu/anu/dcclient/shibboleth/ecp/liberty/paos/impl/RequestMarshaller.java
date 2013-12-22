package au.edu.anu.dcclient.shibboleth.ecp.liberty.paos.impl;

import org.opensaml.common.impl.AbstractSAMLObjectMarshaller;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.MarshallingException;
import org.w3c.dom.Element;

import au.edu.anu.dcclient.shibboleth.ecp.liberty.paos.Request;

public class RequestMarshaller extends AbstractSAMLObjectMarshaller {
	protected void marshallAttributes(XMLObject xmlObject, Element domElement)
			throws MarshallingException {
		Request request = (Request) xmlObject;
		
		if (request.getResponseConsumerURL() != null) {
			domElement.setAttributeNS(null, Request.RESPONSE_CONSUMER_URL_ATTRIB_NAME, request.getResponseConsumerURL());
		}
		if (request.getService() != null) {
			domElement.setAttributeNS(null,  Request.SERVICE_ATTRIB_NAME, request.getService());
		}
		if (request.getMessageID() != null) {
			domElement.setAttributeNS(null, Request.MESSAGE_ID_ATTRIB_NAME, request.getMessageID());
		}
		if (request.isSOAP11MustUnderstand() != null) {
			domElement.setAttributeNS(Request.SOAP11_MUST_UNDERSTAND_ATTR_NAME.getNamespaceURI()
					, Request.SOAP11_MUST_UNDERSTAND_ATTR_NAME.getPrefix() + ":" + Request.SOAP11_MUST_UNDERSTAND_ATTR_NAME.getLocalPart()
					, request.isSOAP11MustUnderstandXSBoolean().toString());
		}
		if (request.getSOAP11Actor() != null) {
			domElement.setAttributeNS(Request.SOAP11_ACTOR_ATTR_NAME.getNamespaceURI()
					, Request.SOAP11_ACTOR_ATTR_NAME.getPrefix() + ":" + Request.SOAP11_ACTOR_ATTR_NAME.getLocalPart(), request.getSOAP11Actor());
		}
	}
}
