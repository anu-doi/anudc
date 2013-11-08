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

import static org.junit.Assert.*;
import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.BagFactory;
import gov.loc.repository.bagit.BagFactory.LoadOption;
import gov.loc.repository.bagit.transformer.impl.ChainingCompleter;
import gov.loc.repository.bagit.writer.impl.FileSystemWriter;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.util.Map.Entry;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.DeserializationConfig.Feature;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.storage.completer.DcStorageCompleter;
import au.edu.anu.datacommons.storage.completer.preserve.PreservationCompleter;
import au.edu.anu.datacommons.storage.filesystem.FileFactory;
import au.edu.anu.datacommons.storage.info.BagSummary;
import au.edu.anu.datacommons.storage.info.FileSummary;
import au.edu.anu.datacommons.test.util.TestUtil;

/**
 * @author Rahul Khanna
 * 
 */
public class BagSummaryTaskTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(BagSummaryTaskTest.class);

	private BagFactory bf;
	private FileFactory ff;

	@Rule
	public TemporaryFolder bagDir = new TemporaryFolder();

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
		LOGGER.info("Using bag directory: {}", bagDir.getRoot().getAbsolutePath());
		bf = new BagFactory();
		ff = new FileFactory(200);
	}

	private void initBag() throws IOException, URISyntaxException {
		File payloadDir = bagDir.newFolder("data");
		// Payload file 1.
		File resFile1 = new File(this.getClass().getResource("completer/preserve/Sample bmp.bmp").toURI());
		File payloadFile1 = new File(payloadDir, resFile1.getName());
		FileUtils.copyFile(resFile1, payloadFile1);
		// Payload file 2.
		File resFile2 = new File(this.getClass().getResource("completer/preserve/Pdf Sample.pdf").toURI());
		File payloadFile2 = new File(payloadDir, resFile2.getName());
		FileUtils.copyFile(resFile2, payloadFile2);

		Bag bag = bf.createBag(bagDir.getRoot(), LoadOption.BY_FILES);
		bag = bag.makeComplete(new ChainingCompleter(new PreservationCompleter(ff), new DcStorageCompleter(ff)));
		bag = bag.makeComplete();
		FileSystemWriter fsWriter = new FileSystemWriter(bf);
		bag = fsWriter.write(bag, bagDir.getRoot());

		bag = bf.createBag(bagDir.getRoot(), LoadOption.BY_FILES);
		assertTrue(bag.verifyValid().isSuccess());
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testBagSummaryTask() throws Exception {
		initBag();

		BagSummaryTask task = new BagSummaryTask(ff, bagDir.getRoot());
		BagSummary bs = task.generateBagSummary();

		assertNotNull(bs);
		// File Summary Map
		assertNotNull(bs.getFileSummaryMap());
		for (Entry<String, FileSummary> fsEntry : bs.getFileSummaryMap().entrySet()) {
			String filepath = fsEntry.getKey();
			FileSummary fs = fsEntry.getValue();
			LOGGER.trace("{}: {} {} {} {}", filepath, fs.getFilename(), fs.getFriendlySize(),
					fs.getMessageDigests().get("MD5"), fs.getPresvFilepath());
		}
	}

	@Test
	public void testJsonMapping() throws Exception {
		initBag();

		BagSummaryTask task = new BagSummaryTask(ff, bagDir.getRoot());
		BagSummary bs = task.generateBagSummary();

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		StringWriter writer = new StringWriter();
		mapper.writeValue(writer, bs);

		LOGGER.trace(writer.toString());

		BagSummary readValue = mapper.readValue(writer.toString(), BagSummary.class);
		LOGGER.trace(readValue.getPid());
	}
	
	@Test
	public void testXmlMarshalling() throws JAXBException {
		BagSummaryTask task = new BagSummaryTask(ff, new File("C:\\Rahul\\FileUpload\\Bags\\test_427"));
		BagSummary bs = task.generateBagSummary();
		JAXBContext context = JAXBContext.newInstance(BagSummary.class);
		Marshaller m = context.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		m.marshal(bs, System.out);
	}
}
