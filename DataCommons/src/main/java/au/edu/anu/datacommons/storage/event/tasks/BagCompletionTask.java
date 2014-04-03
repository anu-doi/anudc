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
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.storage.tagfiles.BagInfoTagFile;
import au.edu.anu.datacommons.storage.tagfiles.TagFilesService;
import au.edu.anu.datacommons.storage.tagfiles.TagManifestMd5TagFile;
import au.edu.anu.datacommons.util.StopWatch;
import au.edu.anu.datacommons.util.Util;

/**
 * <p>
 * Note: This class may wait on other tasks to finish and therefore <strong>must</strong> be scheduled in an unbounded
 * thread pool to prevent deadlock.
 * </p>
 * 
 * @author Rahul Khanna
 * 
 */
public class BagCompletionTask extends AbstractTagFileTask {
	private static final Logger LOGGER = LoggerFactory.getLogger(BagCompletionTask.class);
	private static final String BAGGING_DATE_FORMAT = "yyyy-MM-dd";

	private Collection<Future<?>> waitTasks;

	public BagCompletionTask(String pid, Path bagDir, String relPath, TagFilesService tagFilesSvc,
			Collection<Future<?>> waitTasks) {
		super(pid, bagDir, relPath, tagFilesSvc);
		this.waitTasks = waitTasks;
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
					// Not rethrowing the exception as outcome of the tasks is irrelevant. If another thread depends on
					// a task's successful completion then it can call the future's .get() and handle exception.
					LOGGER.warn("A waitlisted task threw exception: {}", e.getMessage());
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
		String baggingDate = (new SimpleDateFormat(BAGGING_DATE_FORMAT)).format(new Date());
		tagFilesSvc.addEntry(pid, BagInfoTagFile.class, BagInfoTxtImpl.FIELD_BAGGING_DATE, baggingDate);

		// Payload-Oxum
		PayloadOxum payloadOxum = calcPayloadOxum(bagDir.resolve("data/"));
		tagFilesSvc.addEntry(pid, BagInfoTagFile.class, BagInfoTxtImpl.FIELD_PAYLOAD_OXUM, payloadOxum.toString());

		// Bag-Size
		tagFilesSvc.addEntry(pid, BagInfoTagFile.class, BagInfoTxtImpl.FIELD_BAG_SIZE,
				Util.byteCountToDisplaySize(payloadOxum.getOctetCount()));
	}

	private void updateTagManifest() throws IOException {
		Map<String, String> messageDigests = tagFilesSvc.generateMessageDigests(pid, Algorithm.MD5);
		tagFilesSvc.clearAllEntries(pid, TagManifestMd5TagFile.class);
		for (Entry<String, String> tagFileMd : messageDigests.entrySet()) {
			tagFilesSvc.addEntry(pid, TagManifestMd5TagFile.class, tagFileMd.getKey(), tagFileMd.getValue());
		}
	}
	
	private PayloadOxum calcPayloadOxum(Path plDir) throws IOException {
		PayloadOxumFileVisitor poVisitor = new PayloadOxumFileVisitor();
		StopWatch sw = new StopWatch();
		sw.start();
		Files.walkFileTree(plDir, poVisitor);
		sw.stop();

		PayloadOxum po = poVisitor.getPo();
		Object[] logObjs = { plDir.toString(), po.getStreamCount(), Util.byteCountToDisplaySize(po.getOctetCount()),
				sw.getTimeElapsedFormatted() };
		if (TimeUnit.MILLISECONDS.toMinutes(sw.getTimeElapsedMillis()) >= 2L) {
			LOGGER.warn("Tree Walk {} ({} files, {}) : {}", logObjs);
		} else {
			LOGGER.debug("Tree Walk {} ({} files, {}) : {}", logObjs);
		}
		return po;
	}
	
	private static class PayloadOxum {
		// Sum of all payload files in bytes
		private long octetCount;
		// Count of all payload files
		private long streamCount;

		private void updateCounts(long octets) {
			this.octetCount += octets;
			streamCount++;
		}
		
		private long getOctetCount() {
			return octetCount;
		}

		private long getStreamCount() {
			return streamCount;
		}

		@Override
		public String toString() {
			return format("{0}.{1}", Long.toString(octetCount, 10), Long.toString(streamCount, 10));
		}
	}
	
	private static class PayloadOxumFileVisitor extends SimpleFileVisitor<Path> {
		private PayloadOxum po = new PayloadOxum();
		
		@Override
		public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
			if (!isExcludedItem(dir)) {
				return FileVisitResult.CONTINUE;
			} else {
				return FileVisitResult.SKIP_SUBTREE;
			}
		}
		
		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
			if (!isExcludedItem(file)){
				po.updateCounts(Files.size(file));
			}
			return FileVisitResult.CONTINUE;
		}
		
		@Override
		public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
			LOGGER.warn("Unable to visit file {}. Payload Oxum may not be accurate.", file.toString());
			return FileVisitResult.CONTINUE;
		}

		private PayloadOxum getPo() {
			return po;
		}
		
		private boolean isExcludedItem(Path item) {
			return item.getFileName().toString().startsWith(".");
		}
	}
}
