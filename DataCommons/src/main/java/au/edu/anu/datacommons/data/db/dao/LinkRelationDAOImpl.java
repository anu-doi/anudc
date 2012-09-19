package au.edu.anu.datacommons.data.db.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import au.edu.anu.datacommons.data.db.PersistenceManager;
import au.edu.anu.datacommons.data.db.model.LinkRelation;

/**
 * LinkRelationDAOImpl
 * 
 * Australian National University Data Commons
 * 
 * Implementation of retrieving link relations
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
public class LinkRelationDAOImpl extends GenericDAOImpl<LinkRelation, Long> implements
		LinkRelationDAO {

	/**
	 * Constructor
	 * 
	 * Placeholder
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		19/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param type The class type to retrieve/set objects
	 */
	public LinkRelationDAOImpl(Class<LinkRelation> type) {
		super(type);
	}

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
	 * @see au.edu.anu.datacommons.data.db.dao.LinkRelationDAO#getRelations(java.lang.String, java.lang.String)
	 */
	public List<LinkRelation> getRelations(String category1, String category2) {
		EntityManager entityManager = PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
		List<LinkRelation> linkRelations = null;
		try {
			Query query = entityManager.createQuery("from LinkRelation lr where lr.id.category1 = :category1 and lr.id.category2 = :category2");
			query.setParameter("category1", category1);
			query.setParameter("category2", category2);
			
			linkRelations = query.getResultList();
		}
		catch (NoResultException e) {
			LOGGER.debug("No entity found for {}, {}", category1, category2);
		}
		finally {
			entityManager.close();
		}
		
		return linkRelations;
	}
}
