package au.edu.anu.datacommons.data.db.dao;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import au.edu.anu.datacommons.data.db.PersistenceManager;
import au.edu.anu.datacommons.data.db.model.AclSid;

/**
 * AclSidDAOImpl
 * 
 * Australian National University Data Commons
 * 
 * Data Access Object implentation for the acl_sid table
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		28/08/2012	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
public class AclSidDAOImpl extends GenericDAOImpl<AclSid, Long> implements AclSidDAO {
	/**
	 * Constructor
	 * 
	 * Placeholder
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		28/08/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param type
	 */
	public AclSidDAOImpl(Class<AclSid> type) {
		super(type);
	}
	
	/**
	 * getAclSidByUsername
	 * 
	 * Retreive the acl sid row by the username
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		28/08/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param username Username to retrieve acl sid for
	 * @return The acl sid
	 * @see au.edu.anu.datacommons.data.db.dao.AclSidDAO#getAclSidByUsername(java.lang.String)
	 */
	@Override
	public AclSid getAclSidByUsername(String username) {
		EntityManager entityManager = PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
		
		AclSid aclSid = null;
		try {
			Query query = entityManager.createQuery("from AclSid where sid = :username");
			query.setParameter("username", username);
			aclSid = (AclSid) query.getSingleResult();
		}
		catch (NoResultException e) {
			LOGGER.warn("No entity found for username {}", username);
		}
		finally {
			entityManager.close();
		}
		return aclSid;
	}

}
