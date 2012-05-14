package au.edu.anu.datacommons.data.db.dao;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.data.db.PersistenceManager;

/**
 * GenericDAOImpl
 * 
 * Australian National University Data Commons
 * 
 * Generic Data Access Object implementation
 * 
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		03/05/2012	Genevieve Turner (GT)	Initial
 * </pre>
 * 
 * @param <T> The object type to implement
 * @param <PK> The primary key type to implement
 */
public class GenericDAOImpl<T, PK extends Serializable> implements GenericDAO<T, PK> {
	static final Logger LOGGER = LoggerFactory.getLogger(GenericDAOImpl.class);
	
	private Class<T> type_;
	
	/**
	 * Constructor
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.2		09/05/2012	Genevieve Turner (GT)	Included so the actual type can be referenced
	 * </pre>
	 * 
	 * @param type The class type to retrive/set objects
	 */
	public GenericDAOImpl(Class<T> type) {
		// Attempting to have a default constructor that uses ParameterizedType causes errors
		// So we need to have a constructor that sets the class type
		this.type_ = type;
	}
	
	/**
	 * create
	 * 
	 * Creates the object in the database
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/05/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @param t The object to create in the database
	 * @return
	 */
	public T create(T o) {
		EntityManager entityManager = PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
		EntityTransaction entityTransaction = entityManager.getTransaction();
		try {
			entityTransaction.begin();
			entityManager.persist(o);
			entityTransaction.commit();
		}
		catch (Exception e) {
			LOGGER.info("Error saving object", e);
		}
		finally {
			entityManager.close();
		}
		return o;
	}

	/**
	 * getSingleById
	 * 
	 * Retrieves the object by the primary key
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/05/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @param id The primary key of the object to retrieve
	 * @return
	 */
	public T getSingleById(PK id) {
		EntityManager entityManager = PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
		T object = null;
		try {
			object = (T) entityManager.find(type_, id);
		}
		finally {
			LOGGER.info("EntityManager close has been run");
			entityManager.close();
		}
		return object;
	}

	/**
	 * getAll
	 * 
	 * Retrieves all objects of the approriate type
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/05/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @return A list of objects of the appropriate type
	 */
	public List<T> getAll() {
		EntityManager entityManager = PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
		List<T> objects = null;
		try {
			objects = entityManager.createQuery("from " + type_.getName()).getResultList();
		}
		finally {
			entityManager.close();
		}
		return objects;
	}

	/**
	 * update
	 * 
	 * Updates the object in the database
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/05/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @param t The object to update
	 * @return
	 */
	public T update(T o) {
		EntityManager entityManager = PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
		EntityTransaction entityTransaction = entityManager.getTransaction();
		try {
			entityTransaction.begin();
			entityManager.persist(o);
			entityTransaction.commit();
		}
		finally {
			entityManager.close();
		}
		return o;
	}

	/**
	 * delete
	 * 
	 * Removes objects from the database
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/05/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @param t The object to delete
	 */
	public void delete (T o) {
		EntityManager entityManager = PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
		EntityTransaction entityTransaction = entityManager.getTransaction();
		try {
			entityTransaction.begin();
			entityManager.remove(o);
			entityTransaction.commit();
		}
		finally {
			entityManager.close();
		}
	}
}
