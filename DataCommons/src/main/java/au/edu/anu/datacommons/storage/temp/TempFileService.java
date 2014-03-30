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

package au.edu.anu.datacommons.storage.temp;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import au.edu.anu.datacommons.tasks.ThreadPoolService;

/**
 * Service class that calls on SaveInputStreamTask and SavePartStreamTask to save an inputstream (complete or part)
 * to a file on disk.
 * 
 * @author Rahul Khanna
 *
 */
@Component
public class TempFileService {
	private static final Logger LOGGER = LoggerFactory.getLogger(TempFileService.class);
	
	@Autowired(required = true)
	ThreadPoolService threadPoolSvc;
	
	@Autowired(required = true)
	Path uploadDir;

	/**
	 * Calls SaveInputStreamTask to save an InputStream to disk. Method returns immediately. File is saved in another
	 * thread.
	 * 
	 * @param is
	 *            InputStream to read data from
	 * @param expectedLength
	 *            Expected length of the stream. Size of the saved file will be checked against this value. Verification
	 *            skipped if expected length <= 0
	 * @param expectedMd5
	 *            Expected MD5 of the stream
	 * @return Future that returns UploadedFileInfo when file is saved.
	 * @throws IOException
	 */
	public Future<UploadedFileInfo> saveInputStream(InputStream is, long expectedLength, String expectedMd5)
			throws IOException {
		SaveInputStreamTask saveStreamTask = new SaveInputStreamTask(uploadDir, is, expectedLength, expectedMd5);
		Future<UploadedFileInfo> taskFuture = threadPoolSvc.submitCachedPool(saveStreamTask);
		return taskFuture;
	}
	
	/**
	 * Calls SaveInputStreamTask to save a file hosted at a given URL to disk.
	 * 
	 * @param urlStr
	 *            URL to download file from
	 * @param expectedLength
	 *            Expected length of the stream. Size of the saved file will be checked against this value. Verification
	 *            skipped if expected length <= 0
	 * @param expectedMd5
	 *            Expected MD5 of the stream
	 * @return Future that returns UploadedFileInfo when file is saved.
	 * @throws IOException
	 */
	public Future<UploadedFileInfo> saveInputStream(String urlStr, long expectedLength, String expectedMd5)
			throws MalformedURLException, IOException {
		URL url = new URL(urlStr);
		InputStream urlStream = url.openStream();
		Future<UploadedFileInfo> taskFuture = saveInputStream(urlStream, expectedLength, expectedMd5);
		return taskFuture;
	}
	
	/**
	 * Saves an inputstream that is a part of a file to disk.
	 * 
	 * @param is
	 *            InputStream that is a part of a file.
	 * @param part
	 *            Part number as a positive integer
	 * @param isLastPart
	 *            true if this part is the last one in sequence
	 * @param fileId
	 *            File ID to associate the file the part stream is part of
	 * @param expectedLength
	 *            Expected length of the stream. Size of the saved file will be checked against this value. Verification
	 *            skipped if expected length <= 0
	 * @param expectedMd5
	 *            Expected MD5 of the stream
	 * @return Future that returns UploadedFileInfo when file is saved.
	 * @throws IOException
	 */
	public Future<UploadedFileInfo> savePartStream(InputStream is, int part, boolean isLastPart, String fileId,
			long expectedLength, String expectedMd5) throws IOException {
		SavePartStreamTask partStreamTask = new SavePartStreamTask(uploadDir, fileId, is, part, isLastPart,
				expectedLength, expectedMd5);
		Future<UploadedFileInfo> taskFuture = threadPoolSvc.submitCachedPool(partStreamTask);
		return taskFuture;
	}
}	
