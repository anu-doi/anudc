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

package gov.loc.repository.bagit.transfer;

import static junit.framework.Assert.*;
import gov.loc.repository.bagit.FetchTxt;
import org.junit.Test;

public class ThresholdFailStrategyTest
{
	private ThresholdFailStrategy unit = new ThresholdFailStrategy();
	
	@Test
	public void testFailsGlobally()
	{
		this.unit.setFileFailureThreshold(3);
		this.unit.setTotalFailureThreshold(4);
		
		this.check("uri-1", FetchFailureAction.RETRY_CURRENT);
		this.check("uri-2", FetchFailureAction.RETRY_CURRENT);
		this.check("uri-3", FetchFailureAction.RETRY_CURRENT);
		this.check("uri-4", FetchFailureAction.STOP);
		this.check("uri-1", FetchFailureAction.STOP);
		this.check("uri-2", FetchFailureAction.STOP);
		this.check("uri-3", FetchFailureAction.STOP);
	}
	
	@Test
	public void testFailsForFile()
	{
		this.unit.setFileFailureThreshold(3);
		this.unit.setTotalFailureThreshold(11);
		
		this.check("uri-1", FetchFailureAction.RETRY_CURRENT);
		this.check("uri-2", FetchFailureAction.RETRY_CURRENT);
		this.check("uri-1", FetchFailureAction.RETRY_CURRENT);
		this.check("uri-2", FetchFailureAction.RETRY_CURRENT);
		this.check("uri-1", FetchFailureAction.CONTINUE_WITH_NEXT);
		this.check("uri-2", FetchFailureAction.CONTINUE_WITH_NEXT);
		this.check("uri-1", FetchFailureAction.CONTINUE_WITH_NEXT);
		this.check("uri-2", FetchFailureAction.CONTINUE_WITH_NEXT);
	}
	
	private void check(String filename, FetchFailureAction expectedAction)
	{
		FetchTarget target = new FetchTarget(new FetchTxt.FilenameSizeUrl(filename, null, null));
		assertEquals(expectedAction, this.unit.registerFailure(target, null));
	}
}
