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
