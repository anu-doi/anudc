package au.edu.anu.datacommons.data.db.dao;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import au.edu.anu.datacommons.data.db.PersistenceManager;

/**
 * FedoraObjectDAO
 * 
 * Australian National University Data Commons
 * 
 * Class to perform actions with fedora objects in the database
 * 
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		26/04/2012	Genevieve Turner (GT)	Initial
 * </pre>
 * 
 */
@Repository
public class FedoraObjectDAOImpl<FedoraObject, Long> extends GenericDAOImpl implements FedoraObjectDAO
{
	static final Logger LOGGER = LoggerFactory.getLogger(FedoraObjectDAOImpl.class);
	
	/**
	 * getSingleByName
	 * 
	 * Retrieves a fedora object by the pid
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		26/04/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @param name The pid of the object to return
	 * @return The Object returned from the query.
	 */
	public FedoraObject getSingleByName(String name) {
		EntityManager entityManager = PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
		FedoraObject fedoraObject = null;
		try {
			if(entityManager == null) {
				LOGGER.info("Entitty Manager is null");
			}
			Query query = entityManager.createQuery("from FedoraObject where object_id = :pid");
			query.setParameter("pid", name);
			fedoraObject = (FedoraObject) query.getSingleResult();
		}
		catch (NoResultException e) {
			LOGGER.warn("No entity found for pid {}", name);
		}
		finally {
			entityManager.close();
		}
		return fedoraObject;
	}
}
