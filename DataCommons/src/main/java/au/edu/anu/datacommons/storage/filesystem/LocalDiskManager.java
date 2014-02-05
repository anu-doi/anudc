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

package au.edu.anu.datacommons.storage.filesystem;

import static java.text.MessageFormat.format;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.syndication.feed.rss.Channel;

/**
 * @author Rahul Khanna
 *
 */
public class LocalDiskManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(LocalDiskManager.class);
	
	private LockManager<Path> lockMgr = new LockManager<>();
	
	private Path root;

	public LocalDiskManager(String rootPath) throws IOException {
		this(Paths.get(rootPath));
	}
	
	public LocalDiskManager(Path root) throws IOException {
		super();
		this.root = root.toAbsolutePath();
		verifyIsDirectory(this.root);
		verifyIsWritable(this.root);
	}
	
	public boolean fileOrDirExists(String... relPath) {
		File target = new File(createFullPath(relPath));
		return target.exists();
	}
	
	public boolean dirExists(String... relPath) {
		File target = new File(createFullPath(relPath));
		return target.isDirectory();
	}
	
	public boolean fileExists(String... relPath) {
		File target = new File(createFullPath(relPath));
		return target.isFile();
	}
	
	public void move(Path source, String... relPath) throws IOException {
		verifyIsFile(source);
		verifyIsReadable(source);

		Path target = createTargetPath(relPath);
		createDirs(target.getParent());
		
		try {
			lockMgr.writeLock(target);
			Files.move(source, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			Files.deleteIfExists(target);
			throw e;
		} finally {
			lockMgr.writeUnlock(target);
		}
	}

	public void write(InputStream is, String... relPath) throws IOException {
		Path target = createTargetPath(relPath);
		createDirs(target.getParent());
		
		try {
			lockMgr.writeLock(target);
			Files.copy(is, target, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			Files.deleteIfExists(target);
			throw e;
		} finally {
			IOUtils.closeQuietly(is);
			lockMgr.writeUnlock(target);
		}
	}
	
	public void delete(String... relPath) throws IOException {
		Path target = createTargetPath(relPath);
		verifyIsFile(target);
		verifyIsWritable(target);
		
		try {
			lockMgr.writeLock(target);
			Files.delete(target);
		} finally {
			lockMgr.writeUnlock(target);
		}
	}
	
	public InputStream getInputStream(String... relPath) throws IOException {
		Path target = createTargetPath(relPath);
		verifyIsFile(target);
		verifyIsReadable(target);

		return new LockableInputStream(Files.newInputStream(target), this.lockMgr, target); 
	}
	
	public List<Path> enumFiles(boolean includeSubdirs, String... relPath) throws IOException {
		Path target = createTargetPath(relPath);
		verifyIsDirectory(target);
		verifyIsReadable(target);
		
		List<Path> filesInDir = listFilesInDirRelPath(target, includeSubdirs);
		return filesInDir;
	}
	
	String createFullPath(String... relPath) {
		String fullPath = root.toAbsolutePath().toString();
		for (String relPathPart : relPath) {
			fullPath = FilenameUtils.concat(fullPath, stripLeadingSeparator(relPathPart));
		}
		return fullPath;
	}
	
	private void createDirs(Path path) throws IOException {
		Files.createDirectories(path);
	}

	private void verifyIsFile(Path path) throws IOException {
		verifyExists(path);
		if (!Files.isRegularFile(path)) {
			throw new IOException(format("{0} is not a file.", path.toAbsolutePath().toString()));
		}
	}
	
	private void verifyIsDirectory(Path path) throws IOException {
		verifyExists(path);
		if (!Files.isDirectory(path)) {
			throw new IOException(format("{0} is not a directory.", path.toAbsolutePath().toString()));
		}
	}
	
	private void verifyIsReadable(Path path) throws IOException {
		verifyExists(path);
		if (!Files.isReadable(path)) {
			throw new IOException(format("{0} is not readable.", path.toAbsolutePath().toString()));
		}
	}
	
	private void verifyIsWritable(Path path) throws IOException {
		verifyExists(path);
		if (!Files.isWritable(path)) {
			throw new IOException(format("{0} is not writable.", path.toAbsolutePath().toString()));
		}
	}
	
	private void verifyExists(Path path) throws IOException {
		if (!Files.exists(path)) {
			throw new IOException(format("{0} does not exist or is inaccessible.", path.toAbsolutePath().toString()));
		}
	}

	private Path createTargetPath(String... relPath) {
		Path target = Paths.get(root.toString(), relPath);
		return lockMgr.getOrAdd(target);
	}

	private String stripLeadingSeparator(String path) {
		if (path.startsWith("/") || path.startsWith("\\")) {
			return path.substring(1);
		} else {
			return path;
		}
	}
	
	private List<Path> listFilesInDirRelPath(Path root, boolean recurse) throws IOException {
		List<Path> fileList = new ArrayList<>();
		for (Path p : listFilesInDirFullPath(root, recurse)) {
			fileList.add(root.relativize(p));
		}
		return fileList;
	}
	
	private List<Path> listFilesInDirFullPath(Path root, boolean recurse) throws IOException {
		List<Path> fileList = new ArrayList<>();
		DirectoryStream<Path> dirStream = Files.newDirectoryStream(root);
		for (Path p : dirStream) {
			if (Files.isDirectory(p)) {
				// fileList.add(p);
				if (recurse) {
					fileList.addAll(listFilesInDirFullPath(p, true));
				}
			} else if (Files.isRegularFile(p)) {
				fileList.add(p);
			}
		}
		return fileList;
	}
	
	private static class LockableInputStream extends FilterInputStream {
		private LockManager<Path> lockMgr;
		private Path path;
		
		protected LockableInputStream(InputStream in, LockManager<Path> lockMgr, Path path) {
			super(in instanceof BufferedInputStream ? in : new BufferedInputStream(in));
		}
		
		@Override
		public void close() throws IOException {
			super.close();
			lockMgr.readUnlock(path);
			path = null;
		}
	}
}
