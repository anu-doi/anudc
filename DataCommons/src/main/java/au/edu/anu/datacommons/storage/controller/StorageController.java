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

package au.edu.anu.datacommons.storage.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Set;

import au.edu.anu.datacommons.storage.datafile.StagedDataFile;
import au.edu.anu.datacommons.storage.info.FileInfo;
import au.edu.anu.datacommons.storage.info.RecordDataSummary;
import au.edu.anu.datacommons.storage.provider.StorageException;

/**
 * @author Rahul Khanna
 *
 */
public interface StorageController {

	FileInfo addFile(String pid, String filepath, StagedDataFile source) throws IOException, StorageException;

	FileInfo addDir(String pid, String filepath) throws IOException, StorageException;

	void deleteFile(String pid, String filepath) throws IOException, StorageException;

	InputStream getFileStream(String pid, String filepath) throws IOException, StorageException;

	boolean fileExists(String pid, String filepath) throws StorageException;

	boolean dirExists(String pid, String filepath) throws StorageException;

	FileInfo getFileInfo(String pid, String filepath) throws IOException, StorageException;

	RecordDataSummary getRecordDataSummary(String pid) throws IOException, StorageException;

	InputStream createZipStream(String pid, Set<String> relPaths) throws IOException, StorageException;

	void addExtRefs(String pid, Collection<String> urls) throws IOException, StorageException;

	void deleteExtRefs(String pid, Collection<String> urls) throws IOException, StorageException;

	void indexFiles(String pid) throws IOException, StorageException;

	void deindexFiles(String pid) throws IOException, StorageException;
}
