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
import au.edu.anu.datacommons.data.db.model.UserRequestPassword;

/**
 * UserRequestPasswordDAOImpl
 * 
 * Australian National University Data Commons
 * 
 * Implementation class to perform actions with the user password request table
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		27/08/2012	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
public class UserRequestPasswordDAOImpl extends GenericDAOImpl<UserRequestPassword, Long>
		implements UserRequestPasswordDAO {

	/**
	 * Constructor
	 * 
	 * Constructor class that includes the type
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		27/08/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 */
	public UserRequestPasswordDAOImpl() {
		super(UserRequestPassword.class);
	}
	
	/**
	 * getByLink
	 * 
	 * Gets a UserRequestPassword given the specified link
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		27/08/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param link The link to retrieve the UserRequestPassword for
	 * @return The password change request information
	 * @see au.edu.anu.datacommons.data.db.dao.UserRequestPasswordDAO#getByLink(java.lang.String)
	 */
	public UserRequestPassword getByLink(String link) {
		EntityManager entityManager = PersistenceManager.getEntityManagerFactory().createEntityManager();
		UserRequestPassword userRequest = null;
		try {
			Query query = entityManager.createQuery("from UserRequestPassword urp1 where link_id = :link_id and request_date = (select max(urp2.request_date)" +
																				" from UserRequestPassword urp2" +
																				" where urp1.user = urp2.user)");
			query.setParameter("link_id", link);
			userRequest = (UserRequestPassword) query.getSingleResult();
		}
		catch (NoResultException e) {
			LOGGER.info("No User request found");
		}
		finally {
			entityManager.close();
		}
		return userRequest;
	}
}
