package au.edu.anu.dcclient.tasks;

import gov.loc.repository.bagit.utilities.SimpleResult;
import gov.loc.repository.bagit.verify.Verifier;

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.dcbag.DcBag;

public final class VerifyBagTask extends AbstractDcBagTask<SimpleResult>
{
	private static final Logger LOGGER = LoggerFactory.getLogger(VerifyBagTask.class);
	
	private DcBag dcBag;

	/**
	 * VerifyBagTask
	 * 
	 * Australian National University Data Commons
	 * 
	 * Constructor for VerifyBagTask
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		26/06/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 * @param dcBag
	 *            The bag to be verified.
	 */
	public VerifyBagTask(DcBag dcBag)
	{
		super();
		this.dcBag = dcBag;
	}

	/**
	 * call
	 * 
	 * Australian National University Data Commons
	 * 
	 * DESCRIPTION
	 * 
	 * @see java.util.concurrent.Callable#call()
	 * 
	 *      <pre>
	 * Version	Date		Developer			Description
	 * 0.1		26/06/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 * @return Verification result as SimpleResult
	 * @throws Exception
	 */
	@Override
	public SimpleResult call() throws Exception
	{
		stopWatch.start();

		SimpleResult result;
		try
		{
			updateProgress("Verifying integrity of bag for pid", dcBag.getExternalIdentifier(), 1L, 1L);
			result = dcBag.verifyValid();
		}
		finally
		{
			updateProgress("done", null, null, null);
			stopWatch.end();
			LOGGER.info("Time - Verify Bag Task: {}", stopWatch.getFriendlyElapsed());
		}

		return result;
	}
}
