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

package gov.loc.repository.bagit.transfer.fetch;

import static junit.framework.Assert.*;
import java.net.URI;
import gov.loc.cygwin.Cygpath;
import gov.loc.repository.bagit.BagFile;
import gov.loc.repository.bagit.transfer.BagTransferException;
import gov.loc.repository.bagit.transfer.FileFetcher;
import gov.loc.repository.bagit.transfer.dest.ByteArrayFetchDestination;
import gov.loc.repository.bagit.utilities.ResourceHelper;
import org.apache.commons.io.IOUtils;
import org.junit.BeforeClass;
import org.junit.Test;


public class ExternalRsyncFetchProtocolTest
{
	private static boolean skipTests = false; 

	@BeforeClass
	public static void checkForRsyncInstallation()
	{
		ExternalRsyncFetchProtocol protocol = new ExternalRsyncFetchProtocol();

		try 
		{
			protocol.checkRsyncSanity();
			skipTests = false;
		}
		catch(BagTransferException e) 
		{
			skipTests = true;

			System.err.println("Could not find installed rysnc for unit tests.");
			e.printStackTrace(System.err);
		}
	}
	
	@Test
	public void testFetchesCorrectly() throws Exception
	{
		if (skipTests) return;
		
		// Note that this test sets up a non-network rsync
		// transfer, in order to avoid setting up a test
		// rsync daemon.
		
		// Store into memory, for convenience.
		ByteArrayFetchDestination destination = new ByteArrayFetchDestination("foo.txt");

		// Create a new fetcher.
		ExternalRsyncFetchProtocol protocol = new ExternalRsyncFetchProtocol();

		// Get the URI to be fetched.
		FileFetcher fetcher = protocol.createFetcher(new URI("rsync:///bar.txt"), null);
		String path = Cygpath.toUnix(ResourceHelper.getFile("bags/v0_96/bag/data/test1.txt").getAbsolutePath());
		URI uri = new URI(path);
		
		// Run through the fetcher lifecycle.
		fetcher.initialize();
		fetcher.fetchFile(uri, null, destination, null);
		BagFile bagFile = destination.commit();
		fetcher.close();
		
		assertEquals("foo.txt", bagFile.getFilepath());
		String data = IOUtils.toString(bagFile.newInputStream());
		assertEquals("test1", data);
	}
	
	@Test
	public void testUnescapesSpacesBeforePassingToCommandline() throws Exception
	{
		if (skipTests) return;
		
		// Ticket #728
		// https://beryllium.rdc.lctl.gov/trac/transfer/ticket/728
		
		// Note that this test sets up a non-network rsync
		// transfer, in order to avoid setting up a test
		// rsync daemon.
		
		// Store into memory, for convenience.
		ByteArrayFetchDestination destination = new ByteArrayFetchDestination("foo bar.txt");

		// Create a new fetcher.
		ExternalRsyncFetchProtocol protocol = new ExternalRsyncFetchProtocol();
		
		// Get the URI to be fetched.
		FileFetcher fetcher = protocol.createFetcher(new URI("rsync:///bar.txt"), null);
		String path = Cygpath.toUnix(ResourceHelper.getFile("bags/v0_96/bag-with-escapable-characters/data/test file with spaces.txt").getAbsolutePath());
		path = path.replace(" ", "%20");
		
		URI uri = new URI(path);
		
		// Run through the fetcher lifecycle.
		fetcher.initialize();
		fetcher.fetchFile(uri, null, destination, null);
		BagFile bagFile = destination.commit();
		fetcher.close();
		
		assertEquals("foo bar.txt", bagFile.getFilepath());
		String data = IOUtils.toString(bagFile.newInputStream());
		assertEquals("test file with spaces", data);
	}
}
