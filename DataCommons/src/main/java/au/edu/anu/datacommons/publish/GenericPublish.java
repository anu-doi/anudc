package au.edu.anu.datacommons.publish;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.data.fedora.FedoraBroker;
import au.edu.anu.datacommons.data.fedora.FedoraReference;

import com.yourmediashelf.fedora.client.FedoraClientException;

/**
 * GenericPublish
 * 
 * Australian National University Data Commons
 * 
 * Placeholder
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		08/06/2012	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
public class GenericPublish implements Publish {
	private static final Logger LOGGER = LoggerFactory.getLogger(GenericPublish.class);
	
	/**
	 * publish
	 * 
	 * Publishes to the specified location
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		08/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param pid The pid to publish to
	 * @param publishCode The code of the location to publish to
	 * @see au.edu.anu.datacommons.publish.Publish#publish(java.lang.String, java.lang.String)
	 */
	@Override
	public void publish(String pid, String publishCode) {
		LOGGER.debug("Publishing to {}", publishCode);
		FedoraReference reference = new FedoraReference();
		reference.setPredicate_("http://anu.edu.au/publish");
		reference.setObject_(publishCode);
		reference.setIsLiteral_(Boolean.TRUE);
		try {
			FedoraBroker.addRelationship(pid, reference);
		}
		catch (FedoraClientException e) {
			LOGGER.error("Exception publishing to " + publishCode + " for " + pid, e);
		}
	}

	/**
	 * unpublish
	 * 
	 * Unpublishes from the specified lcoation
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		08/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param pid The pid to unpublish from
	 * @param publishCode  THe code of the location to unpublish from
	 * @see au.edu.anu.datacommons.publish.Publish#unpublish(java.lang.String, java.lang.String)
	 */
	@Override
	public void unpublish(String pid, String publishCode) {
		// TODO Auto-generated method stub
		LOGGER.info("Unpublishing from {}", publishCode);
		
	}

}
