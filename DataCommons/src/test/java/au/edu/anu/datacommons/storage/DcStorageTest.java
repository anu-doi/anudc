package au.edu.anu.datacommons.storage;

import static org.junit.Assert.*;
import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.BagFile;
import gov.loc.repository.bagit.Manifest;
import gov.loc.repository.bagit.Manifest.Algorithm;
import gov.loc.repository.bagit.utilities.MessageDigestHelper;
import gov.loc.repository.bagit.writer.impl.ZipWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.dcbag.BagSummary;
import au.edu.anu.dcbag.ExtRefsTxt;
import au.edu.anu.dcbag.VirusScanTxt;

public class DcStorageTest
{
	private static final Logger LOGGER = LoggerFactory.getLogger(DcStorageTest.class);
	private static DcStorage dcStorage;
	private static int pidCounter = 1;

	@ClassRule
	public static final TemporaryFolder bagDir = new TemporaryFolder();
	
	@Rule
	public final TemporaryFolder tempDir = new TemporaryFolder();

	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		LOGGER.info("Setting DcStorage location as: {}", bagDir.getRoot());
		DcStorage.setLocation(bagDir.getRoot());
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
		final String pid1 = getNextPid();
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

	@Ignore
	public void testDownloadFileAddToExistingBag()
	{
		FileWriter fWriter;
		final String pid = getNextPid();
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
			bag.close();

			// Download file from URL and add to existing bag.
			dcStorage.addFileToBag(pid, "Some Pdf.pdf", "http://samplepdf.com/sample.pdf");
			assertTrue(dcStorage.fileExistsInBag(pid, "data/Some Pdf.pdf"));

			dcStorage.addFileToBag(pid, "Some Pdf.pdf", "http://www.stluciadance.com/prospectus_file/sample.pdf");

			// Verify the downloaded file is now included in the bag.
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
			IOUtils.closeQuietly(bag);
		}
	}

	@Ignore
	public void testBagFilesIntegrity()
	{
		FileWriter fWriter;
		final String pid = getNextPid();
		Bag bag;
		try
		{
			// Create a temp file.
			File file1 = bagDir.newFile("File1.txt");
			fWriter = new FileWriter(file1);
			String testStr1 = "This is a test string";
			fWriter.write(testStr1);
			fWriter.close();
			assertTrue(file1.exists());
			assertTrue(file1.length() == testStr1.length());

			// Create another temp File.
			File file2 = bagDir.newFile("File2.txt");
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
	public void testExtRef()
	{
		FileWriter fWriter;
		final String pid = getNextPid();
		Bag bag = null;
		BagSummary bagSummary;
		try
		{
			// Create temp file.
			File file1 = bagDir.newFile("File1.txt");
			fWriter = new FileWriter(file1);
			String testStr1 = "This is a test string";
			fWriter.write(testStr1);
			fWriter.close();

			dcStorage.addFileToBag(pid, file1);
			

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

			bagSummary = dcStorage.getBagSummary(pid);

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

			// Delete the 2 external references.
			dcStorage.deleteExtRef(pid, url1);
			bagSummary = dcStorage.getBagSummary(pid);

			// Verify the ext refs file contains the two URLs.
			assertFalse(bagSummary.getExtRefsTxt().containsValue(url1));
			assertTrue(bagSummary.getExtRefsTxt().containsValue(url2));

			dcStorage.deleteExtRef(pid, url2);
			bagSummary = dcStorage.getBagSummary(pid);

			// Verify the ext refs file contains the two URLs.
			assertFalse(bagSummary.getExtRefsTxt().containsValue(url1));
			assertFalse(bagSummary.getExtRefsTxt().containsValue(url2));
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
		final String pid = getNextPid();
		Bag bag = null;

		// Create a bag first.
		File file1;
		FileWriter fWriter;
		try
		{
			file1 = bagDir.newFile("First File.txt");
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

			File newBagZipFile = bagDir.newFile("NewBag.zip");

			// Create another bag.
			Bag newBag = DcStorage.bagFactory.createBag();
			File file2 = bagDir.newFile("Second File.txt");
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
			IOUtils.closeQuietly(bag);
		}
	}

	@Test
	public void testThreadedAdditions()
	{
		ExecutorService execSvc = Executors.newCachedThreadPool();
		Set<String> pids = new HashSet<String>();
		for (int i = 0; i < 50; i++)
		{
			String pid = getNextPid();
			pids.add(pid);
			AddBagFileWorker thread = new AddBagFileWorker(pid);
			execSvc.execute(thread);
		}
		execSvc.shutdown();
		try
		{
			execSvc.awaitTermination(10, TimeUnit.MINUTES);
		}
		catch (InterruptedException e)
		{
			failOnException(e);
		}

		LOGGER.debug("Finished");
	}
	
	@Test
	public void testDeletions()
	{
		String pid = getNextPid();
		try
		{
			File file1 = createFile(2);
			File file2 = createFile(3);
			LOGGER.info("Adding {} to {}", file1.getAbsolutePath(), pid);
			dcStorage.addFileToBag(pid, file1);
			LOGGER.info("Adding {} to {}", file2.getAbsolutePath(), pid);
			dcStorage.addFileToBag(pid, file2);
			File fileToDelete = new File(dcStorage.getBagDir(pid), "data/" + file1.getName());
			while (!fileToDelete.exists());
			dcStorage.deleteFileFromBag(pid, "data/" + file1.getName());
		}
		catch (IOException e)
		{
			failOnException(e);
		}
		catch (DcStorageException e)
		{
			failOnException(e);
		}
		LOGGER.debug("Finished");
	}

	private String calcMD5(File file)
	{
		return MessageDigestHelper.generateFixity(file, Algorithm.MD5);
	}

	private String calcMD5(InputStream is)
	{
		return MessageDigestHelper.generateFixity(is, Algorithm.MD5);
	}

	private File createFile(int sizeInMB) throws IOException
	{
		File file = null;
		FileOutputStream fileStream = null;
		try
		{
			file = tempDir.newFile();
			LOGGER.debug("Generating file {} of size {} MB...", file.getAbsolutePath(), sizeInMB);
			fileStream = new FileOutputStream(file);
			for (int i = 0; i < sizeInMB; i++)
				fileStream.write(getRandomByteArray(1024 * 1024));
			LOGGER.debug("Written file {} of size {} MB.", file.getAbsolutePath(), sizeInMB);
		}
		finally
		{
			IOUtils.closeQuietly(fileStream);
		}

		return file;
	}

	private byte[] getRandomByteArray(int size)
	{
		byte[] bytes = new byte[size];
		Random rand = new Random();
		rand.nextBytes(bytes);
		return bytes;
	}
	
	private synchronized String getNextPid()
	{
		return "test:" + pidCounter++;
	}

	private void failOnException(Throwable e)
	{
		LOGGER.error(e.getMessage(), e);
		fail(e.getMessage());
	}

	private class AddBagFileWorker implements Runnable
	{
		private final int FILESIZE = 2;
		private String pid;
		
		public AddBagFileWorker(String pid)
		{
			this.pid = pid;
		}
		
		@Override
		public void run()
		{
			// Create a file, get it's details. Add the file to bag.
			try
			{
				File file1 = createFile(FILESIZE);
				String md5sum1 = calcMD5(file1);
				LOGGER.info("Adding {} to {}...", file1, pid);
				dcStorage.addFileToBag(pid, file1);
				
				File file2 = createFile(FILESIZE);
				String md5sum2 = calcMD5(file2);
				dcStorage.addFileToBag(pid, file2);

				File replacementFile1 = createFile(FILESIZE);
				file1.delete();
				if (!replacementFile1.renameTo(file1))
					fail(MessageFormat.format("Unable to rename {0} to {1}.", replacementFile1.getAbsolutePath(), file1.getAbsolutePath()));
				String md5sumReplacement1 = calcMD5(file1);
				dcStorage.addFileToBag(pid, file1);
				
//				assertEquals(md5sum1, calcMD5(dcStorage.getFileStream(pid, "data/" + file1.getName())));
				assertEquals(md5sum2, calcMD5(dcStorage.getFileStream(pid, "data/" + file2.getName())));
				assertEquals(md5sumReplacement1, calcMD5(dcStorage.getFileStream(pid, "data/" + file1.getName())));
			}
			catch (Exception e)
			{
				failOnException(e);
			}
		}
		
	}
}
