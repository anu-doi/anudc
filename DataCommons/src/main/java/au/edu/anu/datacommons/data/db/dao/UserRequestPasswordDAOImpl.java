package au.edu.anu.datacommons.data.db.dao;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import au.edu.anu.datacommons.data.db.PersistenceManager;
import au.edu.anu.datacommons.data.db.model.UserRequestPassword;

/**
 * UserRequestPasswordDAOImpl
 * 
 * Australian National University Data Commons
 * 
 * Implementation class to perform actions with the user password request table
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		27/08/2012	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
public class UserRequestPasswordDAOImpl extends GenericDAOImpl<UserRequestPassword, Long>
		implements UserRequestPasswordDAO {

	/**
	 * Constructor
	 * 
	 * Constructor class that includes the type
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		27/08/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param type The class type to retrieve/set objects
	 */
	public UserRequestPasswordDAOImpl(Class<UserRequestPassword> type) {
		super(type);
	}
	
	/**
	 * getByLink
	 * 
	 * Gets a UserRequestPassword given the specified link
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		27/08/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param link The link to retrieve the UserRequestPassword for
	 * @return The password change request information
	 * @see au.edu.anu.datacommons.data.db.dao.UserRequestPasswordDAO#getByLink(java.lang.String)
	 */
	public UserRequestPassword getByLink(String link) {
		EntityManager entityManager = PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
		UserRequestPassword userRequest = null;
		try {
			Query query = entityManager.createQuery("from UserRequestPassword urp1 where link_id = :link_id and request_date = (select max(urp2.request_date)" +
																				" from UserRequestPassword urp2" +
																				" where urp1.user = urp2.user)");
			query.setParameter("link_id", link);
			userRequest = (UserRequestPassword) query.getSingleResult();
		}
		catch (NoResultException e) {
			LOGGER.info("No User request found");
		}
		finally {
			entityManager.close();
		}
		return userRequest;
	}
}
