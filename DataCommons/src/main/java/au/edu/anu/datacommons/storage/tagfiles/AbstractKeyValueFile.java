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
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An abstract class representing any file containing data in key-value pairs. The default format of the file is: <code>
 * KEY: VALUE
 * </code>. The format can be changed by overriding appropriate methods in this class. Examples are Manifest Tag files
 * that store their content in <code>VALUE  KEY</code> format.
 * <p>
 * Extends LinkedHashMap to retain the order of key values in the file.
 * 
 * @author Rahul Khanna
 * 
 */
public abstract class AbstractKeyValueFile extends LinkedHashMap<String, String> {
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractKeyValueFile.class);
	private static final long serialVersionUID = 1L;
	
	/**
	 * Flag to indicate if there have been any changes in the data since the last read and the write() method should be
	 * called to write those changes to disk.
	 */
	protected boolean hasUnsavedChanges = false;

	public AbstractKeyValueFile(InputStream stream) throws IOException {
		super();
		read(stream);
	}

	public void read(InputStream stream) throws IOException {
		synchronized (this) {
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new InputStreamReader(stream, Charset.defaultCharset()));

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
							hasUnsavedChanges = true;
						}
					}
					line = nextLine;
				}

			} finally {
				IOUtils.closeQuietly(reader);
			}
		}
	}
	
	
	/**
	 * Serialises the key value pairs as specific to the tag file format into a stream that can be read by the caller.
	 * To ensure that the returned InputStream is exactly the same as when the key value pairs will be written to disk,
	 * this class extends LinkedHashMap instead of HashMap.
	 * 
	 * @return Key-Values serializes as InputStream
	 * @throws IOException
	 */
	public InputStream serialize() throws IOException {
		byte[] byteArray = null;
		String lineSeparator = System.getProperty("line.separator");
		synchronized (this) {
			try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
				for (Entry<String, String> entry : this.entrySet()) {
					String line = serializeEntry(entry) + lineSeparator;
					os.write(line.getBytes(Charset.defaultCharset()));
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
			throw new NullPointerException(format("Key cannot be null."));
		} else if (key.length() == 0) {
			throw new IllegalArgumentException(format("Key cannot be a zero-length string."));
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
	
	/**
	 * Removes a specified key and its value
	 * 
	 * @param key
	 *            Key to be removed
	 * @return Old value as String
	 */
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

	public boolean hasUnsavedChanges() {
		return hasUnsavedChanges;
	}
	

	public synchronized void setHasUnsavedChanges(boolean hasUnsavedChanges) {
		this.hasUnsavedChanges = hasUnsavedChanges;
	}
	

	/**
	 * Returns the filepath relative to the bag directory.
	 * 
	 * @return Filepath as String
	 */
	public abstract String getFilepath();

	/**
	 * Unserializes a single line of text.
	 * 
	 * @param line
	 *            Line of text to unserialize
	 * @return Key-Value pair as String[] with [0] as Key and [1] as Value
	 */
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

	/**
	 * @param entry
	 *            entry to serialise
	 * @return serialised key-value pair as string
	 */
	protected String serializeEntry(Entry<String, String> entry) {
		return escapeKey(entry.getKey()) + getSeparator() + entry.getValue();
	}

	/**
	 * Prepends the escape string to all occurrences of the separator character in the key.
	 * 
	 * @param key
	 *            Key with characters that should be escaped
	 * @return Key with separactor character escaped.
	 */
	protected String escapeKey(String key) {
		return key.replace(String.valueOf(getSeparator()), getEscapeChar() + getSeparator());
	}
	
	/**
	 * Removes any escape strings from the key.
	 * 
	 * @param key
	 *            Key as String
	 * @return Key with escape strings removed
	 */
	protected String unescapeKey(String key) {
		return key.replace(getEscapeChar(), "");
	}
	
	/**
	 * @return Escape string used during serialisation to prefix the separator character.
	 */
	protected String getEscapeChar() {
		return "\\";
	}
	
	/**
	 * @return Separator string added between the key and value while serialising a key-value pair. 
	 */
	protected String getSeparator() {
		return ": ";
	}
}
