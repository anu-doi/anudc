package au.edu.anu.datacommons.data.db.dao;

import au.edu.anu.datacommons.data.db.model.PublishLocation;

/**
 * PublishLocationDAO
 * 
 * Australian National University Data Commons
 * 
 * PublishLocationDAOTest
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		28/03/2013	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
public interface PublishLocationDAO extends GenericDAO<PublishLocation, Long> {
	/**
	 * getByCode
	 *
	 * Gets the Publish Location by with the given code
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		28/03/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param code The code to retrieve the publish location for
	 * @return The publish location
	 */
	public PublishLocation getByCode(String code);
}
