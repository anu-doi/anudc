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
import au.edu.anu.datacommons.data.db.model.SelectCode;
import au.edu.anu.datacommons.data.db.model.SelectCodePK;

/**
 * SelectCodeDAOImpl
 * 
 * Australian National University Data Commons
 * 
 * Placeholder
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		22/06/2012	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
public class SelectCodeDAOImpl extends GenericDAOImpl<SelectCode, SelectCodePK> implements
		SelectCodeDAO {
	
	/**
	 * Constructor
	 * 
	 * Constructor class that includes the type
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		22/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 */
	public SelectCodeDAOImpl() {
		super(SelectCode.class);
	}
	
	/**
	 * getOptionsByNames
	 * 
	 * Retireves lists of select codes given the field names
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		22/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param names A list of select field names to retrieve
	 * @return A list of select codes given the field names
	 * @see au.edu.anu.datacommons.data.db.dao.SelectCodeDAO#getOptionsByNames(java.util.List)
	 */
	@Override
	public List<SelectCode> getOptionsByNames(List<String> names) {
		// TODO Auto-generated method stub
		EntityManager entityManager = PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
		List<SelectCode> selectCodes = null;
		try {
			Query query = entityManager.createQuery("from SelectCode where select_name in (:names)");
			query.setParameter("names", names);
			selectCodes = query.getResultList();
		}
		catch (NoResultException e) {
			LOGGER.warn("No options found");
		}
		finally {
			entityManager.close();
		}
		
		return selectCodes;
	}
}
