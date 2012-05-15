package au.edu.anu.datacommons.publish;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.data.fedora.FedoraBroker;
import au.edu.anu.datacommons.data.fedora.FedoraReference;
import au.edu.anu.datacommons.properties.GlobalProps;
import au.edu.anu.datacommons.util.Constants;

import com.yourmediashelf.fedora.client.FedoraClientException;

/**
 * GenericPublish
 * 
 * Australian National University Data Commons
 * 
 * Interface containing the methods for publishing
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
public class ANUPublish implements GenericPublish {
	private static final Logger LOGGER = LoggerFactory.getLogger(ANUPublish.class);

	/**
	 * publish
	 * 
	 * Publishes data to the appropriate service
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		15/05/2012	Genevieve Turner (GT)	Initial build
	 * </pre>
	 * 
	 * @param pid The pid of the object to publish
	 */
	@Override
	public void publish(String pid) {
		LOGGER.debug("publishing to ANU");
		
		FedoraReference fedoraReference = new FedoraReference();
		fedoraReference.setPredicate_(GlobalProps.getProperty(GlobalProps.PROP_FEDORA_OAIPROVIDER_URL));
		fedoraReference.setObject_("oai:" + pid);
		fedoraReference.setIsLiteral_(Boolean.FALSE);
		
		String location = String.format("%s/objects/%s/datastreams/XML_SOURCE/content", GlobalProps.getProperty(GlobalProps.PROP_FEDORA_URI), pid);
		try {
			FedoraBroker.addDatastreamByReference(pid, Constants.XML_PUBLISHED, "M", "XML Published", location);
			FedoraBroker.addRelationship(pid, fedoraReference);
		}
		catch (FedoraClientException e) {
			LOGGER.info("Exception publishing to ANU: ", e);
		}
	}
	
	/**
	 * unpublish
	 * 
	 * Unpublishes data to the appropriate service
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		15/05/2012	Genevieve Turner (GT)	Initial build
	 * </pre>
	 * 
	 * @param pid The id of the object to unpublish
	 */
	@Override
	public void unpublish(String pid) {
		LOGGER.info("unpublishing from ANU");
	}

}
