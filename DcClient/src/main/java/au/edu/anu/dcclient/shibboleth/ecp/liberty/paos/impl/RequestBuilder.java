package au.edu.anu.dcclient.shibboleth.ecp.liberty.paos.impl;

import org.opensaml.common.impl.AbstractSAMLObjectBuilder;
import org.opensaml.common.xml.SAMLConstants;

import au.edu.anu.dcclient.shibboleth.ecp.liberty.paos.Request;

public class RequestBuilder extends AbstractSAMLObjectBuilder<Request> {

	@Override
	public Request buildObject() {
		return buildObject(SAMLConstants.PAOS_NS, Request.DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.PAOS_PREFIX);
	}

	@Override
	public Request buildObject(String namespaceURI, String localName, String namespacePrefix) {
		return new RequestImpl(namespaceURI, localName, namespacePrefix);
	}
	
}
