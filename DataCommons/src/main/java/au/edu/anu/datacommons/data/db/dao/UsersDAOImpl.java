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
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.data.db.PersistenceManager;
import au.edu.anu.datacommons.data.db.model.Users;

/**
 * UsersDAOImpl
 * 
 * Australian National University Data Commons
 * 
 * Class to perform actions with users in the database
 * 
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		16/05/2012	Genevieve Turner (GT)	Initial
 * </pre>
 * 
 */
public class UsersDAOImpl extends GenericDAOImpl<Users, Long> implements UsersDAO {
	static final Logger LOGGER = LoggerFactory.getLogger(UsersDAOImpl.class);
	
	/**
	 * Constructor
	 * 
	 * Constructor class that includes the type
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		16/05/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 */
	public UsersDAOImpl() {
		super(Users.class);
	}
	
	/**
	 * getUserByName
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		16/05/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @param The username of the object to return
	 * @return The object with the username
	 */
	@Override
	public Users getUserByName(String username) {
		EntityManager entityManager = PersistenceManager.getEntityManagerFactory().createEntityManager();
		
		Users users = null;
		try {
			Query query = entityManager.createQuery("from Users where username = :username");
			query.setParameter("username", username);
			users = (Users) query.getSingleResult();
		}
		catch (NoResultException e) {
		}
		finally {
			entityManager.close();
		}
		return users;
	}
	
	public List<Users> findRegisteredUsers(String givenName, String surname, String email) {
		EntityManager entityManager = PersistenceManager.getEntityManagerFactory().createEntityManager();
		
		StringBuilder queryString = new StringBuilder();
		queryString.append("select u From Users u join u.user_registered ur where ");
		boolean addAnd = false;
		if (givenName != null && givenName.trim().length() > 0) {
			queryString.append("lower(ur.given_name) = :givenName ");
			addAnd = true;
		}
		if (surname != null && surname.trim().length() > 0) {
			if (addAnd) {
				queryString.append("AND ");
			}
			queryString.append("lower(ur.last_name) = :familyName ");
			addAnd = true;
		}
		if (email != null && email.trim().length() > 0) {
			if (addAnd) {
				queryString.append("AND ");
			}
			queryString.append("lower(u.username) = :email");
		}
		LOGGER.info("Query is: {}", queryString.toString());
		List<Users> users = null;
		try {
			Query query = entityManager.createQuery(queryString.toString());
			if (givenName != null && givenName.trim().length() > 0) {
				query.setParameter("givenName", givenName.toLowerCase());
			}
			if (surname != null && surname.trim().length() > 0) {
				query.setParameter("familyName", surname.toLowerCase());
			}
			if (email != null && email.trim().length() > 0) {
				query.setParameter("email", email.toLowerCase());
			}
			
			users = (List<Users>) query.getResultList();
		}
		catch (NoResultException e) {
			
		}
		finally {
			entityManager.close();
		}
		
		return users;
	}

}
