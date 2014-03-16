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

import static java.text.MessageFormat.format;
import gov.loc.repository.bagit.Manifest.Algorithm;
import gov.loc.repository.bagit.v0_95.impl.BagInfoTxtImpl;

import java.io.IOException;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.storage.info.RecordDataInfo;
import au.edu.anu.datacommons.storage.info.RecordDataInfoService;
import au.edu.anu.datacommons.storage.tagfiles.BagInfoTagFile;
import au.edu.anu.datacommons.storage.tagfiles.TagFilesService;
import au.edu.anu.datacommons.storage.tagfiles.TagManifestMd5TagFile;

/**
 * <p>
 * Note: This class may wait on other tasks to finish and therefore <strong>must</strong> be scheduled in an unbounded thread
 * pool to prevent deadlock.
 * </p>
 * 
 * @author Rahul Khanna
 * 
 */
public class BagCompletionTask extends AbstractTagFileTask {
	private static final Logger LOGGER = LoggerFactory.getLogger(BagCompletionTask.class);

	private RecordDataInfoService rdiSvc;
	private Collection<Future<?>> waitTasks;

	public BagCompletionTask(String pid, Path bagDir, String relPath, TagFilesService tagFilesSvc,
			RecordDataInfoService rdiSvc, Collection<Future<?>> waitTasks) {
		super(pid, bagDir, relPath, tagFilesSvc);
		this.waitTasks = waitTasks;
		this.rdiSvc = rdiSvc;
	}

	@Override
	protected void processTask() throws Exception {
		waitForTasks();
		updateBagInfo();
		updateTagManifest();	
	}

	private void waitForTasks() {
		if (waitTasks != null) {
			for (Future<?> f : waitTasks) {
				try {
					f.get();
				} catch (InterruptedException | ExecutionException e) {
					// Not rethrowing the exception as outcome of the tasks is irrelevant. If another thread depends on a
					// task's successful completion then it can call the future's .get() and handle exception.
					LOGGER.warn("Task {} threw exception: {}", e);
				}
			}
		}
	}

	private void updateBagInfo() throws IOException {
		Map<String, String> bagInfoEntries = tagFilesSvc.getAllEntries(pid, BagInfoTagFile.class);

		// External-Identifier
		String extId = bagInfoEntries.get(BagInfoTxtImpl.FIELD_EXTERNAL_IDENTIFIER);
		if (extId == null || !extId.equals(pid)) {
			tagFilesSvc.addEntry(pid, BagInfoTagFile.class, BagInfoTxtImpl.FIELD_EXTERNAL_IDENTIFIER, pid);
		}

		// Bagging-Date
		String baggingDate = (new SimpleDateFormat("yyyy-MM-dd")).format(new Date());
		tagFilesSvc.addEntry(pid, BagInfoTagFile.class, BagInfoTxtImpl.FIELD_BAGGING_DATE, baggingDate);

		// Payload-Oxum
		RecordDataInfo rdi = rdiSvc.createRecordDataInfo(pid, bagDir);
		String payloadOxum = format("{0}.{1}", Long.toString(rdi.getSize(), 10), Long.toString(rdi.getNumFiles(), 10));
		tagFilesSvc.addEntry(pid, BagInfoTagFile.class, BagInfoTxtImpl.FIELD_PAYLOAD_OXUM, payloadOxum);

		// Bag-Size
		tagFilesSvc.addEntry(pid, BagInfoTagFile.class, BagInfoTxtImpl.FIELD_BAG_SIZE, rdi.getFriendlySize());
	}

	private void updateTagManifest() throws IOException {
		Map<String, String> messageDigests = tagFilesSvc.generateMessageDigests(pid, Algorithm.MD5);
		for (Entry<String, String> tagFileMd : messageDigests.entrySet()) {
			tagFilesSvc.addEntry(pid, TagManifestMd5TagFile.class, tagFileMd.getKey(), tagFileMd.getValue());
		}
	}
}
