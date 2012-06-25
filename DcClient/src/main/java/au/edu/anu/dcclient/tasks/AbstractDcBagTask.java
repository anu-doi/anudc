package au.edu.anu.dcclient.tasks;

import gov.loc.repository.bagit.ProgressListenable;
import gov.loc.repository.bagit.ProgressListener;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractDcBagTask implements ProgressListenable
{
	protected Set<ProgressListener> plSet = null;

	@Override
	public void addProgressListener(ProgressListener progressListener)
	{
		if (plSet == null)
			plSet = new HashSet<ProgressListener>(1);
		plSet.add(progressListener);
	}

	@Override
	public void removeProgressListener(ProgressListener progressListener)
	{
		if (plSet != null)
			plSet.remove(progressListener);
	}

	public void clearAllProgressListeners()
	{
		if (plSet != null)
			plSet.clear();
	}

	protected void updateProgress(String activity, Object item, Long count, Long total)
	{
		if (plSet != null)
		{
			for (ProgressListener pl : this.plSet)
				pl.reportProgress(activity, item, count, total);
		}
	}
}
