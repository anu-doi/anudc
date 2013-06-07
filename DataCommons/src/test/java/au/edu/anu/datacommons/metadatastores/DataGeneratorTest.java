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
package au.edu.anu.datacommons.metadatastores;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DataGeneratorTest
 * 
 * Australian National University Data Commons
 * 
 * Test class for the Data Generator class
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		27/05/2013	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
public class DataGeneratorTest {
	static final Logger LOGGER = LoggerFactory.getLogger(DataGeneratorTest.class);
	
	/**
	 * test
	 *
	 * Test the methods in the class
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		27/05/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 */
	@Test
	public void test() {
		DataGenerator dataGenerator = new DataGenerator();
		String reference = dataGenerator.getLinkReference("Australian Research Council (ARC)", "LE0989083");
		LOGGER.info("Reference: {}", reference);
		reference = dataGenerator.getLinkReference("Australian Research Council (ARC)", "LE0989082");
		LOGGER.info("Reference: {}", reference);
		Map<String, List<String>> mapValues = dataGenerator.generateActivityFromMetadataStores("CON19327");
		for (Entry<String, List<String>> entry : mapValues.entrySet()) {
			LOGGER.info("Key: {}, Values: {}", entry.getKey(), entry.getValue());
		}
		LOGGER.info("---------------------");
		mapValues = dataGenerator.generateActivityFromMetadataStores("CON9056");
		for (Entry<String, List<String>> entry : mapValues.entrySet()) {
			LOGGER.info("Key: {}, Values: {}", entry.getKey(), entry.getValue());
		}
	}
}
