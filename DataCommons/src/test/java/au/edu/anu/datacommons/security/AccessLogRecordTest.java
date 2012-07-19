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
	static final Logger LOGGER = LoggerFactory.getLogger(UsersTest.class);
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
		entityManagerFactory = Persistence.createEntityManagerFactory("datacommons");
		entityManager = entityManagerFactory.createEntityManager();
	}

	@After
	public void tearDown() throws Exception
	{
		System.out.println("Executing tearDown");
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
		AccessLogRecord recAdded = new AccessLogRecord("http://abc.com", user, "127.0.0.1", Operation.CREATE);
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
