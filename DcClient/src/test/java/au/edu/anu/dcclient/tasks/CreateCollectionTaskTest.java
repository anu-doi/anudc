package au.edu.anu.dcclient.tasks;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.core.UriBuilder;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.dcclient.Global;
import au.edu.anu.dcclient.collection.CollectionInfo;

public class CreateCollectionTaskTest extends AbstractDcBagTaskTest
{
	private static final Logger LOGGER = LoggerFactory.getLogger(CreateCollectionTaskTest.class);
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		AbstractDcBagTaskTest.setUpBeforeClass();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception
	{
		AbstractDcBagTaskTest.tearDownAfterClass();
	}

	@Before
	public void setUp() throws Exception
	{
		super.setUp();
	}

	@After
	public void tearDown() throws Exception
	{
		super.tearDown();
	}

	@Ignore
	public void testCreateCollectionTask() throws Exception
	{
		CollectionInfo ci = new CollectionInfo(new File(this.getClass().getResource("collinfotest.properties").toURI()));
		CreateCollectionTask createCollTask = new CreateCollectionTask(ci, Global.getCreateUri());
		String createdPid = createCollTask.call();
		LOGGER.info("Created Pid {}", createdPid);
		assertNotNull("Created object has a null pid", createdPid);
		assertNotSame(0, createdPid.length());
	}
	
}
