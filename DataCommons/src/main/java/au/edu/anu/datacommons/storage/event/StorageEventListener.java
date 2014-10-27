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

import java.io.IOException;
import java.nio.file.Path;

import au.edu.anu.datacommons.storage.datafile.StagedDataFile;
import au.edu.anu.datacommons.storage.provider.StorageProvider;
import au.edu.anu.datacommons.storage.temp.UploadedFileInfo;

/**
 * @author Rahul Khanna
 *
 */
public interface StorageEventListener {

	public enum EventTime {
		PRE, POST
	}

	public enum EventType {
		ADD_FILE, READ_FILE, UPDATE_FILE, DELETE_FILE, TAGFILE_UPDATE;
	
		public boolean isOneOf(EventType... types) {
			for (EventType iType : types) {
				if (this.equals(iType)) {
					return true;
				}
			}
			return false;
		}
	}

	public abstract void notify(EventTime time, EventType type, String pid, String relPath, StorageProvider provider,
			StagedDataFile source) throws IOException;

}
