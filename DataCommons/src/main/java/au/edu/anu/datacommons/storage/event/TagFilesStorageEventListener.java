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

package au.edu.anu.datacommons.storage.event;

import static java.text.MessageFormat.format;
import gov.loc.repository.bagit.BagFactory.Version;
import gov.loc.repository.bagit.impl.AbstractBagConstants;
import gov.loc.repository.bagit.impl.BagItTxtImpl;
import gov.loc.repository.bagit.utilities.FilenameHelper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import au.edu.anu.datacommons.data.db.model.FedoraObject;
import au.edu.anu.datacommons.security.service.FedoraObjectService;
import au.edu.anu.datacommons.storage.DcStorage;
import au.edu.anu.datacommons.storage.archive.ArchiveTask;
import au.edu.anu.datacommons.storage.archive.ArchiveTask.Operation;
import au.edu.anu.datacommons.storage.controller.StorageController;
import au.edu.anu.datacommons.storage.datafile.StagedDataFile;
import au.edu.anu.datacommons.storage.event.tasks.BagCompletionTask;
import au.edu.anu.datacommons.storage.event.tasks.ManifestMd5Task;
import au.edu.anu.datacommons.storage.event.tasks.MetadataTask;
import au.edu.anu.datacommons.storage.event.tasks.PreservationTask;
import au.edu.anu.datacommons.storage.event.tasks.PronomTask;
import au.edu.anu.datacommons.storage.event.tasks.StorageSearchIndexTask;
import au.edu.anu.datacommons.storage.event.tasks.TimestampTask;
import au.edu.anu.datacommons.storage.event.tasks.VirusScanTask;
import au.edu.anu.datacommons.storage.info.RecordDataInfoService;
import au.edu.anu.datacommons.storage.messagedigest.FileMessageDigests.Algorithm;
import au.edu.anu.datacommons.storage.provider.StorageProvider;
import au.edu.anu.datacommons.storage.provider.StorageException;
import au.edu.anu.datacommons.storage.search.StorageSearchService;
import au.edu.anu.datacommons.storage.tagfiles.BagItTagFile;
import au.edu.anu.datacommons.storage.tagfiles.FileMetadataTagFile;
import au.edu.anu.datacommons.storage.tagfiles.ManifestMd5TagFile;
import au.edu.anu.datacommons.storage.tagfiles.PreservationMapTagFile;
import au.edu.anu.datacommons.storage.tagfiles.PronomFormatsTagFile;
import au.edu.anu.datacommons.storage.tagfiles.TagFilesService;
import au.edu.anu.datacommons.storage.tagfiles.TimestampsTagFile;
import au.edu.anu.datacommons.storage.tagfiles.VirusScanTagFile;
import au.edu.anu.datacommons.storage.temp.UploadedFileInfo;
import au.edu.anu.datacommons.tasks.ThreadPoolService;
import au.edu.anu.datacommons.util.Util;

/**
 * A listener class that that receives notifications from {@link DcStorage} class before and after a file event occurs.
 * A File Event is
 * 
 * <ul>
 * <li>Add File
 * <li>Read File
 * <li>Update File
 * <li>Delete File
 * <li>Update Tagfile
 * </ul>
 * 
 * Each event performs a specific list of tasks pre and post event.
 * 
 * @author Rahul Khanna
 * 
 */
@Component
public class TagFilesStorageEventListener implements StorageEventListener {
	private static final Logger LOGGER = LoggerFactory.getLogger(TagFilesStorageEventListener.class);

	@Autowired(required = true)
	ThreadPoolService threadPoolSvc;

	@Autowired(required = true)
	TagFilesService tagFilesSvc;

	@Resource(name = "fedoraObjectServiceImpl")
	FedoraObjectService fedoraObjectService;

	@Autowired
	StorageSearchService searchSvc;

	private Path archiveRootDir;

	public void setArchiveRootDir(Path archiveRootDir) {
		this.archiveRootDir = archiveRootDir;
	}

	/**
	 * This method gets called to notify this class of an event.
	 * 
	 * @param time
	 *            EventTime.PRE if pre-event notification, EventTime.POST if post-event notification.
	 * @param type
	 *            Type of event
	 * @param pid
	 *            Identifier of collection record
	 * @param bagDir
	 *            Directory where the bag for specified record is located. Note this is not the payload directory within
	 *            the bag.
	 * @param relPath
	 *            Relative path (from the payload directory) to the file on which event was performed. This must point
	 *            to a file and not to a directory. If an event was performed on a directory, then a notification should
	 *            be sent for each file within that directory and each of its subdirectories. This can be null if the
	 *            event was not for a payload file.
	 * @param ufi
	 *            Information about the source file that will replace or has replaced the payload file.
	 * @throws IOException
	 */
	@Override
	public void notify(EventTime time, EventType type, String pid, String relPath, StorageProvider provider, StagedDataFile ufi)
			throws IOException {
		relPath = normalizeRelPath(relPath);
		if (time == EventTime.PRE) {
			try {
				processPreEventTasks(type, pid, relPath, provider, ufi);
			} catch (StorageException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (time == EventTime.POST) {
			try {
				processPostEventTasks(type, pid, relPath, provider, ufi);
			} catch (StorageException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			throw new RuntimeException(format("Unexpected event time - {0}", time));
		}

	}

	/**
	 * Performs pre-event tasks for the specified event type.
	 * 
	 * @param type
	 *            Type of event that triggered the notification
	 * @param pid
	 *            Identifier of collection record
	 * @param bagDir
	 *            Bag directory of specified record.
	 * @param relPath
	 *            Relative path (from the payload directory) to the file on which event will be performed.
	 * @param ufi
	 *            Information about source file that will replace the payload file represented by relPath.
	 * @throws IOException
	 * @throws StorageException 
	 */
	private void processPreEventTasks(EventType type, String pid, String relPath, StorageProvider provider, StagedDataFile ufi)
			throws IOException, StorageException {
		initBagIt(pid);
		if (type.isOneOf(EventType.TAGFILE_UPDATE)) {
			// TODO Create PID dir.
		}
		if (type.isOneOf(EventType.READ_FILE, EventType.UPDATE_FILE, EventType.DELETE_FILE)) {
		}
		if (type.isOneOf(EventType.ADD_FILE)) {
			// createParentPath(pid, bagDir, relPath);
		}
		if (type.isOneOf(EventType.UPDATE_FILE, EventType.DELETE_FILE)) {
			if (!hasHiddenParts(relPath)) {
				archive(type, pid, relPath, provider);
			} else {
				// deleteFileAndEmptyParents(bagDir, relPath);
			}
		}
	}

	/**
	 * Performs post-event tasks for the specified event type.
	 * 
	 * @param type
	 *            Type of event that triggered the notification
	 * @param pid
	 *            Identifier of collection record
	 * @param bagDir
	 *            Bag directory of the specified collection record
	 * @param relPath
	 *            Relative path (from the payload directory) to the file on which event was performed.
	 * @param ufi
	 *            Information about the source file that replaced the payload file represented by relPath. Note that the
	 *            source file may not exist when this method is called, but information about the file will still be
	 *            valid.
	 * @throws IOException
	 * @throws StorageException 
	 */
	private void processPostEventTasks(EventType type, final String pid, String relPath,
			StorageProvider storageProvider, StagedDataFile ufi) throws IOException, StorageException {
		List<Future<?>> waitList = new ArrayList<>();
		String dataPrependedRelPath = "data/" + relPath;
		if (type.isOneOf(EventType.ADD_FILE, EventType.UPDATE_FILE)) {
			waitList.add(threadPoolSvc.submit(new PreservationTask(pid, storageProvider, relPath, tagFilesSvc)));

			if (ufi.getMessageDigests().getMessageDigest(Algorithm.MD5) != null) {
				tagFilesSvc.addEntry(pid, ManifestMd5TagFile.class, dataPrependedRelPath, ufi.getMessageDigests()
						.getMessageDigest(Algorithm.MD5).getMessageDigestAsHex());
			} else {
				waitList.add(threadPoolSvc.submit(new ManifestMd5Task(pid, storageProvider, relPath, tagFilesSvc)));
			}
			waitList.add(threadPoolSvc.submit(new MetadataTask(pid, storageProvider, relPath, tagFilesSvc)));
			waitList.add(threadPoolSvc.submit(new PronomTask(pid, storageProvider, relPath, tagFilesSvc)));
			waitList.add(threadPoolSvc.submit(new TimestampTask(pid, storageProvider, relPath, tagFilesSvc)));
			waitList.add(threadPoolSvc.submit(new VirusScanTask(pid, storageProvider, relPath, tagFilesSvc)));

			if (searchSvc != null && isPublishedAndPublic(fedoraObjectService.getItemByPid(pid))) {
				threadPoolSvc.submit(new StorageSearchIndexTask(pid, storageProvider, relPath, searchSvc));
			}

			threadPoolSvc.submitCachedPool(new BagCompletionTask(pid, storageProvider, relPath, tagFilesSvc, waitList));
		}
		if (type.isOneOf(EventType.DELETE_FILE)) {
			String presvRelpath = tagFilesSvc.getEntryValue(pid, PreservationMapTagFile.class, dataPrependedRelPath);
			if (presvRelpath != null && !presvRelpath.equals("UNCONVERTIBLE")) {
				tagFilesSvc.removeEntry(pid, PreservationMapTagFile.class, presvRelpath);
				storageProvider.deleteFile(pid, presvRelpath.replaceFirst("^data/", ""));
			}
			tagFilesSvc.removeEntry(pid, PreservationMapTagFile.class, dataPrependedRelPath);

			tagFilesSvc.removeEntry(pid, ManifestMd5TagFile.class, dataPrependedRelPath);
			tagFilesSvc.removeEntry(pid, FileMetadataTagFile.class, dataPrependedRelPath);
			tagFilesSvc.removeEntry(pid, PreservationMapTagFile.class, dataPrependedRelPath);
			tagFilesSvc.removeEntry(pid, PronomFormatsTagFile.class, dataPrependedRelPath);
			tagFilesSvc.removeEntry(pid, TimestampsTagFile.class, dataPrependedRelPath);
			tagFilesSvc.removeEntry(pid, VirusScanTagFile.class, dataPrependedRelPath);

			// If a file's deleted, its search index entry must be deleted irrespective of
			// published status.
			if (searchSvc != null) {
				threadPoolSvc.submit(new StorageSearchIndexTask(pid, storageProvider, relPath, searchSvc));
			}

			threadPoolSvc.submitCachedPool(new BagCompletionTask(pid, storageProvider, relPath, tagFilesSvc, waitList));
		}

		if (type.isOneOf(EventType.TAGFILE_UPDATE)) {
			threadPoolSvc.submitCachedPool(new BagCompletionTask(pid, storageProvider, relPath, tagFilesSvc, waitList));
		}
	}

	/**
	 * Initialises the BagIt tag file within the bag of a specified record.
	 * 
	 * @param pid
	 *            Identifier of collection record
	 * @throws IOException
	 * @throws StorageException 
	 */
	private void initBagIt(String pid) throws IOException, StorageException {
		Map<String, String> bagItEntries = tagFilesSvc.getAllEntries(pid, BagItTagFile.class);
		if (bagItEntries.size() != 2) {
			// Must clear all entries to ensure insertion order.
			tagFilesSvc.clearAllEntries(pid, BagItTagFile.class);
			tagFilesSvc.addEntry(pid, BagItTagFile.class, BagItTxtImpl.VERSION_KEY, Version.V0_97.versionString);
			tagFilesSvc.addEntry(pid, BagItTagFile.class, BagItTxtImpl.CHARACTER_ENCODING_KEY,
					AbstractBagConstants.BAG_ENCODING);
		}
	}

	/**
	 * Normalises a relative path (relative to the payload directory) appropriate for a file (notifications are for
	 * files only) by:
	 * 
	 * <ol>
	 * <li>Change all path separators to '/'
	 * <li>Removing all leading and trailing path separators
	 * </ol>
	 * 
	 * @param relPath
	 *            Relative path to normalise. Can be null.
	 * @return Normalised relative path as String. null if relPath is null.
	 */
	private String normalizeRelPath(String relPath) {
		if (relPath != null) {
			StringBuilder processed = new StringBuilder(FilenameHelper.normalizePathSeparators(relPath));
			while (processed.charAt(0) == '/') {
				processed.deleteCharAt(0);
			}

			while (processed.charAt(processed.length() - 1) == '/') {
				processed.deleteCharAt(processed.length() - 1);
			}

			if (LOGGER.isTraceEnabled() && !processed.toString().equals(relPath)) {
				LOGGER.trace("Normalized relative path {} to {}", relPath, processed.toString());
			}
			return processed.toString();
		} else {
			return null;
		}
	}

	/**
	 * Checks if the specified relPath contains dot files or folders.
	 * 
	 * @param relPath
	 *            Relative path (relative to payload directory) to check
	 * 
	 * @return true if path has hidden part(s), false otherwise
	 */
	private boolean hasHiddenParts(String relPath) {
		Path relPathAsPath = Paths.get(relPath);
		for (int i = 0; i < relPathAsPath.getNameCount(); i++) {
			if (relPathAsPath.getName(i).toString().startsWith(".")) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Calls the archive task to archive the specified file.
	 * 
	 * @param type
	 *            Type of event causing the archival of a file - Update or Delete
	 * @param pid
	 *            Identifier of collection record
	 * @param bagDir
	 *            Bag directory
	 * @param relPath
	 *            Relative path (relative to payload dir) to the file to be archived.
	 * @throws IOException
	 * @throws StorageException
	 */
	private void archive(EventType type, String pid, String relPath, StorageProvider provider) throws IOException {
		Path fileToArchive = provider.getFileInfo(pid, relPath).getPath();
		if (this.archiveRootDir != null) {
			LOGGER.info("Archiving {}/data/{} ({}) for event {}", pid, relPath,
					Util.byteCountToDisplaySize(Files.size(fileToArchive)), type.toString());
			Operation op = type == EventType.UPDATE_FILE ? ArchiveTask.Operation.REPLACE : ArchiveTask.Operation.DELETE;
			ArchiveTask archiveTask = new ArchiveTask(this.archiveRootDir.toFile(), pid, fileToArchive.toFile(),
					gov.loc.repository.bagit.Manifest.Algorithm.MD5, op);
			threadPoolSvc.submit(archiveTask);
		}
	}

	/**
	 * Returns the payload directory for a specified bag directory.
	 * 
	 * @param bagDir
	 *            Bag directory whose payload directory is requested
	 * @return Payload directory as Path
	 */
	private Path getPayloadDir(Path bagDir) {
		return bagDir.resolve("data/");
	}

	/**
	 * Returns if a specified collection record is published and has the files-public flag set.
	 * 
	 * @param fo
	 *            FedoraObject representing the collection record.
	 * @return true if published <strong>and</strong> public, false otherwise
	 */
	private boolean isPublishedAndPublic(FedoraObject fo) {
		return fo.getPublished() && fo.isFilesPublic();
	}

	/**
	 * Deletes a file and its parent directories upto, but excluding the payload directory, if they're empty.
	 * 
	 * @param bagDir
	 *            Directory of the bag directory
	 * @param relPath
	 *            Relative path of a file from the payload directory i.e. without "data/" prefix
	 * @throws IOException
	 */
	private void deleteFileAndEmptyParents(Path bagDir, String relPath) throws IOException {
		Path targetFile = getPayloadDir(bagDir).resolve(relPath);
		Files.deleteIfExists(targetFile);
		boolean isEmptyDir;
		for (Path parent = targetFile.getParent(); !parent.equals(getPayloadDir(bagDir)); parent = parent.getParent()) {
			try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(parent)) {
				isEmptyDir = !dirStream.iterator().hasNext();
			}
			if (isEmptyDir) {
				// Must delete dir after directory stream's closed.
				Files.deleteIfExists(parent);
			} else {
				break;
			}
		}
	}
}
