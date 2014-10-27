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

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Map;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import au.edu.anu.datacommons.storage.completer.metadata.MetadataExtractor;
import au.edu.anu.datacommons.storage.completer.metadata.MetadataExtractorImpl;
import au.edu.anu.datacommons.storage.provider.StorageProvider;
import au.edu.anu.datacommons.storage.tagfiles.FileMetadataTagFile;
import au.edu.anu.datacommons.storage.tagfiles.TagFilesService;

/**
 * A storage event task that adds a tag file entry to the metadata tagfile with the value as a multivalued map
 * consisting of metadata values serialized to a JSON object. 
 * 
 * @author Rahul Khanna
 *
 */
public class MetadataTask extends AbstractTagFileTask {
	
	public MetadataTask(String pid, StorageProvider storageProvider, String relPath, TagFilesService tagFilesSvc) {
		super(pid, storageProvider, relPath, tagFilesSvc);
	}

	@Override
	protected void processTask() throws Exception {
		try (InputStream fileStream = createInputStream()) {
			MetadataExtractor me = new MetadataExtractorImpl(fileStream);
			Map<String, String[]> metadataMap = me.getMetadataMap();
			String metadataMapAsJson = serializeToJson(metadataMap);
			tagFilesSvc.addEntry(pid, FileMetadataTagFile.class, dataPrependedRelPath, metadataMapAsJson);
		}
	}
	
	private String serializeToJson(Object obj) throws JsonGenerationException, JsonMappingException, IOException {
		ObjectMapper objMapper = new ObjectMapper();
		return objMapper.writeValueAsString(obj);
	}
}
