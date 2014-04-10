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
package au.edu.anu.datacommons.report.datasource.ldap;

import static org.junit.Assert.fail;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.design.JRDesignDataset;
import net.sf.jasperreports.engine.design.JRDesignField;
import net.sf.jasperreports.engine.design.JRDesignQuery;
import net.sf.jasperreports.engine.query.JRQueryExecuter;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * LdapQueryExecuterTest
 *
 * Australian National University Data Commons
 * 
 * Test class for query executers
 *
 * JUnit coverage:
 * None
 * 
 * @author Genevieve Turner
 *
 */
public class LdapQueryExecuterTest {
	static final Logger LOGGER = LoggerFactory.getLogger(LdapQueryExecuterTest.class);
	
	@Ignore
	@Test
	public void test() {
		//TODO fix this!
		// Note: To make this test work it needs to have parameters passed.  I have no idea on how to
		// create these parameters so that we can test it using them.
		
		JRDesignDataset dataset = new JRDesignDataset(true);
		JRDesignQuery query = new JRDesignQuery();
		
		query.setText("SELECT uid, displayName FILTER (uid=u5125986)");
		dataset.setQuery(query);
		
		LdapQueryExecuterFactory factory = new LdapQueryExecuterFactory();
		try {
			JRQueryExecuter queryExecuter = factory.createQueryExecuter(dataset, null);
			JRDataSource ds = queryExecuter.createDatasource();
			while (ds.next()) {
				JRDesignField uidField = new JRDesignField();
				uidField.setName("uid");
				uidField.setValueClass(String.class);
				Object uidValue = ds.getFieldValue(uidField);
				JRDesignField displayNameField = new JRDesignField();
				displayNameField.setName("displayName");
				displayNameField.setValueClass(String.class);
				Object displayNameValue = ds.getFieldValue(displayNameField);
				LOGGER.info("Uid: {}, Display Name: {}", uidValue, displayNameValue);
			}
		}
		catch (JRException e) {
			LOGGER.error("Error executing report", e);
			fail("Exception creating query executor");
		}
		LOGGER.info("Done");
	}
}
