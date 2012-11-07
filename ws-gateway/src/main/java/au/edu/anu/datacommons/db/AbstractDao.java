package au.edu.anu.datacommons.db;

import java.io.Closeable;

import javax.persistence.EntityManager;

public abstract class AbstractDao implements Closeable
{
	protected EntityManager entityManager;
	
	public AbstractDao()
	{
		this.entityManager = HibernateUtil.getSessionFactory().createEntityManager();
	}
	
	@Override
	public void close()
	{
		if (this.entityManager != null)
			this.entityManager.close();
	}
}
