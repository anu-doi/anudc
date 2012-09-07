package au.edu.anu.datacommons.upload;

import static org.junit.Assert.*;
import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.BagFactory;
import gov.loc.repository.bagit.BagFactory.LoadOption;
import gov.loc.repository.bagit.writer.Writer;
import gov.loc.repository.bagit.writer.impl.FileSystemWriter;
import gov.loc.repository.bagit.writer.impl.ZipWriter;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;

import javax.ws.rs.core.MediaType;

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
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.multipart.FormDataMultiPart;
import com.sun.jersey.multipart.file.FileDataBodyPart;
import com.sun.jersey.test.framework.AppDescriptor;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.spi.container.TestContainer;

public class UploadServiceTest extends JerseyTest
{
	private static final Logger LOGGER = LoggerFactory.getLogger(UploadServiceTest.class);
	private static DcStorage dcStorage;
	
	private WebResource webResource;
	
	@ClassRule
	public static final TemporaryFolder tempDir = new TemporaryFolder();

	public UploadServiceTest()
	{
		super("au.edu.anu.datacommons.upload");
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

	@Test()
	public void testDoGetAsHtml()
	{
		ClientResponse resp = webResource.get(ClientResponse.class);
		LOGGER.debug("HTTP Response: {}", resp.getStatus());
		assertEquals(404, resp.getStatus());
	}
	
	private void failOnException(Throwable e)
	{
		LOGGER.error(e.getMessage(), e);
		fail(e.getMessage());
	}
}
