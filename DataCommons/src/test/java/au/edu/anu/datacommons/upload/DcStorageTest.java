package au.edu.anu.datacommons.upload;

import static org.junit.Assert.*;

import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.BagFile;
import gov.loc.repository.bagit.Manifest.Algorithm;
import gov.loc.repository.bagit.ManifestHelper;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collection;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.storage.DcStorage;
import au.edu.anu.datacommons.storage.DcStorageException;
import au.edu.anu.dcbag.VirusScanTxt;

public class DcStorageTest
{
	private static final Logger LOGGER = LoggerFactory.getLogger(DcStorageTest.class);
	private static DcStorage dcStorage;

	@ClassRule
	public static final TemporaryFolder tempDir = new TemporaryFolder();

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
	}

	@After
	public void tearDown() throws Exception
	{
	}

	@Test
	public void testDownloadFileAddToNewBag()
	{
		final String pid1 = "test:1";
		try
		{
			dcStorage.addFileToBag(pid1, "SomePdf.pdf", "http://samplepdf.com/sample.pdf");
			Bag bag = dcStorage.getBag(pid1);
			Collection<BagFile> plSet = bag.getPayload();
			assertEquals(1, plSet.size());
			for (BagFile bagFile : plSet)
			{
				LOGGER.debug("Filename: {}, ", new Object[] { bagFile.getFilepath(), bagFile.getSize() });
			}
		}
		catch (DcStorageException e)
		{
			failOnException(e);
		}
	}
	
	@Test
	public void testDownloadFileAddToExistingBag()
	{
		FileWriter fWriter;
		final String pid = "test:3";
		try
		{
			// Create a temp file.
			File file1 = tempDir.newFile("File1.txt");
			fWriter = new FileWriter(file1);
			String testStr1 = "This is a test string";
			fWriter.write(testStr1);
			fWriter.close();
			assertTrue(file1.exists());
			assertTrue(file1.length() == testStr1.length());
			
			// Add temp file to a new bag.
			dcStorage.addFileToBag(pid, file1);
			
			// Verify bag contains only 1 file.
			Bag bag = dcStorage.getBag(pid);
			Collection<BagFile> plFiles = bag.getPayload();
			assertEquals(1, plFiles.size());
			
			// Download file from URL and add to existing bag.
			dcStorage.addFileToBag(pid, "Some Pdf.pdf", "http://samplepdf.com/sample.pdf");
			assertTrue(dcStorage.fileExistsInBag(pid, "data/Some Pdf.pdf"));
			
			// Verify that the downloaded file is now included in the bag.
			bag.close();
			bag = dcStorage.getBag(pid);
			plFiles = bag.getPayload();
			assertEquals(2, plFiles.size());
			boolean downloadedFileExists = false;
			for (BagFile file : plFiles)
			{
				LOGGER.info("'{}', size {}.", file.getFilepath(), file.getSize());
				assertTrue(file.exists());
				if (file.getFilepath().equalsIgnoreCase("data/File1.txt"))
				{
					// Recheck the previously added file.
					assertTrue(file.getSize() == testStr1.length());
				}
				else if (file.getFilepath().equalsIgnoreCase("data/Some Pdf.pdf"))
				{
					// Check the size of the downloaded file.
					downloadedFileExists = true;
					assertEquals(218882L, file.getSize());
				}
			}
			assertTrue(downloadedFileExists);
		}
		catch (IOException e)
		{
			failOnException(e);
		}
		catch (DcStorageException e)
		{
			failOnException(e);
		}
		
	}

	@Test
	public void testBagFilesIntegrity()
	{
		FileWriter fWriter;
		final String pid = "test:2";
		Bag bag;
		try
		{
			// Create a temp file.
			File file1 = tempDir.newFile("File1.txt");
			fWriter = new FileWriter(file1);
			String testStr1 = "This is a test string";
			fWriter.write(testStr1);
			fWriter.close();
			assertTrue(file1.exists());
			assertTrue(file1.length() == testStr1.length());
			
			// Create another temp File.
			File file2 = tempDir.newFile("File2.txt");
			fWriter = new FileWriter(file2);
			String testStr2 = "This is a second test string";
			fWriter.write(testStr2);
			fWriter.close();
			assertTrue(file2.exists());
			assertTrue(file2.length() == testStr2.length());
			
			// Check that a bag for the pid being tested doesn't exist.
			bag = dcStorage.getBag(pid);
			assertNull(bag);
			
			// Add the first file to a bag.
			dcStorage.addFileToBag(pid, file1);
			
			// Check the contents of the file against the original string that was written to it.
			String textInFile = IOUtils.toString(dcStorage.getFileStream(pid, "data/" + file1.getName()), "UTF-8");
			textInFile.equals(testStr1);

			// Check the presence of all required tag files.
			bag = dcStorage.getBag(pid);
			Collection<BagFile> tagSet = bag.getTags();
			assertEquals(8, tagSet.size());
			for (BagFile tagFile : tagSet)
				if (!tagFile.getFilepath().equalsIgnoreCase(VirusScanTxt.FILEPATH))
					assertTrue(tagFile.getSize() > 0);
		}
		catch (IOException e)
		{
			failOnException(e);
		}
		catch (DcStorageException e)
		{
			failOnException(e);
		}
	}
	
	private void failOnException(Throwable e)
	{
		LOGGER.error(e.getMessage(), e);
		fail(e.getMessage());
	}
}
