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
import java.net.URI;
import java.util.concurrent.Callable;

import javax.ws.rs.core.UriBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.dcbag.DcBag;
import au.edu.anu.dcclient.Global;

public final class DownloadBagTask extends AbstractDcBagTask implements Callable<File>
{
	private static final Logger LOGGER = LoggerFactory.getLogger(Thread.currentThread().getClass());
	
	private URI pidBagUri;
	private File localBagFile;
	
	public DownloadBagTask(URI bagBaseUri, String pid, File targetDir)
	{
		super();
		localBagFile = new File(targetDir, DcBag.convertToDiskSafe(pid));
		pidBagUri = UriBuilder.fromUri(bagBaseUri).path(pid).build();
	}
	
	@Override
	public File call() throws Exception
	{
		BagFetcher fetcher = new BagFetcher(new BagFactory());
		FetchProtocol http = new HttpFetchProtocol();
		fetcher.registerProtocol("http", http);

		updateProgress("Initialising bag download", pidBagUri.toString(), null, null);
		if (this.plSet != null)
			for (ProgressListener l : plSet)
				fetcher.addProgressListener(l);
		LOGGER.info("Beginning download of bag...");
		SimpleResult result = fetcher.fetchRemoteBag(localBagFile, pidBagUri.toString(), false);
		updateProgress("done", null, null, null);
		LOGGER.debug("Result from Bag Fetch: {}.", result.toString());
		if (!result.isSuccess())
			throw new BagTransferException();
		return localBagFile;
	}
}
