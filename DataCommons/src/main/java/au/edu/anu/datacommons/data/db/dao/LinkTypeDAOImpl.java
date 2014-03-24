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
import javax.persistence.NoResultException;
import javax.persistence.Query;

import au.edu.anu.datacommons.data.db.PersistenceManager;
import au.edu.anu.datacommons.data.db.model.LinkType;

/**
 * LinkTypeDAOImpl
 * 
 * Australian National University Data Commons
 * 
 * Implementation DAO for the link_type table
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		28/09/2012	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
public class LinkTypeDAOImpl extends GenericDAOImpl<LinkType, Long> implements
		LinkTypeDAO {
	/**
	 * Constructor
	 * 
	 * Constructor class that includes the type
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		28/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 */
	public LinkTypeDAOImpl() {
		super(LinkType.class);
	}
	
	/**
	 * getByCode
	 * 
	 * Get the link type by the code
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		28/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param code The code to retrieve
	 * @return The link type associated with the code
	 * @see au.edu.anu.datacommons.data.db.dao.LinkTypeDAO#getByCode(java.lang.String)
	 */
	@Override
	public LinkType getByCode(String code) {
		EntityManager entityManager = PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
		LinkType linkType = null;
		try {
			Query query = entityManager.createQuery("from LinkType lt where lt.code = :code");
			query.setParameter("code", code);
			
			linkType = (LinkType) query.getSingleResult();
		}
		catch (NoResultException e) {
			LOGGER.debug("No entity found for {}", code);
		}
		finally {
			entityManager.close();
		}
		
		return linkType;
	}

}
