package au.edu.anu.datacommons.persistence;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaBuilder;

public final class HibernateUtil
{
	private static EntityManagerFactory entityFactory;
	
	static
	{
		entityFactory = Persistence.createEntityManagerFactory("datacommons");
	}

	public static EntityManagerFactory getSessionFactory()
	{
		return entityFactory;
	}

	public static void shutdown()
	{
		// Close caches and connection pools
		entityFactory.close();
	}
}
