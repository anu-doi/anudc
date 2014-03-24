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
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.storage.DcStorage;
import au.edu.anu.datacommons.storage.event.StorageEventListener;
import au.edu.anu.datacommons.storage.event.StorageEventListener.EventTime;
import au.edu.anu.datacommons.storage.event.StorageEventListener.EventType;
import au.edu.anu.datacommons.storage.event.tasks.AbstractTagFileTask;
import au.edu.anu.datacommons.storage.event.tasks.BagCompletionTask;
import au.edu.anu.datacommons.storage.event.tasks.MetadataTask;
import au.edu.anu.datacommons.storage.event.tasks.PreservationTask;
import au.edu.anu.datacommons.storage.event.tasks.PronomTask;
import au.edu.anu.datacommons.storage.event.tasks.TimestampTask;
import au.edu.anu.datacommons.storage.event.tasks.VirusScanTask;
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
 * @author Rahul Khanna
 *
 */
public class CompletionTask implements Callable<Void>{
	private static final Logger LOGGER = LoggerFactory.getLogger(CompletionTask.class);

	private String pid;
	private Path bagDir;
	private TagFilesService tagFilesSvc;
	private StorageEventListener eventListener;
	private ThreadPoolService threadPoolSvc;
	private DcStorage dcStorage;
	
	List<Class<? extends AbstractKeyValueFile>> classes;
	
	private boolean dryRun = false;
	
	public CompletionTask(String pid, Path bagDir, TagFilesService tagFilesSvc, StorageEventListener eventListener,
			ThreadPoolService threadPoolSvc, DcStorage dcStorage) {
		this.pid = pid;
		this.bagDir = bagDir;
		this.tagFilesSvc = tagFilesSvc;
		this.eventListener = eventListener;
		this.threadPoolSvc = threadPoolSvc;
		this.dcStorage = dcStorage;
		initTagFileClasses();
	}
	
	private void initTagFileClasses() {
		classes = new ArrayList<>(5);
		classes.add(FileMetadataTagFile.class);
		classes.add(PronomFormatsTagFile.class);
		classes.add(TimestampsTagFile.class);
		classes.add(VirusScanTagFile.class);
		classes.add(PreservationMapTagFile.class);
	}

	public void setDryRun(boolean dryRun) {
		this.dryRun = dryRun;
	}

	@Override
	public Void call() throws Exception {
		StopWatch sw = new StopWatch();
		sw.start();
		LOGGER.info("Completing tag files for {}...", pid);
		eventListener.notify(EventTime.PRE, EventType.TAGFILE_UPDATE, pid, bagDir, null, null);
		Set<Path> plFiles = listFilesInDir(getPayloadDir());
		verifyMessageDigests(plFiles);
		verifyTagFiles(plFiles);
		eventListener.notify(EventTime.POST, EventType.TAGFILE_UPDATE, pid, bagDir, null, null);
		sw.stop();
		LOGGER.info("Tag files completed for {}. Time taken {}", pid, sw.getTimeElapsedFormatted());
		return null;
	}
	
	private void verifyMessageDigests(Set<Path> plFiles) throws IOException {
		Map<Path, Future<String>> calcMd = calcMessageDigests(plFiles);
		compareMessageDigests(calcMd);
	}

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

	private AbstractTagFileTask createTask(Class<? extends AbstractKeyValueFile> clazz, String relPath) {
		AbstractTagFileTask task = null;
		if (clazz == FileMetadataTagFile.class) {
			task = new MetadataTask(pid, bagDir, relPath, tagFilesSvc);
		} else if (clazz == PronomFormatsTagFile.class) {
			task = new PronomTask(pid, bagDir, relPath, tagFilesSvc);
		} else if (clazz == TimestampsTagFile.class) {
			task = new TimestampTask(pid, bagDir, relPath, tagFilesSvc);
		} else if (clazz == VirusScanTagFile.class) {
			task = new VirusScanTask(pid, bagDir, relPath, tagFilesSvc);
		} else if (clazz == PreservationMapTagFile.class) {
			task = new PreservationTask(pid, bagDir, relPath, tagFilesSvc, dcStorage);
		} else {
			throw new IllegalArgumentException(clazz.getSimpleName());
		}
		return task;
	}

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

	private String getDataRelPath(Path file) {
		return FilenameHelper.normalizePathSeparators(bagDir.relativize(file).toString());
	}

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
