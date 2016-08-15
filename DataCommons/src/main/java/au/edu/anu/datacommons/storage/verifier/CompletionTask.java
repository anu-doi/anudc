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

import au.edu.anu.datacommons.storage.event.tasks.AbstractTagFileTask;
import au.edu.anu.datacommons.storage.event.tasks.BagCompletionTask;
import au.edu.anu.datacommons.storage.event.tasks.MetadataTask;
import au.edu.anu.datacommons.storage.event.tasks.PreservationTask;
import au.edu.anu.datacommons.storage.event.tasks.PronomTask;
import au.edu.anu.datacommons.storage.event.tasks.TimestampTask;
import au.edu.anu.datacommons.storage.event.tasks.VirusScanTask;
import au.edu.anu.datacommons.storage.info.FileInfo;
import au.edu.anu.datacommons.storage.info.FileInfo.Type;
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
import gov.loc.repository.bagit.Manifest.Algorithm;
import gov.loc.repository.bagit.utilities.FilenameHelper;
import gov.loc.repository.bagit.utilities.MessageDigestHelper;

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
	private StorageProvider sp;
	private TagFilesService tagFilesSvc;
	private ThreadPoolService threadPoolSvc;
	
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
	public CompletionTask(String pid, StorageProvider storageProvider, TagFilesService tagFilesSvc, ThreadPoolService threadPoolSvc) {
		this.pid = pid;
		this.sp = storageProvider;
		this.tagFilesSvc = tagFilesSvc;
		this.threadPoolSvc = threadPoolSvc;
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
		StopWatch sw = new StopWatch();
		sw.start();
		LOGGER.info("Completing tag files for {}...", pid);
//		if (!dryRun) {
//			eventListener.notify(EventTime.PRE, EventType.TAGFILE_UPDATE, pid, bagDir, null, null);
//		}
		FileInfo dirInfo = sp.getDirInfo(pid, "", Integer.MAX_VALUE);
		Set<FileInfo> plFiles = new HashSet<>();
		plFiles = dirInfo.getChildrenRecursive();
		
		verifyMessageDigests(plFiles);
		verifyTagFiles(plFiles);
//		if (!dryRun) {
//			eventListener.notify(EventTime.POST, EventType.TAGFILE_UPDATE, pid, bagDir, null, null);
//		}
		BagCompletionTask bcTask = new BagCompletionTask(pid, sp, null, tagFilesSvc, null);
		bcTask.call();
		sw.stop();
		LOGGER.info("Tag files completed for {}. Time taken {}", pid, sw.getTimeElapsedFormatted());
		return null;
	}
	

	/**
	 * Verifies the message digests stored in a manifest match file's contents.
	 * 
	 * @param plFiles
	 *            Payload files within the bag
	 * @throws IOException
	 */
	private void verifyMessageDigests(Set<FileInfo> plFiles) throws IOException {
		Map<FileInfo, Future<String>> calcMd = calcMessageDigests(plFiles);
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
	private Map<FileInfo, Future<String>> calcMessageDigests(Set<FileInfo> plFiles) {
		Map<FileInfo, Future<String>> calcMd = new HashMap<>();
		for (FileInfo plFile : plFiles) {
			if (plFile.getType().equals(Type.FILE)) {
				final Path fPlFile = plFile.getPath();
				Future<String> mdFuture = threadPoolSvc.submit(new Callable<String>() {
	
					@Override
					public String call() throws Exception {
						return MessageDigestHelper.generateFixity(fPlFile.toFile(), Algorithm.MD5);
					}
					
				});
				calcMd.put(plFile, mdFuture);
			}
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
	private void compareMessageDigests(Map<FileInfo, Future<String>> calcMd) throws IOException {
		// Check that each payload file's calculated MD5 exists in the manifest tag file. Add it if it doesn't.
		for (Entry<FileInfo, Future<String>> calcMdEntry : calcMd.entrySet()) {
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
	private void verifyTagFiles(Set<FileInfo> plFiles) throws IOException {
		for (FileInfo plFile : plFiles) {
			if (plFile.getType().equals(Type.FILE)) {
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
			task = new MetadataTask(pid, sp, relPath, tagFilesSvc);
		} else if (clazz == PronomFormatsTagFile.class) {
			task = new PronomTask(pid, sp, relPath, tagFilesSvc);
		} else if (clazz == TimestampsTagFile.class) {
			task = new TimestampTask(pid, sp, relPath, tagFilesSvc);
		} else if (clazz == VirusScanTagFile.class) {
			task = new VirusScanTask(pid, sp, relPath, tagFilesSvc);
		} else if (clazz == PreservationMapTagFile.class) {
			task = new PreservationTask(pid, sp, relPath, tagFilesSvc, null);
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
	private void removeExtraKeys(Set<FileInfo> plFiles, Class<? extends AbstractKeyValueFile> clazz) throws IOException {
		Set<String> keysForRemoval = new HashSet<>();
		for (Entry<String, String> tagFileEntry : tagFilesSvc.getAllEntries(pid, clazz).entrySet()) {
			String relPath = tagFileEntry.getKey().substring("data/".length());
			if (!sp.fileExists(pid, relPath)) {
				keysForRemoval.add(tagFileEntry.getKey());
			}
		}
		if (!dryRun) {
			for (String key : keysForRemoval) {
				tagFilesSvc.removeEntry(pid, clazz, key);
			}
		}
	}

	private String getDataRelPath(FileInfo file) {
		return FilenameHelper.normalizePathSeparators("data/" + file.getRelFilepath());
	}

}
