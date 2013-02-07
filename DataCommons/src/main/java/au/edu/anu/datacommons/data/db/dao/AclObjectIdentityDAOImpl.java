/*******************************************************************************
 * Australian National University Data Commons
 * Copyright (C) 2013  The Australian National University
 * 
 * This file is part of Australian National University Data Commons.
 * 
 * Australian National University Data Commons is free software: you
 * can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

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
 * 0.2		03/05/2012	Genevieve Turner (GT)	Updated to include constructor class that sets the type in GenericDAOImpl
 * </pre>
 * 
 * @param <AclObjectIdentity> The object type to implement
 * @param <Long> The primary key type to implement
 */
public class AclObjectIdentityDAOImpl<T, PK> extends GenericDAOImpl implements AclObjectIdentityDAO {
	static final Logger LOGGER = LoggerFactory.getLogger(AclObjectIdentityDAOImpl.class);

	/**
	 * Constructor
	 * 
	 * Constructor class that includes the type
	 * 
	 * @param type The class type to retrive/set objects
	 */
	public AclObjectIdentityDAOImpl(Class<T> type) {
		super(type);
	}
	
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
	public T getObjectByClassAndIdentity(java.lang.Long aclClass, java.lang.Long aclIdentity) {
		EntityManager entityManager = PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
		T aclObjectIdentity = null;
		try {
			Query query = entityManager.createQuery("from AclObjectIdentity where object_id_class = :aclClass and object_id_identity = :aclIdentity");
			query.setParameter("aclClass", aclClass);
			query.setParameter("aclIdentity", aclIdentity);
			aclObjectIdentity = (T) query.getSingleResult();
		}
		finally {
			entityManager.close();
		}
		return aclObjectIdentity;
	}
}
