package au.edu.anu.datacommons.publish;

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
	public void publish(String pid, String publishCode);
	

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
}
