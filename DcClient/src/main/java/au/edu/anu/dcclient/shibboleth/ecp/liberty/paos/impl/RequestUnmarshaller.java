package au.edu.anu.dcclient.shibboleth.ecp.liberty.paos.impl;

import javax.xml.namespace.QName;

import org.opensaml.common.impl.AbstractSAMLObjectUnmarshaller;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.UnmarshallingException;
import org.opensaml.xml.schema.XSBooleanValue;
import org.opensaml.xml.util.XMLHelper;
import org.w3c.dom.Attr;

import au.edu.anu.dcclient.shibboleth.ecp.liberty.paos.Request;

public class RequestUnmarshaller extends AbstractSAMLObjectUnmarshaller {
	protected void processAttribute(XMLObject samlObject, Attr attribute)
			throws UnmarshallingException {
		Request request = (Request) samlObject;
		
		QName attrQName = XMLHelper.constructQName(attribute.getNamespaceURI(), attribute.getLocalName(), attribute.getPrefix());
		if (Request.SOAP11_MUST_UNDERSTAND_ATTR_NAME.equals(attrQName)) {
			request.setSOAP11MustUnderstand(XSBooleanValue.valueOf(attribute.getValue()));
		}
		else if (Request.SOAP11_ACTOR_ATTR_NAME.equals(attrQName)) {
			request.setSOAP11Actor(attribute.getValue());
		}
		else if (Request.RESPONSE_CONSUMER_URL_ATTRIB_NAME.equals(attrQName.getLocalPart())) {
			request.setResponseConsumerURL(attribute.getValue());
		}
		else if (Request.SERVICE_ATTRIB_NAME.equals(attrQName.getLocalPart())) {
			request.setService(attribute.getValue());
		}
		else if (Request.MESSAGE_ID_ATTRIB_NAME.equals(attrQName.getLocalPart())) {
			request.setMessageID(attribute.getValue());
		}
		else {
			super.processAttribute(samlObject, attribute);
		}
	}
}
