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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PreDestroy;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.BinaryRequestWriter;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.storage.search.FileIndexDocumentGeneratorTask.StorageSolrDoc;

/**
 * @author Rahul Khanna
 *
 */
public class StorageSearchService {
	private static final Logger LOGGER = LoggerFactory.getLogger(StorageSearchService.class);
	
	HttpSolrServer solrServer;
	
	public StorageSearchService(String svcUrl) {
		initSolrServer(svcUrl);
	}
	
	public StorageSearchService(HttpSolrServer solrServer) {
		this.solrServer = solrServer;
	}
	
	private void initSolrServer(String svcUrl) {
		solrServer = new HttpSolrServer(svcUrl);
		solrServer.setMaxRetries(1);
		solrServer.setParser(new XMLResponseParser());
		solrServer.setRequestWriter(new BinaryRequestWriter());
	}
	

	public void indexFile(File bagDir, File file) throws SolrServerException, IOException {
		StorageSolrDoc doc = createSolrDoc(bagDir, file);
		submitDoc(doc);
		solrServer.commit();
	}

	public void indexAllFiles(File bagDir, File subDir) throws IOException, SolrServerException {
		List<StorageSolrDoc> docs = new ArrayList<StorageSolrDoc>();
		createSolrDocForEachFile(docs, bagDir, subDir);
		for (StorageSolrDoc doc : docs) {
			try {
				submitDoc(doc);
			} catch (IOException | SolrServerException e) {
				LOGGER.warn("Error submitting index document for {}: {}", doc.id, e.getMessage());
			}
		}
		solrServer.commit();
	}
	
	public void deindexAllFiles(File bagDir, File subDir) throws SolrServerException, IOException {
		List<StorageSolrDoc> docs = new ArrayList<StorageSolrDoc>();
		createSolrDocForEachFile(docs, bagDir, subDir);
		for (StorageSolrDoc doc : docs) {
			try {
				solrServer.deleteById(doc.id);
				LOGGER.trace("Removed index document for {}.", doc.id);
			} catch (IOException | SolrServerException e) {
				LOGGER.warn("Error submitting index document for {}: {}", doc.id, e.getMessage());
			}
		}
		solrServer.commit();
	}

	private StorageSolrDoc createSolrDoc(File bagDir, File file) {
		FileIndexDocumentGeneratorTask docGenTask = new FileIndexDocumentGeneratorTask(bagDir, file);
		StorageSolrDoc doc = docGenTask.generateSolrInputDocument();
		return doc;
	}

	private void createSolrDocForEachFile(List<StorageSolrDoc> docs, File bagDir, File subDir) {
		File[] files = subDir.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				createSolrDocForEachFile(docs, bagDir, files[i]);
			} else {
				docs.add(createSolrDoc(bagDir, files[i]));
			}
		}
	}

	private void submitDoc(StorageSolrDoc doc) throws IOException, SolrServerException {
		if (doc.name != null) {
			solrServer.addBean(doc);
			LOGGER.trace("Added index document for {}.", doc.id);
		} else {
			solrServer.deleteById(doc.id);
			LOGGER.trace("Removed index document for {}.", doc.id);
		}
	}

	@PreDestroy
	public void close() {
		try {
			solrServer.commit();
		} catch (SolrServerException | IOException e) {
			LOGGER.error("Unable to commit to Storage Search Index service at {}", solrServer.getBaseURL());
		}
		solrServer.shutdown();
	}
}
