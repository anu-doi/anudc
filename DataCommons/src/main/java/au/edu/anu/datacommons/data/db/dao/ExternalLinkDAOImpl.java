package au.edu.anu.datacommons.data.db.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import au.edu.anu.datacommons.data.db.PersistenceManager;
import au.edu.anu.datacommons.data.db.model.ExternalLinkPattern;

/**
 * ExternalLinkDAOImpl
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
public class ExternalLinkDAOImpl extends GenericDAOImpl<ExternalLinkPattern, Long> implements
		ExternalLinkDAO {
	/**
	 * Constructor
	 * 
	 * Constructor class that includes the type
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		20/11/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param type
	 */
	public ExternalLinkDAOImpl(Class<ExternalLinkPattern> type) {
		super(type);
	}
	
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
	 * @see au.edu.anu.datacommons.data.db.dao.ExternalLinkDAO#getByObjectType(java.lang.String)
	 */
	public List<ExternalLinkPattern> getByObjectType(String objectType) {
		EntityManager entityManager = PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
		List<ExternalLinkPattern> patterns = null;
		
		try {
			Query query = entityManager.createQuery("from ExternalLinkPattern where object_type = :value");
			query.setParameter("value", objectType);
			
			patterns = query.getResultList();
		}
		finally {
			entityManager.close();
		}
		
		return patterns;
	}
}
