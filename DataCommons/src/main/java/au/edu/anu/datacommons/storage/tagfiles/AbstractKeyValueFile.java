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

package au.edu.anu.datacommons.storage.tagfiles;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;

/**
 * @author Rahul Khanna
 * 
 */
public abstract class AbstractKeyValueFile extends HashMap<String, String> {
	private static final long serialVersionUID = 1L;
	
	protected File file;

	public AbstractKeyValueFile(File file) throws IOException {
		super();
		this.file = file;
		if (file.isFile()) {
			read();
		}
	}

	public synchronized void read() throws IOException {
		BufferedReader reader = null;
		try {
			synchronized(this) {
				reader = new BufferedReader(new FileReader(this.file));
				for (String line = reader.readLine(); line != null; line = reader.readLine()) {
					String parts[] = line.split("(?<!\\\\):", 2);
					this.put(parts[0].trim().replace("\\", ""), parts[1].trim());
				}
			}
		} finally {
			IOUtils.closeQuietly(reader);
		}
	}
	
	public synchronized void write() throws IOException {
		BufferedWriter writer = null;
		try {
			synchronized(this) {
				writer = new BufferedWriter(new FileWriter(file));
				for (Entry<String, String> entry : this.entrySet()) {
					writer.write(entry.getKey().replace(":", "\\:") + ": " + entry.getValue());
					writer.newLine();
				}
			}
		} finally {
			IOUtils.closeQuietly(writer);
		}
	}
	
	@Override
	public String put(String key, String value) {
		if (key == null) {
			throw new NullPointerException("Key cannot be null");
		} else if (key.length() == 0) {
			throw new IllegalArgumentException("Key cannot be a zero-length string");
		}
		if (value == null) {
			throw new NullPointerException("Value cannot be null");
		} else if (value.length() == 0) {
			throw new IllegalArgumentException("Value cannot be a zero-length string");
		}
		
		return super.put(key, value);
	}
}
