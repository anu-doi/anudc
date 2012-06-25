package au.edu.anu.dcclient.tasks;

import static org.junit.Assert.*;

import gov.loc.repository.bagit.BagFactory.LoadOption;

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import au.edu.anu.dcbag.DcBag;
import au.edu.anu.dcclient.Global;

public class DownloadBagTaskTest extends AbstractDcBagTaskTest
{
	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();
	
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
		super.setUp();
	}

	@After
	public void tearDown() throws Exception
	{
		super.tearDown();
	}

	@Test
	public void testCall() throws InterruptedException, ExecutionException
	{
		DownloadBagTask task = new DownloadBagTask(Global.getBagUploadUri(), "test:4", tempFolder.getRoot());
		ExecutorService execSvc = Executors.newFixedThreadPool(1);
		Future<File> result = execSvc.submit(task);
		File bagFile= result.get();
		assertTrue("Bag file wasn't saved locally.", bagFile.exists());
		DcBag dcBag = new DcBag(bagFile, LoadOption.BY_FILES);
		assertTrue("Bag is invalid.", dcBag.verifyValid().isSuccess());
	}
}
