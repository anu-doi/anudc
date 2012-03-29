package au.edu.anu.datacommons.connection.db.model;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * AccessGroupTest
 * 
 * Australian National University Data Commons
 * 
 * Test cases for the 'access_group' table.  This test case covers the AccessGroup class
 * and also uses the AccessUser class.
 * 
 * Version	Date		Developer			Description
 * 0.1		13/03/2012	Genevieve Turner	Initial build
 */
public class AccessGroupTest
{
	EntityManagerFactory entityManagerFactory;
	EntityManager entityManager;

	/**
	 * setUp
	 * 
	 * Sets up the test case by instantiating the EntityManagerFactory and the EntityManager
	 * 
	 * Version	Date		Developer			Description
	 * 0.1		13/03/2012	Genevieve Turner	Initial build
	 * 
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception
	{
		entityManagerFactory = Persistence.createEntityManagerFactory("datacommons");
		entityManager = entityManagerFactory.createEntityManager();
	}

	/**
	 * tearDown
	 * 
	 * Finishes the test case by closing the EntityManagerFactory and the EntityManager
	 * connections.
	 * 
	 * Version	Date		Developer			Description
	 * 0.1		13/03/2012	Genevieve Turner	Initial build
	 * 
	 * @throws Exception
	 */
	@After
	public void tearDown() throws Exception
	{
		entityManager.close();
		entityManagerFactory.close();
	}

	/**
	 * test
	 * 
	 * Executes tests by adding, finding and removing rows from the 'access_group' table.
	 * 
	 * Version	Date		Developer			Description
	 * 0.1		13/03/2012	Genevieve Turner	Initial build
	 */
	@Test
	public void test()
	{
		AccessUser testUser1 = new AccessUser(new Long(1), "u5125986");
		List<AccessUser> accessUsers = new ArrayList<AccessUser>();
		accessUsers.add(testUser1);
		
		AccessGroup testGroup1 = new AccessGroup(null, "test", new Long(1));
		testGroup1.setAccessUsers(accessUsers);
		
		entityManager.getTransaction().begin();
		entityManager.persist(testGroup1);
		entityManager.getTransaction().commit();
		
		entityManager.getTransaction().begin();
		List<AccessGroup> result = entityManager.createQuery("from AccessGroup where name='test'", AccessGroup.class).getResultList();
		
		if (result.size() > 0) {
			for (AccessGroup group : result) {
				assertEquals(testGroup1.getName(),group.getName());
				assertEquals(testGroup1.getAccessUsers(), group.getAccessUsers());
				entityManager.remove(group);
			}
		}
		else {
			fail("No Rows Returned");
		}
		
		entityManager.getTransaction().commit();
	}

}
