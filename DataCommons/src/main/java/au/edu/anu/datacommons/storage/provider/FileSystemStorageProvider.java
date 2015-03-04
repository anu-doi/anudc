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

package au.edu.anu.datacommons.storage.provider;

import static java.text.MessageFormat.format;
import gov.loc.repository.bagit.utilities.FilenameHelper;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.storage.datafile.StagedDataFile;
import au.edu.anu.datacommons.storage.filesystem.FileFactory;
import au.edu.anu.datacommons.storage.info.FileInfo;
import au.edu.anu.datacommons.storage.info.FileInfo.Type;

/**
 * @author Rahul Khanna
 *
 */
public class FileSystemStorageProvider extends AbstractStorageProvider {
	private static final Logger LOGGER = LoggerFactory.getLogger(FileSystemStorageProvider.class);

	private Path rootDir;
	
	private FileFactory<Path> ff = new FileFactory<>(200);
	
	public FileSystemStorageProvider(String rootDir) throws IOException {
		this(Paths.get(rootDir));
	}
	
	public FileSystemStorageProvider(Path rootDir) throws IOException {
		validateRootDir(rootDir);
		this.rootDir = rootDir;
	}
	
	@Override
	public FileInfo addFile(String pid, String relativePath, StagedDataFile sourceFile) throws IOException {
		FileInfo addedFile = null;
		Path targetPath = resolveTarget(pid, relativePath);
		validateTargetPath(targetPath);
		prepTargetPath(targetPath);
		
		// Add source file to storage.
		synchronized (ff.getFromCache(targetPath)) {
			moveFilePath(sourceFile, targetPath);
			// Create a FileInfo object representing the file that's been added.
		}
		addedFile = createFileInfo(pid, targetPath, 0);
		LOGGER.trace("Moved file {} to {}", sourceFile.getPath().toString(), targetPath.toString());
		return addedFile;
	}

	@Override
	public FileInfo addDir(String pid, String relativePath) throws IOException {
		Path targetPath = resolveTarget(pid, relativePath);
		validateTargetPath(targetPath);
		prepTargetPath(targetPath);
		
		// Create the directory at specified relative path.
		Files.createDirectory(targetPath);
		LOGGER.trace("Created directory {}", targetPath.toString());
		return createFileInfo(pid, targetPath, 0);
	}

	@Override
	public FileInfo renameFile(String pid, String oldRelativePath, String newRelativePath) throws IOException {
		FileInfo renamedFile = null;
		Path oldTargetPath = resolveTarget(pid, oldRelativePath);
		validateTargetPath(oldTargetPath);
		Path newTargetPath = resolveTarget(pid, newRelativePath);
		validateTargetPath(newTargetPath);
		
		// Check that the target doesn't already exist.
		if (fileExists(newTargetPath) || dirExists(newTargetPath)) {
			throw new IOException(format("Cannot rename {0} to {1} as it already exists.", oldTargetPath, newTargetPath));
		}
		
		prepTargetPath(newTargetPath);
		// Move the file from the old location to new.
		synchronized(ff.getFromCache(oldTargetPath)) {
			synchronized(ff.getFromCache(newTargetPath)) {
				Files.move(oldTargetPath, newTargetPath);
				
				// Check that the new file exists.
				if (!fileExists(newTargetPath) && !dirExists(newTargetPath)) {
					throw new IOException(format("Unable to rename {0} to {1}", oldTargetPath, newTargetPath));
				}
			}
		}
		renamedFile = createFileInfo(pid, newTargetPath, 0);
		LOGGER.trace("Moved file {} to {}", oldTargetPath.toString(), newTargetPath.toString());
		
		// Create a FileInfo object representing the renamed file.
		return renamedFile;
	}

	@Override
	public void deleteFile(String pid, String relativePath) throws IOException {
		Path targetPath = resolveTarget(pid, relativePath);
		validateTargetPath(targetPath);
		if (dirExists(targetPath)) {
			deleteTree(targetPath);
			LOGGER.trace("Deleted directory tree {}", targetPath.toString());
		} else if (fileExists(targetPath)){
			synchronized(ff.getFromCache(targetPath)) {
				Files.delete(targetPath);
			}
			LOGGER.trace("Deleted file {}", targetPath.toString());
		}
		// Check if the file/dir still exists.
		if (fileExists(targetPath) || dirExists(targetPath)) {
			throw new IOException(format("Unable to delete {0}", targetPath.toString()));
		}
	}

	@Override
	public boolean fileExists(String pid, String relativePath) {
		Path targetPath = resolveTarget(pid, relativePath);
		validateTargetPath(targetPath);
		return fileExists(targetPath);
	}

	@Override
	public boolean dirExists(String pid, String relativePath) {
		Path targetPath = resolveTarget(pid, relativePath);
		validateTargetPath(targetPath);
		return dirExists(targetPath);
	}

	@Override
	public FileInfo getFileInfo(String pid, String relativePath) throws IOException {
		Path targetPath = resolveTarget(pid, relativePath);
		validateTargetPath(targetPath);
		FileInfo fileInfo = createFileInfo(pid, targetPath, 0);
		return fileInfo;
	}
	
	@Override
	public FileInfo getDirInfo(String pid, String relativePath, int depth) throws IOException {
		Path targetPath = resolveTarget(pid, relativePath);
		validateTargetPath(targetPath);
		FileInfo fileInfo = createFileInfo(pid, targetPath, depth);
		return fileInfo;
	}

	@Override
	public InputStream readStream(String pid, String relativePath) throws IOException {
		BufferedInputStream bufferedInputStream = null;
		Path targetPath = resolveTarget(pid, relativePath);
		validateTargetPath(targetPath);
		synchronized(ff.getFromCache(targetPath)) {
			bufferedInputStream = new BufferedInputStream(Files.newInputStream(targetPath));
		}
		return bufferedInputStream;
	}
	
	@Override
	public FileInfo writeStream(String pid, String relativePath, InputStream stream) throws IOException {
		Path targetPath = resolveTarget(pid, relativePath);
		validateTargetPath(targetPath);
		if (!(stream instanceof BufferedInputStream)) {
			stream = new BufferedInputStream(stream);
		}
		try {
			synchronized(ff.getFromCache(targetPath)) {
				Files.copy(stream, targetPath, StandardCopyOption.REPLACE_EXISTING);
			}
		} finally {
			IOUtils.closeQuietly(stream);
		}
		return createFileInfo(pid, targetPath, 0);
	}
	
	@Override
	public InputStream readTagFileStream(String pid, String path) throws IOException {
		InputStream bufferedInputStream = null;
		Path tagFilePath = resolvePidDirPath(pid).resolve(path);
		synchronized(ff.getFromCache(tagFilePath)) {
			if (!fileExists(tagFilePath)) {
				bufferedInputStream = new ByteArrayInputStream(new byte[0]);
			} else {
				bufferedInputStream = new BufferedInputStream(Files.newInputStream(tagFilePath));
			}
		}
		return bufferedInputStream;
	}
	
	@Override
	public void writeTagFileStream(String pid, String path, InputStream stream) throws IOException {
		Path tagFilePath = resolvePidDirPath(pid).resolve(path);
		prepTargetPath(tagFilePath);
		if (!(stream instanceof BufferedInputStream)) {
			stream = new BufferedInputStream(stream);
		}
		try {
			synchronized(ff.getFromCache(tagFilePath)) {
				Files.copy(stream, tagFilePath, StandardCopyOption.REPLACE_EXISTING);
			}
		} finally {
			IOUtils.closeQuietly(stream);
		}
	}
	
	
	private Path resolveTarget(String pid, String relativePath) {
		return resolvePayloadDirPath(pid).resolve(relativePath);
	}

	private Path resolvePayloadDirPath(String pid) {
		return resolvePidDirPath(pid).resolve("data/");
	}

	private Path resolvePidDirPath(String pid) {
		return rootDir.resolve(convertToDiskSafe(pid));
	}

	private void deleteTree(Path targetPath) throws IOException {
		SimpleFileVisitor<Path> treeDeleter = new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				Files.delete(file);
				return FileVisitResult.CONTINUE;
			}
			
			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
				Files.delete(dir);
				return FileVisitResult.CONTINUE;
			}
		};
		Files.walkFileTree(targetPath, treeDeleter);
	}

	private boolean fileExists(Path targetPath) {
		return Files.isRegularFile(targetPath);
	}
	
	private boolean dirExists(Path targetPath) {
		return Files.isDirectory(targetPath);
	}

	private void validateTargetPath(Path targetPath) {
		// Check that the target path is not a parent of the root directory. This class must not write to any disk
		// location outside of the root directory.
		boolean isChildOfRoot = false;
		for (Path parent = targetPath.getParent(); parent != null; parent = parent.getParent()) {
			if (parent.equals(this.rootDir)) {
				isChildOfRoot = true;
				break;
			}
		}
		if (!isChildOfRoot) {
			throw new IllegalArgumentException(format("{0} is not a file/directory in {1}", targetPath, this.rootDir));
		}
	}
	
	private void validateExists(Path targetPath) throws FileNotFoundException {
		if (!fileExists(targetPath) && !dirExists(targetPath)) {
			throw new FileNotFoundException(format("File or directory {0} does not exist", targetPath.toString()));
		}
	}
	
	private void validateFileExists(Path targetPath) throws FileNotFoundException {
		if (!fileExists(targetPath)) {
			throw new FileNotFoundException(format("File {0} does not exist", targetPath.toString()));
		}
	}

	private void prepTargetPath(Path targetPath) throws IOException {
		Path parent = targetPath.getParent();
		if (!Files.isDirectory(parent)) {
			Files.createDirectories(parent);
		}
	}

	private FileInfo createFileInfo(String pid, Path targetPath, int depth) throws IOException {
		return createFileInfo(pid, targetPath, depth, null);
	}
	
	private FileInfo createFileInfo(String pid, Path targetPath, int depth, FileInfo parent) throws IOException {
		final FileInfo fi = new FileInfo();
		
		synchronized(ff.getFromCache(targetPath)) {
			fi.setPid(pid);
			fi.setFilename(targetPath.getFileName().toString());
			String relPath = resolvePayloadDirPath(pid).relativize(targetPath).toString();
			fi.setRelFilepath(FilenameHelper.normalizePathSeparators(relPath));
			fi.setLastModified(Files.getLastModifiedTime(targetPath));
			fi.setPath(targetPath);
			fi.setParent(parent);
			
			if (Files.isDirectory(targetPath)) {
				fi.setType(Type.DIR);
				
				if (depth > 0) {
					try (DirectoryStream<Path> ds = Files.newDirectoryStream(targetPath, new DirectoryStream.Filter<Path>() {
	
						@Override
						public boolean accept(Path entry) throws IOException {
							boolean accept = true;
							// Exclude files and dirs that start with '.'
							if (entry.getFileName().toString().startsWith(".")) {
								accept = false;
							}
							return accept;
						}
						
					})) {
						for (Path i : ds) {
							fi.addChild(createFileInfo(pid, i, depth - 1, fi));
						}
					}
				}
				
			} else if (Files.isRegularFile(targetPath)) {
				fi.setType(Type.FILE);
				fi.setSize(Files.size(targetPath));
			}
		}
		
		if (parent == null) {
			if (targetPath.getParent() != null && !targetPath.getParent().equals(this.rootDir)) {
				FileInfo parentFileInfo = createFileInfo(pid, targetPath.getParent(), 0, null);
				fi.setParent(parentFileInfo);
			}
		}		
		return fi;
	}
	
	

	private void validateRootDir(Path rootDir) throws IOException {
		// Ensure the root directory exists.
		if (!Files.isDirectory(rootDir)) {
			throw new IOException(format("Directory {0} does not exist.", rootDir.toString()));
		}

		// Ensure the directory's readable and writeable.
		if (!Files.isReadable(rootDir)) {
			throw new IOException(format("No read access to directory {0}. Check permissions.",
					rootDir.toString()));
		}

		// Ensure the directory's writeable.
		if (!Files.isWritable(rootDir)) {
			throw new IOException(format("No write access to directory {0}. Check permissions.",
					rootDir.toString()));
		}
	}

	private String convertToDiskSafe(String source) {
		return source.trim().toLowerCase().replaceAll("\\*|\\?|\\\\|:|/|\\s", "_");
	}
}
