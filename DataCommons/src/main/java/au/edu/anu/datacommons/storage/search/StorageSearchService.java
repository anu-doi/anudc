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

import static java.text.MessageFormat.format;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PreDestroy;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.BinaryRequestWriter;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.storage.info.FileInfo;
import au.edu.anu.datacommons.storage.info.FileInfo.Type;
import au.edu.anu.datacommons.storage.provider.StorageProvider;

/**
 * Service class that interacts with a Solr instance for data search.
 * 
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

	/**
	 * Creates an instance of HttpSolrServer from a provided URL of a Solr instance.
	 * 
	 * @param svcUrl
	 *            URL at which the Solr instance is hosted.
	 */
	private void initSolrServer(String svcUrl) {
		solrServer = new HttpSolrServer(svcUrl);
		solrServer.setMaxRetries(1);
		solrServer.setParser(new XMLResponseParser());
		solrServer.setRequestWriter(new BinaryRequestWriter());
	}

	/**
	 * Indexes the contents of a specified file in the Solr instance. If the specified file doesn't exist on disk (as a
	 * result of a delete event) then its corresponding solr document is deleted.
	 * 
	 * @throws SolrServerException
	 *             when the Solr instance is unable to index the file.
	 * @throws IOException
	 *             when unable to read the file on disk
	 */
	public void indexFile(String pid, String relPath, StorageProvider storageProvider) throws SolrServerException,
			IOException {
		StorageSolrDoc doc = createSolrDoc(pid, relPath, storageProvider);
		submitDoc(doc);
		solrServer.commit();
	}

	/**
	 * Indexes all files in a collection record. Ideally called when the files-public flag of a record is changed to
	 * true.
	 * 
	 * @param bagDir
	 *            Bag directory
	 * @param subDir
	 *            Payload directory
	 * @throws IOException
	 * @throws SolrServerException
	 */
	public void indexAllFiles(String pid, StorageProvider storageProvider) throws IOException, SolrServerException {
		Set<FileInfo> allChildren = getAllChildren(pid, storageProvider);
		Set<StorageSolrDoc> docs = createSolrDocForEachFile(pid, allChildren, storageProvider);
		for (StorageSolrDoc doc : docs) {
			try {
				submitDoc(doc);
			} catch (IOException | SolrServerException e) {
				LOGGER.warn("Error submitting index document for {}: {}", doc.getId(), e.getMessage());
			}
		}
		solrServer.commit();
	}

	/**
	 * Removes indexes of all files in a collection record. Called when the files-public flag of a record is changed to
	 * false.
	 * 
	 * @param bagDir
	 *            Bag directory
	 * @param subDir
	 *            Payload directory
	 * @throws SolrServerException
	 * @throws IOException
	 */
	public void deindexAllFiles(String pid, StorageProvider storageProvider) throws SolrServerException, IOException {
		Set<FileInfo> allChildren = getAllChildren(pid, storageProvider);

		for (FileInfo fi : allChildren) {
			if (fi.getType() == Type.FILE) {
				String docId = createId(fi.getPid(), fi.getRelFilepath());
				solrServer.deleteById(docId);
				LOGGER.trace("Removed index document for {}.", docId);
			}
		}
		solrServer.commit();
	}

	private StorageSolrDoc createSolrDoc(String pid, String relPath, StorageProvider storageProvider)
			throws IOException {
		String docId = createId(pid, relPath);
		FileIndexDocumentGeneratorTask docGenTask = new FileIndexDocumentGeneratorTask(pid, relPath, storageProvider,
				docId);
		StorageSolrDoc doc = docGenTask.generateSolrInputDocument();
		return doc;
	}

	private Set<StorageSolrDoc> createSolrDocForEachFile(String pid, Collection<FileInfo> fileInfos,
			StorageProvider storageProvider) throws IOException {
		Set<StorageSolrDoc> docs = new HashSet<>();
		for (FileInfo fi : fileInfos) {
			if (fi.getType() == Type.FILE) {
				StorageSolrDoc solrDoc = createSolrDoc(pid, fi.getRelFilepath(), storageProvider);
				docs.add(solrDoc);
			}
		}
		return docs;
	}

	/**
	 * Submits the specified Solr document to the Solr instance. If document is blank (for a file that doesn't exist),
	 * then any previous document for the same file is deleted. If normal document then then the document replaces any
	 * previous document in the solr instance for that file.
	 * 
	 * @param doc
	 * @throws IOException
	 * @throws SolrServerException
	 */
	private void submitDoc(StorageSolrDoc doc) throws IOException, SolrServerException {
		if (doc.getName() != null) {
			solrServer.addBean(doc);
			LOGGER.trace("Added index document for {}.", doc.getId());
		} else {
			solrServer.deleteById(doc.getId());
			LOGGER.trace("Removed index document for {}.", doc.getId());
		}
	}

	private Set<FileInfo> getAllChildren(String pid, StorageProvider storageProvider) throws IOException {
		return storageProvider.getDirInfo(pid, "", Integer.MAX_VALUE).getChildrenRecursive();
	}

	private String createId(String pid, String relPath) {
		return format("{0}/{1}", pid, relPath);
	}

	/**
	 * Calls the shutdown method of the Solr Server instance for a clean shutdown.
	 */
	@PreDestroy
	public void close() {
		solrServer.shutdown();
	}

}
