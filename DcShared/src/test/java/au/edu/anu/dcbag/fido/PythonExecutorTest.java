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

package au.edu.anu.dcbag.fido;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PythonExecutorTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(PythonExecutorTest.class);

	private PythonExecutor pExec;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
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
	public void testPythonVersion() throws IOException {
		pExec = new PythonExecutor(Arrays.asList("--version"));
		pExec.execute();
		logOutputs();
		assertTrue(pExec.getErrorAsString().startsWith("Python"));
	}

	@Test
	public void testPythonProgPassedAsStr() throws IOException {
		pExec = new PythonExecutor(Arrays.asList("-c", "print 'abc'"));
		pExec.execute();
		logOutputs();
		assertTrue(pExec.getOutputAsString().startsWith("abc"));
	}
	
	@Test
	public void testPythonScript() throws IOException {
		File scriptFile = null;
		try {
			scriptFile = new File(this.getClass().getResource("pythonexecutortest.py").toURI());
		} catch (URISyntaxException e) {
			failOnException(e);
		} 
		pExec = new PythonExecutor(Arrays.asList(scriptFile.getAbsolutePath()));
		pExec.execute();
		logOutputs();
		assertTrue(pExec.getOutputAsString().startsWith("Works!"));
	}
	
	private void logOutputs() throws IOException {
		LOGGER.info("Output: '{}'", pExec.getOutputAsString());
		LOGGER.info("Error: '{}'", pExec.getErrorAsString());
	}

	private void failOnException(Throwable e) {
		LOGGER.error(e.getMessage(), e);
		fail(e.getMessage());
	}
}
