package au.edu.anu.datacommons.db;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public final class HibernateUtil
{
	private static EntityManagerFactory entityFactory;
	
	static
	{
		entityFactory = Persistence.createEntityManagerFactory("datacommons");
	}

	protected HibernateUtil()
	{
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
