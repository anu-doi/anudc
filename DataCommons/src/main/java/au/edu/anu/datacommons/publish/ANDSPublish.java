package au.edu.anu.datacommons.publish;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.data.fedora.FedoraBroker;
import au.edu.anu.datacommons.data.fedora.FedoraReference;
import au.edu.anu.datacommons.exception.ValidateException;

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
 * 0.4		15/10/2012	Genevieve Turner(GT)	Added checkValidity		
 * 0.5		10/12/2012	Genevieve Turner (GT)	Updated to use the default validation functions and added the isAllowedToPublish field
 * 0.6		02/01/2012	Genevieve Turner (GT)	Updated to allow for changes to error handling
 * </pre>
 * 
 */
public class ANDSPublish extends GenericPublish implements Publish {
	private static final Logger LOGGER = LoggerFactory.getLogger(ANDSPublish.class);
	
	private boolean isAllowedToPublish = false;

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
	 * 0.5		10/12/2012	Genevieve Turner (GT)	Updated to use isAllowedToPublish
	 * 0.6		02/01/2012	Genevieve Turner (GT)	Updated to allow for changes to error handling
	 * </pre>
	 * 
	 * @param pid The id of the object to publish
	 */
	@Override
	public void publish(String pid, String publishCode) throws ValidateException {
		//Validate validate = new ANDSValidate();
		List<String> errorMessages = checkValidity(pid);
		if (!isAllowedToPublish) {
			List<String> messages = new ArrayList<String>(errorMessages);
			messages.add(0, "Error publishing to " + publishCode);
			throw new ValidateException(messages);
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

	/**
	 * checkValidity
	 * 
	 * Checks the validity of the object
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.4		15/10/2012	Genevieve Turner(GT)	Initial
	 * 0.5		10/12/2012	Genevieve Turner (GT)	Added the setting of isAllowedToPublish
	 * </pre>
	 * 
	 * @param pid The pid to check validity for
	 * @return A list of validation error messages
	 * @see au.edu.anu.datacommons.publish.GenericPublish#checkValidity(java.lang.String)
	 */
	@Override
	public List<String> checkValidity(String pid) {
		Validate validate = new ANDSValidate();
		isAllowedToPublish = validate.isValid(pid);
		return validate.getErrorMessages();
	}
	
	/**
	 * isAllowedToPublish
	 * 
	 * Indicates whether record is valid enough to allow for publishing
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.5		10/12/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return
	 * @see au.edu.anu.datacommons.publish.GenericPublish#isAllowedToPublish()
	 */
	public boolean isAllowedToPublish() {
		return isAllowedToPublish;
	}
}
