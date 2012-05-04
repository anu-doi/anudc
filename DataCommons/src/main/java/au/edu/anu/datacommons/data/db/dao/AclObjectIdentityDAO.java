package au.edu.anu.datacommons.data.db.dao;

/**
 * AclObjectIdentityDAO
 * 
 * Australian National University Data Commons
 * 
 * AclObjectIdentity interface extension
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
public interface AclObjectIdentityDAO<T, PK> extends GenericDAO {
	
	/**
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
	public T getObjectByClassAndIdentity(Long aclClass, Long aclIdentity);
}
