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
