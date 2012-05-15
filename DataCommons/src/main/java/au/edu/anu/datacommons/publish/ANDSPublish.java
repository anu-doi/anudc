package au.edu.anu.datacommons.publish;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.data.fedora.FedoraBroker;
import au.edu.anu.datacommons.data.fedora.FedoraReference;

import com.yourmediashelf.fedora.client.FedoraClientException;

/**
 * ANDSPublish
 * 
 * Australian National University Data Commons
 * 
 * Methods for publishing to ANDS
 * 
 * JUnit coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		15/05/2012	Genevieve Turner (GT)	Initial build
 * </pre>
 * 
 */
public class ANDSPublish implements GenericPublish {
	private static final Logger LOGGER = LoggerFactory.getLogger(ANDSPublish.class);

	/**
	 * publish
	 * 
	 * Publishes to ANDS (Australian National Data Service)
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		15/05/2012	Genevieve Turner (GT)	Initial build
	 * </pre>
	 * 
	 * @param pid The id of the object to publish
	 */
	@Override
	public void publish(String pid) {
		LOGGER.info("publishing to ands");
		FedoraReference reference = new FedoraReference();
		reference.setPredicate_("info:fedora/fedora-system:def/model#hasModel");
		reference.setObject_("info:fedora/def:RIFCSContentModel");
		reference.setIsLiteral_(Boolean.FALSE);
		
		try {
			FedoraBroker.addRelationship(pid, reference);
		}
		catch (FedoraClientException e) {
			LOGGER.error("Exception adding content model to " + pid, e);
		}
	}

	/**
	 * unpublish
	 * 
	 * Unpublishes from ANDS (Australian National Data Service)
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		15/05/2012	Genevieve Turner (GT)	Initial build
	 * </pre>
	 * 
	 * @param pid The id of the object to publish
	 */
	@Override
	public void unpublish(String pid) {
		//TODO Create information for unpublishing
		LOGGER.info("unpublishing from ands");
	}
}
