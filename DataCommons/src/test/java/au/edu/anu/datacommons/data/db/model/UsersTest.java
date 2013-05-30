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

package au.edu.anu.datacommons.data.db.model;

import static org.junit.Assert.*;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author u5125986
 *
 */
public class UsersTest {
	static final Logger LOGGER = LoggerFactory.getLogger(UsersTest.class);
	EntityManagerFactory entityManagerFactory;
	EntityManager entityManager;
	
	/**
	 * setUp
	 * 
	 * Performs functions prior to starting the test
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		17/05/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 */
	@Before
	public void setUp() {
		entityManagerFactory = Persistence.createEntityManagerFactory("datacommons-test");
		entityManager = entityManagerFactory.createEntityManager();
	}
	
	/**
	 * test
	 * 
	 * Performs a test on the users ant user_registered tables
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		17/05/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 */
	@Test
	public void test() {
		UserRegistered user_registered = new UserRegistered();
		user_registered.setGiven_name("test");
		user_registered.setLast_name("user");
		
		Users user = new Users();
		user.setUsername("testuser1");
		user.setPassword("testpassword1");
		user.setEnabled(Boolean.TRUE);
		user.setUser_type(new Long(2));
		
		EntityTransaction entityTransaction = entityManager.getTransaction();
		entityTransaction.begin();
		entityManager.persist(user);
		entityTransaction.commit();
		user_registered.setUser(user);
		user_registered.setId(user.getId());
		user.setUser_registered(user_registered);

		entityTransaction.begin();
		entityManager.persist(user);
		entityTransaction.commit();
		LOGGER.info("After committing registered user");
		
		List<Users> users = entityManager.createQuery("from Users where username='testuser1'", Users.class).getResultList();
		for (Users savedUser : users) {
			assertEquals("testuser1", savedUser.getUsername());
			assertEquals("testpassword1", savedUser.getPassword());
			assertEquals(Boolean.TRUE, savedUser.getEnabled());
			assertEquals(new Long(2), savedUser.getUser_type());
			assertEquals("test", savedUser.getUser_registered().getGiven_name());
			assertEquals("user", savedUser.getUser_registered().getLast_name());
			entityTransaction.begin();
			entityManager.remove(savedUser);
			entityTransaction.commit();
		}
	}
	
	/**
	 * tearDown
	 * 
	 * Executes functions after creating the tests have been performed.
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		17/05/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 */
	@After
	public void tearDown() {
		System.out.println("Executing tearDown");
		if (entityManager != null)
			entityManager.close();
		if (entityManagerFactory != null)
			entityManagerFactory.close();
	}

}
