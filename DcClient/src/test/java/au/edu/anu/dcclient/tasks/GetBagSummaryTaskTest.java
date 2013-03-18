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

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.dcbag.BagSummary;
import au.edu.anu.dcbag.FileSummary;

public class GetBagSummaryTaskTest extends AbstractDcBagTaskTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(GetBagSummaryTaskTest.class);
	private static final String PID = "test:50";
	
	private GetBagSummaryTask task;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		task = new GetBagSummaryTask(new URI("http://sample.txt"), PID);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetInfoTaskJsonMapper() throws IOException {
		InputStream bagSummaryJsonStream = this.getClass().getResourceAsStream("bagsummary.json");
		String jsonStr = IOUtils.toString(bagSummaryJsonStream);
		BagSummary bagSummary = task.mapJsonToBagSummary(jsonStr);
		for (Entry<String, FileSummary> entry : bagSummary.getFileSummaryMap().entrySet()) {
			assertNotNull(entry.getKey());
			FileSummary fs = entry.getValue();
			assertNotNull(fs);
			assertNotNull(fs.getFilename());
			assertNotNull(fs.getFilepath());
			assertNotNull(fs.getSizeInBytes());
			assertNotNull(fs.getFriendlySize());
			assertNotNull(fs.getLastModified());
			assertNotNull(fs.getMd5());
			assertNotNull(fs.getMessageDigests());
			assertNotNull(fs.getMetadata());
			assertNotNull(fs.getPronomFormat());
			assertNotNull(fs.getScanResult());
		}
		LOGGER.trace("Done");
	}

}
