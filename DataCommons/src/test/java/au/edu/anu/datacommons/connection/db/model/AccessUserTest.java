package au.edu.anu.datacommons.connection.db.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * AccessUserTest
 * 
 * Australian National University Data Commons
 * 
 * Test cases for the 'access_user' table.  This test case covers the AccessUser class
 * and also uses the AccessGroup class.
 * 
 * Version	Date		Developer			Description
 * 0.1		13/03/2012	Genevieve Turner	Initial build
 */
public class AccessUserTest
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
	 * Executes tests by adding, finding and removing rows from the 'access_user' table.
	 * 
	 * Version	Date		Developer			Description
	 * 0.1		13/03/2012	Genevieve Turner	Initial build
	 */
	@Test
	public void test()
	{
		AccessGroup testGroup1 = new AccessGroup(new Long(1), "admin", new Long(1));
		List<AccessGroup> accessGroups = new ArrayList<AccessGroup>();
		accessGroups.add(testGroup1);
		AccessUser testUser1 = new AccessUser(null, "e1234567");
		testUser1.setAccessGroups(accessGroups);

		entityManager.getTransaction().begin();
		entityManager.persist(testUser1);
		entityManager.getTransaction().commit();
		
		entityManager.getTransaction().begin();
		List<AccessUser> result = entityManager.createQuery("from AccessUser where uid='e1234567'", AccessUser.class).getResultList();
		
		if (result.size() > 0) {
			for (AccessUser user : result){
				assertEquals(testUser1.getUid(),user.getUid());
				assertEquals(testUser1.getAccessGroups(), user.getAccessGroups());
				entityManager.remove(user);
			}
		}
		else {
			fail("No Rows Returned");
		}
		
		entityManager.getTransaction().commit();
	}
}
