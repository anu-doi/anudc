package au.edu.anu.datacommons.webservice;

import static org.junit.Assert.fail;

import java.io.IOException;

import javax.ws.rs.core.MediaType;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.test.framework.JerseyTest;

public class WebServiceResourceTest extends JerseyTest
{
	private static final Logger LOGGER = LoggerFactory.getLogger(WebServiceResourceTest.class);
	private WebResource webResource;

	public WebServiceResourceTest()
	{
		super("au.edu.anu.datacommons.webservice");
	}

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
		webResource = resource().path("ws");
	}

	@After
	public void tearDown() throws Exception
	{
	}

	@Test
	public void testDoGetAsXml()
	{
		try
		{
			ClientResponse resp = webResource.get(ClientResponse.class);
			String msg = resp.getEntity(String.class);
			
			LOGGER.info("Response from server: " + msg);
		}
		catch (Exception e)
		{
			LOGGER.error(e.getMessage(), e);
			fail(e.getMessage());
		}
	}

	@Test
	public void testDoPostAsXml()
	{
		String xmlRequest = "<?xml version=\"1.0\"?>\r\n" + 
				"<dcrequest>\r\n" + 
				"    <action>Do Something</action>\r\n" + 
				"    <title>Some Title</title>\r\n" +
				"	<file>http://www.inkwelleditorial.com/pdfSample.pdf</file>\r\n" +
				"</dcrequest>\r\n";
		
		try
		{
			ClientResponse resp = webResource.type(MediaType.APPLICATION_XML).accept(MediaType.APPLICATION_XML_TYPE).post(ClientResponse.class, xmlRequest);
			LOGGER.info("Response HTTP Status: {}", String.valueOf(resp.getStatus()));
			LOGGER.info("Response:\r\n{}", resp.getEntity(String.class));
		}
		catch (Exception e)
		{
			LOGGER.error(e.getMessage(), e);
			fail(e.getMessage());
		}
	}

	@Ignore
	public void testServerStartup() throws IOException
	{
		System.in.read();
	}
}
