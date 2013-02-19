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

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import au.edu.anu.datacommons.data.db.PersistenceManager;
import au.edu.anu.datacommons.data.db.model.LinkRelation;

/**
 * LinkRelationDAOImpl
 * 
 * Australian National University Data Commons
 * 
 * Implementation of retrieving link relations
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		19/09/2012	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
public class LinkRelationDAOImpl extends GenericDAOImpl<LinkRelation, Long> implements
		LinkRelationDAO {

	/**
	 * Constructor
	 * 
	 * Placeholder
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		19/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param type The class type to retrieve/set objects
	 */
	public LinkRelationDAOImpl(Class<LinkRelation> type) {
		super(type);
	}

	/**
	 * getRelations
	 * 
	 * Retrieves the relations for the categories
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		19/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param category1 The type of item to retrieve relations for
	 * @param category2 The type of item to relate to
	 * @return A list of relation links
	 * @see au.edu.anu.datacommons.data.db.dao.LinkRelationDAO#getRelations(java.lang.String, java.lang.String)
	 */
	public List<LinkRelation> getRelations(String category1, String category2) {
		EntityManager entityManager = PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
		List<LinkRelation> linkRelations = null;
		try {
			//Query query = entityManager.createQuery("from LinkRelation lr where lr.id.category1 = :category1 and lr.id.category2 = :category2");
			Query query = entityManager.createQuery("from LinkRelation lr where lower(lr.id.category1) = :category1 and lower(lr.id.category2) = :category2");
			query.setParameter("category1", category1.toLowerCase());
			query.setParameter("category2", category2.toLowerCase());
			
			linkRelations = query.getResultList();
		}
		catch (NoResultException e) {
			LOGGER.debug("No entity found for {}, {}", category1, category2);
		}
		finally {
			entityManager.close();
		}
		
		return linkRelations;
	}
}
