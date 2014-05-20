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

import static java.text.MessageFormat.format;
import gov.loc.repository.bagit.Manifest;
import gov.loc.repository.bagit.utilities.FilenameHelper;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import au.edu.anu.datacommons.storage.event.StorageEventListener;
import au.edu.anu.datacommons.storage.event.StorageEventListener.EventTime;
import au.edu.anu.datacommons.storage.event.StorageEventListener.EventType;
import au.edu.anu.datacommons.storage.filesystem.FileFactory;
import au.edu.anu.datacommons.storage.info.FileInfo;
import au.edu.anu.datacommons.storage.info.FileInfo.Type;
import au.edu.anu.datacommons.storage.info.RecordDataInfo;
import au.edu.anu.datacommons.storage.info.RecordDataInfoService;
import au.edu.anu.datacommons.storage.search.StorageSearchService;
import au.edu.anu.datacommons.storage.tagfiles.ExtRefsTagFile;
import au.edu.anu.datacommons.storage.tagfiles.TagFilesService;
import au.edu.anu.datacommons.storage.temp.TempFileService;
import au.edu.anu.datacommons.storage.temp.UploadedFileInfo;
import au.edu.anu.datacommons.storage.verifier.CompletionTask;
import au.edu.anu.datacommons.storage.verifier.VerificationResults;
import au.edu.anu.datacommons.storage.verifier.VerificationTask;
import au.edu.anu.datacommons.tasks.ThreadPoolService;
import au.edu.anu.datacommons.util.Util;

/**
 * Provides data storage management methods for adding/updating/deleting files to be stored in a collection record.
 * 
 * @author Rahul Khanna
 *
 */
@Component
public final class DcStorage {
	private static final Logger LOGGER = LoggerFactory.getLogger(DcStorage.class);

	@Autowired(required = true)
	private RecordDataInfoService rdiSvc;
	@Autowired
	private StorageSearchService searchSvc;
	@Autowired(required = true)
	FileFactory ff;
	@Autowired
	private StorageEventListener eventListener;
	@Autowired(required = true)
	private ThreadPoolService threadPoolSvc;
	@Autowired(required = true)
	private TagFilesService tagFilesSvc;
	@Autowired
	private TempFileService tmpFileSvc;

	private Set<Manifest.Algorithm> algorithms;
	private File bagsRootDir = null;

	public DcStorage(String bagsDirpath) throws IOException {
		this(new File(bagsDirpath));
	}

	/**
	 * Initializes an instance of DataCommons Storage.
	 * 
	 * @throws IOException
	 */
	public DcStorage(File bagsDir) throws IOException {
		this.bagsRootDir = bagsDir;
		initAlg();

		// If the directory specified doesn't exist, create it.
		if (!bagsDir.isDirectory() && !bagsDir.mkdirs()) {
			throw new IOException(format("Unable to create {0}. Check permissions.", bagsDir.getAbsolutePath()));
		}
	}

	/**
	 * Downloads a file from a specified URL and adds it to the bag of a specified collection.
	 * 
	 * @param pid
	 *            Pid of the collection record
	 * @param fileUrl
	 *            URL from where to download the file
	 * @param filepath
	 *            Filename to save as
	 * @throws IOException
	 */
	public void addFile(String pid, URL fileUrl, String filepath) throws IOException {
		if (fileUrl == null) {
			throw new NullPointerException("File URL cannot be null.");
		}

		Future<UploadedFileInfo> futureTask = tmpFileSvc.saveInputStream(fileUrl.toString(), -1, null);
		
		UploadedFileInfo ufi = null;
		try {
			ufi = futureTask.get();
			addFile(pid, ufi, filepath);
		} catch (InterruptedException | ExecutionException e) {
			if (e.getCause() instanceof IOException) {
				throw (IOException) e.getCause();
			} else {
				throw new IOException(e);
			}
		} finally {
			if (ufi != null) {
				Files.deleteIfExists(ufi.getFilepath());
			}
		}
	}

	/**
	 * Adds a file to a collection record.
	 * 
	 * @param pid
	 *            Identifier of collection record
	 * @param sourceFileInfo
	 *            Object containing location and other information about the file to store in bag
	 * @param filepath
	 *            Path relative to the payload directory in which the file will be saved.
	 * @throws IOException
	 *             when unable to read the source file
	 * @throws FileNotFoundException
	 *             when source file doesn't exist
	 */
	public void addFile(String pid, UploadedFileInfo sourceFileInfo, String filepath) throws IOException,
			FileNotFoundException {
		checkNoHiddenParts(filepath);
		processAddFile(pid, sourceFileInfo, filepath);
	}

	/**
	 * Adds a local file to a collection's bag without verifying if the filepath contains any hidden elements. This
	 * method allows files to be saved as 'dot files' and into 'dot directories'. This method is not called as a result
	 * of a user request, but primarily to store alternate versions of user uploaded files, such as storing preservation
	 * format files in '.preserve' directory.
	 * 
	 * @param pid
	 *            Identifier of collection record
	 * @param sourceFileInfo
	 *            Object containing location and other information about the file to store in bag
	 * @param filepath
	 *            Path relative to the payload directory in which the file will be saved.
	 * @throws IOException
	 *             when unable to read the source file
	 * @throws FileNotFoundException
	 *             when source file doesn't exist
	 */
	public void addHiddenFile(String pid, UploadedFileInfo sourceFileInfo, String filepath) throws IOException,
			FileNotFoundException {
		processAddFile(pid, sourceFileInfo, filepath);
	}
	
	private void processAddFile(String pid, UploadedFileInfo sourceFileInfo, String filepath) throws FileNotFoundException, IOException {
		validatePid(pid);
		verifySourceFile(sourceFileInfo.getFilepath());
		normalizeRelPath(filepath);

		File destFile = ff.getFile(getPayloadDir(pid), filepath);
		synchronized (destFile) {
			EventType eventType = destFile.isFile() ? eventType = EventType.UPDATE_FILE : EventType.ADD_FILE;
			eventListener.notify(EventTime.PRE, eventType, pid, getBagDir(pid).toPath(), filepath, sourceFileInfo);
			// Directory would have been created as part of pre event actions. This is to double-check that it exists.
			// Not having the directoryk throws an exception.
			createDirIfNotExists(destFile.getParentFile());
			
			// Existing file should have been archived. This is to delete if that failed.
			if (destFile.isFile()) {
				if (!destFile.delete()) {
					throw new IOException(format("Unable to delete {0}/data/{1}", pid, filepath));
				}
			}
			
			Files.move(sourceFileInfo.getFilepath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			eventListener.notify(EventTime.POST, eventType, pid, getBagDir(pid).toPath(), filepath, sourceFileInfo);
		}
		LOGGER.debug("Added file {}/data/{} ({})", pid, filepath, Util.byteCountToDisplaySize(destFile.length()),
				pid);
	}

	/**
	 * Deletes a file or directory from the bag of a specified record. The bag itself is then completed in a separate
	 * request.
	 * 
	 * @param pid
	 *            Pid of the record whose bag contains the file to be deleted
	 * @param filepath
	 *            Path of the file or directory in the bag. For example, "data/somefile.txt"
	 * @throws IOException
	 *             when unable to delete file.
	 */
	public void deleteItem(String pid, String filepath) throws FileNotFoundException, IOException {
		validatePid(pid);
		normalizeRelPath(filepath);
		checkNoHiddenParts(filepath);

		if (fileExists(pid, filepath)) {
			processDeleteFile(pid, filepath);
		} else if (dirExists(pid, filepath)) {
			deleteDir(pid, filepath);
		} else {
			throw new FileNotFoundException(format("File/Dir {0}/data/{1} not found.", pid, filepath));
		}
	}
	
	/**
	 * Deletes a directory within a collection record. Deletes individual files within the specified directory.
	 * 
	 * @param pid
	 *            Identifier of collection record.
	 * @param filepath
	 *            Path of the directory to delete.
	 * @throws IOException
	 *             when unable to delete
	 */
	private void deleteDir(String pid, String filepath) throws IOException {
		File dirToDel = ff.getFile(getPayloadDir(pid), filepath);
		List<File> fileList = recurseAllFilesInDir(dirToDel);
		for (File f : fileList) {
			processDeleteFile(pid,
					FilenameHelper.removeBasePath(getPayloadDir(pid).getAbsolutePath(), f.getAbsolutePath()));
		}
		deleteTree(dirToDel.toPath());
		if (dirToDel.isDirectory()) {
			throw new IOException(format("Unable to delete {0}/data/{1}", pid, filepath));
		}
	}

	/**
	 * Processes the deletion of a single file. Calls the storage event listener before and after the event to perform
	 * bag completion tasks.
	 * 
	 * @param pid
	 *            Identifier of collection record
	 * @param filepath
	 *            Path of the file relative to payload directory to delete
	 * @throws IOException
	 *             when unable to delete the file
	 */
	public void processDeleteFile(String pid, String filepath) throws IOException {
		File fileToDel = ff.getFile(getPayloadDir(pid), filepath);
		if (fileToDel.isFile()) {
			synchronized (fileToDel) {
				eventListener
						.notify(EventTime.PRE, EventType.DELETE_FILE, pid, getBagDir(pid).toPath(), filepath, null);
				// File should have been moved from its original location as part of pre-event tasks so no need to
				// delete.
				if (fileToDel.isFile() && (!fileToDel.delete())) {
					throw new IOException(format("Unable to delete file {0}/data/{1}", pid, filepath));
				}
				eventListener.notify(EventTime.POST, EventType.DELETE_FILE, pid, getBagDir(pid).toPath(), filepath,
						null);
			}
		}
	}

	private List<File> recurseAllFilesInDir(File root) {
		List<File> fileList = new ArrayList<File>();
		File[] filesAndDirs = root.listFiles();
		for (File f : filesAndDirs) {
			if (f.isDirectory()) {
				fileList.addAll(recurseAllFilesInDir(f));
			} else if (f.isFile()) {
				fileList.add(f);
			}
		}
		return fileList;
	}

	public void createPayloadDir(String pid, String filepath) throws IOException {
		validatePid(pid);
		normalizeRelPath(filepath);
		checkNoHiddenParts(filepath);
		File newDir = ff.getFile(getPayloadDir(pid), filepath);
		if (newDir.exists()) {
			throw new IOException(format("A dir or file already exists at {0} in record {1}.", filepath, pid));
		}

		boolean success = newDir.mkdirs();
		if (!success) {
			throw new IOException(format("Unable to create {0} in {1}", filepath, pid));
		}
	}

	/**
	 * Adds a reference to an external URL in a bag.
	 * 
	 * @param pid
	 *            Pid of a collection as String
	 * @param url
	 *            External URL as String
	 * @throws IOException
	 */
	public void addExtRefs(String pid, Collection<String> urls) throws IOException {
		validatePid(pid);
		verifyNonEmptyCollection(urls);
		
		eventListener.notify(EventTime.PRE, EventType.TAGFILE_UPDATE, pid, getBagDir(pid).toPath(), null, null);
		for (String url : urls) {
			tagFilesSvc.addEntry(pid, ExtRefsTagFile.class, base64Encode(url), url);
		}
		eventListener.notify(EventTime.POST, EventType.TAGFILE_UPDATE, pid, getBagDir(pid).toPath(), null, null);
	}

	/**
	 * Deletes an external reference in the bag of a specified record.
	 * 
	 * @param pid
	 *            Pid of the record whose bag contains the external reference to delete
	 * @param url
	 *            URL to delete
	 * @throws IOException
	 */
	public void deleteExtRefs(String pid, Collection<String> urls) throws IOException {
		validatePid(pid);
		verifyNonEmptyCollection(urls);
		
		eventListener.notify(EventTime.PRE, EventType.TAGFILE_UPDATE, pid, getBagDir(pid).toPath(), null, null);
		for (String url : urls) {
			tagFilesSvc.removeEntry(pid, ExtRefsTagFile.class, base64Encode(url));
		}
		eventListener.notify(EventTime.POST, EventType.TAGFILE_UPDATE, pid, getBagDir(pid).toPath(), null, null);
	}

	public VerificationResults verifyBag(String pid) throws Exception {
		if (!bagDirExists(pid)) {
			throw new FileNotFoundException(format("No bag exists for record {0}", pid));
		}
	
		VerificationTask vTask = new VerificationTask(pid, getBagDir(pid).toPath(), tagFilesSvc, this.threadPoolSvc);
		Future<VerificationResults> vTaskFuture = threadPoolSvc.submitIdlePool(vTask);
		return vTaskFuture.get();
	}

	public void recompleteBag(String pid) throws IOException {
		if (!bagDirExists(pid)) {
			throw new FileNotFoundException(format("No bag exists for record {0}", pid));
		}
		CompletionTask completionTask = new CompletionTask(pid, getBagDir(pid).toPath(), tagFilesSvc, eventListener,
				threadPoolSvc, this);
		threadPoolSvc.submitIdlePool(completionTask);
	}

	/**
	 * Checks if a bag exists for a specified collection.
	 * 
	 * @param pid
	 *            A collection's Pid as String
	 * @return true if bag exists, false otherwise.
	 */
	public boolean bagExists(String pid) {
		return bagDirExists(pid);
	}

	public RecordDataInfo getRecordDataInfo(String pid) throws IOException {
		return rdiSvc.createRecordDataInfo(pid, getPayloadDir(pid).toPath());
	}
	
	public RecordDataInfo getDirLimitedRecordDataInfo(String pid, String filepath) throws IOException {
		return rdiSvc.createDirLimitedRecordDataInfo(pid, getPayloadDir(pid).toPath(), filepath);
	}
	
	public FileInfo getFileInfo(String pid, String filepath) throws IOException {
		return rdiSvc.createFileInfo(pid, getPayloadDir(pid).toPath(), Paths.get(filepath));
	}

	/**
	 * Gets an InputStream of a file within the bag of a specified collection.
	 * 
	 * @param pid
	 *            Pid of the collection
	 * @param filePath
	 *            Path of file within the bag. E.g. data/file.txt
	 * @return Inputstream of file within bag
	 */
	public InputStream getFileStream(String pid, String filepath) throws FileNotFoundException, IOException {
		BufferedInputStream is = null;
		try {
			if (!fileExists(pid, filepath)) {
				throw new FileNotFoundException(format("File {0} not found in record {1}", filepath, pid));
			}
			File file = ff.getFile(getPayloadDir(pid), filepath);
			is = new BufferedInputStream(new FileInputStream(file));
		} catch (IOException e) {
			IOUtils.closeQuietly(is);
			throw e;
		}
		return is;
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
	public InputStream createZipStream(final String pid, Collection<String> fileSet) throws IOException {
		final PipedOutputStream sink = new PipedOutputStream();
		PipedInputStream zipInStream = new PipedInputStream(sink);
		final Collection<String> fFileSet = new HashSet<>();
		RecordDataInfo rdi = rdiSvc.createRecordDataInfo(pid, getBagDir(pid).toPath());
		// If fileset is null, zip all payload files (except hidden files).
		if (fileSet == null) {
			for (FileInfo fi : rdi.getFiles()) {
				fFileSet.add(fi.getRelFilepath());
			}
		} else {
			// Iterate through each item requested - add files as they are, iterate files within requested folders.
			for (String reqItem : fileSet) {
				if (fileExists(pid, reqItem)) {
					fFileSet.add(reqItem);
				} else if (dirExists(pid, reqItem)) {
					for (FileInfo fi : rdi.getFiles()) {
						if (fi.getType() == Type.FILE && fi.getRelFilepath().startsWith(reqItem)) {
							fFileSet.add(fi.getRelFilepath());
						}
					}
				}
			}
		}

		// Writing PipedOutputStream needs to happen in a separate thread to prevent deadlock.
		Callable<Void> zipWriter = new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				try (ZipOutputStream zipOutStream = new ZipOutputStream(sink)) {
					for (String filepath : fFileSet) {
						ZipEntry zipEntry = new ZipEntry(filepath);
						if (fileExists(pid, filepath)) {
							try (InputStream fileStream = getFileStream(pid, filepath)) {
								zipOutStream.putNextEntry(zipEntry);
								IOUtils.copy(fileStream, zipOutStream);
							} finally {
								zipOutStream.closeEntry();
								zipOutStream.flush();
							}
						}
					}
				}
				return null;
			}
		};

		threadPoolSvc.submitCachedPool(zipWriter);
		return zipInStream;
	}

	public boolean fileExists(String pid, String filepath) throws IOException {
		if (!payloadDirExists(pid)) {
			return false;
		}
		File file = ff.getFile(getPayloadDir(pid), filepath);
		return file.isFile();
	}

	public boolean dirExists(String pid, String filepath) throws IOException {
		if (!payloadDirExists(pid)) {
			return false;
		}
		File dir = ff.getFile(getPayloadDir(pid), filepath);
		return dir.isDirectory();
	}

	/**
	 * Returns if the payload directory exists in a bag.
	 * 
	 * @param pid
	 * @return true if exists, false otherwise
	 * @throws IOException
	 */
	boolean payloadDirExists(String pid) throws IOException {
		if (!bagDirExists(pid)) {
			return false;
		}
		File payloadDir = ff.getFile(getBagDir(pid), "data/");
		return payloadDir.isDirectory();
	}

	boolean bagDirExists(String pid) {
		File bagDir = ff.getFile(bagsRootDir, convertToDiskSafe(pid));
		return bagDir.isDirectory();
	}

	File getFile(String pid, String filepath) throws IOException {
		if (!fileExists(pid, filepath)) {
			throw new FileNotFoundException(format("File {0} doesn't exist in record {1}", filepath, pid));
		}
		File file = ff.getFile(getPayloadDir(pid), filepath);
		return file;
	}

	File getPayloadDir(String pid) throws IOException {
		File payloadDir = ff.getFile(getBagDir(pid), "data/");
		return payloadDir;
	}

	/**
	 * Returns a File object pointing to the directory containing the bag of a specified record. If the directory
	 * doesn't exist, creates it and populates it with blank bag files. Use {@code bagDirExists} to check if directory
	 * exists.
	 * 
	 * @param pid
	 *            ID of record
	 * @return File object pointing to directory.
	 * 
	 * @throws IOException
	 */
	File getBagDir(String pid) throws IOException {
		File bagDir = ff.getFile(bagsRootDir, convertToDiskSafe(pid));
		return bagDir;
	}

	public void indexFilesInBag(String pid) throws IOException {
		validatePid(pid);

		final File bagDir = getBagDir(pid);
		final File plDir = getPayloadDir(pid);

		threadPoolSvc.submit(new Callable<Void>() {

			@Override
			public Void call() throws Exception {
				searchSvc.indexAllFiles(bagDir, plDir);
				return null;
			}

		});
	}

	public void deindexFilesInBag(String pid) throws IOException {
		validatePid(pid);

		final File bagDir = getBagDir(pid);
		final File plDir = getPayloadDir(pid);

		threadPoolSvc.submit(new Callable<Void>() {

			@Override
			public Void call() throws Exception {
				searchSvc.deindexAllFiles(bagDir, plDir);
				return null;
			}

		});
	}

	private void deleteTree(Path dir) throws IOException {
		if (Files.exists(dir)) {
			try (DirectoryStream<Path> dirItems = Files.newDirectoryStream(dir)) {
				for (Path item : dirItems) {
					if (Files.isDirectory(item)) {
						deleteTree(item);
					}
					if (Files.isRegularFile(item)) {
						Files.delete(item);
					}
				}
			}
			
			// Retry code required on Windows machines. If a directory is open in another thread, then deleting it
			// throws an exception.
			boolean retry = false;
			do {
				try {
					Files.delete(dir);
					retry = false;
				} catch (FileSystemException e) {
					LOGGER.warn("Retrying delete {}", dir.toString());
					retry = true;
					try {
						Thread.sleep(500L);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			} while (retry);
		}
	}
	

	/**
	 * Creates a specified directory if it doesn't already exists. No action taken if it exists.
	 * 
	 * @param dir
	 *            directory to create as File.
	 * @throws IOException
	 *             if unable to create dir
	 */
	private synchronized void createDirIfNotExists(File dir) throws IOException {
		if (!dir.isDirectory()) {
			if (!dir.mkdirs()) {
				throw new IOException("Unable to create directory: " + dir.getAbsolutePath());
			}
		}
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

	private void validatePid(String pid) {
		if (pid == null || pid.length() == 0) {
			throw new NullPointerException("Pid cannot be null or empty");
		}
	}

	private void checkNoHiddenParts(String filepath) {
		if (containsHiddenDirs(filepath)) {
			throw new IllegalArgumentException(format(
					"Cannot add/delete files to hidden directories. {0} contains hidden directories/files", filepath));
		}
	}

	private void normalizeRelPath(String filepath) {
		if (filepath == null || filepath.length() == 0) {
			throw new NullPointerException("Target filepath cannot be null or empty");
		}
	}

	private void verifySourceFile(Path sourceFile) throws FileNotFoundException {
		if (sourceFile == null || !Files.isRegularFile(sourceFile)) {
			throw new FileNotFoundException(format("Source file {0} doesn't exist.", sourceFile.toString()));
		}
	}
	
	private void verifyNonEmptyCollection(Collection<String> urls) {
		if (urls == null) {
			throw new NullPointerException("URL list cannot be null.");
		}
	}

	private void verifyBagDirExists(String pid) throws FileNotFoundException {
		if (!bagDirExists(pid)) {
			throw new FileNotFoundException(format("Record {0} doesn't contain any files.", pid));
		}
	}

	private void initAlg() {
		algorithms = new HashSet<Manifest.Algorithm>(1);
		algorithms.add(Manifest.Algorithm.MD5);
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

	static boolean containsHiddenDirs(String filepath) {
		boolean hasHiddenDirs = false;

		String unixFilepath = FilenameUtils.separatorsToUnix(filepath);
		String[] parts = unixFilepath.split("/");
		for (int i = 0; i < parts.length; i++) {
			if (parts[i].startsWith(".")) {
				hasHiddenDirs = true;
				break;
			}
		}

		return hasHiddenDirs;
	}

}
