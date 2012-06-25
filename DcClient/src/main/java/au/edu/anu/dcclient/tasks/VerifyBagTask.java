package au.edu.anu.dcclient.tasks;

import gov.loc.repository.bagit.utilities.SimpleResult;
import gov.loc.repository.bagit.verify.Verifier;

import java.util.concurrent.Callable;

import au.edu.anu.dcbag.DcBag;

public final class VerifyBagTask extends AbstractDcBagTask implements Callable<SimpleResult>
{
	private DcBag dcBag;
	
	public VerifyBagTask(DcBag dcBag)
	{
		super();
		this.dcBag = dcBag;
	}
	
	@Override
	public SimpleResult call() throws Exception
	{
		updateProgress("Verifying integrity of bag", dcBag, 1L, 1L);
		SimpleResult result = dcBag.verifyValid();
		updateProgress("done", null, null, null);
		return result;
	}
}
