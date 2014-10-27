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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map.Entry;

/**
 * Abstract class that overrides separator string in AbstractKeyValueFile to one used in Manifest tagfiles. 
 * 
 * @author Rahul Khanna
 *
 */
public abstract class AbstractManifestTagFile extends AbstractKeyValueFile {

	private static final long serialVersionUID = 1L;

	public AbstractManifestTagFile(InputStream stream) throws IOException {
		super(stream);
	}

	@Override
	protected String getSeparator() {
		return "  ";
	}

	@Override
	protected String serializeEntry(Entry<String, String> entry) {
		return entry.getValue() + getSeparator() + entry.getKey();
	}

	@Override
	protected String[] unserializeKeyValue(String line) {
		String[] parts = line.split(getSeparator(), 2);
		String temp = parts[0];
		parts[0] = parts[1];
		parts[1] = temp;
		return parts;
	}
}
