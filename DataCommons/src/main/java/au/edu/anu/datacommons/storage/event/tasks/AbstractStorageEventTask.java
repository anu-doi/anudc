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

package au.edu.anu.datacommons.storage.event.tasks;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;

/**
 * @author Rahul Khanna
 *
 */
public abstract class AbstractStorageEventTask implements Callable<Void> {

	protected String pid;
	protected Path bagDir;
	protected String relPath;
	protected String dataPrependedRelPath;
	protected Path absFilepath;

	public AbstractStorageEventTask(String pid, Path bagDir, String relPath) {
		this.pid = pid;
		this.bagDir = bagDir;
		this.relPath = relPath;

		if (relPath != null) {
			this.dataPrependedRelPath = prependDataDir(relPath);
			this.absFilepath = bagDir.resolve(dataPrependedRelPath);
		}
	}
	
	@Override
	public abstract Void call() throws Exception;

	protected String prependDataDir(String relPath) {
		return "data/" + relPath;
	}

	protected BufferedInputStream createInputStream() throws IOException {
		return new BufferedInputStream(Files.newInputStream(absFilepath));
	}

}
