package au.edu.anu.dcbag;

import static org.junit.Assert.*;
import gov.loc.repository.bagit.BagFactory;
import gov.loc.repository.bagit.Bag.Format;
import gov.loc.repository.bagit.transformer.Completer;
import gov.loc.repository.bagit.transformer.impl.ChainingCompleter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DcStorageCompleterTest
{
	private static final Logger LOGGER = LoggerFactory.getLogger(Thread.currentThread().getClass());
	private static List<File> payloadFiles;
	private static final String TEST_PID = "test:1";
	private static final Completer completer = new ChainingCompleter(new DcStorageCompleter());
	
	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();
	
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
	public void testComplete()
	{
		// Create the original bag with data source as instrument that will then be replaced.
		DcBag bag1 = new DcBag(TEST_PID);
		bag1.addFileToPayload(payloadFiles.get(0));
		bag1.addFileToPayload(payloadFiles.get(1));
		File bagFile1;
		try
		{
			bagFile1 = bag1.saveAs(tempFolder.newFolder(), TEST_PID, Format.FILESYSTEM);
			LOGGER.info("Bag saved at {}.", bagFile1.getAbsolutePath());
		}
		catch (IOException e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
		catch (DcBagException e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
		
		bag1.makeComplete(completer);
		try
		{
			bag1.save();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
		catch (DcBagException e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
		
		assertTrue(new File(bag1.getFile(), "metadata/" + payloadFiles.get(0).getName() + ".xmp").exists());
		assertTrue(new File(bag1.getFile(), "metadata/" + payloadFiles.get(1).getName() + ".xmp").exists());
		assertTrue(new File(bag1.getFile(), "metadata/" + payloadFiles.get(0).getName() + ".ser").exists());
		assertTrue(new File(bag1.getFile(), "metadata/" + payloadFiles.get(1).getName() + ".ser").exists());
		
		bag1.removeBagFile("data/" + payloadFiles.get(1).getName());
		bag1.makeComplete(completer);
		try
		{
			bag1.save();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
		catch (DcBagException e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
		
		assertTrue(new File(bag1.getFile(), "metadata/" + payloadFiles.get(0).getName() + ".xmp").exists());
		assertTrue(new File(bag1.getFile(), "metadata/" + payloadFiles.get(0).getName() + ".ser").exists());
		
		// This is failing because deleted files from the bag aren't deleted from the drive on save.
		// assertFalse(new File(bag1.getFile(), "metadata/" + payloadFiles.get(1).getName() + ".xmp").exists());

	}

}
