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

package au.edu.anu.datacommons.storage.search;

import static org.junit.Assert.*;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

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

import au.edu.anu.datacommons.test.util.TestUtil;

/**
 * @author Rahul Khanna
 *
 */
public class StorageSearchServiceTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(StorageSearchServiceTest.class);

	private static final String BAGIT_PDF = "au/edu/anu/datacommons/storage/completer/fido/BagIt Specification.pdf";
	
	@Rule
	public TemporaryFolder tempDir = new TemporaryFolder();
	
	private StorageSearchService searchSvc;
	private List<File> plFiles = new ArrayList<File>();
	
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
		LOGGER.trace("Using temp dir: {}", tempDir.getRoot().getAbsolutePath());
		searchSvc = new StorageSearchService("http://localhost:8983/solr/testcore2");
		searchSvc.solrServer.deleteByQuery("*:*");
		File plDir = tempDir.newFolder("data");
		File plFile = new File(plDir, "File created " + new SimpleDateFormat("yyyy-MM-dd HHmmss").format(new Date()) + ".txt");
		InputStream fileStream = this.getClass().getClassLoader().getResourceAsStream(BAGIT_PDF);
		FileUtils.copyInputStreamToFile(fileStream, plFile);
		plFiles.add(plFile);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSearchService() throws Exception {
		searchSvc.indexFile(tempDir.getRoot(), plFiles.get(0));
	}
	
	@Test
	public void testAddDocuments() throws Exception {
		final File bagDir = new File("C:\\Rahul\\Temp\\Sample Docs");
		File[] files = bagDir.listFiles();
		ExecutorService es = Executors.newCachedThreadPool();
		List<Future<Throwable>> futures = new ArrayList<Future<Throwable>>();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isFile()) {
				final File fFile = files[i];
				futures.add(es.submit(new Callable<Throwable>() {

					@Override
					public Throwable call() throws Exception {
						try {
							searchSvc.indexFile(bagDir, fFile);
							LOGGER.trace("Finished indexing {}", fFile.getName());
							return null;
						} catch (Exception e) {
							return e;
						}
					}
					
				}));
			}
		}
		
		for (Future<Throwable> f : futures) {
			assertNull(f.get());
		}
		es.shutdown();
		es.awaitTermination(10, TimeUnit.SECONDS);
		LOGGER.trace("Finished indexing {} documents.", futures.size());
	}
}
