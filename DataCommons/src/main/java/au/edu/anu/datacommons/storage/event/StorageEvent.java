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

import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.loc.repository.bagit.utilities.FilenameHelper;
import au.edu.anu.datacommons.storage.datafile.StagedDataFile;
import au.edu.anu.datacommons.storage.provider.StorageProvider;

/**
 * @author Rahul Khanna
 *
 */
public class StorageEvent {
	private static final Logger LOGGER = LoggerFactory.getLogger(StorageEvent.class);
	
	public enum EventType {
		ADD_FILE, READ_FILE, UPDATE_FILE, RENAME_FILE, DELETE_FILE, TAGFILE_UPDATE;
	
		public boolean isOneOf(EventType... types) {
			for (EventType iType : types) {
				if (this.equals(iType)) {
					return true;
				}
			}
			return false;
		}
	}

	
	private EventType type;
	private String pid;
	private StorageProvider provider;
	
	private String relPath;
	private String newRelPath;
	private StagedDataFile source;
	
	public StorageEvent(EventType type, String pid, StorageProvider provider) {
		this.type = type;
		this.pid = pid;
		this.provider = provider;
	}
	
	public StorageEvent(EventType type, String pid, StorageProvider provider, String relPath) {
		this(type, pid, provider);
		this.relPath = normalizeRelPath(relPath);
	}
	
	public StorageEvent(EventType type, String pid, StorageProvider provider, String relPath, StagedDataFile source) {
		this(type, pid, provider, relPath);
		this.source = source;
	}
	
	public StorageEvent(EventType type, String pid, StorageProvider provider, String relPath, String newRelPath) {
		this(type, pid, provider, relPath);
		this.newRelPath = newRelPath;
	}

	public EventType getType() {
		return type;
	}

	public String getPid() {
		return pid;
	}

	public StorageProvider getProvider() {
		return provider;
	}

	public String getRelPath() {
		return relPath;
	}

	public String getNewRelPath() {
		return newRelPath;
	}

	public StagedDataFile getSource() {
		return source;
	}
	
	@Override
	public String toString() {
		return MessageFormat.format("type={0}; pid={1}; relPath={2}; newRelPath={3}", type, pid, relPath, newRelPath);
	}
	
	
	/**
	 * Normalises a relative path (relative to the payload directory) appropriate for a file (notifications are for
	 * files only) by:
	 * 
	 * <ol>
	 * <li>Change all path separators to '/'
	 * <li>Removing all leading and trailing path separators
	 * </ol>
	 * 
	 * @param relPath
	 *            Relative path to normalise. Can be null.
	 * @return Normalised relative path as String. null if relPath is null.
	 */
	private String normalizeRelPath(String relPath) {
		if (relPath != null) {
			StringBuilder processed = new StringBuilder(FilenameHelper.normalizePathSeparators(relPath));
			while (processed.charAt(0) == '/') {
				processed.deleteCharAt(0);
			}

			while (processed.charAt(processed.length() - 1) == '/') {
				processed.deleteCharAt(processed.length() - 1);
			}

			if (LOGGER.isTraceEnabled() && !processed.toString().equals(relPath)) {
				LOGGER.trace("Normalized relative path {} to {}", relPath, processed.toString());
			}
			return processed.toString();
		} else {
			return null;
		}
	}
}
