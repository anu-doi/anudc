package au.edu.anu.datacommons.doi;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.tika.io.IOUtils;
import org.datacite.schema.kernel_2.Resource;
import org.jaxen.function.ContainsFunction;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.xml.data.Data;

public class DoiResourceAdapterTest
{
	private static final Logger LOGGER = LoggerFactory.getLogger(DoiResourceAdapterTest.class);

	private static JAXBContext dataContext;
	private Unmarshaller um;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		dataContext = JAXBContext.newInstance(Data.class);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception
	{
	}

	@Before
	public void setUp() throws Exception
	{
		um = dataContext.createUnmarshaller();
	}

	@After
	public void tearDown() throws Exception
	{
	}

	@Test
	public void testDataResourceAdapter()
	{
		DoiResourceAdapter adapter;
		try
		{
			adapter = new DoiResourceAdapter(getData());
			Resource doiRes = adapter.getDoiResource();
			
			assertNotNull(doiRes);

			// Titles
			assertNotNull(doiRes.getTitles());
			assertNotNull(doiRes.getTitles().getTitle());
			assertEquals(2, doiRes.getTitles().getTitle().size());
			
			// Creators
			assertNotNull(doiRes.getCreators());
			assertNotNull(doiRes.getCreators().getCreator());
			assertEquals(2, doiRes.getCreators().getCreator().size());
			
			// Publisher
			assertEquals(doiRes.getPublisher(), "Australian Data Archive");
			// Publication Year
			assertEquals(doiRes.getPublicationYear(), "1987");
			
			
			System.out.println();
		}
		catch (DoiException e)
		{
			failOnException(e);
		}
		catch (JAXBException e)
		{
			failOnException(e);
		}
	}

	private Data getData() throws JAXBException
	{
		Data data;
		InputStream dataStream = null;
		try
		{
			dataStream = DoiResourceAdapterTest.class.getResourceAsStream("samplefedoraDS.xml");
			data = (Data) um.unmarshal(dataStream);
		}
		finally
		{
			IOUtils.closeQuietly(dataStream);
		}
		
		return data;
	}

	private void failOnException(Throwable e)
	{
		LOGGER.error(e.getMessage(), e);
		fail(e.getMessage());
	}
}
