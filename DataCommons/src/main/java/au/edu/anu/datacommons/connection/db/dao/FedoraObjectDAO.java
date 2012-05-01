package au.edu.anu.datacommons.connection.db.dao;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import au.edu.anu.datacommons.connection.db.GenericDAO;
import au.edu.anu.datacommons.connection.db.model.FedoraObject;

/**
 * FedoraObjectDAO
 * 
 * Australian National University Data Comons
 * 
 * Class to perform actions with fedora objects in the database
 * 
 * JUnit Coverage:
 * None
 * 
 * Version	Date		Developer				Description
 * 0.1		26/04/2012	Genevieve Turner (GT)	Initial
 * 
 */
@Repository
public class FedoraObjectDAO implements GenericDAO
{
	static final Logger LOGGER = LoggerFactory.getLogger(FedoraObjectDAO.class);
	
	private EntityManagerFactory entityManagerFactory;
	private EntityManager entityManager;
	

	/**
	 * Constructor
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		26/04/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * Creates the database connections for this implementation.
	 */
	public FedoraObjectDAO() {
		entityManagerFactory = Persistence.createEntityManagerFactory("datacommons");
		entityManager = entityManagerFactory.createEntityManager();
	}
	/**
	 * finalize
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		26/04/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * Closes the database connections for this implementation.
	 */
	public void finalize() {
		entityManager.close();
		entityManagerFactory.close();
	}
	
	/**
	 * getSingleByName
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		26/04/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * 
	 * @param name The pid of the object
	 * @return The Object returned from the query.
	 */
	public FedoraObject getSingleByName(String name) {
		if(entityManager == null) {
			LOGGER.info("Entitty Manager is null");
		}
		Query query = entityManager.createQuery("from FedoraObject where object_id = :pid", FedoraObject.class);
		query.setParameter("pid", name);
		FedoraObject fedoraObject = (FedoraObject) query.getSingleResult();
		entityManager.close();
		return fedoraObject;
	}
}
