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

package au.edu.anu.datacommons.storage.temp;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.tasks.ThreadPoolService;

/**
 * @author Rahul Khanna
 *
 */
public class TempFileServiceTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(TempFileServiceTest.class);
	
	@InjectMocks
	private TempFileService tempFileSvc;
	
	@Rule
	public TemporaryFolder tempDir = new TemporaryFolder();
	
	@Mock
	private ThreadPoolService threadPoolSvc;
	
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
		LOGGER.info("Temp Dir: {}", tempDir.getRoot().getAbsolutePath());
		tempFileSvc = new TempFileService();
		tempFileSvc.uploadDir = tempDir.getRoot().toPath();
		MockitoAnnotations.initMocks(this);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSaveUrl() throws Exception {
		final String urlStr = "https://policies.anu.edu.au/cs/groups/confidential/@its/documents/edrms/dxbf/mdaw/~edisp/anup_000421.pdf";
		when(threadPoolSvc.submitCachedPool(any(Callable.class))).thenAnswer(new Answer<Future<UploadedFileInfo>>() {

			@Override
			public Future<UploadedFileInfo> answer(InvocationOnMock invocation) throws Throwable {
				Object[] args = invocation.getArguments();
				SaveInputStreamTask stream = (SaveInputStreamTask) args[0];
				UploadedFileInfo ufi = stream.call();
				assertThat(ufi, notNullValue());
				assertThat(ufi.getSize(), greaterThan(0L));
				return null;
			}
			
		});
		tempFileSvc.saveInputStream(urlStr, -1, null);
	}

}
