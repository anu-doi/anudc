package au.edu.anu.dcclient.tasks;

import gov.loc.repository.bagit.BagFactory;
import gov.loc.repository.bagit.BagFactory.LoadOption;
import gov.loc.repository.bagit.ProgressListener;
import gov.loc.repository.bagit.transfer.BagFetchResult;
import gov.loc.repository.bagit.transfer.BagFetcher;
import gov.loc.repository.bagit.transfer.BagTransferException;
import gov.loc.repository.bagit.transfer.FetchFailStrategy;
import gov.loc.repository.bagit.transfer.FetchFailureAction;
import gov.loc.repository.bagit.transfer.FetchProtocol;
import gov.loc.repository.bagit.transfer.FetchTarget;
import gov.loc.repository.bagit.transfer.fetch.HttpFetchProtocol;
import gov.loc.repository.bagit.utilities.SimpleResult;

import java.io.File;
import java.net.Authenticator;
import java.net.URI;
import java.util.concurrent.Callable;

import javax.ws.rs.core.UriBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.dcbag.DcBag;
import au.edu.anu.dcclient.DcAuthenticator;
import au.edu.anu.dcclient.Global;

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
	 * @throws Exception
	 */
	@Override
	public File call() throws Exception
	{
		BagFetcher fetcher = new BagFetcher(new BagFactory());
		// HTTP
		FetchProtocol http = new HttpFetchProtocol();
		fetcher.registerProtocol("http", http);
		// HTTPS
		HttpFetchProtocol https = new HttpFetchProtocol();
		// TODO Change the following in production when using trusted CA-certified cert
		https.setRelaxedSsl(true);
		fetcher.registerProtocol("https", https);

		updateProgress("Initialising bag download", pidBagUri.toString(), null, null);
		if (this.plSet != null)
			for (ProgressListener l : plSet)
				fetcher.addProgressListener(l);
		LOGGER.info("Beginning download of bag...");
		SimpleResult result;
		try
		{
			result = fetcher.fetchRemoteBag(localBagFile, pidBagUri.toString(), false);
		}
		catch (BagTransferException e)
		{
			throw e;
		}

		// Following code is added due to a possible bug in BagIt lib - the fetch.txt should be deleted after all payload files are downloaded.
		if (result.isSuccess())
		{
			DcBag dcBag = new DcBag(localBagFile, LoadOption.BY_FILES);
			dcBag.removeBagFile(dcBag.getBag().getFetchTxt().getFilepath());
			dcBag.save();
			dcBag.close();
		}
		updateProgress("done", null, null, null);
		LOGGER.debug("Result from Bag Fetch: {}.", result.toString());
		if (!result.isSuccess())
			throw new BagTransferException("Unable to download bag from ANU Data Commons");
		return localBagFile;
	}
}
