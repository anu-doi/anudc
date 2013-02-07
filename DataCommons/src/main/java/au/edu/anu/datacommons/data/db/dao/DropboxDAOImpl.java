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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.collectionrequest.CollectionDropbox;
import au.edu.anu.datacommons.data.db.PersistenceManager;
import au.edu.anu.datacommons.data.db.model.Groups;
import au.edu.anu.datacommons.data.db.model.Users;

/**
 * DropboxDAOImpl
 * 
 * Australian National University Data Commons
 * 
 * Implementation class for the DropboxDAO
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		29/06/2012	Genevieve Turner (GT)	Initial
 * 0.2		19/07/2012	Genevieve Turner (GT)	Added a function to get dropboxes for the specified user
 * </pre>
 *
 */
public class DropboxDAOImpl extends GenericDAOImpl<CollectionDropbox, Long> implements DropboxDAO {
	static final Logger LOGGER = LoggerFactory.getLogger(DropboxDAOImpl.class);

	/**
	 * Constructor
	 * 
	 * Constructor class that includes the type
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		29/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param type The class type to retrieve/set objects
	 */
	public DropboxDAOImpl(Class<CollectionDropbox> type) {
		super(type);
	}
	
	/**
	 * getPermittedRequests
	 *
	 * Gets a list of collection dropbox that the user is permitted to view 
	 * (determined by the listed groups)
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		29/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param groups A list of groups for which the user has permissions to view the dropbox
	 * @return A list of CollectionDropboxes based on the 
	 * @see au.edu.anu.datacommons.data.db.dao.DropboxDAO#getPermittedRequests(java.util.List)
	 */
	public List<CollectionDropbox> getPermittedRequests(List<Groups> groups) {
		EntityManager entityManager = PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
		Query query = null;
		
		List<Long> groupIds = new ArrayList<Long>();
		for (Groups group : groups) {
			groupIds.add(group.getId());
			LOGGER.info("Review group: {}", group.getId());
		}

		query = entityManager.createQuery("SELECT cd FROM CollectionDropbox cd left join cd.collectionRequest cr left join cr.fedoraObject fo WHERE fo.group_id in (:groups)",CollectionDropbox.class);
		query.setParameter("groups", groupIds);
		
		List<CollectionDropbox> collectionRequests = query.getResultList();
		
		entityManager.close();
		return collectionRequests;
	}
	
	/**
	 * getSingleByIdEager
	 *
	 * Retrieves the CollectionDropbox eagerly.
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		29/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param id The id of the dropbox to retreive
	 * @return The collection dropbox with the given id
	 * @see au.edu.anu.datacommons.data.db.dao.DropboxDAO#getSingleByIdEager(java.lang.Long)
	 */
	public CollectionDropbox getSingleByIdEager(Long id) {
		EntityManager entityManager = PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
		
		Query query = entityManager.createQuery("SELECT cd FROM CollectionDropbox cd WHERE cd.id = :id");
		query.setParameter("id", id);

		CollectionDropbox collectionDropbox = (CollectionDropbox) query.getSingleResult();
		entityManager.close();
		return collectionDropbox;
	}

	/**
	 * getSingleByAccessCode
	 *
	 * Retrieves the CollectionDropbox by the access code
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		29/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param accessCode The access code of the dropbox that is being retrieved
	 * @return The collection dropbox
	 * @see au.edu.anu.datacommons.data.db.dao.DropboxDAO#getSingleByAccessCode(java.lang.Long)
	 */
	public CollectionDropbox getSingleByAccessCode(Long accessCode) {
		EntityManager entityManager = PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
		
		Query query = entityManager.createQuery("FROM CollectionDropbox cd left join fetch cd.collectionRequest cr left join fetch cr.items ci left join fetch cd.dropboxAccessLog cl WHERE cd.accessCode=:accessCode");
		query.setParameter("accessCode", accessCode);
		
		CollectionDropbox collectionDropbox = (CollectionDropbox) query.getSingleResult();
		
		entityManager.close();
		
		return collectionDropbox;
	}
	
	/**
	 * getUserDropboxes
	 * 
	 * Gets a list of dropboxes for the given user
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.2		19/07/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param user The user to retrieve dropboxes for
	 * @return A list of dropboxes for the given user
	 * @see au.edu.anu.datacommons.data.db.dao.DropboxDAO#getUserDropboxes(au.edu.anu.datacommons.data.db.model.Users)
	 */
	public List<CollectionDropbox> getUserDropboxes(Users user) {
		
		EntityManager entityManager = PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
		Query query = null;
		
		query = entityManager.createQuery("SELECT cd FROM CollectionDropbox cd join cd.collectionRequest cr WHERE cr.requestor = :user",CollectionDropbox.class);
		query.setParameter("user", user);
		
		List<CollectionDropbox> collectionDropboxes = query.getResultList();
		
		entityManager.close();
		return collectionDropboxes;
	}
}
