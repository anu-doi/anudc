package au.edu.anu.dcclient.shibboleth.ecp.liberty.paos;

import javax.xml.namespace.QName;

import org.opensaml.common.SAMLObject;
import org.opensaml.common.xml.SAMLConstants;
import org.opensaml.ws.soap.soap11.ActorBearing;
import org.opensaml.ws.soap.soap11.MustUnderstandBearing;

public interface Request extends SAMLObject, MustUnderstandBearing,
		ActorBearing {
	public static final String DEFAULT_ELEMENT_LOCAL_NAME = "Request";
	
	public static final QName DEFAULT_ELEMENT_NAME = new QName(SAMLConstants.PAOS_NS, DEFAULT_ELEMENT_LOCAL_NAME, SAMLConstants.PAOS_PREFIX);
	
	public static final String TYPE_LOCAL_NAME = "RequestType";
	
	public static final QName TYPE_NAME = new QName(SAMLConstants.PAOS_NS, TYPE_LOCAL_NAME, SAMLConstants.PAOS_PREFIX);
	
	public static final String RESPONSE_CONSUMER_URL_ATTRIB_NAME = "responseConsumerURL";
	
	public static final String SERVICE_ATTRIB_NAME = "service";
	
	public static final String MESSAGE_ID_ATTRIB_NAME = "messageID";

	public String getResponseConsumerURL();
	
	public void setResponseConsumerURL(String responseConsumerURL);
	
	public String getService();
	
	public void setService(String service);
	
	public String getMessageID();
	
	public void setMessageID(String messageID);

}
