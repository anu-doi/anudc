/*******************************************************************************
 * Australian National University Data Commons
 * Copyright (C) 2013  The Australian National University
 * 
 * This file is part of Australian National University Data Commons.
 * 
 * Australian National University Data Commons is free software: you
 * can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package au.edu.anu.datacommons.storage;

import static java.text.MessageFormat.*;
import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.BagFactory;
import gov.loc.repository.bagit.BagFactory.LoadOption;
import gov.loc.repository.bagit.BagFile;
import gov.loc.repository.bagit.Manifest;
import gov.loc.repository.bagit.Manifest.Algorithm;
import gov.loc.repository.bagit.ManifestHelper;
import gov.loc.repository.bagit.transformer.impl.TagManifestCompleter;
import gov.loc.repository.bagit.writer.impl.FileSystemWriter;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.data.fedora.FedoraBroker;
import au.edu.anu.datacommons.properties.GlobalProps;
import au.edu.anu.datacommons.storage.archive.ArchiveItem;
import au.edu.anu.datacommons.storage.archive.ArchiveItem.Operation;
import au.edu.anu.datacommons.storage.archive.ArchiveTask;
import au.edu.anu.datacommons.storage.info.BagPropsTxt.DataSource;
import au.edu.anu.datacommons.storage.info.BagSummary;
import au.edu.anu.datacommons.storage.info.ExtRefsTxt;
import au.edu.anu.datacommons.storage.info.FileSummaryMap;

import com.yourmediashelf.fedora.client.FedoraClientException;

public final class DcStorage implements Closeable {
	private static final Logger LOGGER = LoggerFactory.getLogger(DcStorage.class);
	private static DcStorage inst = null;

	private ExecutorService execSvc;

	private Set<Manifest.Algorithm> algorithms;
	// private final Set<String> lockedPids = Collections.synchronizedSet(new HashSet<String>());
	private File bagsDir = null;

	public static final BagFactory bagFactory = new BagFactory();

	/**
	 * Constructor for DcStorage. Initialises the hashing algorithm(s) to use, completers, and writers.
	 */
	protected DcStorage(File bagsDir, ExecutorService execSvc) {
		algorithms = new HashSet<Manifest.Algorithm>();
		algorithms.add(Manifest.Algorithm.MD5);
		this.bagsDir = bagsDir;
		this.execSvc = execSvc;

		// If the directory specified doesn't exist, create it.
		if (!bagsDir.exists() && !bagsDir.mkdirs())
			throw new RuntimeException(format("Unable to create {0}. Check permissions.", bagsDir.getAbsolutePath()));
	}

	/**
	 * Returns the singleton instance of DcStorage object.
	 * 
	 * @return DcStorage object as DcStorage
	 */
	public static synchronized DcStorage getInstance() {
		if (inst == null) {
			inst = new DcStorage(GlobalProps.getBagsDirAsFile(), Executors.newSingleThreadExecutor());
		}
		return inst;
	}

	File getLocation() {
		return bagsDir;
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
	public void addFileToBag(String pid, String filename, URL fileUrl) throws DcStorageException {
		File downloadedFile = null;
		try {
			TempFileTask dlTask = new TempFileTask(fileUrl);
			try {
				downloadedFile = dlTask.call();
			} catch (Exception e) {
				throw new IOException(e);
			}
			addFileToBag(pid, downloadedFile, filename);
		} catch (IOException e) {
			DcStorageException dce = new DcStorageException(e);
			throw dce;
		} finally {
			FileUtils.deleteQuietly(downloadedFile);
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
	public void addFileToBag(String pid, File file, String filename) throws DcStorageException {
		Bag bag = null;
		File targetFile = new File(getBagDir(pid), "data/" + filename);
		try {
			bag = getBag(pid);
			if (bag == null) {
				bag = createBlankBag(pid);
			} else {
				if (targetFile.exists()) {
					ArchiveTask archiveTask = new ArchiveTask();
					Manifest.Algorithm firstAlg = algorithms.toArray(new Manifest.Algorithm[0])[0];
					String firstAlgMd = bag.getChecksums("data/" + filename).get(firstAlg);
					ArchiveItem archiveItem = new ArchiveItem(pid, targetFile, firstAlg, firstAlgMd, Operation.REPLACE);
					archiveTask.addArchiveItem(archiveItem);
					try {
						archiveTask.call();
					} catch (Exception e) {
						throw new DcStorageException(format("Unable to archive {0}", targetFile.getName()), e);
					}
				}
			}
			try {
				moveFileToPayload(pid, file, filename);
			} catch (IOException e) {
				DcStorageException dce = new DcStorageException(e);
				throw dce;
			}
			CompleterTask compTask = new CompleterTask(bagFactory, getBagDir(pid));
			compTask.addPayloadFileAddedUpdated("data/" + filename);
			execSvc.submit(compTask);
		} finally {
			IOUtils.closeQuietly(bag);
		}
	}

	/**
	 * Deletes a file from the bag of a specified record. The bag itself is then completed in a separate request.
	 * 
	 * @param pid
	 *            Pid of the record whose bag contains the file to be deleted
	 * @param bagFilePath
	 *            Path of the file in the bag. For example, "data/somefile.txt"
	 * @throws DcStorageException
	 *             when unable to delete the file
	 */
	public void deleteFileFromBag(String pid, String bagFilePath) throws DcStorageException {
		Bag bag = null;
		File fileToDelete = new File(getBagDir(pid), bagFilePath);
		try {
			if (!bagExists(pid)) {
				throw new DcStorageException(format("No files present in pid {0}.", bagFilePath));
			}

			bag = getBag(pid);
			if (!fileExistsInBag(pid, bagFilePath))
				throw new DcStorageException(format("File {0} doesn't exist in pid {1}.", bagFilePath, pid));

			ArchiveTask archiveTask = new ArchiveTask();
			Manifest.Algorithm firstAlg = algorithms.toArray(new Manifest.Algorithm[0])[0];
			String firstAlgMd = bag.getChecksums(bagFilePath).get(firstAlg);
			ArchiveItem archiveItem = new ArchiveItem(pid, fileToDelete, firstAlg, firstAlgMd, Operation.DELETE);
			archiveTask.addArchiveItem(archiveItem);
			try {
				archiveTask.call();
			} catch (Exception e) {
				throw new DcStorageException(format("Unable to archive {0}", fileToDelete.getName()), e);
			}

			CompleterTask task = new CompleterTask(bagFactory, getBagDir(pid));
			task.addPayloadFileDeleted(bagFilePath);
			execSvc.submit(task);
		} finally {
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
	public void addExtRefs(String pid, Collection<String> urls) throws DcStorageException {
		Bag bag = null;
		try {
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

			// Convert the URL to Base64 string to be used as a key. Urls have ':' which cannot be present in keys.
			// Encoding the url makes the key unique ensuring each url appears only once.
			for (String url : urls) {
				extRefsTxt.put(base64Encode(url), url);
			}
			bag.putBagFile(extRefsTxt);
			TagManifestCompleter tagManifestCompleter = new TagManifestCompleter(bagFactory);
			bag = bag.makeComplete(tagManifestCompleter);

			// Write the bag.
			FileSystemWriter writer = new FileSystemWriter(bagFactory);
			writer.setTagFilesOnly(true);
			bag = writer.write(bag, bag.getFile());
		} finally {
			IOUtils.closeQuietly(bag);
		}
	}

	/**
	 * Deletes an external reference in the bag of a specified record.
	 * 
	 * @param pid
	 *            Pid of the record whose bag contains the external reference to delete
	 * @param url
	 *            URL to delete
	 * @throws DcStorageException
	 *             when unable to delete the external reference
	 */
	public void deleteExtRefs(String pid, Collection<String> urls) throws DcStorageException {
		if (!bagExists(pid))
			throw new DcStorageException(format("Unable to delete external references. Bag for pid {0} doesn't exist.",
					pid));
		Bag bag = null;
		try {
			bag = getBag(pid);
			BagFile extRefsFile = bag.getBagFile(ExtRefsTxt.FILEPATH);
			if (extRefsFile == null)
				throw new DcStorageException(format(
						"Unable to delete external references. No external references exist for pid {0}", pid));
			ExtRefsTxt extRefsTxt = new ExtRefsTxt(ExtRefsTxt.FILEPATH, extRefsFile, getCharacterEncoding(bag));
			for (String url : urls) {
				String base64EncodedUrl = base64Encode(url);
				if (extRefsTxt.containsKey(base64EncodedUrl)) {
					extRefsTxt.remove(base64EncodedUrl);
				}
			}
			bag.putBagFile(extRefsTxt);

			// Complete bag.
			TagManifestCompleter tagManifestCompleter = new TagManifestCompleter(bagFactory);
			bag = bag.makeComplete(tagManifestCompleter);

			// Write Bag.
			FileSystemWriter writer = new FileSystemWriter(bagFactory);
			writer.setTagFilesOnly(true);
			bag = writer.write(bag, bag.getFile());
		} finally {
			IOUtils.closeQuietly(bag);
		}
	}

	public void recompleteBag(String pid) throws DcStorageException {
		if (!bagExists(pid)) {
			throw new DcStorageException(format("No bag exists for {0}", pid));
		}
		CompleterTask compTask = new CompleterTask(bagFactory, getBagDir(pid));
		compTask.setCompleteAllFiles();
		execSvc.submit(compTask);
	}
	

	/**
	 * Checks if a bag exists for a specified collection.
	 * 
	 * @param pid
	 *            A collection's Pid as String
	 * @return true if bag exists, false otherwise.
	 */
	public boolean bagExists(String pid) {
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
	 */
	public boolean fileExistsInBag(String pid, String filepath) {
		Bag bag = getBag(pid);
		if (bag == null)
			return false;

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
	Bag getBag(String pid) {
		Bag bag = null;
		for (int i = 0; i < Bag.Format.values().length; i++) {
			File possibleBagFile = new File(bagsDir, convertToDiskSafe(pid) + Bag.Format.values()[i].extension);
			if (possibleBagFile.exists()) {
				LOGGER.trace("Bag for pid {} at {}", pid, possibleBagFile.getAbsolutePath());
				bag = bagFactory.createBag(possibleBagFile, LoadOption.BY_FILES);
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
	public BagSummary getBagSummary(String pid) throws DcStorageException {
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
	public FileSummaryMap getFileSummaryMap(String pid) throws DcStorageException {
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
	public InputStream getFileStream(String pid, String filePath) throws DcStorageException {
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
	 * Returns an inputstream containing the zip stream of multiple files. The InputStream is filled in a separate
	 * thread while the calling function reads from it in another thread.
	 * 
	 * @param pid
	 *            Pid of the collection whose files to retrieve
	 * @param fileSet
	 *            Set of files whose contents are to be included in the zipstream.
	 * @return ZipStream as InputStream
	 * @throws IOException
	 */
	public InputStream getFilesAsZipStream(String pid, final Set<String> fileSet) throws IOException {
		final PipedOutputStream sink = new PipedOutputStream();
		final Bag bag = getBag(pid);
		PipedInputStream zipInStream = new PipedInputStream(sink);

		// Writing PipedOutputStream needs to happen in a separate thread to prevent deadlock.
		Runnable zipWriter = new Runnable() {
			@Override
			public void run() {
				byte[] buffer = new byte[(int) FileUtils.ONE_MB];
				ZipOutputStream zipOutStream = new ZipOutputStream(sink);

				try {
					for (String filePath : fileSet) {
						ZipEntry zipEntry = new ZipEntry(filePath.replaceFirst("data/", ""));
						BagFile bagFile = bag.getBagFile(filePath);
						if (bagFile != null) {
							InputStream bagFileInStream = null;
							bagFileInStream = bag.getBagFile(filePath).newInputStream();
							zipOutStream.putNextEntry(zipEntry);
							for (int numBytesRead = bagFileInStream.read(buffer); numBytesRead != -1; numBytesRead = bagFileInStream
									.read(buffer))
								zipOutStream.write(buffer, 0, numBytesRead);
							IOUtils.closeQuietly(bagFileInStream);
							zipOutStream.closeEntry();
							zipOutStream.flush();
						}
					}
				} catch (IOException e) {
					IOUtils.closeQuietly(zipOutStream);
					zipOutStream = null;
				} finally {
					IOUtils.closeQuietly(zipOutStream);
				}
			}
		};

		new Thread(zipWriter).start();
		return zipInStream;
	}

	/**
	 * Gets the MD5 value of a file stored in the payload or tag manifest. The MD5 is not computed, only read from the
	 * manifest that contains it.
	 * 
	 * @param pid
	 *            Pid of the record containing the specified file
	 * @param filepath
	 *            Relative path of the file within the bag. For example, "data/somefile.txt"
	 * @return MD5 sum as String
	 * @throws DcStorageException
	 *             When unable to read the MD5 checksum
	 */
	public String getFileMd5(String pid, String filepath) throws DcStorageException {
		Bag bag = getBag(pid);
		if (bag == null)
			throw new DcStorageException(format("Bag not found for {0}", pid));

		return bag.getChecksums(filepath).get(Algorithm.MD5);
	}

	/**
	 * Gets the size of the file stored in the bag of a specified record.
	 * 
	 * @param pid
	 *            Pid of the record whose bag contains the specified file
	 * @param filepath
	 *            Relative path of the file. For example, "data/somefile.txt"
	 * @return Size of the file in bytes as long
	 * @throws DcStorageException
	 *             when unable to get the file size
	 */
	public long getFileSize(String pid, String filepath) throws DcStorageException {
		Bag bag = getBag(pid);
		if (bag == null)
			throw new DcStorageException(format("Bag not found for {0}", pid));

		return bag.getBagFile(filepath).getSize();
	}

	/**
	 * Gets the location where a bag is stored on disk as a File object.
	 * 
	 * <p>
	 * <em>For use in JUnit testing only.</em>
	 * 
	 * @param pid
	 *            Pid of the collection whose bag's location is requested
	 * @return Bag location as File
	 */
	File getBagDir(String pid) {
		return new File(bagsDir, convertToDiskSafe(pid) + Bag.Format.FILESYSTEM.extension);
	}

	/**
	 * Moves a specified file into the payload directory of a bag.
	 * 
	 * @param pid
	 *            Pid of the record in whose bag the file
	 * @param fileToMove
	 *            File object representing the file on disk to move into the payload directory
	 * @param filename
	 *            Name of the file to store as
	 * @throws IOException
	 *             when unable to move the file
	 */
	private void moveFileToPayload(String pid, File fileToMove, String filename) throws IOException {
		File payloadDir = new File(getBagDir(pid), "data/");
		File targetFile = new File(payloadDir, filename);
		if (targetFile.exists()) {
			LOGGER.debug("File {} already exists. Deleting.", targetFile);
			if (!targetFile.delete()) {
				LOGGER.error("Unable to delete preexisting {} in bag for {}. Check permissions.", targetFile.getName(),
						pid);
				throw new IOException(format("Unable to delete file {0} in {1}", targetFile.getName(), pid));
			}
		}
		LOGGER.debug("Moving {} to {} and saving as {}.", fileToMove.getAbsolutePath(), payloadDir, filename);
		if (!fileToMove.renameTo(targetFile)) {
			LOGGER.error("Unable to move {} to {} to save as {}. Check permissions", fileToMove.getAbsolutePath(),
					payloadDir, filename);
			throw new IOException(format("Unable to move file {0} to payload directory of {1}",
					fileToMove.getAbsolutePath(), pid));
		}
		LOGGER.debug("Succesfully moved {} to {} and saved as {}.", fileToMove.getAbsolutePath(), payloadDir, filename);
	}

	/**
	 * Updates or creates the FILE0 datastream of a specified record to indicate that a record has files stored against
	 * it.
	 * 
	 * @param pid
	 *            Pid of the record whose FILE0 datastream to create/update
	 * @throws FedoraClientException
	 *             when unable to update the datastream
	 */
	private void updateDatastream(String pid) throws FedoraClientException {
		// Create a placeholder datastream.
		FedoraBroker.addDatastreamBySource(pid, "FILE" + "0", "FILE0", "<text>Files available.</text>");
	}

	/**
	 * Validates the replacement of an existing bag with a new one. If the new bag's source is set to 'instrument', then
	 * the new bag must contain the exact same files as in the old bag in addition to any new files.
	 * 
	 * @param oldBag
	 *            The old bag that will be replaced
	 * @param newBag
	 *            The new bag that will replace the old bag
	 * @throws DcStorageException
	 *             when the replacement is not valid
	 */
	private void validateReplacementBag(Bag oldBag, Bag newBag) throws DcStorageException {
		BagSummary newBagSummary = new BagSummary(newBag);
		if (newBagSummary.getDataSource() == DataSource.INSTRUMENT) {
			// // Verify the integrity of tagmanifest.
			// Manifest tagManifest = bag.getTagManifest(BAGS_ALGORITHM);
			// List<Manifest> payloadManifestList = bag.getPayloadManifests();
			//
			// for (Manifest iPlManifest : payloadManifestList)
			// {
			// if (tagManifest.containsKey(iPlManifest.getFilepath()))
			// {
			// String hashInManifest = tagManifest.get(iPlManifest.getFilepath());
			// if (!MessageDigestHelper.fixityMatches(iPlManifest.newInputStream(), iPlManifest.getAlgorithm(),
			// hashInManifest))
			// {
			// LOGGER.error("Payload manifest hash invalid.");
			// throw new DcBagException("Payload manifest hash invalid.");
			// }
			// }
			// }
			//
			// if (getBagProperty(BagPropsTxt.FIELD_DATASOURCE).equals(DataSource.INSTRUMENT.toString()))
			// {
			// // Hash check files in payload manifest.
			// Set<Entry<String, String>> plManifestFiles = bag.getPayloadManifest(BAGS_ALGORITHM).entrySet();
			// for (Entry<String, String> iEntry : plManifestFiles)
			// {
			// BagFile iFile = bag.getBagFile(iEntry.getKey());
			//
			// // Check if file exists. Then check its hash value matches the one in the manifest.
			// if (iFile == null || !iFile.exists())
			// throw new DcBagException("Bag doesn't contain file " + iFile.getFilepath());
			// if (!MessageDigestHelper.fixityMatches(iFile.newInputStream(), BAGS_ALGORITHM, iEntry.getValue()))
			// throw new DcBagException("Bag contains modified existing files.");
			// }
			// }

			List<Manifest> oldPlManifests = oldBag.getPayloadManifests();
			List<Manifest> newPlManifests = newBag.getPayloadManifests();
			for (Manifest iOldPlManifest : oldPlManifests) {
				for (Manifest iNewPlManifest : newPlManifests) {
					if (iOldPlManifest.getAlgorithm() == iNewPlManifest.getAlgorithm()) {
						// Iterate through each payload file in the old payload manifest, ensure it exists and hash
						// matches in new bag.
						for (String iOldPlFile : iOldPlManifest.keySet()) {
							// The new bag should contain the files already present in the current bag.
							if (!iNewPlManifest.containsKey(iOldPlFile)) {
								LOGGER.error("New bag doesn't contain payload file: {}", iOldPlFile);
								throw new DcStorageException("New bag doesn't contain file: " + iOldPlFile);
							}

							// Hashes for current payload files should match the hashes for the same files in the new
							// bag.
							if (!iOldPlManifest.get(iOldPlFile).equals(iNewPlManifest.get(iOldPlFile))) {
								LOGGER.error("Hash doesn't match for file: {}", iOldPlFile);
								throw new DcStorageException("Hash doesn't match for file: " + iOldPlFile);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Archives a bag by copying the directory containing the bag or renaming it. The new name of the directory is the
	 * old name with the current date and time appended to it.
	 * 
	 * @param bag
	 *            Bag to be archived
	 * @param deleteOriginal
	 *            true if the old bag should be deleted after archiving it to make the directory available for use by a
	 *            new bag, false if the old bag should be copied into an archive without making any changes to it
	 *            allowing for addition or deleting of individual files in the bag
	 * @throws IOException
	 *             If unable to archive the bag
	 */
	private void archiveBag(Bag bag, boolean deleteOriginal) throws IOException {
		File curBagFile = bag.getFile();
		Date dateNow = new Date();

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
		bag.close();
		File archivedBagFile = new File(bagsDir, curBagFile.getName() + "-" + dateFormat.format(dateNow));
		if (deleteOriginal) {
			curBagFile.renameTo(archivedBagFile);
		} else {
			if (curBagFile.isDirectory())
				FileUtils.copyDirectory(curBagFile, archivedBagFile, true);
			else
				FileUtils.copyFile(curBagFile, archivedBagFile, true);
		}
	}

	/**
	 * Creates a blank bag for a record. A blank bag contains no payload files but contains all essential files as
	 * specified in the BagIt Specification.
	 * 
	 * @param pid
	 * @return Created Bag
	 * @see <a href="http://www.digitalpreservation.gov/documents/bagitspec.pdf">The BagIt File Packaging Format</a>
	 */
	private Bag createBlankBag(String pid) {
		Bag bag = bagFactory.createBag();
		if (bag.getBagItTxt() == null)
			bag.putBagFile(bag.getBagPartFactory().createBagItTxt());

		for (Manifest.Algorithm iAlg : algorithms) {
			// Payload manifest
			if (bag.getPayloadManifest(iAlg) == null)
				bag.putBagFile(bag.getBagPartFactory().createManifest(
						ManifestHelper.getPayloadManifestFilename(iAlg, bag.getBagConstants())));

			// Tag Manifest
			if (bag.getTagManifest(iAlg) == null)
				bag.putBagFile(bag.getBagPartFactory().createManifest(
						ManifestHelper.getTagManifestFilename(iAlg, bag.getBagConstants())));
		}

		// BagInfoTxt
		if (bag.getBagInfoTxt() == null)
			bag.putBagFile(bag.getBagPartFactory().createBagInfoTxt());
		bag.getBagInfoTxt().addExternalIdentifier(pid);

		bag = bag.makeComplete();

		FileSystemWriter writer = new FileSystemWriter(bagFactory);
		bag = writer.write(bag, getBagDir(pid));

		// Create payload directory
		File payloadDir = new File(bag.getFile(), "data/");
		if (!payloadDir.exists())
			payloadDir.mkdir();

		return bag;
	}

	/**
	 * Returns a Base64 encoding of a provided String
	 * 
	 * @param stringToEncode
	 *            String to encode
	 * @return Base64 encoded String
	 */
	private String base64Encode(String stringToEncode) {
		String base64Encoded = new String(Base64.encodeBase64(stringToEncode.getBytes()));
		return base64Encoded;
	}

	/**
	 * Gets the character encoding value stored in bagit.txt within a bag. The character set identifies the character
	 * set encoding of tag files.
	 * 
	 * @param bag
	 *            Bag from which the Character Encoding String is to be retrieved
	 * @return Character encoding as String
	 */
	private String getCharacterEncoding(Bag bag) {
		return bag.getBagItTxt().getCharacterEncoding();
	}

	/**
	 * Waits until all pending tasks queued in this class' Executor Service are completed, or a threshold time of 15
	 * minutes is reached.
	 */
	@Override
	public void close() {
		LOGGER.info("Shutting down DcStorage threads...");
		if (!execSvc.isShutdown()) {
			execSvc.shutdown();
			try {
				// Wait until all threads have finished or timeout threshold reached.
				execSvc.awaitTermination(15, TimeUnit.MINUTES);
				LOGGER.info("DcStorage shutdown successfully.");
			} catch (InterruptedException e) {
				LOGGER.warn("Executor Service normal shutdown interrupted.", e);
			}
		}
	}

	/**
	 * Utility method that returns a disk safe version of a String for use in a file or directory name. This method
	 * replaces the characters *,?,\,:,/,SPACE and replaces with an underscore.
	 * 
	 * @param source
	 *            Source string to make disk safe
	 * @return Disk safe version of the source string
	 */
	public static String convertToDiskSafe(String source) {
		return source.trim().toLowerCase().replaceAll("\\*|\\?|\\\\|:|/|\\s", "_");
	}
}
