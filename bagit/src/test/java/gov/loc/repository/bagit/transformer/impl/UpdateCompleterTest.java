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

package gov.loc.repository.bagit.transformer.impl;

import static org.junit.Assert.*;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.BagFactory;
import gov.loc.repository.bagit.BagInfoTxt;
import gov.loc.repository.bagit.BagFactory.LoadOption;
import gov.loc.repository.bagit.progresslistener.LoggingProgressListener;
import gov.loc.repository.bagit.utilities.ResourceHelper;
import gov.loc.repository.bagit.utilities.SimpleResult;

import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class UpdateCompleterTest {

	BagFactory bagFactory = new BagFactory();
	UpdateCompleter completer;	
	File bagFile;
	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	
	private File createTestBag() throws Exception {
		File sourceBagDir = ResourceHelper.getFile(MessageFormat.format("bags/{0}/bag", BagFactory.LATEST.toString().toLowerCase()));
		File testBagDir = new File(sourceBagDir.getParentFile(), "test_bag");
		if (testBagDir.exists()) {
			FileUtils.forceDelete(testBagDir);
		}
		FileUtils.copyDirectory(sourceBagDir, testBagDir);
		return testBagDir;
	}
	
	@Before
	public void setup() throws Exception {
		completer = new UpdateCompleter(this.bagFactory);
		completer.addProgressListener(new LoggingProgressListener());
		bagFile = this.createTestBag();
		//Add a payload file
		//Delete a payload file
		//Well, actually I'll just rename a file
		File file1 = new File(bagFile, "data/test1.txt");
		file1.renameTo(new File(bagFile, "data/xtest1.txt"));
		//Change a payload file
		File file2 = new File(bagFile, "data/test2.txt");
		FileWriter writer1 = new FileWriter(file2);
		writer1.append("x");
		writer1.close();
		
		//Add a tag
		File file3 = new File(bagFile, "newtag.txt");
		FileWriter writer2 = new FileWriter(file3);
		writer2.append("newtag");
		writer2.close();
				
	}
	
	@Test
	public void testComplete() throws Exception {
		Bag bag = this.bagFactory.createBag(bagFile, LoadOption.BY_FILES);
		Bag newBag = null;
		try {
			assertFalse(bag.verifyValid().isSuccess());
			newBag = completer.complete(bag);
			SimpleResult result = newBag.verifyValid();
			assertTrue(result.isSuccess());
			BagInfoTxt bagInfoTxt = newBag.getBagInfoTxt();
			//Original doesn't have payload-oxum, so neither should completed
			assertNull(bagInfoTxt.getPayloadOxum());
			assertEquals(this.dateFormat.format(new Date()), bagInfoTxt.getBaggingDate());
		} finally {
			bag.close();
			if (newBag != null) newBag.close();
		}
	}

	@Test
	public void testCompleteWithoutBagInfoTxt() throws Exception {
		File bagInfoTxtFile = new File(bagFile, bagFactory.getBagConstants().getBagInfoTxt());
		FileUtils.forceDelete(bagInfoTxtFile);

		Bag bag = this.bagFactory.createBag(bagFile, LoadOption.BY_FILES);
		Bag newBag = null;
		try {
			assertFalse(bag.verifyValid().isSuccess());
			newBag = completer.complete(bag);
			try {
				SimpleResult result = newBag.verifyValid();
				assertTrue(result.isSuccess());
				assertNull(newBag.getBagInfoTxt());
			} finally {
				newBag.close();
			}
		} finally {
			bag.close();
			if (newBag != null) newBag.close();
		}
	}
	
	@Test
	public void testCompleteLimit() throws Exception {
		Bag bag = this.bagFactory.createBag(bagFile, LoadOption.BY_FILES);
		Bag newBag = null;
		Bag newBag2 = null;
		try {
			assertFalse(bag.verifyValid().isSuccess());
			completer.setLimitAddPayloadFilepaths(new ArrayList<String>());
			completer.setLimitUpdatePayloadFilepaths(new ArrayList<String>());
			completer.setLimitDeletePayloadFilepaths(new ArrayList<String>());
			completer.setLimitAddTagFilepaths(new ArrayList<String>());
			completer.setLimitUpdateTagFilepaths(new ArrayList<String>());
			completer.setLimitDeleteTagFilepaths(new ArrayList<String>());
			newBag = completer.complete(bag);
			assertFalse(newBag.verifyValid().isSuccess());
			assertTrue(newBag.getChecksums("newtag.txt").isEmpty());
			
			
			completer.setLimitAddPayloadFilepaths(Arrays.asList(new String[] {"data/xtest1.txt"}));
			completer.setLimitUpdatePayloadFilepaths(Arrays.asList(new String[] {"data/test2.txt"}));
			completer.setLimitDeletePayloadFilepaths(Arrays.asList(new String[] {"data/test1.txt"}));
			completer.setLimitAddTagFilepaths(Arrays.asList(new String[] {"newtag.txt"}));
			
			newBag2 = completer.complete(bag);
			assertTrue(newBag2.verifyValid().isSuccess());
			assertFalse(newBag.getChecksums("newtag.txt").isEmpty());
		} finally {
			bag.close();
			if (newBag != null) newBag.close();
			if (newBag2 != null) newBag2.close();
		}
	}


}
