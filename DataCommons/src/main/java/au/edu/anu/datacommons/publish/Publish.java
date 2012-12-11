package au.edu.anu.datacommons.publish;

import java.util.List;

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
 * 0.2		08/06/2012	Genevieve Turner (GT)	Renamed to Publish from GenericPublish
 * 0.3		15/10/2012	Genevieve Turner (GT)	Added checkValidity function
 * 0.4		11/12/2012	Genevieve Turner (GT)	Added isAllowedToPublish method
 * </pre>
 * 
 */
public interface Publish {
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
	public void publish(String pid, String publishCode) throws ValidateException;
	

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
	public void unpublish(String pid, String publishCode);
	
	/**
	 * checkValidity
	 *
	 * Checks the validity of the object
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.3		15/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param pid The pid to check validity for
	 * @return A list of validation error messages
	 */
	public List<String> checkValidity(String pid);
	
	/**
	 * isAllowedToPublish
	 *
	 * Indicates that the record is valid enough for publishing
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.4		11/12/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return True if the records is valid enough for publish
	 */
	public boolean isAllowedToPublish();
}
