package au.edu.anu.dcclient.tasks;

import static org.junit.Assert.assertNotNull;

import java.net.Authenticator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.dcclient.DcAuthenticator;
import au.edu.anu.dcclient.Global;

import com.sun.jersey.api.client.ClientResponse;

public class GetInfoTaskTest extends AbstractDcBagTaskTest
{
	private static final Logger LOGGER = LoggerFactory.getLogger(GetInfoTaskTest.class);
	private static final String PID = "test:50";

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
	public void testGetInfoTask() throws InterruptedException, ExecutionException
	{
		Authenticator.setDefault(new DcAuthenticator("rahul.khanna@anu.edu.au", "user"));
		ExecutorService execSvc = Executors.newFixedThreadPool(1);
		GetInfoTask task = new GetInfoTask(Global.getBagUploadUri(), PID);
		task.addProgressListener(getProgressListener());
		Future<ClientResponse> pidInfoResp = execSvc.submit(task);
		assertNotNull(pidInfoResp);
		assertNotNull("No response received from server. Check if the server's up and running.", pidInfoResp.get());
		LOGGER.info("HTTP Status: {}", pidInfoResp.get().getStatus());
	}

}
