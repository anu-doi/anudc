package au.edu.anu.datacommons.upload;

import static org.junit.Assert.*;

import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.BagFile;
import gov.loc.repository.bagit.Manifest;
import gov.loc.repository.bagit.Manifest.Algorithm;
import gov.loc.repository.bagit.ManifestHelper;
import gov.loc.repository.bagit.utilities.MessageDigestHelper;
import gov.loc.repository.bagit.writer.impl.ZipWriter;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Map.Entry;

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
import au.edu.anu.dcbag.BagSummary;
import au.edu.anu.dcbag.ExtRefsTxt;
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
		Bag bag = null;
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
			bag = dcStorage.getBag(pid);
			Collection<BagFile> plFiles = bag.getPayload();
			assertEquals(1, plFiles.size());
			BagFile bagFile = bag.getBagFile("data/File1.txt");
			// Check file entry exists in all payload manifests.
			for (Manifest plManifest : bag.getPayloadManifests())
			{
				assertTrue(plManifest.containsKey("data/File1.txt"));
				MessageDigestHelper.fixityMatches(bagFile.newInputStream(), plManifest.getAlgorithm(), plManifest.get("data/File1.txt"));
			}

			// Download file from URL and add to existing bag.
			dcStorage.addFileToBag(pid, "Some Pdf.pdf", "http://samplepdf.com/sample.pdf");
			assertTrue(dcStorage.fileExistsInBag(pid, "data/Some Pdf.pdf"));
			
			dcStorage.addFileToBag(pid, "Some Pdf.pdf", "http://www.stluciadance.com/prospectus_file/sample.pdf");

			// Verify the downloaded file is now included in the bag.
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
					assertEquals(54836L, file.getSize());
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
		finally
		{
			if (bag != null)
			{
				try
				{
					bag.close();
				}
				catch (IOException e)
				{
					LOGGER.warn(e.getMessage(), e);
				}
			}
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

	@Test
	public void testAddExtRef()
	{
		FileWriter fWriter;
		final String pid = "test:4";
		Bag bag = null;
		try
		{
			// Create temp file.
			File file1 = tempDir.newFile("File1.txt");
			fWriter = new FileWriter(file1);
			String testStr1 = "This is a test string";
			fWriter.write(testStr1);
			fWriter.close();

			dcStorage.addFileToBag(pid, file1);
			assertTrue(file1.exists());
			assertTrue(file1.length() == testStr1.length());

			// Add one external references.
			String url1 = "http://www.google.com.au:8080/";
			String url2 = "http://www.twitter.com:9999/";
			dcStorage.addExtRef(pid, url1);

			// Verify ext-refs.txt exists.
			bag = dcStorage.getBag(pid);
			Collection<BagFile> tagFiles = bag.getTags();
			boolean extRefsTagFileExists = false;
			for (BagFile tagFile : tagFiles)
			{
				if (tagFile.getFilepath().equals("ext-refs.txt"))
				{
					extRefsTagFileExists = true;
					break;
				}
			}
			assertTrue("The file ext-refs.txt doesn't exist in bag.", extRefsTagFileExists);
			// Check the file's entry exists in all tag manifests.
			for (Manifest tagManifest : bag.getTagManifests())
			{
				assertTrue(tagManifest.containsKey(ExtRefsTxt.FILEPATH));
				assertTrue(MessageDigestHelper.fixityMatches(bag.getBagFile(ExtRefsTxt.FILEPATH).newInputStream(), tagManifest.getAlgorithm(),
						tagManifest.get(ExtRefsTxt.FILEPATH)));
			}

			// Add second external reference.
			dcStorage.addExtRef(pid, url2);

			BagSummary bagSummary = dcStorage.getBagSummary(pid);

			// Verify the ext refs file contains the two URLs.
			assertTrue(bagSummary.getExtRefsTxt().containsValue(url1));
			assertTrue(bagSummary.getExtRefsTxt().containsValue(url2));

			// Check that the tagfile itself exists.
			bag = dcStorage.getBag(pid);
			tagFiles = bag.getTags();
			extRefsTagFileExists = false;
			for (BagFile tagFile : tagFiles)
			{
				if (tagFile.getFilepath().equals("ext-refs.txt"))
				{
					extRefsTagFileExists = true;
					break;
				}
			}
			assertTrue("The file ext-refs.txt doesn't exist in bag.", extRefsTagFileExists);
			// Check the file's entry exists in all tag manifests.
			for (Manifest tagManifest : bag.getTagManifests())
			{
				assertTrue(tagManifest.containsKey(ExtRefsTxt.FILEPATH));
				assertTrue(MessageDigestHelper.fixityMatches(bag.getBagFile(ExtRefsTxt.FILEPATH).newInputStream(), tagManifest.getAlgorithm(),
						tagManifest.get(ExtRefsTxt.FILEPATH)));
			}
		}
		catch (IOException e)
		{
			failOnException(e);
		}
		catch (DcStorageException e)
		{
			failOnException(e);
		}
		finally
		{
			try
			{
				if (bag != null)
					bag.close();
			}
			catch (IOException e)
			{
				LOGGER.warn(e.getMessage(), e);
			}
		}
	}
	
	@Test
	public void testReplaceBag()
	{
		final String pid = "test:5";
		Bag bag = null;
		
		// Create a bag first.
		File file1;
		FileWriter fWriter;
		try
		{
			file1 = tempDir.newFile("First File.txt");
			fWriter = new FileWriter(file1);
			fWriter.write("Test String");
			fWriter.close();
			
			dcStorage.addFileToBag(pid, file1);
			bag = dcStorage.getBag(pid);
			
			// Verify file exists in bag.
			boolean fileExists = false;
			for (BagFile plFile : bag.getPayload())
			{
				if (plFile.getFilepath().equalsIgnoreCase("data/" + file1.getName()))
				{
					fileExists = true;
					break;
				}
			}
			assertTrue(fileExists);
			
			File newBagZipFile = tempDir.newFile("NewBag.zip");
			
			// Create another bag.
			Bag newBag = DcStorage.bagFactory.createBag();
			File file2 = tempDir.newFile("Second File.txt");
			fWriter = new FileWriter(file2);
			fWriter.write("Test string in second file.");
			fWriter.close();
			newBag.addFileToPayload(file2);
			newBag.putBagFile(DcStorage.bagFactory.getBagPartFactory().createBagItTxt());
			newBag.putBagFile(DcStorage.bagFactory.getBagPartFactory().createBagInfoTxt());
			newBag.getBagInfoTxt().addExternalIdentifier(pid);
			newBag = newBag.makeComplete();
			newBag = new ZipWriter(DcStorage.bagFactory).write(newBag, newBagZipFile);
			assertTrue(newBagZipFile.exists());
			assertTrue(newBag.verifyValid().isSuccess());
			
			// Replace existing bag with new one.
			dcStorage.storeBag(pid, newBag);
			bag = dcStorage.getBag(pid);
			
			// Verify the bag.
			assertTrue(bag.verifyValid().isSuccess());
			
			// First file shouldn't exist and second file should.
			for (Manifest plManifest : bag.getPayloadManifests())
			{
				assertFalse(plManifest.containsKey("data/" + file1.getName()));
				assertTrue(plManifest.containsKey("data/" + file2.getName()));
			}
			
			// Verify tag files.
			for (Manifest tagManifest : bag.getTagManifests())
			{
				assertFalse(tagManifest.containsKey("metadata/" + file1.getName() + ".ser"));
				assertFalse(tagManifest.containsKey("metadata/" + file1.getName() + ".xmp"));
				assertTrue(tagManifest.containsKey("metadata/" + file2.getName() + ".ser"));
				assertTrue(tagManifest.containsKey("metadata/" + file2.getName() + ".xmp"));
			}
			
		}
		catch (IOException e)
		{
			failOnException(e);
		}
		catch (DcStorageException e)
		{
			failOnException(e);
		}
		finally
		{
			if (bag != null)
			{
				try
				{
					bag.close();
				}
				catch (IOException e)
				{
					LOGGER.warn(e.getMessage(), e);
				}
			}
		}
	}

	private void failOnException(Throwable e)
	{
		LOGGER.error(e.getMessage(), e);
		fail(e.getMessage());
	}
}
