package au.edu.anu.datacommons.webservice.bindings;

import static org.junit.Assert.fail;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CombinedStatusResponseTest
{
	private static final Logger LOGGER = LoggerFactory.getLogger(CombinedStatusResponseTest.class);
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception
	{
	}

	@Before
	public void setUp() throws Exception
	{
	}

	@After
	public void tearDown() throws Exception
	{
	}

	@Test
	public void test()
	{
		CombinedStatusResponse resp = new CombinedStatusResponse();
		resp.setStatus(CombinedStatusResponse.Status.SUCCESS);
		resp.setMsg("Completed");
		
		try
		{
			JAXBContext context = JAXBContext.newInstance("au.edu.anu.datacommons.phenomics.bindings");
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			m.marshal(resp, System.out);
		}
		catch (JAXBException e)
		{
			failOnException(e);
		}
		
	}

	private void failOnException(Throwable e)
	{
		LOGGER.error(e.getMessage(), e);
		fail(e.getMessage());
	}
}
