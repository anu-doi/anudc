package au.edu.anu.dcclient.tasks;

import gov.loc.repository.bagit.ProgressListenable;
import gov.loc.repository.bagit.ProgressListener;

import java.util.HashSet;
import java.util.Set;

import org.junit.BeforeClass;

public abstract class AbstractDcBagTask implements ProgressListenable
{
	protected Set<ProgressListener> plSet = null;

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
}
