package au.edu.anu.dcclient;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.ConnectException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.LoggingFilter;
import com.sun.jersey.test.framework.JerseyTest;

public class CustomClientTest
{
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomClientTest.class);
	
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

	@Ignore
	public void testSetAuth()
	{
		// This requires a trace server running on localhost.
		Client c = CustomClient.getInstance();
		CustomClient.setAuth("testuser", "testpass");
		CustomClient.setAuth("testuser2", "testpass2");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		c.addFilter(new LoggingFilter(ps));
		WebResource webResource = c.resource("http://localhost:10101/test");
		ClientResponse resp;
		String request = null;
		try
		{
			resp = webResource.get(ClientResponse.class);
		}
		catch (Exception e)
		{
			// Don't care about the response from server, if one.
		}

		request = baos.toString();
		LOGGER.debug(request);

		// Check that the authentication values are testuser2 and testpass2.
		assertTrue(request.contains("Authorization: Basic dGVzdHVzZXIyOnRlc3RwYXNzMg=="));
	}

}
