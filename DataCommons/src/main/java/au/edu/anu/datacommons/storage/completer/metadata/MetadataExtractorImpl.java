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

package au.edu.anu.datacommons.storage.completer.metadata;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Calls Apache Tika to extract metadata from a file into a multi-valued hashmap.
 * 
 * @author Rahul Khanna
 *
 */
public class MetadataExtractorImpl implements MetadataExtractor {
	private static final Logger LOGGER = LoggerFactory.getLogger(MetadataExtractorImpl.class);

	private Metadata metadata = new Metadata();
	private Map<String, String[]> metadataMap = null;

	public MetadataExtractorImpl(InputStream dataStream) throws IOException, SAXException, TikaException {
		parse(dataStream);
	}

	public MetadataExtractorImpl(File inFile) throws IOException, SAXException, TikaException {
		this(new BufferedInputStream(new FileInputStream(inFile)));
	}

	/**
	 * Extracts metadata from a specified inputstream using Apache Tika.
	 * 
	 * @param dataStream
	 *            InputStream from which to extract data
	 * @throws IOException
	 * @throws SAXException
	 * @throws TikaException
	 */
	private void parse(InputStream dataStream) throws IOException, SAXException, TikaException {
		try {
			ContentHandler textHandler = new DefaultHandler();
			Parser parser = new AutoDetectParser(new AutoDetectParser(), new FitsParser());
			ParseContext parseContext = new ParseContext();
			parser.parse(dataStream, textHandler, this.metadata, parseContext);
			LOGGER.trace("Parsing using Tika successful. {} metadata elements extracted.", metadata.size());
			extractMetadataToMap();
			
			if (LOGGER.isTraceEnabled()) {
				logMetadata(this.metadata);
			}
		} finally {
			IOUtils.closeQuietly(dataStream);
		}
	}

	@Override
	public Metadata getMetadata() {
		return this.metadata;
	}

	@Override
	public Map<String, String[]> getMetadataMap() {
		return metadataMap;
	}

	private void extractMetadataToMap() {
		metadataMap = new HashMap<String, String[]>();
		for (String key : metadata.names()) {
			metadataMap.put(key, metadata.getValues(key));
		}
	}

	private void logMetadata(Metadata metadata) {
		LOGGER.trace(this.metadataMap.keySet().toString());
	}
}
