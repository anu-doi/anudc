package au.edu.anu.dcclient.tasks;

import static org.junit.Assert.*;

import gov.loc.repository.bagit.BagFactory.LoadOption;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
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

	@Ignore
	public void testDownloadBagTask() throws InterruptedException, ExecutionException
	{
		DownloadBagTask task = new DownloadBagTask(Global.getBagUploadUri(), "test:1", tempFolder.getRoot());
		ExecutorService execSvc = Executors.newFixedThreadPool(1);
		Future<File> result = execSvc.submit(task);
		File bagFile= result.get();
		assertTrue("Bag file wasn't saved locally.", bagFile.exists());
		DcBag dcBag = new DcBag(bagFile, LoadOption.BY_FILES);
		assertTrue("Bag is invalid.", dcBag.verifyValid().isSuccess());
	}
}
