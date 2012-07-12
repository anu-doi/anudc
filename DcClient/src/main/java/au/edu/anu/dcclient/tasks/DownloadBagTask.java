package au.edu.anu.dcclient.tasks;

import gov.loc.repository.bagit.BagFactory;
import gov.loc.repository.bagit.BagFactory.LoadOption;
import gov.loc.repository.bagit.ProgressListener;
import gov.loc.repository.bagit.transfer.BagFetcher;
import gov.loc.repository.bagit.transfer.BagTransferException;
import gov.loc.repository.bagit.transfer.FetchProtocol;
import gov.loc.repository.bagit.transfer.fetch.HttpFetchProtocol;
import gov.loc.repository.bagit.utilities.SimpleResult;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.Callable;

import javax.ws.rs.core.UriBuilder;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.dcbag.DcBag;
import au.edu.anu.dcbag.DcBagException;

public final class DownloadBagTask extends AbstractDcBagTask implements Callable<File>
{
	private static final Logger LOGGER = LoggerFactory.getLogger(Thread.currentThread().getClass());

	private URI pidBagUri;
	private File localBagFile;

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
		BagFetcher fetcher = new BagFetcher(new BagFactory());
		HttpFetchProtocol http = new HttpFetchProtocol();						// HTTP
		fetcher.registerProtocol("http", http);

		HttpFetchProtocol https = new HttpFetchProtocol();					// HTTPS
		// TODO Change the following in production when using trusted CA-certified cert
		https.setRelaxedSsl(true);
		fetcher.registerProtocol("https", https);

		updateProgress("Initialising bag download", pidBagUri.toString(), null, null);
		if (this.plSet != null)
			for (ProgressListener pl : plSet)
				fetcher.addProgressListener(pl);
		LOGGER.info("Beginning download of bag...");
		SimpleResult result;
		File tempBagFile = new File(System.getProperty("java.io.tmpdir"), localBagFile.getName());
		DcBag dcBag = null;
		try
		{
			if (tempBagFile.exists())
				FileUtils.deleteDirectory(tempBagFile);
			LOGGER.info("Temp location of downloaded bag: " + tempBagFile.getCanonicalPath());
			result = fetcher.fetchRemoteBag(tempBagFile, pidBagUri.toString(), false);
			if (result.isSuccess())
			{
				dcBag = new DcBag(localBagFile, LoadOption.BY_FILES);
				dcBag.replaceWith(tempBagFile, true);
				// Following code is added due to a possible bug in BagIt lib - the fetch.txt should be deleted after all payload files are downloaded.
				dcBag.removeBagFile(dcBag.getBag().getFetchTxt().getFilepath());
				//				dcBag.save();
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
			try
			{
				FileUtils.deleteDirectory(tempBagFile);
			}
			finally
			{
				// Do nothing.
			}
			updateProgress("done", null, null, null);
		}

		LOGGER.debug("Result from Bag Fetch: {}.", result.toString());
		return localBagFile;
	}
}
