package au.edu.anu.dcclient.tasks;

import gov.loc.repository.bagit.utilities.SimpleResult;
import gov.loc.repository.bagit.verify.Verifier;

import java.util.concurrent.Callable;

import au.edu.anu.dcbag.DcBag;

public final class VerifyBagTask extends AbstractDcBagTask implements Callable<SimpleResult>
{
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
		updateProgress("Verifying integrity of bag", dcBag, 1L, 1L);
		SimpleResult result = dcBag.verifyValid();
		updateProgress("done", null, null, null);
		return result;
	}
}
