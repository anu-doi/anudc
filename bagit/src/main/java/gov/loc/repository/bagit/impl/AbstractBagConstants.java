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

package gov.loc.repository.bagit.impl;

import gov.loc.repository.bagit.Bag.BagConstants;;

public abstract class AbstractBagConstants implements BagConstants {
	public static final String PAYLOAD_MANIFEST_PREFIX = "manifest-";
	public static final String TAG_MANIFEST_PREFIX = "tagmanifest-";
	public static final String PAYLOAD_MANIFEST_SUFFIX = ".txt";
	public static final String TAG_MANIFEST_SUFFIX = ".txt";
	public static final String BAG_ENCODING = "UTF-8";
	public static final String BAGIT_TXT = "bagit.txt";
	public static final String DATA_DIRECTORY = "data";
	public static final String BAGINFO_TXT = "bag-info.txt";
	public static final String FETCH_TXT = "fetch.txt";
	
	public String getPayloadManifestPrefix() {
		return PAYLOAD_MANIFEST_PREFIX;
	}
	public String getTagManifestPrefix() {
		return TAG_MANIFEST_PREFIX;
	}
	public String getPayloadManifestSuffix() {
		return PAYLOAD_MANIFEST_SUFFIX;
	}
	public String getTagManifestSuffix() {
		return TAG_MANIFEST_SUFFIX;
	}
	public String getBagEncoding() {
		return BAG_ENCODING;
	}
	public String getBagItTxt() {
		return BAGIT_TXT;
	}
	public String getDataDirectory() {
		return DATA_DIRECTORY;
	}
	
	public String getBagInfoTxt() {
		return BAGINFO_TXT;
	}
	
	public String getFetchTxt() {
		return FETCH_TXT;
	}
}
