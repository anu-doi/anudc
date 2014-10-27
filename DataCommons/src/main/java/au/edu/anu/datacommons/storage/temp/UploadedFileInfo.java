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

import static java.text.MessageFormat.format;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import au.edu.anu.datacommons.storage.datafile.StagedDataFile;
import au.edu.anu.datacommons.storage.messagedigest.FileMessageDigests;
import au.edu.anu.datacommons.storage.messagedigest.FileMessageDigests.Algorithm;
import au.edu.anu.datacommons.util.Util;

/**
 * POJO class that contains information about a file that's been uploaded using {@link SaveInputStreamTask} or
 * {@link SavePartStreamTask}
 * 
 * @author Rahul Khanna
 * 
 */
public class UploadedFileInfo implements StagedDataFile {
	private Path filepath;
	private long size;
	private String md5;
	private FileMessageDigests digests;

	public UploadedFileInfo(Path filepath, long size, String md5) {
		super();
		this.filepath = filepath;
		this.size = size; 
		this.md5 = md5;
		try {
			this.digests = new FileMessageDigests();
			if (md5 != null) {
				this.digests.addMessageDigest(Algorithm.MD5, Hex.decodeHex(md5.toCharArray()));
			}
		} catch (DecoderException e) {
			// No op.
		}
	}

	/**
	 * @return Saved file's Path
	 */
	public Path getFilepath() {
		return filepath;
	}

	/**
	 * @return Size of saved file, measured in bytes
	 */
	public long getSize() {
		return size;
	}

	/**
	 * @return Hex encoded MD5 of the saved of the saved file.
	 */
	public String getMd5() {
		return md5;
	}

	@Override
	public String toString() {
		return format("{0} {1} ({2}) {3}", filepath.toAbsolutePath().toString(), size,
				Util.byteCountToDisplaySize(size), md5);
	}

	@Override
	public Path getPath() {
		return getFilepath();
	}

	@Override
	public InputStream getStream() throws IOException {
		return new BufferedInputStream(Files.newInputStream(getFilepath()));
	}

	@Override
	public FileMessageDigests getMessageDigests() {
		return digests;
	}
}
