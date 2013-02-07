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

package au.edu.anu.dcclient.tasks;

import gov.loc.repository.bagit.ProgressListenable;
import gov.loc.repository.bagit.ProgressListener;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;

import au.edu.anu.dcclient.CustomClient;
import au.edu.anu.dcclient.stopwatch.StopWatch;
import au.edu.anu.dcclient.stopwatch.Timeable;

import com.sun.jersey.api.client.Client;

/**
 * Represents an abstract task that performs an action related to a bag containing a set of files.
 *
 * @param <T> the type of the object returned by the task which is specific to each class that extends this class. 
 */
public abstract class AbstractDcBagTask<T> implements Callable<T>, ProgressListenable, Timeable
{
	protected Set<ProgressListener> plSet = null;
	protected StopWatch stopWatch = new StopWatch();
	protected Client client = CustomClient.getInstance();

	/**
	 * addProgressListener
	 * 
	 * Australian National University Data Commons
	 * 
	 * Adds a progress listener to the list of progress listeners that will be sent progress updates.
	 * 
	 * @see gov.loc.repository.bagit.ProgressListenable#addProgressListener(gov.loc.repository.bagit.ProgressListener)
	 * 
	 *      <pre>
	 * Version	Date		Developer			Description
	 * 0.1		26/06/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 * @param progressListener
	 *            Progress listener to which updates will be sent.
	 */
	@Override
	public void addProgressListener(ProgressListener progressListener)
	{
		if (plSet == null)
			plSet = new HashSet<ProgressListener>(1);
		plSet.add(progressListener);
	}

	/**
	 * removeProgressListener
	 * 
	 * Australian National University Data Commons
	 * 
	 * Removes a progress listener from the list of progress listeners
	 * 
	 * @see gov.loc.repository.bagit.ProgressListenable#removeProgressListener(gov.loc.repository.bagit.ProgressListener)
	 * 
	 *      <pre>
	 * Version	Date		Developer			Description
	 * 0.1		26/06/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 * @param progressListener
	 *            Progress listener to remove.
	 */
	@Override
	public void removeProgressListener(ProgressListener progressListener)
	{
		if (plSet != null)
			plSet.remove(progressListener);
	}

	/**
	 * clearAllProgressListeners
	 * 
	 * Australian National University Data Commons
	 * 
	 * Removes all progress listeners.
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		26/06/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 *
	 */
	public void clearAllProgressListeners()
	{
		if (plSet != null)
			plSet.clear();
	}

	/**
	 * updateProgress
	 * 
	 * Australian National University Data Commons
	 * 
	 * Sends a progress update to all progress listeners.
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		26/06/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 * @param activity
	 * @param item
	 * @param count
	 * @param total
	 */
	protected void updateProgress(String activity, Object item, Long count, Long total)
	{
		if (plSet != null)
		{
			for (ProgressListener pl : this.plSet)
				pl.reportProgress(activity, item, count, total);
		}
	}
	
	@Override
	public StopWatch getStopWatch()
	{
		return this.stopWatch;
	}
}
