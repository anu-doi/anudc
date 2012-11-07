package au.edu.anu.datacommons.gateway.logging;

import javax.persistence.EntityTransaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.db.AbstractDao;
import au.edu.anu.datacommons.db.DaoException;

public class LogDao extends AbstractDao
{
	private static final Logger LOGGER = LoggerFactory.getLogger(LogDao.class);
	
	public void create(WebSvcLog log) throws DaoException
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

	public WebSvcLog update(WebSvcLog log) throws DaoException
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
