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

package gov.loc.repository.bagit.utilities;

import java.text.MessageFormat;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FilenameHelper {
	
	private static final Log log = LogFactory.getLog(FilenameHelper.class);
			
	public static String normalizePathSeparators(String filename) {
		if (filename == null) {
			return null;
		}
		String newFilename = FilenameUtils.separatorsToUnix(filename);			
		log.trace(MessageFormat.format("Normalized {0} to {1}", filename, newFilename));
		return newFilename;
	}
	
	public static String removeBasePath(String basePath, String filename) {
		if (filename == null) {
			throw new RuntimeException("Cannot remove basePath from null");
		}		
		String normBasePath = normalizePathSeparators(basePath);
		String normFilename = normalizePathSeparators(filename);
		String filenameWithoutBasePath = null;
		if (basePath == null || basePath.length() == 0) {
			filenameWithoutBasePath = normFilename;
		}
		else {
			if (! normFilename.startsWith(normBasePath)) {
				throw new RuntimeException(MessageFormat.format("Cannot remove basePath {0} from {1}", basePath, filename));
			}
			if (normBasePath.equals(normFilename)) {
				filenameWithoutBasePath = "";
			}
			else if (normBasePath.endsWith("/")){
				filenameWithoutBasePath = normFilename.substring(normBasePath.length());				
			} else {
				filenameWithoutBasePath = normFilename.substring(normBasePath.length() + 1);
			}
		}
		log.trace(MessageFormat.format("Removing {0} (normalized to {1}) from {2} (normalized to {3}) resulted in {4}", basePath, normBasePath, filename, normFilename, filenameWithoutBasePath));
		return filenameWithoutBasePath;
	}

	/**
	 * Normalizes a file path by replacing various special
	 * path tokens (., .., etc.) with their canonical equivalents.
	 * @param filepath The file path to normalize.
	 * @return The normalized file path.
	 */
	public static String normalizePath(String filepath) {
		if (filepath.startsWith("./") || filepath.startsWith(".\\")) {
			filepath = filepath.substring(2);
		}
		filepath = filepath.replace("/./", "/");
		filepath = filepath.replace("\\.\\", "\\");
		int endPos = filepath.indexOf("/../");
		while(endPos != -1) {
			int startPos = endPos-1;
			while(startPos >= 0 && '/' != filepath.charAt(startPos)) {
				startPos--;
			}
			if (startPos > 0) {
				filepath = filepath.substring(0,startPos) + "/" + filepath.substring(endPos+4);
			} else {
				filepath = filepath.substring(endPos+4);
			}
			endPos = filepath.indexOf("/../");
		}
		endPos = filepath.indexOf("\\..\\");
		while(endPos != -1) {
			int startPos = endPos-1;
			while(startPos >= 0 && '\\' != filepath.charAt(startPos)) {
				startPos--;
			}
			if (startPos > 0) {
				filepath = filepath.substring(0,startPos) + "\\" + filepath.substring(endPos+4);
			} else {
				filepath = filepath.substring(endPos+4);
			}
			endPos = filepath.indexOf("\\..\\");
		}
		return filepath;
	}
	
	public static String getName(String filepath) {
		String name = FilenameUtils.getName(filepath);
		log.trace(MessageFormat.format("Name extracted from {0} is {1}", filepath, name));
		return name;
	}
	
	public static String concatFilepath(String basepath, String filenameToAdd) {
		String filepath = normalizePathSeparators(FilenameUtils.concat(basepath, filenameToAdd));
		log.trace(MessageFormat.format("Concatenation of {0} and {1} is {2}", basepath, filenameToAdd, filepath));
		return filepath;
	}

}
