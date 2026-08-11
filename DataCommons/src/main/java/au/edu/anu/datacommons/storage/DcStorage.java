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

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import au.edu.anu.datacommons.storage.event.StorageEventListener;
import au.edu.anu.datacommons.storage.info.RecordDataInfoService;
import au.edu.anu.datacommons.storage.search.StorageSearchService;
import au.edu.anu.datacommons.storage.tagfiles.TagFilesService;
import au.edu.anu.datacommons.storage.temp.TempFileService;
import au.edu.anu.datacommons.tasks.ThreadPoolService;

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

}
