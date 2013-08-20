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

package au.edu.anu.datacommons.storage.completer.metadata;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.io.IOUtils;
import org.apache.tika.exception.TikaException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 * 
 * @author Rahul Khanna
 *
 */
public class MetadataExtractorImplTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(MetadataExtractorImplTest.class);

	private static File bagItPdf;
	private static File fitsFile;

	private MetadataExtractor mde;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		bagItPdf = new File(MetadataExtractorImplTest.class.getResource("BagIt Specification.pdf").toURI());
		fitsFile = new File(MetadataExtractorImplTest.class.getResource("Nasa - WFPC2u5780205r_c0fx.fits").toURI());
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testPdfMetadata() throws Exception {
		mde = new MetadataExtractorImpl(bagItPdf);
		Map<String, String[]> mdMap = mde.getMetadataMap();
		LOGGER.trace("Attributes count: {}", mdMap.size());
		assertEquals(26, mdMap.size());
		assertTrue(mdMap.get("dc:title")[0].equals("BagIt File Packaging Format v 0.97"));
	}

	@Test
	public void testFitsMetadata() throws Exception {
		mde = new MetadataExtractorImpl(fitsFile);
		Map<String, String[]> mdMap = mde.getMetadataMap();
		assertEquals(180, mdMap.size());
	}

	/**
	 * Test to check that the metadata extractor doesn't fall over with out of memory/heap errors with large files.
	 * Also, that the metadata is extracted in a reasonable amount of time.
	 * 
	 * @throws Exception
	 */
	@Test(timeout = 5000)
	public void testLargeRandomMetadata() throws Exception {
		final long fileSizeMb = 4 * 1024;	// 4 GB.
		final Random rand = new Random();
		ExecutorService threadPool = Executors.newSingleThreadExecutor();
		final PipedOutputStream pos = new PipedOutputStream();
		PipedInputStream pis = null;
		threadPool.submit(new Callable<Throwable>() {

			@Override
			public Throwable call() throws Exception {
				byte[] wBuffer = new byte[1024 * 1024];
				try {
					for (int i = 0; i < fileSizeMb; i++) {
						rand.nextBytes(wBuffer);
						pos.write(wBuffer);
					}
				} catch (Throwable e) {
					return e;
				} finally {
					IOUtils.closeQuietly(pos);
				}
				return null;
			}
		});

		try {
			pis = new PipedInputStream(pos);
			mde = new MetadataExtractorImpl(pis);
		} finally {
			IOUtils.closeQuietly(pis);
		}
		Map<String, String[]> metadataMap = mde.getMetadataMap();
		for (Entry<String, String[]> entry : metadataMap.entrySet()) {
			LOGGER.trace(entry.getKey());
			for (int i = 0; i < entry.getValue().length; i++) {
				LOGGER.trace("\t {}", entry.getValue()[i]);
			}
		}

		assertThat(metadataMap.entrySet(), hasSize(1));
		assertThat(metadataMap, hasEntry("Content-Type", new String[] {"application/octet-stream"}));
	}
}
