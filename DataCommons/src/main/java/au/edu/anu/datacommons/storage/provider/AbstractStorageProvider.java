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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import au.edu.anu.datacommons.storage.datafile.StagedDataFile;

/**
 * @author Rahul Khanna
 *
 */
public abstract class AbstractStorageProvider implements StorageProvider {

	protected void moveFilePath(StagedDataFile source, Path targetPath) throws IOException {
		Files.move(source.getPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
		checkTargetExists(targetPath);
		checkFileSize(source, targetPath);
	}
	
	protected void copyFilePath(StagedDataFile source, Path targetPath) throws IOException {
		Files.copy(source.getPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
		checkTargetExists(targetPath);
		checkFileSize(source, targetPath);
	}
	
	protected String formatRelPath(Path path) {
		String formatted = path.toString().replace('\\', '/');
		return formatted;
	}

	private void checkTargetExists(Path targetPath) throws IOException {
		if (!Files.isRegularFile(targetPath)) {
			throw new IOException(format("File {0} does not exist after move.", targetPath.toString()));
		}
	}

	private void checkFileSize(StagedDataFile source, Path targetPath) throws IOException {
		if (source.getSize() != -1) {
			int maxTries = 3;
			for (int attempt = 0; ; attempt++) {
				long actualSize = Files.size(targetPath);
				if (source.getSize() == actualSize) {
					break;
				} else if (attempt < maxTries) {
					try {
						Thread.sleep(1000L);
					} catch (InterruptedException e) {
						// No op
					}
				} else {
					throw new IOException(format("File {0} ({1} bytes) is not expected size ({2} bytes)",
							targetPath.toString(), actualSize, source.getSize()));
				}
			}
		}
	}
}
