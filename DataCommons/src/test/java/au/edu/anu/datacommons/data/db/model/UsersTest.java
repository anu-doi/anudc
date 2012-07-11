package au.edu.anu.datacommons.data.db.model;

import static org.junit.Assert.assertEquals;

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
		entityManagerFactory = Persistence.createEntityManagerFactory("datacommons");
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
