package au.edu.anu.datacommons.data.db.dao;

import au.edu.anu.datacommons.data.db.model.LinkType;

/**
 * LinkTypeDAO
 * 
 * Australian National University Data Commons
 * 
 * Data Access Object Interface for the link_type table
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		28/09/2012	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
public interface LinkTypeDAO extends GenericDAO<LinkType, Long> {
	public LinkType getByCode(String type);
}
