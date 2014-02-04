package au.edu.anu.dcclient.shibboleth.idp;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.dcclient.Global;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;

/*
 * ShibbolethIdpController
 *
 * Australian National University Data Commons
 * 
 * A controller that retrieves and proceses a list of 
 *
 * JUnit coverage:
 * ShibbolethIdpControllerTest
 * 
 * @author Genevieve Turner
 *
 */
public class ShibbolethIdpController {
	static final Logger LOGGER = LoggerFactory.getLogger(ShibbolethIdpController.class);
	
	private static List<IdentityProvider> entities_;
	
	public List<IdentityProvider> getShibbolethIdpList() {
		if (entities_ == null) {
			ClientConfig clientConfig = new DefaultClientConfig();
			clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
			Client client = Client.create(clientConfig);
			
			LOGGER.debug("IdP List URL: {}", Global.getIdpList());
			WebResource resource = client.resource(Global.getIdpList());
			ClientResponse response = resource.get(ClientResponse.class);
			if (response.getStatus() == 200) {
				entities_ = response.getEntity(new GenericType<List<IdentityProvider>>(){});
			}
			else {
				LOGGER.error("Error response status: {}", response.getStatus());
			}
		}
		LOGGER.info("Returning Entities");
		return entities_;
	}
}
