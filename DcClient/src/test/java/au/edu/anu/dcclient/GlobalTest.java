package au.edu.anu.dcclient;

import static org.junit.Assert.*;

import java.net.URI;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class GlobalTest
{
	private static final Logger LOGGER = LoggerFactory.getLogger(Thread.currentThread().getClass());
	
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
	public void testGetBagUploadUri()
	{
		URI uri = Global.getBagUploadUri();
		Client client = Client.create();
		WebResource resource = client.resource(Global.getBagUploadUrl());
		try
		{
			ClientResponse clientResponse = resource.get(ClientResponse.class);
		}
		catch (Exception e)
		{
			fail("Unexpected response from application server: " + uri.toString());
		}
	}

}
