package au.edu.anu.datacommons.data.db.dao;

import java.util.List;

import au.edu.anu.datacommons.data.db.model.LinkRelation;

/**
 * LinkRelationDAO
 * 
 * Australian National University Data Commons
 * 
 * Interface for retrieving link relations
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		19/09/2012	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
public interface LinkRelationDAO extends GenericDAO<LinkRelation, Long> {
	/**
	 * getRelations
	 *
	 * Retrieves the relations for the categories
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		19/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param category1 The type of item to retrieve relations for
	 * @param category2 The type of item to relate to
	 * @return A list of relation links
	 */
	public List<LinkRelation> getRelations(String category1, String category2);
}
