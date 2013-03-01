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

import static java.text.MessageFormat.*;
import static org.junit.Assert.*;
import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.BagFactory;
import gov.loc.repository.bagit.BagFactory.LoadOption;
import gov.loc.repository.bagit.BagFile;
import gov.loc.repository.bagit.Manifest;
import gov.loc.repository.bagit.Manifest.Algorithm;
import gov.loc.repository.bagit.ManifestHelper;
import gov.loc.repository.bagit.writer.impl.FileSystemWriter;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Random;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CompleterTaskTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(DcStorageCompleter.class);
	private static final String PID = "test:1";

	@Rule
	public TemporaryFolder bagRootFolder = new TemporaryFolder();

	private static BagFactory bf;
	private Bag bag;
	private CompleterTask compTask;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		bf = new BagFactory();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		createBlankBag();
		writeBag();
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test: Creates an empty bag and adds 2 payload files to the payload dir. Creates and executes a CompleterTask with
	 * UpdateCompleter limited to the first file.
	 * 
	 * Expected Result: After completion, the bag should contain both files in the payload dir. The payload manifest
	 * should contain 1 entry - for the first file.
	 */
	@Test
	public void testCompleterTaskAdd() {
		File payloadDir = bagRootFolder.newFolder("data");
		LOGGER.info("Payload directory of bag at: {}", payloadDir.getAbsolutePath());
		File payloadFile1 = new File(payloadDir, "Valid Payload File.txt");
		File payloadFile2 = new File(payloadDir, "Temp File Created.txt");
		try {
			assertTrue(payloadFile1.createNewFile());
			createAndFill(payloadFile1, 512L);
		} catch (IOException e) {
			failOnException(e);
		}
		try {
			assertTrue(payloadFile2.createNewFile());
			createAndFill(payloadFile2, 256L);
		} catch (IOException e) {
			failOnException(e);
		}

		bag = bf.createBag(bag.getFile(), LoadOption.BY_FILES);
		compTask = new CompleterTask(bf, bag);
		compTask.addPayloadFileAddedUpdated(format("data/{0}", payloadFile1.getName()));
		try {
			bag = compTask.call();
		} catch (Exception e) {
			failOnException(e);
		}
		
		Collection<BagFile> payload = bag.getPayload();
		assertEquals(payload.size(), 2);
		assertTrue(fileExistsInPayload(payload, format("data/{0}", payloadFile1.getName())));
		assertTrue(fileExistsInPayload(payload, format("data/{0}", payloadFile2.getName())));
		
		Manifest manifest = bag.getPayloadManifest(Algorithm.MD5);
		assertEquals("Manifest size unexpected.", 1, manifest.size());
		assertTrue(manifest.containsKey(format("data/{0}", payloadFile1.getName())));
		assertFalse(manifest.containsKey(format("data/{0}", payloadFile2.getName())));
	}
	
	/**
	 * Test: Creates an empty bag and adds 2 payload files to the payload dir. Completes the bag. Then deletes the 2nd
	 * payload file from the payload directory. Creates and executes a CompleterTask with UpdateCompleter limited to the
	 * second file.
	 * 
	 * Expected Result: After completion, the bag should contain only the first payload file. Manifests should be
	 * updated to reflect the same.
	 */
	@Test
	public void testCompleterTaskDelete() {
		Collection<BagFile> payload;
		Manifest manifest;
		File payloadDir = bagRootFolder.newFolder("data");
		LOGGER.info("Payload directory of bag at: {}", payloadDir.getAbsolutePath());
		File payloadFile1 = new File(payloadDir, "FirstFile.txt");
		File payloadFile2 = new File(payloadDir, "Second File.txt");
		try {
			assertTrue(payloadFile1.createNewFile());
			createAndFill(payloadFile1, 512L);
		} catch (IOException e) {
			failOnException(e);
		}
		try {
			assertTrue(payloadFile2.createNewFile());
			createAndFill(payloadFile2, 256L);
		} catch (IOException e) {
			failOnException(e);
		}

		bag = bf.createBag(bag.getFile(), LoadOption.BY_FILES);
		compTask = new CompleterTask(bf, bag);
		compTask.addPayloadFileAddedUpdated(format("data/{0}", payloadFile1.getName()));
		compTask.addPayloadFileAddedUpdated(format("data/{0}", payloadFile2.getName()));
		try {
			bag = compTask.call();
		} catch (Exception e) {
			failOnException(e);
		}

		payload = bag.getPayload();
		assertEquals(payload.size(), 2);
		assertTrue(fileExistsInPayload(payload, format("data/{0}", payloadFile1.getName())));
		assertTrue(fileExistsInPayload(payload, format("data/{0}", payloadFile2.getName())));
		
		manifest = bag.getPayloadManifest(Algorithm.MD5);
		assertEquals("Manifest size unexpected.", 2, manifest.size());
		assertTrue(manifest.containsKey(format("data/{0}", payloadFile1.getName())));
		assertTrue(manifest.containsKey(format("data/{0}", payloadFile2.getName())));
		
		assertTrue(payloadFile2.delete());
		bag = bf.createBag(bag.getFile(), LoadOption.BY_FILES);
		compTask = new CompleterTask(bf, bag);
		compTask.addPayloadFileDeleted(format("data/{0}", payloadFile2.getName()));
		try {
			bag = compTask.call();
		} catch (Exception e) {
			failOnException(e);
		}
		
		payload = bag.getPayload();
		assertEquals(payload.size(), 1);
		assertTrue(fileExistsInPayload(payload, format("data/{0}", payloadFile1.getName())));
		assertFalse(fileExistsInPayload(payload, format("data/{0}", payloadFile2.getName())));
		
		manifest = bag.getPayloadManifest(Algorithm.MD5);
		assertEquals("Manifest size unexpected.", 1, manifest.size());
		assertTrue(manifest.containsKey(format("data/{0}", payloadFile1.getName())));
		assertFalse(manifest.containsKey(format("data/{0}", payloadFile2.getName())));
		assertTrue(bag.verifyValid().isSuccess());
	}
	
	@Test
	public void testCompleterTaskNotLimited() {
		Collection<BagFile> payload;
		Manifest manifest;
		File payloadDir = bagRootFolder.newFolder("data");
		LOGGER.info("Payload directory of bag at: {}", payloadDir.getAbsolutePath());
		File payloadFile1 = new File(payloadDir, "FirstFile.txt");
		File payloadFile2 = new File(payloadDir, "Second File.txt");
		try {
			assertTrue(payloadFile1.createNewFile());
			createAndFill(payloadFile1, 512L);
		} catch (IOException e) {
			failOnException(e);
		}
		try {
			assertTrue(payloadFile2.createNewFile());
			createAndFill(payloadFile2, 256L);
		} catch (IOException e) {
			failOnException(e);
		}

		bag = bf.createBag(bag.getFile(), LoadOption.BY_FILES);
		compTask = new CompleterTask(bf, bag);
		try {
			bag = compTask.call();
		} catch (Exception e) {
			failOnException(e);
		}

		LOGGER.trace("Done");
	}

	private boolean fileExistsInPayload(Collection<BagFile> payload, String filepath) {
		boolean payloadFileExists = false;
		for (BagFile ibf : payload) {
			if (ibf.getFilepath().equals(filepath)) {
				payloadFileExists = true;
				break;
			}
		}
		return payloadFileExists;
	}

	private void createAndFill(File file, long sizeInKb) throws IOException {
		if (!file.exists()) {
			assertTrue(file.createNewFile());
		}
		BufferedOutputStream fos = null;
		try {
			fos = new BufferedOutputStream(new FileOutputStream(file));
			for (long i = 0; i < sizeInKb; i++) {
				byte[] bytes = new byte[1024];
				new Random().nextBytes(bytes);
				fos.write(bytes);
			}
		} finally {
			IOUtils.closeQuietly(fos);
		}
	}

	private void createBlankBag() {
		bag = bf.createBag();
		if (bag.getBagItTxt() == null) {
			bag.putBagFile(bag.getBagPartFactory().createBagItTxt());
		}
		bag.putBagFile(bag.getBagPartFactory().createManifest(
				ManifestHelper.getPayloadManifestFilename(Algorithm.MD5, bag.getBagConstants())));
		bag.putBagFile(bag.getBagPartFactory().createManifest(
				ManifestHelper.getTagManifestFilename(Algorithm.MD5, bag.getBagConstants())));
		if (bag.getBagInfoTxt() == null) {
			bag.putBagFile(bag.getBagPartFactory().createBagInfoTxt());
		}
		bag.getBagInfoTxt().addExternalIdentifier(PID);
		bag = bag.makeComplete();
		File payloadDir = new File(bag.getFile(), "data/");
		if (!payloadDir.exists()) {
			payloadDir.mkdir();
		}
	}

	private void writeBag() {
		FileSystemWriter writer = new FileSystemWriter(bf);
		bag = writer.write(bag, bagRootFolder.getRoot());
	}

	private void failOnException(Throwable e) {
		LOGGER.error(e.getMessage(), e);
		fail(e.getMessage());
	}
}
