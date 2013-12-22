package au.edu.anu.dcclient.shibboleth.ecp.liberty.paos.impl;

import java.util.List;

import org.opensaml.common.impl.AbstractSAMLObject;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.schema.XSBooleanValue;

import au.edu.anu.dcclient.shibboleth.ecp.liberty.paos.Request;

public class RequestImpl extends AbstractSAMLObject implements Request {
	private String responseConsumerURL;
	private String service;
	private String messageId;
	private String soap11Actor;
	private XSBooleanValue soap11MustUnderstand;
	
	protected RequestImpl(String namespaceURI, String elementLocalName, String namespacePrefix) {
		super(namespaceURI, elementLocalName, namespacePrefix);
	}
	
	public List<XMLObject> getOrderedChildren() {
		return null;
	}

	public Boolean isSOAP11MustUnderstand() {
		if (soap11MustUnderstand != null) {
			return soap11MustUnderstand.getValue();
		}
		return Boolean.FALSE;
	}

	public XSBooleanValue isSOAP11MustUnderstandXSBoolean() {
		return soap11MustUnderstand;
	}

	public void setSOAP11MustUnderstand(Boolean newMustUnderstand) {
		if (newMustUnderstand != null) {
			soap11MustUnderstand = prepareForAssignment(soap11MustUnderstand, new XSBooleanValue(newMustUnderstand, true));
		}
		else {
			soap11MustUnderstand = prepareForAssignment(soap11MustUnderstand, null);
		}
	}

	public void setSOAP11MustUnderstand(XSBooleanValue soap11MustUnderstand) {
		this.soap11MustUnderstand = soap11MustUnderstand;
	}

	public String getSOAP11Actor() {
		return soap11Actor;
	}

	public void setSOAP11Actor(String soap11Actor) {
		this.soap11Actor = soap11Actor;
	}

	public String getResponseConsumerURL() {
		return responseConsumerURL;
	}

	public void setResponseConsumerURL(String responseConsumerURL) {
		this.responseConsumerURL = responseConsumerURL;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getMessageID() {
		return messageId;
	}

	public void setMessageID(String messageID) {
		this.messageId = messageID;
	}

}
