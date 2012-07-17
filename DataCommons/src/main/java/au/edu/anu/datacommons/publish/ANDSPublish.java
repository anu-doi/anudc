package au.edu.anu.datacommons.publish;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.data.fedora.FedoraBroker;
import au.edu.anu.datacommons.data.fedora.FedoraReference;
import au.edu.anu.datacommons.exception.ValidationException;

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
 * 0.2		08/06/2012	Genevieve Turner (GT)	Updated to incorporate some changes to publishing
 * 0.3		17/06/2012	Genevieve Turner (GT)	Added validation prior to publishing
 * </pre>
 * 
 */
public class ANDSPublish extends GenericPublish implements Publish {
	private static final Logger LOGGER = LoggerFactory.getLogger(ANDSPublish.class);

	/**
	 * publish
	 * 
	 * Publishes to ANDS (Australian National Data Service)
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		15/05/2012	Genevieve Turner (GT)	Initial build
	 * 0.2		08/06/2012	Genevieve Turner (GT)	Updated to incorporate some changes to publishing
	 * 0.3		17/06/2012	Genevieve Turner (GT)	Added validation prior to publishing
	 * </pre>
	 * 
	 * @param pid The id of the object to publish
	 */
	@Override
	public void publish(String pid, String publishCode) {
		Validate validate = new ANDSValidate();
		if (!validate.isValid(pid)) {
			StringBuffer errorMessage = new StringBuffer();
			errorMessage.append("Error publishing to ");
			errorMessage.append(publishCode);
			errorMessage.append("\n");
			for (String message : validate.getErrorMessages()){
				errorMessage.append(message);
				errorMessage.append("\n");
			}

			throw new ValidationException(errorMessage.toString());
			//throw new ValidationException("Error Publishing to ANDS");
		}
		
		super.publish(pid, publishCode);
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
	public void unpublish(String pid, String publishCode) {
		//TODO Create information for unpublishing
		LOGGER.info("unpublishing from ands");
	}
}
