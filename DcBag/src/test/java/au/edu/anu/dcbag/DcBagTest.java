package au.edu.anu.dcbag;

import static org.junit.Assert.*;

import gov.loc.repository.bagit.Bag.Format;
import gov.loc.repository.bagit.BagFactory;
import gov.loc.repository.bagit.ProgressListener;
import gov.loc.repository.bagit.BagFactory.LoadOption;
import gov.loc.repository.bagit.v0_97.impl.BagImpl;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.dcbag.DcBag;

public class DcBagTest
{
	private static final Logger LOGGER = LoggerFactory.getLogger(Thread.currentThread().getClass());
	private static List<File> payloadFiles;
	private static File bagDir;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		payloadFiles = new ArrayList<File>();
		assertNotNull(payloadFiles);

		payloadFiles.add(new File(DcBagTest.class.getResource("1M.fil").toURI()));
		payloadFiles.add(new File(DcBagTest.class.getResource("2M.fil").toURI()));
		payloadFiles.add(new File(DcBagTest.class.getResource("5M.fil").toURI()));
		payloadFiles.add(new File(DcBagTest.class.getResource("10M.fil").toURI()));
		assertEquals(payloadFiles.size(), 4);

		bagDir = new File(payloadFiles.get(0).getParentFile(), "bag");
		assertNotNull(bagDir);

		if (!bagDir.exists())
			bagDir.mkdirs();
		else if (bagDir.listFiles().length > 0)
		{
			deleteTree(bagDir);
			bagDir.mkdirs();
		}
		assertTrue(bagDir.exists());
		assertTrue(bagDir.isDirectory());
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
	public void testCreateNewBag() throws Exception
	{
		// Create new bag.
		DcBag dcBag = new DcBag("test:1");
		assertNotNull(dcBag);

		// Add files to payload.
		dcBag.addFileToPayload(payloadFiles.get(0));
		dcBag.addFileToPayload(payloadFiles.get(1));
		dcBag.addFileToPayload(payloadFiles.get(2));
		File bagFile = dcBag.saveAs(bagDir, "test:1", Format.FILESYSTEM);
		assertEquals("Files in payload not = 3", 3, dcBag.getPayloadFileList().size());
		assertTrue("Bag file doesn't exist.", bagFile.exists());
		assertTrue("Bag is invalid.", dcBag.verifyValid().isSuccess());

		// Close the bag.
		dcBag.close();
		dcBag = null;
		
		// Reopen the bag, add and remove files. Save.
		dcBag = new DcBag(bagDir, "test:1", LoadOption.BY_FILES);
		dcBag.addFileToPayload(payloadFiles.get(3));
		dcBag.removeBagFile("data/" + payloadFiles.get(2).getName());
		dcBag.addProgressListener(getProgressListener());
		dcBag.save();
		
		dcBag.close();
		dcBag = null;
		
		dcBag = new DcBag(bagDir, "test:1", LoadOption.BY_FILES);
		assertTrue("External identifier not test:1", dcBag.getExternalIdentifier().equals("test:1"));
		assertEquals("Number of files in payload is not 3.", 3, dcBag.getPayloadFileList().size());
	}
	

	private static void deleteTree(File dirToDelete) throws InterruptedException
	{
		File[] filesInUploadDir = dirToDelete.listFiles();
		for (int i = 0; i < filesInUploadDir.length; i++)
		{
			// If it's a file, delete it. Else, if dir, recurse through it.
			if (filesInUploadDir[i].isDirectory())
			{
				deleteTree(filesInUploadDir[i]);
				filesInUploadDir[i].delete();
			}
			else if (filesInUploadDir[i].isFile())
				filesInUploadDir[i].delete();
			else
				fail("Unknown file/dir type in base upload dir.");

			while (filesInUploadDir[i].exists())
				Thread.sleep(10);
		}
	}
	
	private ProgressListener getProgressListener()
	{
		return new ProgressListener()
		{
			@Override
			public void reportProgress(String activity, Object item, Long count, Long total)
			{
				LOGGER.info("Progress: Activity '{}', item '{}', count {}, total {}.", new Object[] { activity, item, count, total });
			}
		};
	}
}
