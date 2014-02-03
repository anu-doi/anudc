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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.syndication.feed.rss.Channel;

/**
 * @author Rahul Khanna
 *
 */
public class LocalDiskManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(LocalDiskManager.class);
	
	private Path root;

	public LocalDiskManager(String rootPath) throws IOException {
		this(Paths.get(rootPath));
	}
	
	public LocalDiskManager(Path root) throws IOException {
		super();
		this.root = root.toAbsolutePath();
		verifyIsDirectory(this.root);
		verifyWritable(this.root);
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
		verifyReadable(source);

		Path target = createTargetPath(relPath);
		// TODO Lock target file and parent dirs.
		Files.move(source, target, StandardCopyOption.ATOMIC_MOVE);
	}

	public void write(InputStream is, String... relPath) throws IOException {
		Path target = createTargetPath(relPath);
		// TODO Lock target.
		Files.copy(is, target);
	}
	
	String createFullPath(String... relPath) {
		String fullPath = root.toAbsolutePath().toString();
		for (String relPathPart : relPath) {
			fullPath = FilenameUtils.concat(fullPath, stripLeadingSeparator(relPathPart));
		}
		return fullPath;
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
	
	private void verifyReadable(Path path) throws IOException {
		verifyExists(path);
		if (!Files.isReadable(path)) {
			throw new IOException(format("{0} is not readable.", path.toAbsolutePath().toString()));
		}
	}
	
	private void verifyWritable(Path path) throws IOException {
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
		return target;
	}

	private String stripLeadingSeparator(String path) {
		if (path.startsWith("/") || path.startsWith("\\")) {
			return path.substring(1);
		} else {
			return path;
		}
	}
}
