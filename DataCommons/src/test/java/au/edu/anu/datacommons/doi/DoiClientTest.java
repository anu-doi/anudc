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

package au.edu.anu.datacommons.doi;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.StringWriter;
import java.net.URI;

import javax.ws.rs.core.UriBuilder;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.datacite.schema.kernel_4.Resource;
import org.datacite.schema.kernel_4.Resource.Creators;
import org.datacite.schema.kernel_4.Resource.Creators.Creator;
import org.datacite.schema.kernel_4.Resource.Creators.Creator.CreatorName;
import org.datacite.schema.kernel_4.Resource.Identifier;
import org.datacite.schema.kernel_4.Resource.Titles;
import org.datacite.schema.kernel_4.Resource.Titles.Title;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.test.framework.JerseyTest;

public class DoiClientTest extends JerseyTest
{
	private static final Logger LOGGER = LoggerFactory.getLogger(DoiClientTest.class);

	private DoiClient doiClient;
	private String sampleDoi = "10.5072/13/50639BFE25F18";
	
	private static JAXBContext context;
	
	private Marshaller marshaller;
	private Unmarshaller unmarshaller;

	public DoiClientTest()
	{
		super("au.edu.anu.datacommons.doi");
		//		LOGGER.trace("In Constructor");
		//		WebResource webResource = resource();
		//		DoiConfig doiConfig = new DoiConfigImpl(webResource.getURI().toString(), appId);
		//		doiClient = new DoiClient(doiConfig);
		
		doiClient = new DoiClient();
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		context = JAXBContext.newInstance(Resource.class);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception
	{
	}

	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "http://datacite.org/schema/kernel-2.2 http://schema.datacite.org/meta/kernel-2.2/metadata.xsd");
	}

	@After
	public void tearDown() throws Exception
	{
		super.tearDown();
	}

	@Ignore
	public void testMint()
	{
		try
		{
			doiClient.mint("https://datacommons.anu.edu.au:8443/DataCommons/item/anudc:3320", generateSampleResource());
			String respStr = doiClient.getDoiResponseAsString();
			LOGGER.trace(respStr);
		}
		catch (Exception e)
		{
			failOnException(e);
		}
	}

	@Ignore
	public void testUpdate()
	{
		try
		{
			Resource res = new Resource();
			Creators creators = new Creators();
			Creator creator = new Creator();
			CreatorName creatorName = new CreatorName();
			creatorName.setValue("Creator 1");
			creator.setCreatorName(creatorName);
			creators.getCreator().add(creator);
			res.setCreators(creators);
			
			Titles titles = new Titles();
			Title title = new Title();
			title.setValue("Title 1");
			titles.getTitle().add(title);
			res.setTitles(titles);
			
			res.setPublisher("Publisher 1");
			res.setPublicationYear("1987");
			
			Identifier id = new Identifier();
			id.setValue(sampleDoi);
			id.setIdentifierType("DOI");
			res.setIdentifier(id);
			
			doiClient.update(sampleDoi, null, res);
			Resource newRes = doiClient.getMetadata(sampleDoi);
			String resAsStr = getResourceAsString(newRes);
			LOGGER.trace(resAsStr);
		}
		catch (Exception e)
		{
			failOnException(e);
		}
	}

	@Ignore
	public void testDeactivate()
	{
		try
		{
			doiClient.deactivate(sampleDoi);
			assertTrue(doiClient.getDoiResponseAsString().indexOf("AbsolutePath:" + resource().getURI().toString() + "deactivate.xml/") != -1);
//			assertTrue(doiClient.getDoiResponseAsString().indexOf("QueryParam:app_id=TEST" + appId) != -1);
			assertTrue(doiClient.getDoiResponseAsString().indexOf("QueryParam:doi=" + sampleDoi) != -1);
		}
		catch (Exception e)
		{
			failOnException(e);
		}
	}

	@Ignore
	public void testActivate()
	{
		try
		{
			doiClient.activate(sampleDoi);
			assertTrue(doiClient.getDoiResponseAsString().indexOf("AbsolutePath:" + resource().getURI().toString() + "activate.xml/") != -1);
//			assertTrue(doiClient.getDoiResponseAsString().indexOf("QueryParam:app_id=TEST" + appId) != -1);
			assertTrue(doiClient.getDoiResponseAsString().indexOf("QueryParam:doi=" + sampleDoi) != -1);
		}
		catch (Exception e)
		{
			failOnException(e);
		}
	}

	@Test
	public void testGetDoiMetaData()
	{
		try
		{
			Resource res = doiClient.getMetadata(sampleDoi);
			StringWriter strW = new StringWriter();
			marshaller.marshal(res, strW);
//			assertTrue(doiClient.getDoiResponseAsString().indexOf("AbsolutePath:" + resource().getURI().toString() + "xml.xml/") != -1);
//			assertTrue(doiClient.getDoiResponseAsString().indexOf("QueryParam:doi=" + sampleDoi) != -1);
		}
		catch (Exception e)
		{
			failOnException(e);
		}
	}
	
	private Resource generateSampleResource()
	{
		Resource metadata = new Resource();

		Titles titles = new Titles();
		Title title1 = new Title();
		title1.setValue("Some title without a type");

		titles.getTitle().add(title1);

		metadata.setTitles(titles);

		Creators creators = new Creators();
		metadata.setCreators(creators);
		Creator creator = new Creator();
		CreatorName creatorName = new CreatorName();
		creatorName.setValue("Smith, John");
		creator.setCreatorName(creatorName);

		metadata.getCreators().getCreator().add(creator);

		metadata.setPublisher("Some random publisher");
		metadata.setPublicationYear("2010");

		return metadata;
	}
	
	private String getResourceAsString(Resource res) throws JAXBException
	{
		StringWriter strW = new StringWriter();
		marshaller.marshal(res, strW);
		return strW.toString();
	}

	private void failOnException(Throwable e)
	{
		LOGGER.error(e.getMessage(), e);
		fail(e.getMessage());
	}
}
