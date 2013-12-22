package au.edu.anu.dcclient.shibboleth.auth;

import org.opensaml.DefaultBootstrap;
import org.opensaml.xml.Configuration;
import org.opensaml.xml.ConfigurationException;

import au.edu.anu.dcclient.shibboleth.ecp.liberty.paos.Request;
import au.edu.anu.dcclient.shibboleth.ecp.liberty.paos.impl.RequestBuilder;
import au.edu.anu.dcclient.shibboleth.ecp.liberty.paos.impl.RequestMarshaller;
import au.edu.anu.dcclient.shibboleth.ecp.liberty.paos.impl.RequestUnmarshaller;

public class Registry {
	/**
	 * Start up open saml and register object providers
	 * 
	 * @throws ConfigurationException
	 */
	public static void initialise() throws ConfigurationException {
		// start the 
		DefaultBootstrap.bootstrap();
		registerObjectProviders();
	}
	
	/**
	 * Register the object providers
	 */
	public static void registerObjectProviders() {
		Configuration.registerObjectProvider(Request.DEFAULT_ELEMENT_NAME, new RequestBuilder(), new RequestMarshaller(), new RequestUnmarshaller());
	}
}
