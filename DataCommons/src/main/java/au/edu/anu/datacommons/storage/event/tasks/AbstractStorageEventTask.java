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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.util.StopWatch;

/**
 * Abstract class that is extended by concrete storage event task classes. Storage Event Task classes are scheduled
 * to run in a background thread. Subclasses must override process method and include actual processing relevant
 * to that task in that method.
 * 
 * @author Rahul Khanna
 *
 */
public abstract class AbstractStorageEventTask implements Callable<Void> {
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractStorageEventTask.class);
	
	protected String pid;
	protected Path bagDir;
	protected String relPath;
	protected String dataPrependedRelPath;
	protected Path absFilepath;
	
	protected StopWatch stopwatch = new StopWatch();

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
	public Void call() throws Exception {
		beginTask();
		try {
			processTask();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			throw e;
		}
		endTask();
		return null;
	}
	
	/**
	 * Preprends "data/" to the relPath so the returned path is relative to the bag directory instead of the payload
	 * directory.
	 * 
	 * @param relPath
	 *            Path relative to the payload directory
	 * @return Path relative to the bag directory as String
	 */
	protected String prependDataDir(String relPath) {
		return "data/" + relPath;
	}

	/**
	 * Opens a BufferedInputStream to the file for which the storage event was triggered.
	 * 
	 * @return BufferedInputStream
	 * @throws IOException
	 */
	protected BufferedInputStream createInputStream() throws IOException {
		return new BufferedInputStream(Files.newInputStream(absFilepath));
	}
	
	protected void beginTask() {
		stopwatch.start();
	}

	protected void endTask() {
		stopwatch.stop();
		LOGGER.trace("Time elapsed - {} task for {}/{}: {}", this.getClass().getSimpleName(), pid, dataPrependedRelPath,
				stopwatch.getTimeElapsedFormatted());
	}
	
	/**
	 * Performs the processing required for a storage event task.
	 * 
	 * @throws Exception
	 */
	protected abstract void processTask() throws Exception;
}
