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

package au.edu.anu.datacommons.storage.verifier;

import gov.loc.repository.bagit.Manifest.Algorithm;
import gov.loc.repository.bagit.utilities.FilenameHelper;
import gov.loc.repository.bagit.utilities.MessageDigestHelper;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.storage.DcStorage;
import au.edu.anu.datacommons.storage.event.StorageEventListener;
import au.edu.anu.datacommons.storage.event.StorageEventListener.EventTime;
import au.edu.anu.datacommons.storage.event.tasks.AbstractTagFileTask;
import au.edu.anu.datacommons.storage.event.tasks.MetadataTask;
import au.edu.anu.datacommons.storage.event.tasks.PreservationTask;
import au.edu.anu.datacommons.storage.event.tasks.PronomTask;
import au.edu.anu.datacommons.storage.event.tasks.TimestampTask;
import au.edu.anu.datacommons.storage.event.tasks.VirusScanTask;
import au.edu.anu.datacommons.storage.provider.StorageProvider;
import au.edu.anu.datacommons.storage.tagfiles.AbstractKeyValueFile;
import au.edu.anu.datacommons.storage.tagfiles.FileMetadataTagFile;
import au.edu.anu.datacommons.storage.tagfiles.ManifestMd5TagFile;
import au.edu.anu.datacommons.storage.tagfiles.PreservationMapTagFile;
import au.edu.anu.datacommons.storage.tagfiles.PronomFormatsTagFile;
import au.edu.anu.datacommons.storage.tagfiles.TagFilesService;
import au.edu.anu.datacommons.storage.tagfiles.TimestampsTagFile;
import au.edu.anu.datacommons.storage.tagfiles.VirusScanTagFile;
import au.edu.anu.datacommons.tasks.ThreadPoolService;
import au.edu.anu.datacommons.util.StopWatch;

/**
 * Task class that completes a bag as per the BagIt specification. Occassionally after making changes to files in a
 * collection record, some tag files may not be updated correctly, or at all, for example due to unable to write to
 * disk.
 * 
 * @author Rahul Khanna
 * 
 */
public class CompletionTask implements Callable<Void>{
	private static final Logger LOGGER = LoggerFactory.getLogger(CompletionTask.class);

	private String pid;
	private Path bagDir;
	private StorageProvider storageProvider;
	private TagFilesService tagFilesSvc;
	private StorageEventListener eventListener;
	private ThreadPoolService threadPoolSvc;
	private DcStorage dcStorage;
	
	List<Class<? extends AbstractKeyValueFile>> classes;
	
	private boolean dryRun = false;
	
	/**
	 * Creates an instance of the completion task object that can be submitted to a thread pool for processing in
	 * another thread.
	 * 
	 * @param pid
	 *            Identifier of the collection record to complete
	 * @param storageProvider
	 *            Bag directory of the record
	 * @param tagFilesSvc
	 *            Tag files service
	 * @param eventListener
	 *            Storage event listener to which changes to tag files will be notified
	 * @param threadPoolSvc
	 *            Thread Pool service to which sub-completion tasks will be submitted
	 * @param dcStorage
	 *            DcStorage class
	 */
	public CompletionTask(String pid, StorageProvider storageProvider, TagFilesService tagFilesSvc, StorageEventListener eventListener,
			ThreadPoolService threadPoolSvc, DcStorage dcStorage) {
		this.pid = pid;
		this.storageProvider = storageProvider;
		this.tagFilesSvc = tagFilesSvc;
		this.eventListener = eventListener;
		this.threadPoolSvc = threadPoolSvc;
		this.dcStorage = dcStorage;
		initTagFileClasses();
	}
	
	/**
	 * Initialises a list of tag file classes that will be checked for completeness. 
	 */
	private void initTagFileClasses() {
		classes = new ArrayList<>(5);
		classes.add(FileMetadataTagFile.class);
		classes.add(PronomFormatsTagFile.class);
		classes.add(TimestampsTagFile.class);
		classes.add(VirusScanTagFile.class);
		classes.add(PreservationMapTagFile.class);
	}

	/**
	 * Sets if the completion task only performs a dry run of the completion process without actually making any
	 * changes. 
	 * 
	 * @param dryRun
	 */
	public void setDryRun(boolean dryRun) {
		this.dryRun = dryRun;
	}

	@Override
	public Void call() throws Exception {
//		StopWatch sw = new StopWatch();
//		sw.start();
//		LOGGER.info("Completing tag files for {}...", pid);
//		if (!dryRun) {
//			eventListener.notify(EventTime.PRE, EventType.TAGFILE_UPDATE, pid, bagDir, null, null);
//		}
//		Set<Path> plFiles = listFilesInDir(getPayloadDir());
//		checkArtifacts(plFiles);
//		verifyMessageDigests(plFiles);
//		verifyTagFiles(plFiles);
//		if (!dryRun) {
//			eventListener.notify(EventTime.POST, EventType.TAGFILE_UPDATE, pid, bagDir, null, null);
//		}
//		sw.stop();
//		LOGGER.info("Tag files completed for {}. Time taken {}", pid, sw.getTimeElapsedFormatted());
//		return null;
		
		// TODO Implement
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Checks for presence of following artifacts from old storage formats: <li>
	 * <ul>
	 * Files in metadata/ directory which previously stored file metadata. Now it is serialised to JSON and stored in
	 * file-metadata.txt</li>
	 * 
	 * @param plFiles
	 *            Payload files within the bag
	 * @throws IOException
	 */
	private void checkArtifacts(Set<Path> plFiles) throws IOException {
		Path metadataDir = bagDir.resolve("metadata/");
		if (Files.isDirectory(metadataDir)) {
			try {
				FileUtils.deleteDirectory(metadataDir.toFile());
			} catch (IOException e) {
				LOGGER.error("Unable to delete {}: {}", metadataDir.toString(), e.getMessage());
			}
		}
	}

	/**
	 * Verifies the message digests stored in a manifest match file's contents.
	 * 
	 * @param plFiles
	 *            Payload files within the bag
	 * @throws IOException
	 */
	private void verifyMessageDigests(Set<Path> plFiles) throws IOException {
		Map<Path, Future<String>> calcMd = calcMessageDigests(plFiles);
		compareMessageDigests(calcMd);
	}

	/**
	 * Calculates message digest for payload files provided.
	 * 
	 * @param plFiles
	 *            Payload files
	 * @return Message digests as <code>Map<Path, Future<String>></code> where Keys are the path to the payload files,
	 *         and <code>Future<String></code>
	 * 
	 */
	private Map<Path, Future<String>> calcMessageDigests(Set<Path> plFiles) {
		Map<Path, Future<String>> calcMd = new HashMap<>();
		for (Path plFile : plFiles) {
			final Path fPlFile = plFile;
			Future<String> mdFuture = threadPoolSvc.submit(new Callable<String>() {

				@Override
				public String call() throws Exception {
					return MessageDigestHelper.generateFixity(fPlFile.toFile(), Algorithm.MD5);
				}
				
			});
			calcMd.put(plFile, mdFuture);
		}
		return calcMd;
	}
	
	/**
	 * Compares provided (calculated) message digests with the MD5 values in the manifest tag file.
	 * 
	 * @param calcMd
	 *            Calculated Message Digests
	 * @throws IOException
	 */
	private void compareMessageDigests(Map<Path, Future<String>> calcMd) throws IOException {
		// Check that each payload file's calculated MD5 exists in the manifest tag file. Add it if it doesn't.
		for (Entry<Path, Future<String>> calcMdEntry : calcMd.entrySet()) {
			try {
				String dataRelPath = getDataRelPath(calcMdEntry.getKey());
				String md5 = calcMdEntry.getValue().get();
				String md5InTagFile = tagFilesSvc.getEntryValue(pid, ManifestMd5TagFile.class, dataRelPath);
				if (md5InTagFile == null) {
					// Entry doesn't exist in manifest tag file.
					LOGGER.info("{}/{} didn't contain entry for {}. Adding MD5 {}", pid, ManifestMd5TagFile.FILEPATH,
							dataRelPath, md5);
					if (!dryRun) {
						tagFilesSvc.addEntry(pid, ManifestMd5TagFile.class, dataRelPath, md5);
					}
				} else if (!md5InTagFile.equals(md5)) {
					// Entry has incorrect MD5 in manifest tag file.
					LOGGER.info("{}/{} contains incorrect entry for {}. Calculated MD5 {} doesn't match specified {}",
							pid, ManifestMd5TagFile.FILEPATH, dataRelPath, md5, md5InTagFile);
					if (!dryRun) {
						tagFilesSvc.addEntry(pid, ManifestMd5TagFile.class, dataRelPath, md5);
					}
				}
			} catch (InterruptedException | ExecutionException e) {
				LOGGER.error(e.getMessage(), e);
			}
		}

		// Check that for each entry in the payload manifest there's a payload file on disk. Delete entry if one doesn't
		// exist.
		removeExtraKeys(calcMd.keySet(), ManifestMd5TagFile.class);
	}
	
	/**
	 * Verifies the contents of each of the tag files. Checks that an entry exists in a tag file for every payload file.
	 * 
	 * @param plFiles
	 *            Payload files against which tag file entries will be verified.
	 * @throws IOException
	 */
	private void verifyTagFiles(Set<Path> plFiles) throws IOException {
		for (Path plFile : plFiles) {
			String dataRelPath = getDataRelPath(plFile);

			for (Class<? extends AbstractKeyValueFile> clazz : classes) {
				String valueInTagFile = tagFilesSvc.getEntryValue(pid, clazz, dataRelPath);
				if (valueInTagFile == null) {
					LOGGER.info("{}/{} doesn't contain entry for {}.", pid, clazz.getSimpleName(), dataRelPath);
					if (!dryRun) {
						AbstractTagFileTask task = createTask(clazz, dataRelPath.replaceFirst("^data/", ""));
						Future<Void> future = threadPoolSvc.submit(task);
						// Running each task one at a time so as to not fill up the task queue.
						try {
							future.get();
						} catch (InterruptedException | ExecutionException e) {
							LOGGER.error(e.getMessage(), e);
						}
					}
				}
			}
		}

		for (Class<? extends AbstractKeyValueFile> clazz : classes) {
			removeExtraKeys(plFiles, clazz);
		}
	}

	/**
	 * Creates a task object for generating a tag file entry for a payload file.
	 * 
	 * @param clazz
	 *            Tag file class missing an entry for the payload file
	 * @param relPath
	 *            Relative path to the payload file
	 * @return Instance of an appropriate task object that adds an entry into the specified tag file
	 */
	private AbstractTagFileTask createTask(Class<? extends AbstractKeyValueFile> clazz, String relPath) {
		AbstractTagFileTask task = null;
		if (clazz == FileMetadataTagFile.class) {
			task = new MetadataTask(pid, storageProvider, relPath, tagFilesSvc);
		} else if (clazz == PronomFormatsTagFile.class) {
			task = new PronomTask(pid, storageProvider, relPath, tagFilesSvc);
		} else if (clazz == TimestampsTagFile.class) {
			task = new TimestampTask(pid, storageProvider, relPath, tagFilesSvc);
		} else if (clazz == VirusScanTagFile.class) {
			task = new VirusScanTask(pid, storageProvider, relPath, tagFilesSvc);
		} else if (clazz == PreservationMapTagFile.class) {
			task = new PreservationTask(pid, storageProvider, relPath, tagFilesSvc);
		} else {
			throw new IllegalArgumentException(clazz.getSimpleName());
		}
		return task;
	}

	/**
	 * Removes keys for non existent files in the specified tag file.
	 * 
	 * @param plFiles
	 *            Payload files. Entries in tag files for files other than these will be removed.
	 * @param clazz
	 *            Tag file to from extra keys from
	 * @throws IOException
	 */
	private void removeExtraKeys(Set<Path> plFiles, Class<? extends AbstractKeyValueFile> clazz) throws IOException {
		Set<String> keysForRemoval = new HashSet<>();
		for (Entry<String, String> tagFileEntry : tagFilesSvc.getAllEntries(pid, clazz).entrySet()) {
			Path plFile = bagDir.resolve(tagFileEntry.getKey());
			if (!Files.isRegularFile(plFile)) {
				keysForRemoval.add(tagFileEntry.getKey());
			}
		}
		if (!dryRun) {
			for (String key : keysForRemoval) {
				tagFilesSvc.removeEntry(pid, clazz, key);
			}
		}
	}

	/**
	 * Extracts a the portion of a path relative to the bag directory.
	 * 
	 * @param file
	 *            Path to a file
	 * @return Relative path as String
	 */
	private String getDataRelPath(Path file) {
		return FilenameHelper.normalizePathSeparators(bagDir.relativize(file).toString());
	}

	/**
	 * Enumerates files in a directory and its subdirectories.
	 * 
	 * @param dir
	 *            Directory to walk
	 * @return Set of files in that directory and its subdirectories.
	 * @throws IOException
	 */
	private Set<Path> listFilesInDir(Path dir) throws IOException {
		Set<Path> files = new HashSet<Path>();
		
		if (Files.isDirectory(dir)) {
			try (DirectoryStream<Path> dirItems = Files.newDirectoryStream(dir)) {
				for (Path dirItem : dirItems) {
					if (Files.isDirectory(dirItem)) {
						files.addAll(listFilesInDir(dirItem));
					} else if (Files.isRegularFile(dirItem)){
						files.add(dirItem.toAbsolutePath());
					}
				}
			}
		}
		
		return files;
	}
	
	private Path getPayloadDir() {
		return bagDir.resolve("data/");
	}
}
