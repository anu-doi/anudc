package au.edu.anu.datacommons.doi.logging;

import java.io.Closeable;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.data.db.PersistenceManager;
import au.edu.anu.datacommons.data.db.dao.DaoException;

public class ExtWebResourceLogDao implements Closeable
{
	private static final Logger LOGGER = LoggerFactory.getLogger(ExtWebResourceLogDao.class);
	
	protected EntityManager entityManager;
	
	public ExtWebResourceLogDao()
	{
		this.entityManager = PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
	}
	
	@Override
	public void close()
	{
		if (this.entityManager != null)
			this.entityManager.close();
	}
	
	public void create(ExtWebResourceLog log) throws DaoException
	{
		EntityTransaction et = null;
		try
		{
			et = this.entityManager.getTransaction();
			et.begin();
			this.entityManager.persist(log);
			et.commit();
		}
		catch (Exception e)
		{
			try
			{
				if (et != null)
					et.rollback();
			}
			catch (Exception e1)
			{
				LOGGER.warn(e1.getMessage(), e1);
			}
			throw new DaoException(e);
		}
	}
	
	public ExtWebResourceLog update(ExtWebResourceLog log) throws DaoException
	{
		EntityTransaction et = null;
		try
		{
			et = this.entityManager.getTransaction();
			et.begin();
			log = this.entityManager.merge(log);
			et.commit();
		}
		catch (Exception e)
		{
			try
			{
				if (et != null)
					et.rollback();
			}
			catch (Exception e1)
			{
				LOGGER.warn(e1.getMessage(), e1);
			}
			throw new DaoException(e);
		}
		
		return log;
	}
}
