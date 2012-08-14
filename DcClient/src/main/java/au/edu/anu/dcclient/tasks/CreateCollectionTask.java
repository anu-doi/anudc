package au.edu.anu.dcclient.tasks;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.util.concurrent.Callable;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.apache.http.protocol.HttpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

import au.edu.anu.dcclient.Global;
import au.edu.anu.dcclient.collection.CollectionInfo;

public class CreateCollectionTask extends AbstractDcBagTask implements Callable<String>
{
	private static final Logger LOGGER = LoggerFactory.getLogger(CreateCollectionTask.class);

	private CollectionInfo collInfo;
	private URI createUri;

	public CreateCollectionTask(CollectionInfo collInfo, URI createUri)
	{
		this.collInfo = collInfo;
		this.createUri = createUri;
	}

	@Override
	public String call() throws Exception
	{
		Client client = Client.create();
		PasswordAuthentication auth = Authenticator.requestPasswordAuthentication(createUri.getHost(), null, createUri.getPort(), createUri.getScheme(),
				"Please provide password for: " + Global.getBagUploadUrl(), "scheme");
		if (auth != null)
			client.addFilter(new HTTPBasicAuthFilter(auth.getUserName(), new String(auth.getPassword())));
		WebResource webResource = client.resource(UriBuilder.fromUri(createUri).queryParam("layout", "def:display").queryParam("tmplt", "tmplt:1").build());
		ClientResponse response = webResource.accept(MediaType.TEXT_PLAIN_TYPE).type(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
				.header("User-Agent", "BagIt Library Parallel Fetcher").post(ClientResponse.class, collInfo);
		String createdPid;
		if (response.getClientResponseStatus() != Status.CREATED)
			throw new Exception("Unable to create a collection. Server returned HTTP " + response.getStatus());
		createdPid = response.getEntity(String.class);
		LOGGER.info("Created object with pid: {}", createdPid);
		return createdPid;
	}
}