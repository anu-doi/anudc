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

package gov.loc.repository.bagit.impl;

import java.io.File;
import java.text.MessageFormat;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.utilities.FilenameHelper;
import gov.loc.repository.bagit.utilities.LongRunningOperationBase;

public class AddFilesToTagsOperation extends LongRunningOperationBase {

	private static final Log log = LogFactory.getLog(AddFilesToTagsOperation.class);
	private Bag bag;
	
	public AddFilesToTagsOperation(Bag bag){
		this.bag = bag;		
	}
	
	public void addFilesToTags(List<File> files) {			
		int count = 0;
		for(File file : files) {
			if (this.isCancelled()) return;
			count = this.addTag(file, file.getParentFile(), count);
		}
		return;
	}

	public void addFileToTags(File file) {
		if (! file.exists()) {
			throw new RuntimeException(MessageFormat.format("{0} does not exist.", file));
		}
		this.addTag(file, file.getParentFile(), 0);
	}

	private int addTag(File file, File rootDir, int count) {
		if (this.isCancelled()) return 0;
		if (! file.canRead()) {
			throw new RuntimeException("Can't read " + file.toString());
		}
		//If directory, recurse on children
		if (file.isDirectory()) {			
			for(File child : file.listFiles()) {
				if (this.isCancelled()) return 0;
				String filepath = file.getAbsolutePath();
				this.progress("Adding tag file", filepath, count, null);
				log.trace(MessageFormat.format("Adding tag {0}", filepath));
				count = this.addTag(child, rootDir, count);
			}
					
		} else if (file.isFile()) {
			
			//If file, add to payloadMap
			String filepath = "";
			if (rootDir != null) {
				filepath += FilenameHelper.removeBasePath(rootDir.toString(), file.toString());
			} else {
				filepath += file.toString();
			}
			if (filepath.indexOf('\\') != -1)  {
				throw new UnsupportedOperationException(MessageFormat.format("This library does not support \\ in filepaths: {0}. See README.txt.", filepath));
			}
			count++;				
			log.debug(MessageFormat.format("Adding {0} to tags.", filepath));
			this.bag.putBagFile(new FileBagFile(filepath, file));
		}
		else {
			throw new RuntimeException("Neither a directory nor file");
		}
		return count;
	}
	
}
