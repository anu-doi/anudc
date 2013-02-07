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

import static org.junit.Assert.assertTrue;
import gov.loc.repository.bagit.Bag.Format;
import gov.loc.repository.bagit.BagFactory.LoadOption;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.dcbag.DcBag;

public class SaveBagTaskTest extends AbstractDcBagTaskTest
{
	private static final Logger LOGGER = LoggerFactory.getLogger(Thread.currentThread().getClass());
	
	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();

	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
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
	public void testCall() throws IOException, URISyntaxException, InterruptedException, ExecutionException
	{
		DcBag dcBag =  new DcBag(new File(this.getClass().getResource("test_1").toURI()), LoadOption.BY_FILES);
		ExecutorService execSvc = Executors.newSingleThreadExecutor();
		SaveBagTask task = new SaveBagTask(dcBag, tempFolder.getRoot(), "test:1", Format.ZIP);
		task.addProgressListener(getProgressListener());
		Future<File> saveTaskResult = execSvc.submit(task);
		LOGGER.info("Submitted task. Waiting for it to complete...");
		File savedFile = saveTaskResult.get();
		LOGGER.info("Save task completed");
		assertTrue("Bag file doesn't exist.", savedFile.exists());
		assertTrue("Bag's size should be greater than 0.", savedFile.length() > 0);
		LOGGER.info("Returned file {} of size {}", savedFile.getName(), savedFile.length());
	}
}
