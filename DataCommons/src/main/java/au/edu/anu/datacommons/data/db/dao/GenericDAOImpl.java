package au.edu.anu.datacommons.data.db.dao;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

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
	
	private Class<T> type;
	/*
	public GenericDAOImpl(Class<T> type) {
		this.type = type;
	}
	*/
	
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
		LOGGER.info("Creating object");
		EntityManager entityManager = PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
		EntityTransaction entityTransaction = entityManager.getTransaction();
		try {
			//entityManager.
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
			object = entityManager.find(type, id);
		}
		finally {
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
		//T object = null;
		List<T> objects = null;
		try {
			objects = entityManager.createQuery("from " + type.getName()).getResultList();
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
