package au.edu.anu.datacommons.storage;

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
import gov.loc.repository.bagit.transformer.impl.UpdateCompleter;
import gov.loc.repository.bagit.transformer.impl.UpdatePayloadOxumCompleter;
import gov.loc.repository.bagit.utilities.SimpleResult;
import gov.loc.repository.bagit.writer.impl.FileSystemWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.mail.internet.MimeUtility;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.tika.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.data.fedora.FedoraBroker;
import au.edu.anu.datacommons.properties.GlobalProps;
import au.edu.anu.datacommons.util.Util;
import au.edu.anu.dcbag.BagPropsTxt.DataSource;
import au.edu.anu.dcbag.BagSummary;
import au.edu.anu.dcbag.ExtRefsTxt;
import au.edu.anu.dcbag.FileSummaryMap;
import au.edu.anu.dcbag.VirusScanTxt;

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

	protected DcStorage() throws IOException
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

		// Set storage location if not set already.
		if (bagsDir == null)
			bagsDir = GlobalProps.getBagsDirAsFile();

		// If the directory specified doesn't exist, create it.
		if (!bagsDir.exists())
		{
			if (!bagsDir.mkdirs())
				throw new IOException(MessageFormat.format("Unable to create {0}. Check permissions.", bagsDir.getAbsolutePath()));
		}
	}

	public static synchronized DcStorage getInstance()
	{
		if (inst == null)
		{
			try
			{
				inst = new DcStorage();
			}
			catch (IOException e)
			{
				inst = null;
				LOGGER.error(e.getMessage(), e);
			}
		}
		return inst;
	}

	public static void setLocation(File newBagsDir)
	{
		if (inst == null)
			bagsDir = newBagsDir;
		else
			throw new RuntimeException("Can't change storage location after instantiation.");
	}

	public void addFileToBag(String pid, String filename, String fileUrl) throws DcStorageException
	{
		try
		{
			addFileToBag(pid, filename, new URL(fileUrl));
		}
		catch (MalformedURLException e)
		{
			DcStorageException dce = new DcStorageException(e);
			LOGGER.error(e.getMessage(), dce);
			throw dce;
		}
	}

	public void addFileToBag(String pid, String filename, URL fileUrl) throws DcStorageException
	{
		File tempFile = null;
		File tempRenamedFile = null;
		try
		{
			tempFile = File.createTempFile("DcTempFile", null);
			tempRenamedFile = new File(tempFile.getParentFile(), filename);
			if (!tempFile.renameTo(tempRenamedFile))
				throw new IOException(MessageFormat.format("Unable to rename {0} to {1}.", tempFile.getAbsolutePath(), tempRenamedFile.getAbsolutePath()));

			LOGGER.debug("Saving {} as {}...", fileUrl.toString(), tempRenamedFile.getAbsolutePath());
			FileUtils.copyURLToFile(fileUrl, tempRenamedFile);
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
			if (tempFile != null && tempFile.exists())
				FileUtils.deleteQuietly(tempFile);
			if (tempRenamedFile != null && tempRenamedFile.exists())
				FileUtils.deleteQuietly(tempRenamedFile);
		}
	}

	public void addFileToBag(String pid, File file) throws DcStorageException
	{
		// TODO Lock Pid

		try
		{
			Bag bag = getBag(pid);
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
			writeBag(bag, pid);
			bag = bag.makeComplete(postSaveCompleter);
			writeBag(bag, pid);
			try
			{
				bag.close();
			}
			catch (IOException e)
			{
				LOGGER.warn(e.getMessage(), e);
			}
		}
		catch (IOException e)
		{
			DcStorageException dce = new DcStorageException(e);
			LOGGER.error(e.getMessage(), dce);
			throw dce;
		}
	}

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
				extRefsTxt = new ExtRefsTxt(ExtRefsTxt.FILEPATH, bag.getBagItTxt().getCharacterEncoding());
			else
				extRefsTxt = new ExtRefsTxt(ExtRefsTxt.FILEPATH, extRefsFile, bag.getBagItTxt().getCharacterEncoding());

			// Convert the URL to Base64 string to be used as a key. Urls have ':' which cannot be present in keys. Encoding the url makes the key unique ensuring
			// each url appears only once.
			byte[] base64EncodedUrl = Base64.encodeBase64(url.getBytes());
			extRefsTxt.put(new String(base64EncodedUrl), url);
			bag.putBagFile(extRefsTxt);
			bag = bag.makeComplete(tagFilesCompleter);
			
			// Write the bag.
			bag = writeBag(bag, pid);
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
	
	public void storeBag(String pid, File bagFile) throws DcStorageException
	{
		Bag bag = bagFactory.createBag(bagFile, LoadOption.BY_FILES);
		try
		{
			storeBag(pid, bag);
		}
		finally
		{
			try
			{
				bag.close();
			}
			catch (IOException e)
			{
				LOGGER.warn("Unable to close bag.");
			}
		}
	}

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
				try
				{
					curBag.close();
				}
				catch (IOException e)
				{
					LOGGER.warn(e.getMessage(), e);
				}
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

		try
		{
			bag.close();
		}
		catch (IOException e)
		{
			LOGGER.warn(e.getMessage(), e);
		}
	}

	public boolean bagExists(String pid)
	{
		return (getBag(pid) != null);
	}

	public boolean fileExistsInBag(String pid, String filepath) throws DcStorageException
	{
		Bag bag = getBag(pid);
		if (bag == null)
			throw new DcStorageException(MessageFormat.format("Bag doesn't exist for Pid {0}.", pid));

		BagFile bFile = bag.getBagFile(filepath);
		return (bFile != null);
	}

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

	public BagSummary getBagSummary(String pid) throws DcStorageException
	{
		Bag bag = getBag(pid);
		if (bag == null)
			throw new DcStorageException("Bag not found.");
		BagSummary bagSummary = new BagSummary(bag);
		return bagSummary;
	}

	public FileSummaryMap getFileSummaryMap(String pid) throws DcStorageException
	{
		Bag bag = getBag(pid);
		if (bag == null)
			throw new DcStorageException("Bag not found.");
		FileSummaryMap fsMap = new FileSummaryMap(bag);
		return fsMap;
	}

	public InputStream getFileStream(String pid, String filePath) throws DcStorageException
	{
		Bag bag = getBag(pid);
		if (bag == null)
			throw new DcStorageException("Bag not found.");
		BagFile file = bag.getBagFile(filePath);
		if (file == null)
			throw new DcStorageException("File not found within bag.");
		InputStream fileStream = file.newInputStream();
		return fileStream;
	}

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
				byte[] buffer = new byte[1048576];
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
							{
								zipOutStream.write(buffer, 0, numBytesRead);
							}
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

	public static String convertToDiskSafe(String source)
	{
		return source.trim().toLowerCase().replaceAll("\\*|\\?|\\\\|:|/|\\s", "_");
	}
}
