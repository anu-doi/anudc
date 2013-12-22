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

public class ShibbolethIdpController {
	static final Logger LOGGER = LoggerFactory.getLogger(ShibbolethIdpController.class);
	
	private static List<ShibbolethIdp> entities_;
	
	public List<ShibbolethIdp> getShibbolethIdpList() {
		if (entities_ == null) {
			ClientConfig clientConfig = new DefaultClientConfig();
			clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
			Client client = Client.create(clientConfig);

			//WebResource resource = client.resource("https://dc7-dev1.anu.edu.au/Shibboleth.sso/DiscoFeed");
			WebResource resource = client.resource(Global.getDiscoveryFeedUriAsString());
			ClientResponse response = resource.get(ClientResponse.class);
			if (response.getStatus() == 200) {
				entities_ = response.getEntity(new GenericType<List<ShibbolethIdp>>(){});
			}
		}
		return entities_;
	}
}
