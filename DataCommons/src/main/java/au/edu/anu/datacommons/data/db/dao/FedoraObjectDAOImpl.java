package au.edu.anu.datacommons.data.db.dao;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.data.db.PersistenceManager;
import au.edu.anu.datacommons.data.db.model.FedoraObject;

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
 * 0.2		03/05/2012	Genevieve Turner (GT)	Updated to include constructor class that sets the type in GenericDAOImpl
 * </pre>
 * 
 */
public class FedoraObjectDAOImpl extends GenericDAOImpl<FedoraObject, Long> implements FedoraObjectDAO
{
	static final Logger LOGGER = LoggerFactory.getLogger(FedoraObjectDAOImpl.class);
	
	/**
	 * Constructor
	 * 
	 * Constructor class that includes the type
	 * 
	 * @param type The class type to retrive/set objects
	 */
	public FedoraObjectDAOImpl(Class<FedoraObject> type) {
		super(type);
	}
	
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
