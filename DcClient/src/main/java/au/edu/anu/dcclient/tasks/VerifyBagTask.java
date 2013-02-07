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

import gov.loc.repository.bagit.utilities.SimpleResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.dcbag.DcBag;

public final class VerifyBagTask extends AbstractDcBagTask<SimpleResult>
{
	private static final Logger LOGGER = LoggerFactory.getLogger(VerifyBagTask.class);
	
	private final DcBag dcBag;

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
			System.out.print("Verifying integrity of bag...");
			result = dcBag.verifyValid();
			System.out.println("[OK]");
		}
		catch (Exception e)
		{
			System.out.println("[ERROR]");
			throw e;
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
