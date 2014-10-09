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
import au.edu.anu.datacommons.data.db.model.PublishLocation;

/**
 * PublishLocationDAOImpl
 * 
 * Australian National University Data Commons
 * 
 * Placeholder
 *
 * JUnit Coverage:
 * PublishLocationDAOTest
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		28/03/2013	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
public class PublishLocationDAOImpl extends GenericDAOImpl<PublishLocation, Long> implements
		PublishLocationDAO {
	/**
	 * Constructor
	 * 
	 * Constructor class that includes the type
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		29/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 */
	public PublishLocationDAOImpl() {
		super(PublishLocation.class);
	}

	/**
	 * getByCode
	 * 
	 * Gets the Publish Location by with the given code
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/04/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param code The code to retrieve the publish location for
	 * @return The publish location
	 * @see au.edu.anu.datacommons.data.db.dao.PublishLocationDAO#getByCode(java.lang.String)
	 */
	@Override
	public PublishLocation getByCode(String code) {
		EntityManager entityManager = PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
		PublishLocation location = null;
		try {
			Query query = entityManager.createQuery("from PublishLocation where code = :code");
			query.setParameter("code", code);
			
			location = (PublishLocation) query.getSingleResult();
		}
		catch (NoResultException e) {
			LOGGER.warn("No publish location found");
		}
		finally {
			entityManager.close();
		}
		return location;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PublishLocation> getAllWithTemplates() {
		EntityManager entityManager = PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
		List<PublishLocation> objects = null;
		try {
			objects = entityManager.createQuery("SELECT DISTINCT pl from PublishLocation pl join fetch pl.templates").getResultList();
		}
		finally {
			entityManager.close();
		}
		return objects;
	}
}
