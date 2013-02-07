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

package au.edu.anu.dcclient;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class provides a common thread pool for worker threads to which tasks can be assigned.
 */
public class ThreadPoolManager
{
	private static ExecutorService es = null;
	
	/**
	 * Protected constructor so instantiation can only happen through getExecSvc.
	 */
	protected ThreadPoolManager()
	{
	}
	
	/**
	 * Returns the only instance of the Executor Service.
	 * 
	 * @return ExecutorService
	 */
	public synchronized static ExecutorService getExecSvc()
	{
		if (es == null)
			es = Executors.newSingleThreadExecutor();
		return es;
	}
}
