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

package au.edu.anu.dcbag;

import static org.junit.Assert.*;
import gov.loc.repository.bagit.Bag.Format;
import gov.loc.repository.bagit.BagFactory.LoadOption;
import gov.loc.repository.bagit.ProgressListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DcBagTest
{
	private static final Logger LOGGER = LoggerFactory.getLogger(Thread.currentThread().getClass());
	private static List<File> payloadFiles;
	private static final String TEST_PID = "test:1";
	
	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();

	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		payloadFiles = new ArrayList<File>();
		assertNotNull(payloadFiles);

		payloadFiles.add(new File(DcBagTest.class.getResource("1M.fil").toURI()));
		payloadFiles.add(new File(DcBagTest.class.getResource("2M.fil").toURI()));
		payloadFiles.add(new File(DcBagTest.class.getResource("5M.fil").toURI()));
		payloadFiles.add(new File(DcBagTest.class.getResource("10M.fil").toURI()));
		assertEquals(payloadFiles.size(), 4);

	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception
	{
	}

	@Before
	public void setUp() throws Exception
	{
	}

	@After
	public void tearDown() throws Exception
	{
	}

	@Test
	public void testCreateNewBag() throws Exception
	{
		// Create new bag.
		DcBag dcBag = new DcBag(TEST_PID);
		assertNotNull(dcBag);

		// Add files to payload.
		dcBag.addFileToPayload(payloadFiles.get(0));
		dcBag.addFileToPayload(payloadFiles.get(1));
		dcBag.addFileToPayload(payloadFiles.get(2));
		File bagFile = dcBag.saveAs(tempFolder.getRoot(), TEST_PID, Format.FILESYSTEM);
		assertEquals("Files in payload != 3", 3, dcBag.getPayloadFileList().size());
		assertTrue("Bag file doesn't exist.", bagFile.exists());
		assertTrue("Bag is invalid.", dcBag.verifyValid().isSuccess());

		// Close the bag.
		dcBag.close();
		dcBag = null;

		// Reopen the bag, add and remove files. Save.
		dcBag = new DcBag(tempFolder.getRoot(), TEST_PID, LoadOption.BY_FILES);
		dcBag.addFileToPayload(payloadFiles.get(3));
		dcBag.removeBagFile("data/" + payloadFiles.get(2).getName());
		dcBag.addProgressListener(getProgressListener());
		dcBag.save();

		dcBag.close();
		dcBag = null;

		dcBag = new DcBag(tempFolder.getRoot(), TEST_PID, LoadOption.BY_FILES);
		assertTrue("External identifier not " + TEST_PID, dcBag.getExternalIdentifier().equals(TEST_PID));
		assertEquals("Number of files in payload is not 3.", 3, dcBag.getPayloadFileList().size());
	}

	/**
	 * testUpdateBagPayload
	 * 
	 * Australian National University Data Commons
	 * 
	 * Tests that a bag correctly updates itself when a file is added into its payload directory without called the bag's add file to payload method.
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		02/07/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testUpdateBagPayload() throws Exception
	{
		DcBag dcBag = new DcBag(TEST_PID);

		// Add files to payload.
		dcBag.addFileToPayload(payloadFiles.get(0));

		File bagFile = dcBag.saveAs(tempFolder.getRoot(), TEST_PID, Format.FILESYSTEM);
		assertTrue("Bag file is not a directory.", bagFile.isDirectory());

		dcBag.close();
		dcBag = null;

		// Copy a payload file into the data dir without adding to bag.
		File dest = new File(tempFolder.getRoot(), DcBag.convertToDiskSafe(TEST_PID) + "/data/" + payloadFiles.get(3).getName());
		FileUtils.copyFile(payloadFiles.get(3), dest);
		assertTrue(dest.exists());
		assertEquals(payloadFiles.get(3).length(), dest.length());

		// Reopen bag and save it.
		dcBag = new DcBag(tempFolder.getRoot(), TEST_PID, LoadOption.BY_FILES);
		dcBag.save();

		// Ensure the added file hasn't been deleted.
		assertTrue(dest.exists());
		assertEquals(payloadFiles.get(3).length(), dest.length());

		dcBag.close();
	}

	@Test
	public void testUpdateSecuredBag() throws Exception
	{
		DcBag dcBag = new DcBag(TEST_PID);

		// Add files to payload and set data source property.
		dcBag.addFileToPayload(payloadFiles.get(0));
		dcBag.setBagProperty(BagPropsTxt.FIELD_DATASOURCE, BagPropsTxt.DataSource.INSTRUMENT.toString());
		File bagFile = dcBag.saveAs(tempFolder.getRoot(), TEST_PID, Format.FILESYSTEM);

		// Close bag.
		dcBag.close();
		dcBag = null;

		// Copy a payload file into the data dir without adding to bag. Valid operation.
		File dest = new File(tempFolder.getRoot(), DcBag.convertToDiskSafe(TEST_PID) + "/data/" + payloadFiles.get(3).getName());
		FileUtils.copyFile(payloadFiles.get(3), dest);
		assertTrue(dest.exists());
		assertEquals(payloadFiles.get(3).length(), dest.length());

		dcBag = new DcBag(bagFile, LoadOption.BY_FILES);
		dcBag.customValidate();
		dcBag.close();
	}

	@Test (expected = Exception.class)
	public void testUpdateSecuredBagException() throws Exception
	{
		DcBag dcBag = new DcBag(TEST_PID);

		// Add files to payload and set data source property.
		dcBag.addFileToPayload(payloadFiles.get(0));
		dcBag.setBagProperty(BagPropsTxt.FIELD_DATASOURCE, BagPropsTxt.DataSource.INSTRUMENT.toString());
		File bagFile = dcBag.saveAs(tempFolder.getRoot(), TEST_PID, Format.FILESYSTEM);

		// Close bag.
		dcBag.close();
		dcBag = null;

		// Replace an existing file in the payload directory with a new file.
		File dest = new File(tempFolder.getRoot(), DcBag.convertToDiskSafe(TEST_PID) + "/data/" + payloadFiles.get(0).getName());
		FileUtils.copyFile(payloadFiles.get(1), dest);
		assertTrue(dest.exists());
		assertEquals(payloadFiles.get(1).length(), dest.length());

		dcBag = new DcBag(bagFile, LoadOption.BY_FILES);
		
		dcBag.save();
		dcBag.close();
	}
	
	@Test (expected = DcBagException.class)
	public void testReplaceInstrumentBagWithException() throws IOException, DcBagException
	{
		// Create the original bag with data source as instrument that will then be replaced.
		DcBag bag1 = new DcBag(TEST_PID);
		bag1.addFileToPayload(payloadFiles.get(0));
		bag1.addFileToPayload(payloadFiles.get(1));
		bag1.setBagProperty(BagPropsTxt.FIELD_DATASOURCE, BagPropsTxt.DataSource.INSTRUMENT.toString());
		File bagFile1 = bag1.saveAs(tempFolder.newFolder(), TEST_PID, Format.FILESYSTEM);
		
		// Make a copy of the original bag. In the copy we'll modify existing payload files.
		File bagFile2 = bag1.saveAs(tempFolder.newFolder(), TEST_PID, Format.FILESYSTEM);
		
		DcBag bag2 = new DcBag(bagFile2, LoadOption.BY_FILES);
		// Temporarily change the bag property so an existing payload file can be modified.
		bag2.setBagProperty(BagPropsTxt.FIELD_DATASOURCE, BagPropsTxt.DataSource.GENERAL.toString());
		bagFile2 = bag2.save();
		assertTrue("Bag's property should be General", bag2.getBagProperty(BagPropsTxt.FIELD_DATASOURCE).equals(BagPropsTxt.DataSource.GENERAL.toString()));
		
		// Change a payload file.
		File plFileToDelete = new File(bag2.getFile(), "data/" + payloadFiles.get(0).getName());
		assertTrue(plFileToDelete.exists());
		boolean deleteResult = plFileToDelete.delete();
		assertTrue(deleteResult);
		bag2 = new DcBag(bagFile2, LoadOption.BY_FILES);
		bagFile2 = bag2.save();
		
		// Replace first bag with the new one.
		bag1 = new DcBag(bagFile1, LoadOption.BY_FILES);
		bag1.replaceWith(bagFile2, true);
		
		// Ensure bag1 didn't change.
		 Set<Entry<String, String>> plList = bag1.getPayloadFileList();
		 assertEquals("Bag 1's paylist file count != 2", 2, plList.size());
	}
	
	@Test
	public void testReplacementInstrumentBag() throws IOException, DcBagException
	{
		// Create the original bag with data source as instrument that will then be replaced.
		DcBag bag1 = new DcBag(TEST_PID);
		bag1.addFileToPayload(payloadFiles.get(0));
		bag1.addFileToPayload(payloadFiles.get(1));
		bag1.setBagProperty(BagPropsTxt.FIELD_DATASOURCE, BagPropsTxt.DataSource.INSTRUMENT.toString());
		File bagFile1 = bag1.saveAs(tempFolder.newFolder(), TEST_PID, Format.FILESYSTEM);
		
		// Make a copy of the original bag. In the copy we'll modify existing payload files.
		File bagFile2 = bag1.saveAs(tempFolder.newFolder(), TEST_PID, Format.FILESYSTEM);
		bag1 = new DcBag(bagFile1, LoadOption.BY_FILES);
		DcBag bag2 = new DcBag(bagFile2, LoadOption.BY_FILES);
		
		assertTrue("Bag 1 is invalid", bag1.verifyValid().isSuccess());
		assertTrue("Bag 2 is invalid", bag2.verifyValid().isSuccess());
		
		File dataDir = new File(bag2.getFile(), "data/");
		FileUtils.copyFile(payloadFiles.get(2), new File(dataDir, payloadFiles.get(2).getName()));
		bag2.close();
		bag2 = new DcBag(bagFile2, LoadOption.BY_FILES);
		bag2.save();
		
		bag1.replaceWith(bag2.getFile(), false);
		
		assertEquals(3, bag1.getPayloadFileList().size());
	}

	private ProgressListener getProgressListener()
	{
		return new ProgressListener()
		{
			@Override
			public void reportProgress(String activity, Object item, Long count, Long total)
			{
				LOGGER.info("Progress: Activity '{}', item '{}', count {}, total {}.", new Object[] { activity, item, count, total });
			}
		};
	}
}
