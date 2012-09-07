package au.edu.anu.datacommons.webservice;

import static org.junit.Assert.*;
import static org.junit.Assert.fail;

import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.BagFile;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import javax.ws.rs.core.MediaType;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.storage.DcStorage;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.test.framework.JerseyTest;

public class WebServiceResourceTest extends JerseyTest
{
	private static final Logger LOGGER = LoggerFactory.getLogger(WebServiceResourceTest.class);

	private static DcStorage dcStorage;
	
	private WebResource webResource;
	
	@ClassRule
	public static final TemporaryFolder tempDir = new TemporaryFolder();

	public WebServiceResourceTest()
	{
		super("au.edu.anu.datacommons.webservice");
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		File bagsDir = tempDir.newFolder();
		LOGGER.info("Setting DcStorage location as: {}", bagsDir.getAbsolutePath());
		DcStorage.setLocation(bagsDir);
		dcStorage = DcStorage.getInstance();
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
			assertEquals(Status.OK, resp.getClientResponseStatus());
			String msg = resp.getEntity(String.class);
			
			LOGGER.debug("Response from server: " + msg);
			assertEquals("<?xml version=\"1.0\"?><SomeXmlTag>Hello World</SomeXmlTag>", msg);
		}
		catch (Exception e)
		{
			failOnException(e);
		}
	}

	@Test
	public void testDoPostAsXml()
	{
		String pid = "test:1";
		String filename = "SomeFile.pdf";
		String fileUrl = "http://samplepdf.com/sample.pdf";
		String xmlRequest = "<?xml version=\"1.0\"?>\r\n" + 
				"<dcrequest pid=\"" + pid + "\">\r\n" +
				"	<action>Do Something</action>\r\n" + 
				"	<title>Some Title</title>\r\n" +
				"	<file name=\"" + filename + "\">" + fileUrl + "</file>\r\n" +
				"</dcrequest>\r\n";
		Bag bag = null;
		try
		{
			ClientResponse resp = webResource.type(MediaType.APPLICATION_XML).accept(MediaType.APPLICATION_XML_TYPE).post(ClientResponse.class, xmlRequest);
			LOGGER.info("Response HTTP Status: {}", String.valueOf(resp.getStatus()));
			LOGGER.info("Response:\r\n{}", resp.getEntity(String.class));
			
			// Verify that the file's been downloaded and added to the bag.
			bag = dcStorage.getBag(pid);
			Collection<BagFile> plFiles = bag.getPayload();
			assertEquals(1, plFiles.size());
			for (BagFile file : plFiles)
			{
				assertTrue(file.exists());
				assertEquals("data/" + filename, file.getFilepath());
				assertEquals(218882L, file.getSize());
			}
		}
		catch (Exception e)
		{
			failOnException(e);
		}
		finally
		{
			IOUtils.closeQuietly(bag);
		}
	}

	@Ignore
	public void testServerStartup() throws IOException
	{
		System.in.read();
	}
	
	private void failOnException(Throwable e)
	{
		LOGGER.error(e.getMessage(), e);
		fail(e.getMessage());
	}
}
