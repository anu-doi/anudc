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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import au.edu.anu.datacommons.storage.datafile.StagedDataFile;
import au.edu.anu.datacommons.storage.info.FileInfo;

/**
 * @author Rahul Khanna
 *
 */
public interface StorageProvider {
	FileInfo addFile(String pid, String relativePath, StagedDataFile sourceFile) throws IOException;
	
	FileInfo addDir(String pid, String relativePath) throws IOException;

	FileInfo renameFile(String pid, String oldRelativePath, String newRelativePath) throws IOException;
	
	void deleteFile(String pid, String relativePath) throws IOException;
	
	boolean fileExists(String pid, String relativePath);
	
	boolean dirExists(String pid, String relativePath);
	
	FileInfo getFileInfo(String pid, String relativePath) throws IOException;
	
	FileInfo getDirInfo(String pid, String relativePath, int depth) throws IOException;

	InputStream readStream(String pid, String relativePath) throws IOException;
	
	FileInfo writeStream(String pid, String relativePath, InputStream stream) throws IOException;

	InputStream readTagFileStream(String pid, String path) throws IOException;

	void writeTagFileStream(String pid, String path, InputStream stream) throws IOException;
}
