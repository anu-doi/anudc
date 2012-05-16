package au.edu.anu.datacommons.data.db.dao;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import javax.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.data.db.PersistenceManager;
import au.edu.anu.datacommons.data.db.model.Users;

/**
 * UsersDAOImpl
 * 
 * Australian National University Data Commons
 * 
 * Class to perform actions with users in the database
 * 
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		16/05/2012	Genevieve Turner (GT)	Initial
 * </pre>
 * 
 */
public class UsersDAOImpl extends GenericDAOImpl<Users, Long> implements UsersDAO {
	static final Logger LOGGER = LoggerFactory.getLogger(UsersDAOImpl.class);
	
	/**
	 * Constructor
	 * 
	 * Constructor class that includes the type
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		16/05/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @param type The class type to retrive/set objects
	 */
	public UsersDAOImpl(Class<Users> type) {
		super(type);
	}
	
	/**
	 * getUserByName
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		16/05/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @param The username of the object to return
	 * @return The object with the username
	 */
	@Override
	public Users getUserByName(String username) {
		EntityManager entityManager = PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
		
		Users users = null;
		try {
			Query query = entityManager.createQuery("from Users where username = :username");
			query.setParameter("username", username);
			users = (Users) query.getSingleResult();
		}
		catch (NoResultException e) {
			LOGGER.warn("No entity found for pid {}", username);
		}
		finally {
			entityManager.close();
		}
		return users;
	}

}
