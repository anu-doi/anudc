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

import au.edu.anu.datacommons.collectionrequest.CollectionRequest;
import au.edu.anu.datacommons.data.db.PersistenceManager;
import au.edu.anu.datacommons.data.db.model.Groups;

/**
 * CollectionRequestDAOImpl
 * 
 * Australian National University Data Commons
 * 
 * Implementation class for the CollectionRequestDAO
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		29/06/2012	Genevieve Turner (GT)	Initial
 * 0.2		19/07/2012	Genevieve Turner (GT)	Fixed an issue where if the user doesn't belong to any groups multiple rows are shown for each status
 * </pre>
 *
 */
public class CollectionRequestDAOImpl extends GenericDAOImpl<CollectionRequest, Long> implements CollectionRequestDAO {
	static final Logger LOGGER = LoggerFactory.getLogger(CollectionRequestDAOImpl.class);
	
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
	public CollectionRequestDAOImpl(Class<CollectionRequest> type) {
		super(type);
	}
	
	/**
	 * getPermittedRequests
	 * 
	 * Retrieves a list of requests that the user has permission to view.
	 * i.e. it retrieves the users own requests, and if they are a reviewere for any
	 * of the groups they are able to view those requests.
	 *
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		29/06/2012	Genevieve Turner(GT)	Initial
	 * 0.2		19/07/2012	Genevieve Turner (GT)	Fixed an issue where if the user doesn't belong to any groups multiple rows are shown for each status
	 * </pre>
	 * 
	 * @param userId
	 * @param groups
	 * @return
	 * @see au.edu.anu.datacommons.data.db.dao.CollectionRequestDAO#getPermittedRequests(java.lang.Long, java.util.List)
	 */
	public List<CollectionRequest> getPermittedRequests(Long userId, List<Groups> groups) {
		EntityManager entityManager = PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
		Query query = null;
		
		if (groups != null && groups.size() > 0) {
			List<Long> groupIds = new ArrayList<Long>();
			for (Groups group : groups) {
				groupIds.add(group.getId());
			}

			query = entityManager.createQuery("SELECT DISTINCT cr FROM CollectionRequest cr join cr.fedoraObject fo left join fetch cr.status WHERE (cr.requestor.id = :user_id) OR (fo.group_id IN (:groups)) ORDER BY cr.timestamp DESC",CollectionRequest.class);
			query.setParameter("user_id", userId);
			query.setParameter("groups", groupIds);
		}
		else {
			query = entityManager.createQuery("SELECT DISTINCT cr FROM CollectionRequest cr left join fetch cr.status WHERE cr.requestor.id = :user_id ORDER BY cr.timestamp DESC",CollectionRequest.class);
			query.setParameter("user_id", userId);
		}
		
		List<CollectionRequest> collectionRequests = query.getResultList();
		
		entityManager.close();
		
		return collectionRequests;
	}
	
	/**
	 * getSingleByIdEager
	 * 
	 * Eagerly retrieves the class (and sub classes) of an object by the id
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		29/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param id The id of the object ot retrieve
	 * @return The collection request associated with the id
	 * @see au.edu.anu.datacommons.data.db.dao.CollectionRequestDAO#getSingleByIdEager(java.lang.Long)
	 */
	public CollectionRequest getSingleByIdEager(Long id) {
		EntityManager entityManager = PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
		Query query = entityManager.createQuery("SELECT cr FROM CollectionRequest cr left join fetch cr.answers left join fetch cr.items left join fetch cr.status join fetch cr.fedoraObject WHERE cr.id = :id");
		query.setParameter("id", id);
		
		CollectionRequest collectionRequest = (CollectionRequest) query.getSingleResult();
		
		entityManager.close();
		return collectionRequest;
	}
}
