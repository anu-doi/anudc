package au.edu.anu.datacommons.data.db.dao;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.data.db.PersistenceManager;

/**
 * AclObjectIdentityDAOImpl
 * 
 * Australian National University Data Commons
 * 
 * AclObjectIdentity data access implementation
 * 
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		03/05/2012	Genevieve Turner (GT)	Initial
 * </pre>
 * 
 * @param <AclObjectIdentity> The object type to implement
 * @param <Long> The primary key type to implement
 */
public class AclObjectIdentityDAOImpl<AclObjectIdentity, Long> extends GenericDAOImpl implements AclObjectIdentityDAO {
	static final Logger LOGGER = LoggerFactory.getLogger(AclObjectIdentityDAOImpl.class);

	/**
	 * getObjectByClassAndIdentity
	 * 
	 * Retreives an object based on the provided class, and identity values
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/05/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @param aclClass The id of the class to retrieve
	 * @param aclIdentity The id of the object to retrieve a row for
	 * @return Returns the retrieved object
	 */
	public AclObjectIdentity getObjectByClassAndIdentity(java.lang.Long aclClass, java.lang.Long aclIdentity) {
		EntityManager entityManager = PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
		AclObjectIdentity aclObjectIdentity = null;
		try {
			if(entityManager == null) {
				LOGGER.info("Entitty Manager is null");
			}
			Query query = entityManager.createQuery("from AclObjectIdentity where object_id_class = :aclClass and object_id_identity = :aclIdentity");
			query.setParameter("aclClass", aclClass);
			query.setParameter("aclIdentity", aclIdentity);
			aclObjectIdentity = (AclObjectIdentity) query.getSingleResult();
		}
		finally {
			entityManager.close();
		}
		return aclObjectIdentity;
	}
}
