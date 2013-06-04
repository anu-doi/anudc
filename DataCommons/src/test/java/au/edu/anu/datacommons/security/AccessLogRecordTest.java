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

package au.edu.anu.datacommons.security;

import static org.junit.Assert.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.data.db.dao.UsersDAO;
import au.edu.anu.datacommons.data.db.dao.UsersDAOImpl;
import au.edu.anu.datacommons.data.db.model.Users;
import au.edu.anu.datacommons.data.db.model.UsersTest;
import au.edu.anu.datacommons.security.AccessLogRecord.Operation;

public class AccessLogRecordTest
{
	private static final Logger LOGGER = LoggerFactory.getLogger(UsersTest.class);
	EntityManagerFactory entityManagerFactory;
	EntityManager entityManager;


	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception
	{
	}

	@Before
	public void setUp() throws Exception
	{
		entityManagerFactory = Persistence.createEntityManagerFactory("datacommons-test");
		entityManager = entityManagerFactory.createEntityManager();
	}

	@After
	public void tearDown() throws Exception
	{
		if (entityManager != null)
			entityManager.close();
		if (entityManagerFactory != null)
			entityManagerFactory.close();
	}

	@Test
	public void testAccessLogRecordStringUsersString()
	{
		// Add an access log
		UsersDAO usersDao = new UsersDAOImpl(Users.class);
		Users user = usersDao.getUserByName("u4465201");
		AccessLogRecord recAdded = new AccessLogRecord("http://abc.com", user, "127.0.0.1", "DataCommons", Operation.CREATE);
		entityManager.getTransaction().begin();
		entityManager.persist(recAdded);
		entityManager.getTransaction().commit();
		
		// Retrieve the access log.
		entityManager.getTransaction().begin();
		AccessLogRecord recRetrieved = entityManager.find(AccessLogRecord.class, recAdded.getId());
		assertEquals(recAdded.getId(), recRetrieved.getId());
		assertTrue(recAdded.getTimestamp().equals(recRetrieved.getTimestamp()));
		assertTrue(recAdded.getIpAddr().equals(recRetrieved.getIpAddr()));
		entityManager.getTransaction().commit();
		
		// Delete the access log.
		entityManager.getTransaction().begin();
		entityManager.remove(recAdded);
		entityManager.getTransaction().commit();
		
		// Check that the record's deleted.
		entityManager.getTransaction().begin();
		recRetrieved = entityManager.find(AccessLogRecord.class, recAdded.getId());
		assertNull(recRetrieved);
		entityManager.getTransaction().commit();
	}

}
