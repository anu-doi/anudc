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

import gov.loc.repository.bagit.Bag.Format;
import gov.loc.repository.bagit.BagFactory;
import gov.loc.repository.bagit.BagFactory.LoadOption;
import gov.loc.repository.bagit.BagFile;
import gov.loc.repository.bagit.ProgressListener;
import gov.loc.repository.bagit.transfer.BagFetcher;
import gov.loc.repository.bagit.transfer.BagTransferException;
import gov.loc.repository.bagit.transfer.fetch.HttpFetchProtocol;
import gov.loc.repository.bagit.utilities.SimpleResult;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.dcbag.DcBag;
import au.edu.anu.dcbag.DcBagException;
import au.edu.anu.dcclient.Global;

/**
 * Represents a task that downloads a bag from Data Commons.
 */
public final class DownloadBagTask extends AbstractDcBagTask<File>
{
	private static final Logger LOGGER = LoggerFactory.getLogger(DownloadBagTask.class);

	private final URI pidBagUri;
	private final File localBagFile;

	/**
	 * DownloadBagTask
	 * 
	 * Australian National University Data Commons
	 * 
	 * Constructor for DownloadBagTask
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		26/06/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 * @param bagBaseUri
	 *            Base URI of bags in ANU Data Commons.
	 * @param pid
	 *            Pid of the collection whose bag will be downloaded.
	 * @param targetDir
	 *            Directory where the bag will be downloaded.
	 */
	public DownloadBagTask(URI bagBaseUri, String pid, File targetDir)
	{
		super();
		localBagFile = new File(targetDir, DcBag.convertToDiskSafe(pid));
		pidBagUri = UriBuilder.fromUri(bagBaseUri).path(pid).build();
	}

	/**
	 * call
	 * 
	 * Australian National University Data Commons
	 * 
	 * Method called by the ExecutorService to perform the download bag task.
	 * 
	 * @see java.util.concurrent.Callable#call()
	 * 
	 *      <pre>
	 * Version	Date		Developer			Description
	 * 0.1		26/06/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 * @return Bag downloaded as File.
	 */
	@Override
	public File call() throws BagTransferException, IOException, DcBagException
	{
		stopWatch.start();

		updateProgress("Initialising bag download", pidBagUri.toString(), null, null);
		BagFetcher fetcher = createFetcher();

		LOGGER.info("Downloading bag...");
		SimpleResult result = null;
		File tempBagFile = new File(System.getProperty("java.io.tmpdir"), localBagFile.getName());
		DcBag dcBag = null;
		try
		{
			if (tempBagFile.exists())
				FileUtils.deleteQuietly(tempBagFile);
			LOGGER.info("Temp location of downloaded bag: " + tempBagFile.getCanonicalPath());
			result = fetcher.fetchRemoteBag(tempBagFile, pidBagUri.toString(), false);
			if (result.isSuccess())
			{
				// If a local bag exists, replace it, else saveAs.
				if (localBagFile.exists())
				{
					dcBag = new DcBag(localBagFile, LoadOption.BY_FILES);
					dcBag.replaceWith(tempBagFile, true);
				}
				else
				{
					dcBag = new DcBag(tempBagFile, LoadOption.BY_FILES);
					dcBag.saveAs(Global.getLocalBagStoreAsFile(), dcBag.getExternalIdentifier(), Format.FILESYSTEM);
				}
				// Following code is added due to a possible bug in BagIt lib - the fetch.txt should be deleted after all payload files are downloaded.
				BagFile fetchTxt = dcBag.getBag().getFetchTxt();
				if (fetchTxt != null)
					dcBag.removeBagFile(dcBag.getBag().getFetchTxt().getFilepath());
				dcBag.save();
				dcBag.close();
			}
			else
			{
				LOGGER.error("Download result is false.");
				throw new BagTransferException("Download result is false");
			}
		}
		finally
		{
			LOGGER.debug("Deleting the temp bag file.");
			if (dcBag != null)
				dcBag.close();
			FileUtils.deleteQuietly(tempBagFile);
			updateProgress("done", null, null, null);

			if (result != null)
				LOGGER.debug("Result from Bag Fetch: {}.", result.toString());

			stopWatch.end();
			LOGGER.info("Time - Download Bag Task: {}", stopWatch.getFriendlyElapsed());
		}

		return localBagFile;
	}

	/**
	 * Returns a BagFetcher object that is used to download bags from Data Commons.
	 * 
	 * @return BagFetcher
	 */
	private BagFetcher createFetcher()
	{
		BagFetcher bagFetcher = new BagFetcher(new BagFactory());
		
		// Register HTTP.
		HttpFetchProtocol http = new HttpFetchProtocol();
		bagFetcher.registerProtocol("http", http);

		// Register HTTPS.
		HttpFetchProtocol https = new HttpFetchProtocol();
		// TODO Change the following in production when using trusted CA-certified cert
		https.setRelaxedSsl(true);
		bagFetcher.registerProtocol("https", https);

		// Add progress listeners.
		if (this.plSet != null)
			for (ProgressListener pl : plSet)
				bagFetcher.addProgressListener(pl);

		return bagFetcher;
	}
}
