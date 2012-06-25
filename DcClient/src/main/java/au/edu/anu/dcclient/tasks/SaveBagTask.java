package au.edu.anu.dcclient.tasks;

import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.ProgressListener;

import java.io.File;
import java.util.concurrent.Callable;

import au.edu.anu.dcbag.DcBag;

public final class SaveBagTask extends AbstractDcBagTask implements Callable<File>
{
	private DcBag dcBag;
	private File targetDir = null;
	private Bag.Format format = null;
	private String extId = null;
	
	public SaveBagTask(DcBag dcBag)
	{
		super();
		this.dcBag = dcBag;
	}
	
	public SaveBagTask(DcBag dcBag, File targetDir, String extId, Bag.Format format)
	{
		super();
		this.dcBag = dcBag;
		this.targetDir = targetDir;
		this.extId = extId;
		this.format = format;
	}

	@Override
	public File call() throws Exception
	{
		updateProgress("Preparing bag for saving", null, null, null);
		if (dcBag.getExternalIdentifier() == null || dcBag.getExternalIdentifier().equals(""))
			throw new Exception("Bag doesn't have an external identifier specified.");
		if (this.plSet != null)
			for (ProgressListener l : plSet)
				dcBag.addProgressListener(l);
		
		File savedFile;
		if (this.targetDir == null)
			savedFile = dcBag.save();
		else
			savedFile = dcBag.saveAs(targetDir, extId, format);
		updateProgress("done", null, null, null);
		return savedFile;
	}
}
