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

package au.edu.anu.datacommons.external.geographicmetadata;

import static org.junit.Assert.assertThat;

import javax.ws.rs.core.MultivaluedMap;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.core.util.MultivaluedMapImpl;

import au.edu.anu.datacommons.webservice.bindings.Collection;
import au.edu.anu.datacommons.webservice.bindings.FedoraItem;

/**
 * @author Rahul Khanna
 *
 */
public class MetadataProviderGmdTest {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MetadataProviderGmdTest.class);
	
	private static Client client;
	
	private MetadataProviderGmd gmdProvider;
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		client = Client.create();
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
	public void test() throws Exception {
		gmdProvider = new MetadataProviderGmd();
		gmdProvider.client = client;
		
		MultivaluedMap<String, String> params = new MultivaluedMapImpl();
		params.add("gmdXmlUrl", "http://geonetwork.nci.org.au/geonetwork/srv/eng/xml.metadata.get?id=70849");
		FedoraItem fedoraItem = gmdProvider.retrieveMetadata(params);
		
		assertThat(fedoraItem, Matchers.instanceOf(Collection.class));
		Collection coll = (Collection) fedoraItem;
		assertThat(coll.getTitle(), Matchers.is("SkyMapper Telescope - image archive"));
		assertThat(coll.getMetadataLanguage(), Matchers.is("en"));
		assertThat(coll.getExtIds(), Matchers.hasItem("f7285_6629_8378_9056"));
		assertThat(coll.getBriefDesc(), Matchers.is("Archive of images taken by the SkyMapper telescope, located at Siding Spring Observatory, near Coonabarabran, NSW, Australia"));
		assertThat(coll.getCoverageDateTextList(), Matchers.hasItem("Data collection began in 2008"));
		assertThat(coll.getEmails(), Matchers.hasItem("brian.schmidt@anu.edu.au"));
		assertThat(coll.getContactAddress(), Matchers.is("Australian National University, Canberra, ACT\r\n" + 
				"Canberra\r\n" + 
				"2601\r\n" + 
				"Australia"));
		
	}

}
