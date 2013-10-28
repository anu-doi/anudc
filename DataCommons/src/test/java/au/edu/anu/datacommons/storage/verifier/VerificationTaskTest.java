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

package au.edu.anu.datacommons.storage.verifier;

import static org.junit.Assert.*;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.storage.verifier.ResultMessage.Category;
import au.edu.anu.datacommons.storage.verifier.ResultMessage.Severity;

/**
 * @author Rahul Khanna
 *
 */
public class VerificationTaskTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(VerificationTaskTest.class);

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
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testBagVerification() throws Exception {
		VerificationTask vt = new VerificationTask(new File("C:\\Rahul\\FileUpload\\Bags\\test_427"));
		VerificationResults results = vt.call();
		
		LOGGER.info("Verification results for {}", results.getBagId());
		for (ResultMessage msg : results) {
			LOGGER.trace("{}-{}: [{}] {}", msg.getSeverity(), msg.getCategory(), msg.getFilepath(), msg.getMessage());
		}
	}

	@Test
	public void testMarshalling() throws Exception {
		VerificationResults results = new VerificationResults("test_123");
		results.addMessage(new ResultMessage(Severity.ERROR, Category.ARTIFACT_FOUND, "metadata/abc.ser", "Message"));
		results.addMessage(new ResultMessage(Severity.WARN, Category.CHECKSUM_MISMATCH, "metadata/abc.ser", "Message"));
		JAXBContext context = JAXBContext.newInstance(VerificationResults.class);
		Marshaller m = context.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		m.marshal(results, System.out);

	}
}
