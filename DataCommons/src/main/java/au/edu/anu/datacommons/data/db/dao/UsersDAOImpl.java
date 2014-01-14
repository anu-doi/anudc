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

import org.junit.Ignore;
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
 * UsersDAOTest
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
	 * 
	 * @param type The class type to retrive/set objects
	 */
	public UsersDAOImpl(Class<Users> type) {
		super(type);
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
		EntityManager entityManager = PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
		
		Users users = null;
		try {
			Query query = entityManager.createQuery("from Users where username = :username");
			query.setParameter("username", username);
			users = (Users) query.getSingleResult();
		}
		catch (NoResultException e) {
			LOGGER.warn("No entity found for username {}", username);
		}
		finally {
			entityManager.close();
		}
		return users;
	}
	
	public List<Users> findUsers(String givenName, String surname, String email) {
		EntityManager entityManager = PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
		if ((givenName == null || givenName.length() == 0)
				&& (surname == null || surname.length() == 0)
				&& (email == null || email.length() == 0)) {
			return null;
		}
		
		try {
			String queryStr = generateFindQueryString(givenName, surname, email);
			LOGGER.info("Query String: {}", queryStr);
			Query query = entityManager.createQuery(queryStr);
			setParameters(query, givenName, surname, email);
			
			@SuppressWarnings("unchecked")
			List<Users> shibbolethUsers = (List<Users>) query.getResultList();
			return shibbolethUsers;
		}
		catch (NoResultException e) {
			LOGGER.warn("No users found for: Given Name ({},,Surname - ({}), Email ({})", givenName, surname, email);
		}
		finally {
			entityManager.close();
		}
		
		return null;
	}
	
	private String generateFindQueryString(String givenName, String surname, String email) {
		StringBuilder sb = new StringBuilder();
		boolean hasPreviousFilter = false;
		
		sb.append("SELECT user FROM Users user LEFT JOIN user.userExtra userInfo WHERE ");
		if (givenName != null && givenName.length() > 0) {
			if (hasPreviousFilter) {
				sb.append("AND ");
			}
			sb.append("(lower(userInfo.given_name) like :givenName OR lower(userInfo.displayName) like :givenName) ");
			hasPreviousFilter = true;
		}
		if (surname != null && surname.length() > 0) {
			if (hasPreviousFilter) {
				sb.append("AND ");
			}
			sb.append("(lower(userInfo.last_name) like :surname OR lower(userInfo.displayName) like :surname) ");
			hasPreviousFilter = true;
		}
		if(email != null && email.length() > 0) {
			if (hasPreviousFilter) {
				sb.append("AND ");
			}
			sb.append("(lower(user.username) like :email OR lower(userInfo.email) like :email) ");
			hasPreviousFilter = true;
		}
		
		return sb.toString();
	}
	
	private void setParameters(Query query, String givenName, String surname, String email) {
		if (givenName != null && givenName.length() > 0) {
			query.setParameter("givenName", "%" + givenName.toLowerCase() + "%");
		}
		if (surname != null && surname.length() > 0) {
			LOGGER.info("Surname: {}", surname);
			query.setParameter("surname", "%" + surname.toLowerCase() + "%");
		}
		if (email != null && email.length() > 0) {
			query.setParameter("email", "%" + email.toLowerCase() + "%");
		}
	}
	

//14:36:01.236 [main] (UsersDAOImpl.java:112) INFO : Query String: SELECT user FROM Users user LEFT JOIN user.userExtra userInfo WHERE (lower(userInfo.given_name) like :givenName) OR lower(userInfo.displayName) like :givenName) 
//14:36:01.416 [main] (UsersDAOImpl.java:112) INFO : Query String: SELECT user FROM Users user LEFT JOIN user.userExtra userInfo WHERE ((lower(user.username) like :email) OR (lower(userInfo.email)) 

}
