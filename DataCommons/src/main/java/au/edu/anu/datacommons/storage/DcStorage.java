package au.edu.anu.datacommons.storage;

import static java.text.MessageFormat.format;
import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.BagFactory;
import gov.loc.repository.bagit.BagFactory.LoadOption;
import gov.loc.repository.bagit.BagFile;
import gov.loc.repository.bagit.Manifest;
import gov.loc.repository.bagit.ManifestHelper;
import gov.loc.repository.bagit.transformer.Completer;
import gov.loc.repository.bagit.transformer.impl.ChainingCompleter;
import gov.loc.repository.bagit.transformer.impl.DefaultCompleter;
import gov.loc.repository.bagit.transformer.impl.TagManifestCompleter;
import gov.loc.repository.bagit.transformer.impl.UpdatePayloadOxumCompleter;
import gov.loc.repository.bagit.utilities.SimpleResult;
import gov.loc.repository.bagit.writer.impl.FileSystemWriter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.data.fedora.FedoraBroker;
import au.edu.anu.datacommons.properties.GlobalProps;
import au.edu.anu.dcbag.BagSummary;
import au.edu.anu.dcbag.ExtRefsTxt;
import au.edu.anu.dcbag.FileSummaryMap;
import au.edu.anu.dcbag.BagPropsTxt.DataSource;

import com.yourmediashelf.fedora.client.FedoraClientException;

public final class DcStorage
{
	private static final Logger LOGGER = LoggerFactory.getLogger(DcStorage.class);

	private static Set<Manifest.Algorithm> algorithms;
	private static DcStorage inst = null;
	private static Set<String> lockedPids = Collections.synchronizedSet(new HashSet<String>());
	private static File bagsDir = null;
	private static Completer tagFilesCompleter;
	private static Completer preSaveCompleter;
	private static Completer postSaveCompleter;
	private static FileSystemWriter writer;

	public static final BagFactory bagFactory = new BagFactory();

	/**
	 * Constructor for DcStorage. Initialises the hashing algorithm(s) to use, completers, and writers.
	 */
	protected DcStorage()
	{
		// Algorithms for manifest files.
		algorithms = new HashSet<Manifest.Algorithm>();
		algorithms.add(Manifest.Algorithm.MD5);

		// Bag Completers.
		preSaveCompleter = new ChainingCompleter(new DefaultCompleter(bagFactory));
		postSaveCompleter = new ChainingCompleter(new DcStorageCompleter(), new TagManifestCompleter(bagFactory));
		tagFilesCompleter = new ChainingCompleter(new TagManifestCompleter(bagFactory), new UpdatePayloadOxumCompleter(bagFactory));
		
		// Bag Writer
		writer = new FileSystemWriter(bagFactory);
		writer.setSkipIfPayloadFileExists(false);

		// Set storage location if not set already.
		if (bagsDir == null)
			bagsDir = GlobalProps.getBagsDirAsFile();

		// If the directory specified doesn't exist, create it.
		if (!bagsDir.exists() && !bagsDir.mkdirs())
		{
			throw new RuntimeException(format("Unable to create {0}. Check permissions.", bagsDir.getAbsolutePath()));
		}
	}

	/**
	 * Returns the singleton instance of DcStorage object.
	 * 
	 * @return DcStorage object as DcStorage
	 */
	public static synchronized DcStorage getInstance()
	{
		if (inst == null)
			inst = new DcStorage();
		return inst;
	}

	public static void setLocation(File newBagsDir)
	{
		if (inst == null)
			bagsDir = newBagsDir;
		else
			throw new RuntimeException("Can't change storage location after instantiation.");
	}

	/**
	 * Downloads a file from a specified URL and adds it to the bag of a specified collection.
	 * 
	 * @param pid
	 *            Pid of the collection record
	 * @param filename
	 *            Filename to save as
	 * @param fileUrl
	 *            URL of the externally hosted file as String
	 * @throws DcStorageException
	 */
	public void addFileToBag(String pid, String filename, String fileUrl) throws DcStorageException
	{
		try
		{
			addFileToBag(pid, filename, new URL(fileUrl));
		}
		catch (MalformedURLException e)
		{
			DcStorageException dce = new DcStorageException(e.getMessage(), e);
			LOGGER.error(e.getMessage(), dce);
			throw dce;
		}
	}

	/**
	 * Downloads a file from a specified URL and adds it to the bag of a specified collection.
	 * 
	 * @param pid
	 *            Pid of the collection record
	 * @param filename
	 *            Filename to save as
	 * @param fileUrl
	 *            URL from where to download the file
	 * @throws DcStorageException
	 */
	public void addFileToBag(String pid, String filename, URL fileUrl) throws DcStorageException
	{
		File tempFile = null;
		File tempRenamedFile = null;
		try
		{
			tempFile = File.createTempFile("DcTempFile", null);
			tempRenamedFile = new File(tempFile.getParentFile(), filename);
			if (!tempFile.renameTo(tempRenamedFile))
				throw new IOException(format("Unable to rename {0} to {1}.", tempFile.getAbsolutePath(), tempRenamedFile.getAbsolutePath()));

			LOGGER.debug("Saving {} as {}...", fileUrl.toString(), tempRenamedFile.getAbsolutePath());
			// Connection and read timeout set to 30 seconds.
			FileUtils.copyURLToFile(fileUrl, tempRenamedFile, 30000, 30000);
			addFileToBag(pid, tempRenamedFile);
		}
		catch (IOException e)
		{
			DcStorageException dce = new DcStorageException(e);
			LOGGER.error(e.getMessage(), dce);
			throw dce;
		}
		finally
		{
			// Delete the file downloaded from URL.
			FileUtils.deleteQuietly(tempFile);
			FileUtils.deleteQuietly(tempRenamedFile);
		}
	}

	/**
	 * Adds a local file to a collection's bag.
	 * 
	 * @param pid
	 *            Pid of the collection record
	 * @param file
	 *            File object
	 * @throws DcStorageException
	 */
	public void addFileToBag(String pid, File file) throws DcStorageException
	{
		// TODO Lock Pid
		Bag bag = null;
		try
		{
			bag = getBag(pid);
			if (bag == null)
				bag = createBlankBag(pid);
			else
			{
				archiveBag(bag, false);
				// Reload bag instance because now it points to the archived bag.
				bag = getBag(pid);
			}
			bag.addFileToPayload(file);
			bag = bag.makeComplete(preSaveCompleter);
			bag = writeBag(bag, pid);
			bag = bag.makeComplete(postSaveCompleter);
			bag = writeBag(bag, pid);
		}
		catch (IOException e)
		{
			DcStorageException dce = new DcStorageException(e);
			LOGGER.error(e.getMessage(), dce);
			throw dce;
		}
		finally
		{
			IOUtils.closeQuietly(bag);
		}
	}
	
	public void deleteFileFromBag(String pid, String bagFilePath) throws DcStorageException 
	{
		Bag bag = null;
		if (!bagExists(pid))
			throw new DcStorageException(format("No files present in pid {0}.", bagFilePath));
		try
		{
			bag = getBag(pid);
			if (!fileExistsInBag(pid, bagFilePath))
				throw new DcStorageException(format("File {0} doesn't exist in pid {1}.", bagFilePath, pid));
			archiveBag(bag, false);
			bag.removeBagFile(bagFilePath);
			bag = bag.makeComplete(preSaveCompleter);
			bag = bag.makeComplete(postSaveCompleter);
			bag = writeBag(bag, pid);
		}
		catch (IOException e)
		{
			throw new DcStorageException(e.getMessage(), e);
		}
		finally
		{
			IOUtils.closeQuietly(bag);
		}
	}

	/**
	 * Adds a reference to an external URL in a bag.
	 * 
	 * @param pid
	 *            Pid of a collection as String
	 * @param url
	 *            External URL as String
	 * @throws DcStorageException
	 */
	public void addExtRef(String pid, String url) throws DcStorageException
	{
		Bag bag = null;
		try
		{
			bag = getBag(pid);
			if (bag == null)
				bag = createBlankBag(pid);

			BagFile extRefsFile = bag.getBagFile(ExtRefsTxt.FILEPATH);
			// If file doesn't exist, create it.
			ExtRefsTxt extRefsTxt;
			if (extRefsFile == null)
				extRefsTxt = new ExtRefsTxt(ExtRefsTxt.FILEPATH, getCharacterEncoding(bag));
			else
				extRefsTxt = new ExtRefsTxt(ExtRefsTxt.FILEPATH, extRefsFile, getCharacterEncoding(bag));

			// Convert the URL to Base64 string to be used as a key. Urls have ':' which cannot be present in keys. Encoding the url makes the key unique ensuring
			// each url appears only once.
			extRefsTxt.put(base64Encode(url), url);
			bag.putBagFile(extRefsTxt);
			bag = bag.makeComplete(tagFilesCompleter);
			
			// Write the bag.
			writer.setTagFilesOnly(true);
			writer.setSkipIfPayloadFileExists(true);
			bag = writeBag(bag, pid);
		}
		finally
		{
			writer.setTagFilesOnly(false);
			writer.setSkipIfPayloadFileExists(false);
			IOUtils.closeQuietly(bag);
		}
	}

	public void deleteExtRef(String pid, String url) throws DcStorageException
	{
		if (!bagExists(pid))
			throw new DcStorageException(format("Unable to delete external reference {0}. Bag for pid {1} doesn't exist.", url, pid));
		Bag bag = null;
		try
		{
			bag = getBag(pid);
			BagFile extRefsFile = bag.getBagFile(ExtRefsTxt.FILEPATH);
			if (extRefsFile == null)
				throw new DcStorageException(format("Unable to delete external reference {0}. No external references exist for pid {1}", url, pid));
			ExtRefsTxt extRefsTxt = new ExtRefsTxt(ExtRefsTxt.FILEPATH, extRefsFile, getCharacterEncoding(bag));
			String base64EncodedUrl = base64Encode(url);
			if (!extRefsTxt.containsKey(base64EncodedUrl))
				throw new DcStorageException(format("External reference {0} not found in pid {1}", url, pid));
			extRefsTxt.remove(base64EncodedUrl);
			bag.putBagFile(extRefsTxt);
			bag = bag.makeComplete(tagFilesCompleter);
			writer.setTagFilesOnly(true);
			writer.setSkipIfPayloadFileExists(true);
			bag = writeBag(bag, pid);
		}
		finally
		{
			writer.setTagFilesOnly(false);
			writer.setSkipIfPayloadFileExists(false);
			IOUtils.closeQuietly(bag);
		}
	}
	
	/**
	 * Adds a bag to a collection.
	 * 
	 * @see #storeBag(String, Bag)
	 * @param pid
	 *            Pid of a collection as String
	 * @param bagFile
	 *            Bag as a File object
	 * @throws DcStorageException
	 */
	public void storeBag(String pid, File bagFile) throws DcStorageException
	{
		Bag bag = bagFactory.createBag(bagFile, LoadOption.BY_FILES);
		try
		{
			storeBag(pid, bag);
		}
		finally
		{
			IOUtils.closeQuietly(bag);
		}
	}

	/**
	 * Adds a bag to a collection.
	 * 
	 * @param pid
	 *            Pid of a collection as String.
	 * 
	 * @param bag
	 *            Bag as Bag object
	 * @throws DcStorageException
	 */
	public void storeBag(String pid, Bag bag) throws DcStorageException
	{
		// Verify the bag.
		LOGGER.info("Verifying replacement bag for {}...", pid);
		SimpleResult verfResults = bag.verifyValid();
		if (verfResults.isSuccess() == false)
		{
			LOGGER.error("Bag validation failed. Bag will not be stored.");
			throw new DcStorageException("Bag validation failed. Bag will not be stored.");
		}

		if (!bag.getBagInfoTxt().getExternalIdentifier().equals(pid))
			throw new DcStorageException("Bag received is not for Pid: " + pid);

		Bag curBag = getBag(pid);
		if (curBag != null)
		{
			try
			{
				validateReplacementBag(curBag, bag);
				archiveBag(curBag, true);
			}
			catch (IOException e)
			{
				DcStorageException dce = new DcStorageException(e);
				LOGGER.error(e.getMessage(), dce);
				throw dce;
			}
			finally
			{
				IOUtils.closeQuietly(curBag);
			}
		}

		// Complete the bag.
		bag = bag.makeComplete(preSaveCompleter);
		bag = writeBag(bag, pid);
		bag = bag.makeComplete(postSaveCompleter);
		bag = writeBag(bag, pid);

		// Update FILE0 datastream.
		try
		{
			updateDatastream(pid);
		}
		catch (FedoraClientException e)
		{
			LOGGER.warn(e.getMessage(), e);
		}

		IOUtils.closeQuietly(bag);
	}

	/**
	 * Checks if a bag exists for a specified collection.
	 * 
	 * @param pid
	 *            A collection's Pid as String
	 * @return true if bag exists, false otherwise.
	 */
	public boolean bagExists(String pid)
	{
		return (getBag(pid) != null);
	}

	/**
	 * Checks if a file exists in a bag of a specified collection.
	 * 
	 * @param pid
	 *            Pid of a collection
	 * @param filepath
	 *            Path of the file within the bag. E.g. <code>data/abc.txt</code>
	 * @return true if exists, false otherwise
	 * @throws DcStorageException
	 */
	public boolean fileExistsInBag(String pid, String filepath) throws DcStorageException
	{
		Bag bag = getBag(pid);
		if (bag == null)
			throw new DcStorageException(format("Bag doesn't exist for Pid {0}.", pid));

		BagFile bFile = bag.getBagFile(filepath);
		return (bFile != null);
	}

	/**
	 * Gets the bag of a specified collection record.
	 * 
	 * @param pid
	 *            Pid of a collection record
	 * 
	 * @return Bag as Bag object
	 */
	public Bag getBag(String pid)
	{
		LOGGER.debug("Checking if a bag exists for {}...", pid);
		Bag bag = null;
		for (int i = 0; i < Bag.Format.values().length; i++)
		{
			File possibleBagFile = new File(bagsDir, convertToDiskSafe(pid) + Bag.Format.values()[i].extension);
			if (possibleBagFile.exists())
			{
				LOGGER.debug("Bag for pid {} at {}", pid, possibleBagFile.getAbsolutePath());
				bag = bagFactory.createBag(possibleBagFile, LoadOption.BY_MANIFESTS);
				break;
			}
		}
		if (bag == null)
			LOGGER.debug("Bag not found for pid {}.", pid);
		return bag;
	}

	/**
	 * Gets the bag summary of a bag.
	 * 
	 * @param pid
	 *            Pid of the collection record whose bag summary to retrieve
	 * 
	 * @return Summary as Bag Summary
	 * 
	 * @throws DcStorageException
	 */
	public BagSummary getBagSummary(String pid) throws DcStorageException
	{
		Bag bag = getBag(pid);
		if (bag == null)
			throw new DcStorageException(format("Bag not found for pid {0}.", pid));
		BagSummary bagSummary = new BagSummary(bag);
		return bagSummary;
	}

	/**
	 * File Summary Map of files in a bag.
	 * 
	 * @param pid
	 *            Pid of the collection record whose FileSummaryMap to retrieve.
	 * 
	 * @return File Summary Map as FileSummaryMap.
	 * 
	 * @throws DcStorageException
	 */
	public FileSummaryMap getFileSummaryMap(String pid) throws DcStorageException
	{
		Bag bag = getBag(pid);
		if (bag == null)
			throw new DcStorageException(format("Bag not found for pid {0}.", pid));
		FileSummaryMap fsMap = new FileSummaryMap(bag);
		return fsMap;
	}

	/**
	 * Gets an InputStream of a file within the bag of a specified collection.
	 * 
	 * @param pid
	 *            Pid of the collection
	 * @param filePath
	 *            Path of file within the bag. E.g. data/file.txt
	 * @return Inputstream of file within bag
	 * @throws DcStorageException
	 */
	public InputStream getFileStream(String pid, String filePath) throws DcStorageException
	{
		Bag bag = getBag(pid);
		if (bag == null)
			throw new DcStorageException(format("Bag not found for pid {0}.", pid));
		BagFile file = bag.getBagFile(filePath);
		if (file == null)
			throw new DcStorageException(format("File {0} not found within bag for pid {1}.", filePath, pid));
		InputStream fileStream = file.newInputStream();
		return fileStream;
	}

	/**
	 * Returns an inputstream containing the zip stream of multiple files. The InputStream is filled in a separate thread while the calling function reads from
	 * it in another thread.
	 * 
	 * @param pid
	 *            Pid of the collection whose files to retrieve
	 * @param fileSet
	 *            Set of files whose contents are to be included in the zipstream.
	 * @return ZipStream as InputStream
	 * @throws IOException
	 */
	public InputStream getFilesAsZipStream(String pid, final Set<String> fileSet) throws IOException
	{
		final PipedOutputStream sink = new PipedOutputStream();
		final Bag bag = getBag(pid);
		PipedInputStream zipInStream = new PipedInputStream(sink);

		// Writing PipedOutputStream needs to happen in a separate thread to prevent deadlock.
		Runnable zipWriter = new Runnable()
		{
			@Override
			public void run()
			{
				byte[] buffer = new byte[(int) FileUtils.ONE_MB];
				ZipOutputStream zipOutStream = new ZipOutputStream(sink);

				try
				{
					for (String filePath : fileSet)
					{
						ZipEntry zipEntry = new ZipEntry(filePath);
						BagFile bagFile = bag.getBagFile(filePath);
						if (bagFile != null)
						{
							InputStream bagFileInStream = null;
							bagFileInStream = bag.getBagFile(filePath).newInputStream();
							zipOutStream.putNextEntry(zipEntry);
							for (int numBytesRead = bagFileInStream.read(buffer); numBytesRead != -1; numBytesRead = bagFileInStream.read(buffer))
								zipOutStream.write(buffer, 0, numBytesRead);
							IOUtils.closeQuietly(bagFileInStream);
							zipOutStream.closeEntry();
							zipOutStream.flush();
						}
					}
				}
				catch (IOException e)
				{
					IOUtils.closeQuietly(zipOutStream);
					zipOutStream = null;
				}
				finally
				{
					IOUtils.closeQuietly(zipOutStream);
				}
			}
		};

		new Thread(zipWriter).start();
		return zipInStream;
	}

	private void updateDatastream(String pid) throws FedoraClientException
	{
		// Create a placeholder datastream.
		FedoraBroker.addDatastreamBySource(pid, "FILE" + "0", "FILE0", "<text>Files available.</text>");
	}

	private void validateReplacementBag(Bag oldBag, Bag newBag) throws DcStorageException
	{
		BagSummary newBagSummary = new BagSummary(newBag);
		if (newBagSummary.getDataSource() == DataSource.INSTRUMENT)
		{
			//			// Verify the integrity of tagmanifest.
			//			Manifest tagManifest = bag.getTagManifest(BAGS_ALGORITHM);
			//			List<Manifest> payloadManifestList = bag.getPayloadManifests();
			//			
			//			for (Manifest iPlManifest : payloadManifestList)
			//			{
			//				if (tagManifest.containsKey(iPlManifest.getFilepath()))
			//				{
			//					String hashInManifest = tagManifest.get(iPlManifest.getFilepath());
			//					if (!MessageDigestHelper.fixityMatches(iPlManifest.newInputStream(), iPlManifest.getAlgorithm(), hashInManifest))
			//					{
			//						LOGGER.error("Payload manifest hash invalid.");
			//						throw new DcBagException("Payload manifest hash invalid.");
			//					}
			//				}
			//			}
			//
			//			if (getBagProperty(BagPropsTxt.FIELD_DATASOURCE).equals(DataSource.INSTRUMENT.toString()))
			//			{
			//				// Hash check files in payload manifest.
			//				Set<Entry<String, String>> plManifestFiles = bag.getPayloadManifest(BAGS_ALGORITHM).entrySet();
			//				for (Entry<String, String> iEntry : plManifestFiles)
			//				{
			//					BagFile iFile = bag.getBagFile(iEntry.getKey());
			//
			//					// Check if file exists. Then check its hash value matches the one in the manifest.
			//					if (iFile == null || !iFile.exists())
			//						throw new DcBagException("Bag doesn't contain file " + iFile.getFilepath());
			//					if (!MessageDigestHelper.fixityMatches(iFile.newInputStream(), BAGS_ALGORITHM, iEntry.getValue()))
			//						throw new DcBagException("Bag contains modified existing files.");
			//				}
			//			}

			List<Manifest> oldPlManifests = oldBag.getPayloadManifests();
			List<Manifest> newPlManifests = newBag.getPayloadManifests();
			for (Manifest iOldPlManifest : oldPlManifests)
			{
				for (Manifest iNewPlManifest : newPlManifests)
				{
					if (iOldPlManifest.getAlgorithm() == iNewPlManifest.getAlgorithm())
					{
						// Iterate through each payload file in the old payload manifest, ensure it exists and hash matches in new bag.
						for (String iOldPlFile : iOldPlManifest.keySet())
						{
							// The new bag should contain the files already present in the current bag.
							if (!iNewPlManifest.containsKey(iOldPlFile))
							{
								LOGGER.error("New bag doesn't contain payload file: {}", iOldPlFile);
								throw new DcStorageException("New bag doesn't contain file: " + iOldPlFile);
							}

							// Hashes for current payload files should match the hashes for the same files in the new bag.
							if (!iOldPlManifest.get(iOldPlFile).equals(iNewPlManifest.get(iOldPlFile)))
							{
								LOGGER.error("Hash doesn't match for file: {}", iOldPlFile);
								throw new DcStorageException("Hash doesn't match for file: " + iOldPlFile);
							}
						}
					}
				}
			}
		}
	}

	private Bag writeBag(Bag bag, String pid)
	{
		return bag.write(writer, new File(bagsDir, convertToDiskSafe(pid) + Bag.Format.FILESYSTEM.extension));
	}

	private void archiveBag(Bag bag, boolean deleteOriginal) throws IOException
	{
		File file = bag.getFile();
		Date dateNow = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
		bag.close();
		File targetFile = new File(GlobalProps.getBagsDirAsFile(), file.getName() + "-" + dateFormat.format(dateNow));
		if (deleteOriginal)
		{
			file.renameTo(targetFile);
		}
		else
		{
			if (file.isDirectory())
				FileUtils.copyDirectory(file, targetFile, true);
			else
				FileUtils.copyFile(file, targetFile, true);
		}
	}

	private Bag createBlankBag(String pid)
	{
		Bag bag = bagFactory.createBag();
		if (bag.getBagItTxt() == null)
			bag.putBagFile(bag.getBagPartFactory().createBagItTxt());

		for (Manifest.Algorithm iAlg : algorithms)
		{
			// Payload manifest
			if (bag.getPayloadManifest(iAlg) == null)
				bag.putBagFile(bag.getBagPartFactory().createManifest(ManifestHelper.getPayloadManifestFilename(iAlg, bag.getBagConstants())));

			// Tag Manifest
			if (bag.getTagManifest(iAlg) == null)
				bag.putBagFile(bag.getBagPartFactory().createManifest(ManifestHelper.getTagManifestFilename(iAlg, bag.getBagConstants())));
		}

		// BagInfoTxt
		if (bag.getBagInfoTxt() == null)
			bag.putBagFile(bag.getBagPartFactory().createBagInfoTxt());
		bag.getBagInfoTxt().addExternalIdentifier(pid);

		return bag;
	}
	
	private String base64Encode(String stringToEncode)
	{
		String base64Encoded = new String(Base64.encodeBase64(stringToEncode.getBytes()));
		return base64Encoded;
	}

	private String getCharacterEncoding(Bag bag)
	{
		return bag.getBagItTxt().getCharacterEncoding();
	}

	public static String convertToDiskSafe(String source)
	{
		return source.trim().toLowerCase().replaceAll("\\*|\\?|\\\\|:|/|\\s", "_");
	}
}
