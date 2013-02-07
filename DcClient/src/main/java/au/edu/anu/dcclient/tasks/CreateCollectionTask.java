package au.edu.anu.dcclient.tasks;

import java.net.URI;
import java.text.MessageFormat;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.dcclient.Global;
import au.edu.anu.dcclient.collection.CollectionInfo;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

/**
 * Represents a task that creates/updates a collection in Data Commons.
 */
public class CreateCollectionTask extends AbstractDcBagTask<String>
{
	private static final Logger LOGGER = LoggerFactory.getLogger(CreateCollectionTask.class);

	private final CollectionInfo collInfo;
	private final URI createUri;

	public CreateCollectionTask(CollectionInfo collInfo, URI createUri)
	{
		this.collInfo = collInfo;
		this.createUri = createUri;
	}

	@Override
	public String call() throws Exception
	{
		String createdPid = null;

		if (collInfo.getPid() == null)
		{
			// The parameter file doesn't contain a pid, create an object and store the pid of newly created object in parameter file.
			stopWatch.start();
			try
			{
				WebResource webResource = client.resource(UriBuilder.fromUri(createUri).queryParam("layout", "def:display").queryParam("tmplt", "tmplt:1")
						.build());
				ClientResponse response = webResource.accept(MediaType.TEXT_PLAIN_TYPE).type(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
						.post(ClientResponse.class, collInfo.getCreateCollMap());
				if (response.getClientResponseStatus() != Status.CREATED)
					throw new Exception("Unable to create a collection. Server returned HTTP " + response.getStatus());
				createdPid = response.getEntity(String.class);
				collInfo.setPid(createdPid);
				LOGGER.info("Created object with pid: {}", createdPid);
				
				// Add relations
				webResource = client.resource(UriBuilder.fromUri(Global.getAddLinkUri()).path(collInfo.getPid()).build());
				for (String[] rel : collInfo.getRelationSet())
				{
					MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
					formData.add("linkType", rel[0]);
					formData.add("itemId", rel[1]);
					response = webResource.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE).accept(MediaType.TEXT_PLAIN_TYPE).post(ClientResponse.class, formData);
					if (response.getClientResponseStatus() == Status.OK)
						LOGGER.info(MessageFormat.format("Created {0} relationship with {1}.", rel[0], rel[1]));
					else
						LOGGER.error(MessageFormat.format("Unable to set {0} relation with {1}.", rel[0], rel[1]));
				}
			}
			finally
			{
				stopWatch.end();
				LOGGER.info("Time - Create Collection Task: {}", stopWatch.getFriendlyElapsed());
			}
		}
		else
		{
			// Sync the collection details.
			// TODO Implement syncing.
		}

		return createdPid;
	}
}
