package au.edu.anu.dcclient.tasks;

import static org.junit.Assert.*;

import gov.loc.repository.bagit.ProgressListener;

import java.net.URI;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.ws.rs.core.UriBuilder;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.dcclient.Global;
import au.edu.anu.dcclient.tasks.GetInfoTask;

import com.sun.jersey.api.client.ClientResponse;

public class GetInfoTaskTest extends AbstractDcBagTaskTest
{
	private static final Logger LOGGER = LoggerFactory.getLogger(Thread.currentThread().getClass());
	private static final String PID = "test:4";

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
	public void testCall() throws InterruptedException, ExecutionException
	{
		URI pidBagUri = UriBuilder.fromUri(Global.getBagUploadUri()).path(PID).build();
		ExecutorService execSvc = Executors.newFixedThreadPool(1);
		GetInfoTask task = new GetInfoTask(pidBagUri);
		task.addProgressListener(getProgressListener());
		Future<ClientResponse> pidInfoResp = execSvc.submit(task);
		assertNotNull(pidInfoResp);
		assertNotNull("No response received from server. Check if the server's up and running.", pidInfoResp.get());
		LOGGER.info("HTTP Status: {}", pidInfoResp.get().getStatus());
	}

}
