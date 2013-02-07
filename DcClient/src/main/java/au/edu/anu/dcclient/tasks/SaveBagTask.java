package au.edu.anu.dcclient.tasks;

import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.ProgressListener;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.dcbag.DcBag;
import au.edu.anu.dcbag.DcBagException;

/**
 * This class represents a task that saves a bag on the local drive making it ready for upload to Data Commons.
 */
public final class SaveBagTask extends AbstractDcBagTask<File>
{
	private static final Logger LOGGER = LoggerFactory.getLogger(SaveBagTask.class);
	
	private final DcBag dcBag;
	private File targetDir = null;
	private Bag.Format format = null;
	private String extId = null;

	/**
	 * SaveBagTask
	 * 
	 * Australian National University Data Commons
	 * 
	 * Constructor for SaveBagTask
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		26/06/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 * @param dcBag
	 *            The bag to be saved.
	 */
	public SaveBagTask(DcBag dcBag)
	{
		super();
		this.dcBag = dcBag;
	}

	/**
	 * SaveBagTask
	 * 
	 * Australian National University Data Commons
	 * 
	 * Constructor for SaveBagTask
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		26/06/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 * @param dcBag
	 *            The bag to be saved.
	 * @param targetDir
	 *            The directory where the bag is to be saved.
	 * @param extId
	 *            The external identifier (pid) of the bag.
	 * @param format
	 *            Format of the bag.
	 */
	public SaveBagTask(DcBag dcBag, File targetDir, String extId, Bag.Format format)
	{
		super();
		this.dcBag = dcBag;
		this.targetDir = targetDir;
		this.extId = extId;
		this.format = format;
	}

	/**
	 * call
	 * 
	 * Australian National University Data Commons
	 * 
	 * Saves the bag.
	 * 
	 * @see java.util.concurrent.Callable#call()
	 * 
	 *      <pre>
	 * Version	Date		Developer			Description
	 * 0.1		26/06/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 * @return The saved bag as File.
	 * @throws DcBagException 
	 * @throws IOException 
	 * @throws Exception
	 */
	@Override
	public File call() throws DcBagException, IOException
	{
		stopWatch.start();

		File savedFile;
		try
		{
			updateProgress("Preparing bag for saving", null, null, null);
			if (dcBag.getExternalIdentifier() == null || dcBag.getExternalIdentifier().equals(""))
				throw new DcBagException("Bag doesn't have an external identifier specified.");
			if (this.plSet != null)
				for (ProgressListener l : plSet)
					dcBag.addProgressListener(l);

			if (this.targetDir == null)
				savedFile = dcBag.save();
			else
				savedFile = dcBag.saveAs(targetDir, extId, format);
		}
		finally
		{
			updateProgress("done", null, null, null);
			stopWatch.end();
			LOGGER.info("Time - Save Bag Task: {}", stopWatch.getFriendlyElapsed());
		}

		return savedFile;
	}
}
