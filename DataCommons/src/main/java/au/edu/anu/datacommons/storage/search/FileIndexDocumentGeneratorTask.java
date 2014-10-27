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

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.Callable;

import org.apache.commons.io.IOUtils;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import au.edu.anu.datacommons.storage.completer.metadata.FitsParser;
import au.edu.anu.datacommons.storage.info.FileInfo;
import au.edu.anu.datacommons.storage.provider.StorageProvider;

/**
 * Task that generates a {@link StorageSolrDoc} for a file for submission to a Solr instance.
 * 
 * @author Rahul Khanna
 * 
 */
public class FileIndexDocumentGeneratorTask implements Callable<StorageSolrDoc> {
	private static final Logger LOGGER = LoggerFactory.getLogger(FileIndexDocumentGeneratorTask.class);

	private String pid;
	private String relPath;
	private StorageProvider storageProvider;
	private String docId;
	
	Metadata metadata;
	StringWriter contents;


	public FileIndexDocumentGeneratorTask(String pid, String relPath, StorageProvider storageProvider, String docId) {
		super();
		this.pid = pid;
		this.relPath = relPath;
		this.storageProvider = storageProvider;
		this.docId = docId;
	}

	@Override
	public StorageSolrDoc call() throws Exception {
		return generateSolrInputDocument();
	}

	public StorageSolrDoc generateSolrInputDocument() throws IOException {
		StorageSolrDoc docPojo = new StorageSolrDoc();

		docPojo.setId(this.docId);
		if (storageProvider.fileExists(this.pid, this.relPath)) {
			FileInfo fi = storageProvider.getFileInfo(this.pid, this.relPath);

			if (fi.getFilename().lastIndexOf('.') > 0) {
				docPojo.setName(fi.getFilename().substring(0, fi.getFilename().lastIndexOf('.')));
				docPojo.setExt(fi.getFilename().substring(fi.getFilename().lastIndexOf('.') + 1));
			} else {
				docPojo.setName(fi.getFilename());
			}
			docPojo.setSize(fi.getSize());
			docPojo.setLast_modified(new Date(fi.getLastModified().toMillis()));
			
			// Metadata & Contents
			try (InputStream fileStream = storageProvider.readStream(pid, fi.getRelFilepath())) {
				extractMetadataAndContents(fileStream);
				processMetadata(docPojo);
				docPojo.setContent(this.contents.toString());
			} catch (SAXException | TikaException e) {
				LOGGER.warn("Unable to extract content from {}: {}", fi.getFilename(), e.getMessage());
			}
		}

		return docPojo;
	}

	/**
	 * Maps the extracted metadata to fields in the Solr document, such as title, authors etc.
	 * 
	 * @param doc
	 *            Solr Document to which metadata information will be added.
	 */
	private void processMetadata(StorageSolrDoc doc) {
		if (metadata.get("Content-Type") != null) {
			doc.setMime_type(metadata.get("Content-Type"));
		}
		if (metadata.get(TikaCoreProperties.TITLE) != null) {
			doc.setTitle(metadata.get(TikaCoreProperties.TITLE));
		}
		if (metadata.get(TikaCoreProperties.CREATOR) != null) {
			doc.setAuthors(metadata.get(TikaCoreProperties.CREATOR).split(";"));
			if (doc.getAuthors().length == 1) {
				doc.setAuthors(metadata.get(TikaCoreProperties.CREATOR).split(","));
			}
			for (int i = 0; i < doc.getAuthors().length; i++) {
				doc.getAuthors()[i] = doc.getAuthors()[i].trim();
			}
		}
		for (String key : this.metadata.names()) {
			doc.getMetadata().put("metadata_" + key, Arrays.asList(this.metadata.getValues(key)));
		}
	}

	
	/**
	 * Extracts metadata and plain text contents from a specified InputStream.
	 * 
	 * @param fileStream
	 *            Stream from which metadata and contents are to be extracted
	 * @throws IOException
	 * @throws SAXException
	 * @throws TikaException
	 */
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
}
