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

import static java.text.MessageFormat.format;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Rahul Khanna
 * 
 */
public abstract class AbstractKeyValueFile extends LinkedHashMap<String, String> {
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractKeyValueFile.class);
	private static final long serialVersionUID = 1L;
	
	protected final Path path;
	protected boolean hasUnsavedChanges = false;

	public AbstractKeyValueFile(File file) throws IOException {
		this(file.toPath());
	}
	
	public AbstractKeyValueFile(Path path) throws IOException {
		super();
		this.path = path;
		read();
	}

	public void read() throws IOException {
		boolean hasErrors = false;
		if (Files.isRegularFile(this.path)) {
			synchronized (this) {
				try (BufferedReader reader = Files.newBufferedReader(this.path, StandardCharsets.UTF_8)) {
					for (String line = reader.readLine(); line != null;) {
						// If the next line starts with white spaces concatinate it to the current line.
						String nextLine = reader.readLine();
						while (nextLine != null && nextLine.startsWith("  ")) {
							line += nextLine.substring(2);
							nextLine = reader.readLine();
						}

						if (line.length() > 0) {
							String parts[] = unserializeKeyValue(line);
							if (parts != null) {
								this.put(parts[0], parts[1]);
							} else {
								hasErrors = true;
							}
						}
						line = nextLine;
					}
				}
			}
		}
		hasUnsavedChanges = hasErrors ? true : false;
	}
	
	public void write() throws IOException {
		synchronized (this) {
			if (hasUnsavedChanges) {
				try (BufferedWriter writer = Files.newBufferedWriter(this.path, StandardCharsets.UTF_8)){
					for (Entry<String, String> entry : this.entrySet()) {
						writer.write(serializeEntry(entry));
						writer.newLine();
					}
				}
				hasUnsavedChanges = false;
				LOGGER.trace("File {} written to disk.", this.path.toString());
			}
		}
	}
	
	public InputStream serialize() throws IOException {
		byte[] byteArray = null;
		String lineSeparator = System.getProperty("line.separator");
		synchronized (this) {
			try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
				for (Entry<String, String> entry : this.entrySet()) {
					String line = serializeEntry(entry) + lineSeparator;
					os.write(line.getBytes(StandardCharsets.UTF_8));
				}
				byteArray = os.toByteArray();
			}
		}
		return new ByteArrayInputStream(byteArray);
	}
	
	@Override
	public void clear() {
		synchronized (this) {
			if (this.size() > 0) {
				super.clear();
				hasUnsavedChanges = true;
			}
		}
	}
	
	@Override
	public String put(String key, String value) {
		if (key == null) {
			throw new NullPointerException(format("Key cannot be null. File: {0}", this.path.toString()));
		} else if (key.length() == 0) {
			throw new IllegalArgumentException(format("Key cannot be a zero-length string. File: {0}", this.path.toString()));
		}
		if (value == null) {
			value = "";
		}
		
		String oldValue;
		synchronized(this) {
			if (!this.containsKey(key) || !this.get(key).equals(value)) {
				hasUnsavedChanges = true;
			}
			oldValue = super.put(key, value);
		}
		return oldValue;
	}
	
	public String remove(String key) {
		String oldValue;
		synchronized (this) {
			if (this.containsKey(key)) {
				hasUnsavedChanges = true;
			}
			oldValue = super.remove(key);
		}
		return oldValue;
	}

	public File getFile() {
		return this.path.toFile();
	}
	
	public abstract String getFilepath();

	protected String[] unserializeKeyValue(String line) {
		String parts[];
		parts = line.split("(?<!\\" + getEscapeChar() + ")" + getSeparator(), 2);
		if (parts.length == 2 && parts[0].length() > 0) {
			parts[0] = unescapeKey(parts[0]).trim();
			parts[1] = parts[1].trim();
		} else {
			parts = null;
		}
		return parts;
	}

	protected String serializeEntry(Entry<String, String> entry) {
		return escapeKey(entry.getKey()) + getSeparator() + entry.getValue();
	}

	protected String escapeKey(String key) {
		return key.replace(String.valueOf(getSeparator()), getEscapeChar() + getSeparator());
	}
	
	protected String unescapeKey(String key) {
		return key.replace(getEscapeChar(), "");
	}
	
	protected String getEscapeChar() {
		return "\\";
	}
	
	protected String getSeparator() {
		return ": ";
	}
}
