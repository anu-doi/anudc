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
import javax.persistence.Query;

import au.edu.anu.datacommons.data.db.PersistenceManager;
import au.edu.anu.datacommons.data.db.model.ExternalLinkPattern;

/**
 * ExternalLinkDAOImpl
 * 
 * Australian National University Data Commons
 * 
 * Data Access Object Implementation for the ExternalLinkPattern class
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		20/11/2012	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
public class ExternalLinkDAOImpl extends GenericDAOImpl<ExternalLinkPattern, Long> implements
		ExternalLinkDAO {
	/**
	 * Constructor
	 * 
	 * Constructor class that includes the type
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		20/11/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 */
	public ExternalLinkDAOImpl() {
		super(ExternalLinkPattern.class);
	}
	
	/**
	 * getByObjectType
	 * 
	 * Retrieve the patterns associated with the given object type
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		20/11/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param objectType The object type
	 * @return The patterns associated with the object type
	 * @see au.edu.anu.datacommons.data.db.dao.ExternalLinkDAO#getByObjectType(java.lang.String)
	 */
	public List<ExternalLinkPattern> getByObjectType(String objectType) {
		EntityManager entityManager = PersistenceManager.getEntityManagerFactory().createEntityManager();
		List<ExternalLinkPattern> patterns = null;
		
		try {
			Query query = entityManager.createQuery("from ExternalLinkPattern where object_type = :value");
			query.setParameter("value", objectType);
			
			patterns = query.getResultList();
		}
		finally {
			entityManager.close();
		}
		
		return patterns;
	}
}
