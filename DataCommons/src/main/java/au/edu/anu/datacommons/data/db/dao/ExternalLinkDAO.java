package au.edu.anu.datacommons.data.db.dao;

import java.util.List;

import au.edu.anu.datacommons.data.db.model.ExternalLinkPattern;

/**
 * ExternalLinkDAO
 * 
 * Australian National University Data Commons
 * 
 * Data Access Object Implementation for the ExternalLinkPattern class
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		20/11/2012	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
public interface ExternalLinkDAO extends GenericDAO<ExternalLinkPattern, Long> {
	/**
	 * getByObjectType
	 *
	 * Retrieve the patterns associated with the given object type
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		20/11/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param objectType The object type
	 * @return The patterns associated with the object type
	 */
	public List<ExternalLinkPattern> getByObjectType(String objectType);
}
