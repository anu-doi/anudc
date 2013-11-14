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

package au.edu.anu.datacommons.storage.search;

import static java.text.MessageFormat.*;
import gov.loc.repository.bagit.utilities.FilenameHelper;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.commons.io.IOUtils;
import org.apache.solr.client.solrj.beans.Field;
import org.apache.solr.common.SolrInputDocument;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.mime.MimeTypes;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.html.BoilerpipeContentHandler;
import org.apache.tika.sax.BodyContentHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import au.edu.anu.datacommons.storage.completer.metadata.FitsParser;

/**
 * @author Rahul Khanna
 * 
 */
public class FileIndexDocumentGeneratorTask implements Callable<FileIndexDocumentGeneratorTask.StorageSolrDoc> {
	private static final Logger LOGGER = LoggerFactory.getLogger(FileIndexDocumentGeneratorTask.class);

	private File bagDir;
	private File srcFile;

	Metadata metadata;
	StringWriter contents;

	public FileIndexDocumentGeneratorTask(File bagDir, File srcFile) {
		super();
		this.bagDir = bagDir;
		this.srcFile = srcFile;
	}

	@Override
	public StorageSolrDoc call() throws Exception {
		return generateSolrInputDocument();
	}

	public StorageSolrDoc generateSolrInputDocument() {
		StorageSolrDoc docPojo = new StorageSolrDoc();

		docPojo.id = createId();
		if (srcFile.isFile()) {
			if (srcFile.getName().lastIndexOf('.') > 0) {
				docPojo.name = srcFile.getName().substring(0, srcFile.getName().lastIndexOf('.'));
				docPojo.ext = srcFile.getName().substring(srcFile.getName().lastIndexOf('.') + 1);
			} else {
				docPojo.name = srcFile.getName();
			}
			docPojo.size = srcFile.length();
			docPojo.last_modified = new Date(srcFile.lastModified());

			// Metadata & Contents
			try {
				extractMetadataAndContents(this.srcFile);
				processMetadata(docPojo);
				docPojo.content = this.contents.toString();
			} catch (IOException | SAXException | TikaException e) {
				LOGGER.warn("Unable to extract content from {}: {}", srcFile.getAbsolutePath(), e.getMessage());
			}
		}

		return docPojo;
	}

	private void processMetadata(StorageSolrDoc doc) {
		if (metadata.get("Content-Type") != null) {
			doc.mime_type = metadata.get("Content-Type");
		}
		if (metadata.get(TikaCoreProperties.TITLE) != null) {
			doc.title = metadata.get(TikaCoreProperties.TITLE);
		}
		if (metadata.get(TikaCoreProperties.CREATOR) != null) {
			doc.authors = metadata.get(TikaCoreProperties.CREATOR).split(";");
			if (doc.authors.length == 1) {
				doc.authors = metadata.get(TikaCoreProperties.CREATOR).split(",");
			}
			for (int i = 0; i < doc.authors.length; i++) {
				doc.authors[i] = doc.authors[i].trim();
			}
		}
		for (String key : this.metadata.names()) {
			doc.metadata.put("metadata_" + key, Arrays.asList(this.metadata.getValues(key)));
		}
	}

	void extractMetadataAndContents(File file) throws IOException, SAXException, TikaException {
		InputStream fileStream = null;
		try {
			fileStream = new BufferedInputStream(new FileInputStream(file));
			extractMetadataAndContents(fileStream);
		} finally {
			IOUtils.closeQuietly(fileStream);
		}
	}

	void extractMetadataAndContents(InputStream fileStream) throws IOException, SAXException, TikaException {
		AutoDetectParser parser = new AutoDetectParser(new AutoDetectParser(), new FitsParser());
		this.contents = new StringWriter();
		this.metadata = new Metadata();
		ContentHandler handler = new BodyContentHandler(this.contents);
		ParseContext context = new ParseContext();
		try {
			parser.parse(fileStream, handler, metadata, context);
		} finally {
			IOUtils.closeQuietly(fileStream);
		}
	}

	private String createId() {
		return format("{0}/{1}", bagDir.getName(),
				FilenameHelper.removeBasePath(bagDir.getAbsolutePath(), srcFile.getAbsolutePath()));
	}

	public static class StorageSolrDoc {
		@Field
		String id;

		@Field
		String name;

		@Field
		String ext;

		@Field
		long size;

		@Field
		Date last_modified;

		@Field
		String mime_type;

		@Field
		String title;

		@Field("author")
		String[] authors;

		@Field("metadata_*")
		Map<String, List<String>> metadata = new HashMap<String, List<String>>();

		@Field
		String content;
	}
}
