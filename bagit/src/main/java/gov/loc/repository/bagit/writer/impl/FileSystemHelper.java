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

package gov.loc.repository.bagit.writer.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.Pipe.SourceChannel;
import java.nio.channels.ReadableByteChannel;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import gov.loc.repository.bagit.BagFile;
import gov.loc.repository.bagit.utilities.TempFileHelper;

public class FileSystemHelper {

	private static final int BUFFERSIZE = 65536;
		
	public static void write(BagFile bagFile, File file) {
		try {
			File parentDir = file.getParentFile();
			if (! parentDir.exists()) {
				FileUtils.forceMkdir(parentDir);
			}
			
			FileOutputStream out = new FileOutputStream(TempFileHelper.getTempFile(file));
			InputStream in = bagFile.newInputStream();
			FileChannel targetChannel = null;
			ReadableByteChannel sourceChannel = null;
			try {
//				byte[] dataBytes = new byte[BUFFERSIZE];
//				int nread = in.read(dataBytes);
//				while (nread > 0) {
//					out.write(dataBytes, 0, nread);
//				    nread = in.read(dataBytes);
//				}
				
				targetChannel = out.getChannel();
				sourceChannel = Channels.newChannel(in);
				ByteBuffer buffer = ByteBuffer.allocate(BUFFERSIZE);
				while (sourceChannel.read(buffer) != -1)
				{
					buffer.flip();
					targetChannel.write(buffer);
					buffer.compact();
				}

				buffer.flip();
				while (buffer.hasRemaining())
					targetChannel.write(buffer);
			} finally {
				IOUtils.closeQuietly(sourceChannel);
				IOUtils.closeQuietly(in);
				IOUtils.closeQuietly(targetChannel);
				out.flush();
				IOUtils.closeQuietly(out);
			}
			TempFileHelper.switchTemp(file);
		}
		catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		
	}
		
}
