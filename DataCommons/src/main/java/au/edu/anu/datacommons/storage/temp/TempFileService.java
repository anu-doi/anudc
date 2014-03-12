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

	public Future<UploadedFileInfo> saveInputStream(InputStream is, long expectedLength, String expectedMd5)
			throws IOException {
		return threadPoolSvc.submitCachedPool(new SaveInputStreamTask(uploadDir, is, expectedLength, expectedMd5));
	}
	
	public Future<UploadedFileInfo> saveInputStream(String urlStr, long expectedLength, String expectedMd5)
			throws MalformedURLException, IOException {
		URL url = new URL(urlStr);
		InputStream urlStream = url.openStream();
		return saveInputStream(urlStream, expectedLength, expectedMd5);
	}
}	
