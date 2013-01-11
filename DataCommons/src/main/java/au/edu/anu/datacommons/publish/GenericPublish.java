package au.edu.anu.datacommons.publish;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.data.fedora.FedoraBroker;
import au.edu.anu.datacommons.data.fedora.FedoraReference;
import au.edu.anu.datacommons.exception.ValidateException;

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
 * 0.2		15/10/2012	Genevieve Turner (GT)	Added checkValidity function
 * 0.3		11/12/2012	Genevieve Turner (GT)	Added a check for if the record is publishable
 * </pre>
 *
 */
public class GenericPublish implements Publish {
	private static final Logger LOGGER = LoggerFactory.getLogger(GenericPublish.class);
	
	private boolean isAllowedToPublish = false;
	
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
	public void publish(String pid, String publishCode) throws ValidateException {
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

	/**
	 * checkValidity
	 * 
	 * Checks the validity of the object
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.2		15/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param pid The pid to check validity for
	 * @return A list of validation error messages
	 * @see au.edu.anu.datacommons.publish.Publish#checkValidity(java.lang.String)
	 */
	public List<String> checkValidity(String pid) {
		Validate validate = new FieldValidate();
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
	 * 0.1		11/12/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return
	 * @see au.edu.anu.datacommons.publish.Publish#isAllowedToPublish()
	 */
	public boolean isAllowedToPublish() {
		return isAllowedToPublish;
	}
}
