package au.edu.anu.datacommons.data.db;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PersistenceManager
 * 
 * Australian National University Data Commons
 * 
 * A manager class that creates and closes an EntityManagerFactory that can then be used throughout
 * the application
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
public class PersistenceManager {
	static final Logger LOGGER = LoggerFactory.getLogger(PersistenceManager.class);
	
	private static final PersistenceManager singleton_ = new PersistenceManager();
	
	protected EntityManagerFactory emf;
	
	/**
	 * getInstance
	 * 
	 * Returns an instance of the object
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/05/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @return Returns the PersistenceManager
	 */
	public static PersistenceManager getInstance() {
		return singleton_;
	}
	
	/**
	 * Constructor class
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/05/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 */
	private PersistenceManager() {
		
	}
	
	/**
	 * getEntityManagerFactory
	 * 
	 * Returns the entity manager factory.
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/05/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @return
	 */
	public EntityManagerFactory getEntityManagerFactory() {
		if (emf == null) {
			createEntityManagerFactory();
		}
		return emf;
	}
	
	/**
	 * closeEntityManagerFactory
	 * 
	 * Closes the entity maanger factory
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/05/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 */
	public void closeEntityManagerFactory() {
		if (emf != null) {
			emf.close();
			emf = null;
			LOGGER.info("Persistence finished at " + new java.util.Date());
		}
	}
	
	/**
	 * createEntityManagerFactory
	 * 
	 * Creates the entity manager factory
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/05/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 */
	protected void createEntityManagerFactory() {
		this.emf = Persistence.createEntityManagerFactory("datacommons");
		LOGGER.info("Persistence started at " + new java.util.Date());
	}
}
