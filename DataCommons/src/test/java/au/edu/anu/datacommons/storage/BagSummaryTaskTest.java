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

package au.edu.anu.datacommons.storage;

import static org.junit.Assert.assertNotNull;
import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.BagFactory;
import gov.loc.repository.bagit.BagFactory.LoadOption;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map.Entry;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.DeserializationConfig.Feature;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.storage.info.BagSummary;
import au.edu.anu.datacommons.storage.info.FileSummary;

/**
 * @author Rahul Khanna
 *
 */
public class BagSummaryTaskTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(BagSummaryTaskTest.class);

	private BagFactory bf;
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		bf = new BagFactory();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	// Requires env setup.
	@Ignore
	public void test() {
		Bag bag = bf.createBag(new File("C:\\Rahul\\FileUpload\\Bags\\test_387"), LoadOption.BY_FILES);

		BagSummaryTask task = new BagSummaryTask(bag);
		BagSummary bs = task.generateBagSummary();
		
		assertNotNull(bs);
		// File Summary Map
		assertNotNull(bs.getFileSummaryMap());
		for (Entry<String, FileSummary> fsEntry : bs.getFileSummaryMap().entrySet()) {
			String filepath = fsEntry.getKey();
			FileSummary fs = fsEntry.getValue();
			LOGGER.trace("{}: {} {} {}", filepath, fs.getFilename(), fs.getFriendlySize(), fs.getMessageDigests().get("MD5"));
		}
	}

	// Requires env setup.
	@Ignore
	public void testJsonMapping() throws JsonGenerationException, JsonMappingException, IOException {
		Bag bag = bf.createBag(new File("C:\\Rahul\\FileUpload\\Bags\\test_427"), LoadOption.BY_FILES);

		BagSummaryTask task = new BagSummaryTask(bag);
		BagSummary bs = task.generateBagSummary();
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		StringWriter writer = new StringWriter();
		mapper.writeValue(writer, bs);
		
		LOGGER.trace(writer.toString());
		
		BagSummary readValue = mapper.readValue(writer.toString(), BagSummary.class);
		LOGGER.trace(readValue.getPid());
	}
}
