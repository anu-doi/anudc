package au.edu.anu.datacommons.data.db.dao;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import au.edu.anu.datacommons.data.db.PersistenceManager;
import au.edu.anu.datacommons.data.db.model.PublishLocation;

/**
 * PublishLocationDAOImpl
 * 
 * Australian National University Data Commons
 * 
 * Placeholder
 *
 * JUnit Coverage:
 * PublishLocationDAOTest
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		28/03/2013	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
public class PublishLocationDAOImpl extends GenericDAOImpl<PublishLocation, Long> implements
		PublishLocationDAO {
	/**
	 * Constructor
	 * 
	 * Constructor class that includes the type
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		29/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param type The class type to retrieve/set objects
	 */
	public PublishLocationDAOImpl(Class<PublishLocation> type) {
		super(type);
	}

	/**
	 * getByCode
	 * 
	 * Gets the Publish Location by with the given code
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/04/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param code The code to retrieve the publish location for
	 * @return The publish location
	 * @see au.edu.anu.datacommons.data.db.dao.PublishLocationDAO#getByCode(java.lang.String)
	 */
	@Override
	public PublishLocation getByCode(String code) {
		EntityManager entityManager = PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
		PublishLocation location = null;
		try {
			Query query = entityManager.createQuery("from PublishLocation where code = :code");
			query.setParameter("code", code);
			
			location = (PublishLocation) query.getSingleResult();
		}
		catch (NoResultException e) {
			LOGGER.warn("No publish location found");
		}
		finally {
			entityManager.close();
		}
		return location;
	}
}
