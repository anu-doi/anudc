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

package au.edu.anu.datacommons.storage.event;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import gov.loc.repository.bagit.BagFactory.Version;
import gov.loc.repository.bagit.impl.AbstractBagConstants;
import gov.loc.repository.bagit.impl.BagItTxtImpl;

import java.io.IOException;
import java.util.LinkedHashMap;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.storage.event.StorageEvent.EventType;
import au.edu.anu.datacommons.storage.event.StorageEventListener.EventTime;
import au.edu.anu.datacommons.storage.tagfiles.BagItTagFile;
import au.edu.anu.datacommons.storage.tagfiles.TagFilesService;

/**
 * @author Rahul Khanna
 *
 */
public class TagFilesStorageEventListenerTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(TagFilesStorageEventListenerTest.class);
	
	@Rule
	public TemporaryFolder bagsRoot = new TemporaryFolder();
	
	@InjectMocks
	private TagFilesStorageEventListener listener = new TagFilesStorageEventListener();
	
	@Mock
	private TagFilesService tfSvc;
	
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
		MockitoAnnotations.initMocks(this);
		LOGGER.trace("Using Bags Root: {}", bagsRoot.getRoot().getAbsolutePath());
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testPreAdd() throws IOException {
		String pid = "test:1";
		String relPath = "dir1/dir2/a.txt";
		StorageEvent event = new StorageEvent(EventType.ADD_FILE, pid, null, relPath);
		listener.notify(EventTime.PRE, event);

		InOrder inOrder = Mockito.inOrder(tfSvc);
		inOrder.verify(tfSvc).getAllEntries(pid, BagItTagFile.class);
		inOrder.verify(tfSvc).clearAllEntries(pid, BagItTagFile.class);
		inOrder.verify(tfSvc).addEntry(pid, BagItTagFile.class, BagItTxtImpl.VERSION_KEY, Version.V0_97.versionString);
		inOrder.verify(tfSvc).addEntry(pid, BagItTagFile.class, BagItTxtImpl.CHARACTER_ENCODING_KEY,
				AbstractBagConstants.BAG_ENCODING);

		String relPath2 = "dir1/dir2/b.txt";

		@SuppressWarnings("unchecked")
		LinkedHashMap<String, String> mockedMap = mock(LinkedHashMap.class);
		when(mockedMap.size()).thenReturn(2);
		when(tfSvc.getAllEntries(pid, BagItTagFile.class)).thenReturn(mockedMap);
		event = new StorageEvent(EventType.ADD_FILE, pid, null, relPath2);
		listener.notify(EventTime.PRE, event);
		verify(tfSvc, times(1)).clearAllEntries(pid, BagItTagFile.class);
	}
	

}
