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
import gov.loc.repository.bagit.utilities.FilenameHelper;
import gov.loc.repository.bagit.writer.impl.FileSystemWriter;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import au.edu.anu.datacommons.data.fedora.FedoraBroker;
import au.edu.anu.datacommons.properties.GlobalProps;
import au.edu.anu.datacommons.security.service.FedoraObjectService;
import au.edu.anu.datacommons.storage.DcStorage.FileInfo.Type;
import au.edu.anu.datacommons.storage.archive.ArchiveTask;
import au.edu.anu.datacommons.storage.archive.ArchiveTask.Operation;
import au.edu.anu.datacommons.storage.completer.CompleterTask;
import au.edu.anu.datacommons.storage.filesystem.FileFactory;
import au.edu.anu.datacommons.storage.info.BagSummary;
import au.edu.anu.datacommons.storage.search.StorageSearchService;
import au.edu.anu.datacommons.storage.tagfiles.ExtRefsTagFile;
import au.edu.anu.datacommons.storage.temp.TempFileTask;
import au.edu.anu.datacommons.storage.verifier.VerificationResults;
import au.edu.anu.datacommons.storage.verifier.VerificationTask;
import au.edu.anu.datacommons.util.Util;

import com.yourmediashelf.fedora.client.FedoraClientException;

public final class DcStorage implements Closeable {
	private static final Logger LOGGER = LoggerFactory.getLogger(DcStorage.class);
	public static final BagFactory bagFactory = new BagFactory();

	ExecutorService threadPool = Executors.newSingleThreadExecutor();

	private StorageSearchService searchSvc;

	private Set<Manifest.Algorithm> algorithms;
	private File bagsRootDir = null;
	private FileFactory ff;
	File archiveRootDir;
	File stagingDir;

	public DcStorage(String bagsDirpath, FileFactory ff) throws IOException {
		this(new File(bagsDirpath), ff);
	}

	/**
	 * Initializes an instance of DataCommons Storage.
	 * 
	 * @throws IOException
	 */
	public DcStorage(File bagsDir, FileFactory ff) throws IOException {
		this.bagsRootDir = bagsDir;
		this.ff = ff;
		initAlg();

		// If the directory specified doesn't exist, create it.
		if (!bagsDir.isDirectory() && !bagsDir.mkdirs()) {
			throw new IOException(format("Unable to create {0}. Check permissions.", bagsDir.getAbsolutePath()));
		}
	}

	public File getArchiveRootDir() {
		return archiveRootDir;
	}

	public void setArchiveRootDir(File archiveRootDir) {
		this.archiveRootDir = archiveRootDir;
	}

	public File getStagingDir() {
		return stagingDir;
	}

	public void setStagingDir(File stagingDir) {
		this.stagingDir = stagingDir;
	}

	public FileFactory getFileFactory() {
		return ff;
	}

	public void setFileFactory(FileFactory fileFactory) {
		this.ff = fileFactory;
	}

	public StorageSearchService getSearchSvc() {
		return searchSvc;
	}

	public void setSearchSvc(StorageSearchService searchSvc) {
		this.searchSvc = searchSvc;
	}

	File getBagsRootDir() {
		return bagsRootDir;
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
	 * @throws DcStorageException
	 * @throws IOException
	 */
	public void addFileToBag(String pid, URL fileUrl, String filepath, boolean shouldIndex) throws IOException {
		if (fileUrl == null) {
			throw new NullPointerException("File URL cannot be null.");
		}

		File downloadedFile = null;
		try {
			TempFileTask dlTask = new TempFileTask(fileUrl, stagingDir);
			try {
				downloadedFile = dlTask.call();
			} catch (Exception e) {
				throw new IOException(e);
			}
			addFileToBag(pid, downloadedFile, filepath, shouldIndex);
		} finally {
			FileUtils.deleteQuietly(downloadedFile);
		}
	}

	/**
	 * Adds a local file to a collection's bag.
	 * 
	 * @param pid
	 *            Pid of the collection record
	 * @param sourceFile
	 *            File object
	 * @throws IOException
	 * @throws FileNotFoundException
	 *             TODO
	 * @throws DcStorageException
	 */
	public void addFileToBag(String pid, File sourceFile, String filepath, boolean shouldIndex) throws IOException,
			FileNotFoundException {
		if (pid == null || pid.length() == 0) {
			throw new NullPointerException("Pid cannot be null");
		}
		if (sourceFile == null || !sourceFile.isFile()) {
			throw new FileNotFoundException(format("Source file {0} doesn't exist.", sourceFile.getAbsolutePath()));
		}
		if (filepath == null || filepath.length() == 0) {
			throw new NullPointerException("Target filepath cannot be null");
		}
		if (containsHiddenDirs(filepath)) {
			throw new IllegalArgumentException(format(
					"Cannot add files to hidden directories. {0} contains hidden directories/files", filepath));
		}

		File destFile = ff.getFile(getPayloadDir(pid), filepath);
		synchronized (destFile) {
			LOGGER.info("Adding file {} ({}) to record {}", filepath,
					FileUtils.byteCountToDisplaySize(sourceFile.length()), pid);
			if (destFile.isFile()) {
				scheduleFileArchival(pid, destFile, Operation.REPLACE);
			}
			createDirIfNotExists(destFile.getParentFile());
			if (!sourceFile.renameTo(destFile)) {
				throw new IOException(format("Unable to move {0} to {1}", sourceFile.getAbsolutePath(),
						destFile.getAbsolutePath()));
			}
		}
		CompleterTask compTask = new CompleterTask(bagFactory, ff, getBagDir(pid));
		compTask.addPayloadFileAddedUpdated(FilenameHelper.removeBasePath(getBagDir(pid).getAbsolutePath(),
				destFile.getAbsolutePath()));
		threadPool.submit(compTask);
		if (shouldIndex) {
			indexFile(getBagDir(pid), destFile);
		}
	}

	/**
	 * Deletes a file from the bag of a specified record. The bag itself is then completed in a separate request.
	 * 
	 * @param pid
	 *            Pid of the record whose bag contains the file to be deleted
	 * @param filepath
	 *            Path of the file in the bag. For example, "data/somefile.txt"
	 * @throws DcStorageException
	 *             when unable to delete the file
	 * @throws IOException
	 */
	public void deleteFileFromBag(String pid, String filepath) throws FileNotFoundException, IOException {
		if (pid == null || pid.length() == 0) {
			throw new NullPointerException("Pid cannot be null");
		}
		if (filepath == null || filepath.length() == 0) {
			throw new NullPointerException("Target filepath cannot be null");
		}
		if (containsHiddenDirs(filepath)) {
			throw new IllegalArgumentException(format(
					"Cannot delete files from hidden directories. {0} contains hidden directories/files", filepath));
		}

		if (fileExists(pid, filepath)) {
			deleteFile(pid, filepath);
		} else if (dirExists(pid, filepath)) {
			deleteDir(pid, filepath);
		} else {
			throw new FileNotFoundException(format("File/Dir {0} not found in record {1}.", filepath, pid));
		}
	}

	private void deleteDir(String pid, String filepath) throws IOException {
		File dirToDel = ff.getFile(getPayloadDir(pid), filepath);
		List<File> fileList = recurseAllFilesInDir(dirToDel);
		for (File f : fileList) {
			deleteFile(pid, FilenameHelper.removeBasePath(getPayloadDir(pid).getAbsolutePath(), f.getAbsolutePath()));
		}
		FileUtils.deleteDirectory(dirToDel);
	}
	
	private List<File> recurseAllFilesInDir(File root) {
		List<File> fileList = new ArrayList<File>();
		File[] filesAndDirs = root.listFiles();
		for (File f : filesAndDirs) {
			if (f.isFile()) {
				fileList.add(f);
			} else if (f.isDirectory()) {
				fileList.addAll(recurseAllFilesInDir(f));
			}
		}
		return fileList;
	}

	private void deleteFile(String pid, String filepath) throws IOException {
		File fileToDel = ff.getFile(getPayloadDir(pid), filepath);
		synchronized (fileToDel) {
			scheduleFileArchival(pid, fileToDel, Operation.DELETE);
		}
		CompleterTask compTask = new CompleterTask(bagFactory, ff, getBagDir(pid));
		compTask.addPayloadFileDeleted(FilenameHelper.removeBasePath(getBagDir(pid).getAbsolutePath(),
				fileToDel.getAbsolutePath()));
		threadPool.submit(compTask);
		indexFile(getBagDir(pid), fileToDel);
	}

	public void addDirectory(String pid, String filepath) throws IOException {
		if (pid == null || pid.length() == 0) {
			throw new NullPointerException("Pid cannot be null");
		}
		if (filepath == null || filepath.length() == 0) {
			throw new NullPointerException("Target filepath cannot be null");
		}
		if (containsHiddenDirs(filepath)) {
			throw new IllegalArgumentException(format(
					"Cannot add hidden directories. {0} contains hidden directories/files", filepath));
		}

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
	public void addExtRefs(String pid, Collection<String> urls) throws FileNotFoundException, IOException {
		if (pid == null || pid.length() == 0) {
			throw new NullPointerException("Pid cannot be null");
		}
		if (urls == null) {
			throw new NullPointerException("URL list cannot be null.");
		}
		File extRefsFile = ff.getFile(getBagDir(pid), ExtRefsTagFile.FILEPATH);
		synchronized (extRefsFile) {
			ExtRefsTagFile extRefs = new ExtRefsTagFile(extRefsFile);
			for (String url : urls) {
				if (!extRefs.containsKey(base64Encode(url))) {
					extRefs.put(base64Encode(url), url);
				} else {
					LOGGER.warn("External Reference {} already exists in record {}.", url, pid);
				}
			}
			extRefs.write();
			completeTagFiles(pid);
		}
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
	public void deleteExtRefs(String pid, Collection<String> urls) throws FileNotFoundException, IOException {
		if (pid == null || pid.length() == 0) {
			throw new NullPointerException("Pid cannot be null");
		}
		if (!bagDirExists(pid)) {
			throw new FileNotFoundException(format("Record {0} doesn't contain any files.", pid));
		}
		if (urls == null) {
			throw new NullPointerException("URL list cannot be null.");
		}
		File extRefsFile = ff.getFile(getBagDir(pid), ExtRefsTagFile.FILEPATH);
		synchronized (extRefsFile) {
			ExtRefsTagFile extRefs = new ExtRefsTagFile(extRefsFile);
			for (String url : urls) {
				if (extRefs.containsKey(base64Encode(url))) {
					extRefs.remove(base64Encode(url));
				} else {
					LOGGER.warn("External Reference {} didn't exists in record {}.", url, pid);
				}
			}
			extRefs.write();
			completeTagFiles(pid);
		}
	}

	public void recompleteBag(String pid) throws IOException {
		if (!bagDirExists(pid)) {
			throw new FileNotFoundException(format("No bag exists for record {0}", pid));
		}
		CompleterTask compTask = new CompleterTask(bagFactory, ff, getBagDir(pid));
		compTask.setCompleteAllFiles();
		threadPool.submit(compTask);
	}

	public VerificationResults verifyBag(String pid) throws Exception {
		if (!bagDirExists(pid)) {
			throw new FileNotFoundException(format("No bag exists for record {0}", pid));
		}

		VerificationTask vTask = new VerificationTask(ff, getBagDir(pid));
		return vTask.call();
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
			File possibleBagFile = ff.getFile(bagsRootDir, convertToDiskSafe(pid) + Bag.Format.values()[i].extension);
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
	public BagSummary getBagSummary(String pid) throws IOException {
		if (pid == null || pid.length() == 0) {
			throw new NullPointerException("Pid cannot be null");
		}
		if (!bagDirExists(pid)) {
			throw new FileNotFoundException(format("Record {0} doesn't contain any files", pid));
		}
		BagSummaryTask bsTask = new BagSummaryTask(ff, getBagDir(pid));
		BagSummary bs = bsTask.generateBagSummary();
		return bs;
	}

	public SortedSet<FileInfo> getFilesInDir(String pid, String path) throws IOException {
		if (pid == null || pid.length() == 0) {
			throw new NullPointerException("Pid cannot be null");
		}
		if (!payloadDirExists(pid)) {
			throw new FileNotFoundException(format("Record {0} doesn't contain any files", pid));
		}
		File payloadDir = getPayloadDir(pid);
		File dir = ff.getFile(payloadDir, path);
		if (!dir.isDirectory()) {
			throw new FileNotFoundException(format("Directory {0} doesn't exist in {1}", path, pid));
		}

		File[] files = dir.listFiles(new StorageFileFilter(getPayloadDir(pid)));
		SortedSet<FileInfo> fileInfos = new TreeSet<FileInfo>();
		for (File f : files) {
			FileInfo fi = new FileInfo();
			fi.filename = f.getName();
			fi.size = f.length();
			fi.type = f.isDirectory() ? FileInfo.Type.DIR : FileInfo.Type.FILE;
			fi.setRelFilepath(FilenameHelper.removeBasePath(payloadDir.getAbsolutePath(), f.getAbsolutePath()));
			fileInfos.add(fi);
		}

		return fileInfos;
	}

	public List<FileInfo> getParentDirs(String pid, String path) throws IOException {
		if (pid == null || pid.length() == 0) {
			throw new NullPointerException("Pid cannot be null");
		}
		if (!payloadDirExists(pid)) {
			throw new FileNotFoundException(format("Record {0} doesn't contain any files", pid));
		}
		File payloadDir = getPayloadDir(pid);
		File item = ff.getFile(payloadDir, path);
		if (item.isFile()) {
			item = item.getParentFile();
		}
		List<FileInfo> parents = new ArrayList<FileInfo>();
		while (!item.equals(payloadDir.getParentFile())) {
			FileInfo parent = new FileInfo();
			parent.setFilename(item.getName());
			parent.setRelFilepath(FilenameHelper.removeBasePath(payloadDir.getAbsolutePath(), item.getAbsolutePath()));
			parent.setSize(item.length());
			parent.setType(FileInfo.Type.DIR);

			parents.add(parent);
			item = item.getParentFile();
		}
		File parent = ff.getFile(payloadDir, path).getParentFile();
		if (parent.equals(payloadDir)) {
			parent = null;
		}
		Collections.reverse(parents);
		return parents;
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
	public InputStream getFilesAsZipStream(String pid, final Collection<String> fileSet) throws IOException {
		final PipedOutputStream sink = new PipedOutputStream();
		final Bag bag = getBag(pid);
		PipedInputStream zipInStream = new PipedInputStream(sink);

		// Writing PipedOutputStream needs to happen in a separate thread to prevent deadlock.
		Runnable zipWriter = new Runnable() {
			@Override
			public void run() {
				byte[] buffer = new byte[8192];
				ZipOutputStream zipOutStream = new ZipOutputStream(sink);

				try {
					for (String filepath : fileSet) {
						ZipEntry zipEntry = new ZipEntry(filepath);
						BagFile bagFile = bag.getBagFile(filepath);
						if (bagFile != null) {
							InputStream bagFileInStream = null;
							bagFileInStream = bag.getBagFile(filepath).newInputStream();
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
	public String getFileMd5(String pid, String filepath) throws IOException {
		if (pid == null || pid.length() == 0) {
			throw new NullPointerException("Pid cannot be null");
		}
		if (filepath == null || filepath.length() == 0) {
			throw new NullPointerException("Target filepath cannot be null");
		}

		if (!fileExists(pid, filepath)) {
			throw new FileNotFoundException(format("File {0} doesn't exist in record {1}", filepath, pid));
		}
		Bag bag = getBag(pid);
		if (bag == null) {
			throw new IOException(format("Bag not found for {0}", pid));
		}

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
	public long getFileSize(String pid, String filepath) throws FileNotFoundException, IOException {
		if (!fileExists(pid, filepath)) {
			throw new FileNotFoundException(format("File {0} not found in record {1}", filepath, pid));
		}
		File file = ff.getFile(getPayloadDir(pid), filepath);
		return file.length();
	}

	public Date getFileLastModified(String pid, String filepath) throws FileNotFoundException, IOException {
		if (!fileExists(pid, filepath)) {
			throw new FileNotFoundException(format("File {0} doesn't exist in record {1}", filepath, pid));
		}
		return new Date(ff.getFile(getPayloadDir(pid), filepath).lastModified());
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
		synchronized (payloadDir) {
			createDirIfNotExists(payloadDir);
		}
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
		synchronized (bagDir) {
			if (!bagDirExists(pid)) {
				createDirIfNotExists(bagDir);
				createBlankBag(pid);
			}
		}
		return bagDir;
	}

	private void indexFile(final File bagDir, final File file) {
		threadPool.submit(new Callable<Void>() {

			@Override
			public Void call() throws Exception {
				searchSvc.indexFile(bagDir, file);
				return null;
			}
		});
	}

	public void indexFilesInBag(String pid) throws IOException {
		if (pid == null || pid.length() == 0) {
			throw new NullPointerException("Pid cannot be null");
		}

		final File bagDir = getBagDir(pid);
		final File plDir = getPayloadDir(pid);

		threadPool.submit(new Callable<Void>() {

			@Override
			public Void call() throws Exception {
				searchSvc.indexAllFiles(bagDir, plDir);
				return null;
			}

		});
	}

	public void deindexFilesInBag(String pid) throws IOException {
		if (pid == null || pid.length() == 0) {
			throw new NullPointerException("Pid cannot be null");
		}

		final File bagDir = getBagDir(pid);
		final File plDir = getPayloadDir(pid);

		threadPool.submit(new Callable<Void>() {

			@Override
			public Void call() throws Exception {
				searchSvc.deindexAllFiles(bagDir, plDir);
				return null;
			}

		});
	}

	/**
	 * Creates a specified directory if it doesn't already exists. No action taken if it exists.
	 * 
	 * @param dir
	 *            directory to create as File.
	 * @throws IOException
	 *             if unable to create dir
	 */
	private void createDirIfNotExists(File dir) throws IOException {
		if (!dir.isDirectory()) {
			if (!dir.mkdirs()) {
				throw new IOException("Unable to create directory: " + dir.getAbsolutePath());
			}
		}
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

	private void scheduleFileArchival(String pid, File fileToArchive, Operation op) throws IOException {
		if (this.archiveRootDir == null) {
			LOGGER.warn("Archive directory not specified. File {} will be deleted and not archived.",
					fileToArchive.getAbsolutePath());
			if (!fileToArchive.delete()) {
				throw new IOException(format("Unable to delete file {0}", fileToArchive.getAbsolutePath()));
			}
		} else {
			LOGGER.info("File {} ({}) already exists in record {}. It will be archived and replaced with new file.",
					fileToArchive.getName(), FileUtils.byteCountToDisplaySize(fileToArchive.length()), pid);
			ArchiveTask archiveTask = new ArchiveTask(this.archiveRootDir, pid, fileToArchive, Algorithm.MD5, op);
			threadPool.submit(archiveTask);
		}
	}

	private void completeTagFiles(String pid) {
		Bag bag = null;
		try {
			bag = bagFactory.createBag(getBagDir(pid), LoadOption.BY_FILES);

			// Complete tag files.
			TagManifestCompleter tagManifestCompleter = new TagManifestCompleter(bagFactory);
			bag = bag.makeComplete(tagManifestCompleter);

			// Write the bag.
			FileSystemWriter writer = new FileSystemWriter(bagFactory);
			writer.setTagFilesOnly(true);
			bag = writer.write(bag, getBagDir(pid));
		} catch (IOException e) {
			LOGGER.warn(e.getMessage(), e);
		} finally {
			IOUtils.closeQuietly(bag);
		}
	}

	/**
	 * Creates a blank bag for a record. A blank bag contains no payload files but contains all essential files as
	 * specified in the BagIt Specification.
	 * 
	 * @param pid
	 * @return Created Bag
	 * @throws IOException
	 * @see <a href="http://www.digitalpreservation.gov/documents/bagitspec.pdf">The BagIt File Packaging Format</a>
	 */
	private Bag createBlankBag(String pid) throws IOException {
		Bag bag = bagFactory.createBag();
		try {
			if (bag.getBagItTxt() == null) {
				bag.putBagFile(bag.getBagPartFactory().createBagItTxt());
			}

			for (Manifest.Algorithm iAlg : algorithms) {
				// Payload manifest
				if (bag.getPayloadManifest(iAlg) == null) {
					bag.putBagFile(bag.getBagPartFactory().createManifest(
							ManifestHelper.getPayloadManifestFilename(iAlg, bag.getBagConstants())));
				}

				// Tag Manifest
				if (bag.getTagManifest(iAlg) == null) {
					bag.putBagFile(bag.getBagPartFactory().createManifest(
							ManifestHelper.getTagManifestFilename(iAlg, bag.getBagConstants())));
				}
			}

			// BagInfoTxt
			if (bag.getBagInfoTxt() == null) {
				bag.putBagFile(bag.getBagPartFactory().createBagInfoTxt());
			}
			bag.getBagInfoTxt().addExternalIdentifier(pid);

			bag = bag.makeComplete();

			FileSystemWriter writer = new FileSystemWriter(bagFactory);
			writer.setTagFilesOnly(true);
			bag = writer.write(bag, getBagDir(pid));
		} finally {
			IOUtils.closeQuietly(bag);
		}
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
	 * Waits until all pending tasks queued in this class' Executor Service are completed, or a threshold time of 15
	 * minutes is reached.
	 */
	@Override
	@PreDestroy
	public void close() {
		if (!threadPool.isShutdown()) {
			LOGGER.info("Shutting down DcStorage threads...");
			threadPool.shutdown();
			try {
				// Wait until all threads have finished or timeout threshold reached.
				threadPool.awaitTermination(15, TimeUnit.MINUTES);
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

	private void initAlg() {
		algorithms = new HashSet<Manifest.Algorithm>(1);
		algorithms.add(Manifest.Algorithm.MD5);
	}

	@XmlRootElement
	public static class RecordDataInfo {
		private String pid;
		private List<FileInfo> parents = new ArrayList<FileInfo>();
		private Collection<FileInfo> files = new ArrayList<FileInfo>();
		private String uri;

		@XmlElement
		public String getPid() {
			return pid;
		}

		public void setPid(String pid) {
			this.pid = pid;
		}

		@XmlElement
		public List<FileInfo> getParents() {
			return parents;
		}

		public void setParents(List<FileInfo> parents) {
			this.parents = parents;
		}

		@XmlElementWrapper
		public Collection<FileInfo> getFiles() {
			return files;
		}

		public void setFiles(Collection<FileInfo> files) {
			this.files = files;
		}

		@XmlElement
		public String getUri() {
			return uri;
		}

		public void setUri(String uri) {
			this.uri = uri;
		}
	}

	@XmlType
	public static class FileInfo implements Comparable<FileInfo> {
		public enum Type {
			DIR, FILE
		};

		private String filename;
		private String relFilepath;
		private Type type;
		private long size;
		private String uri;

		@XmlElement
		public String getFilename() {
			return filename;
		}

		public void setFilename(String filename) {
			this.filename = filename;
		}

		@XmlElement
		public String getRelFilepath() {
			return relFilepath;
		}

		public void setRelFilepath(String relFilepath) {
			this.relFilepath = relFilepath;
		}

		@XmlElement
		public Type getType() {
			return type;
		}

		public void setType(Type type) {
			this.type = type;
		}

		@XmlElement
		public long getSize() {
			return size;
		}

		public void setSize(long size) {
			this.size = size;
		}

		@XmlElement
		public String getFriendlySize() {
			return Util.byteCountToDisplaySize(this.size);
		}

		@XmlElement
		public String getUri() {
			return uri;
		}

		public void setUri(String uri) {
			this.uri = uri;
		}

		@Override
		public int compareTo(FileInfo o) {
			if (this.type == Type.DIR && o.type == Type.FILE) {
				return -1;
			} else if (this.type == Type.FILE && o.type == Type.DIR) {
				return 1;
			} else {
				return this.filename.compareToIgnoreCase(o.filename);
			}
		}
	}

}
