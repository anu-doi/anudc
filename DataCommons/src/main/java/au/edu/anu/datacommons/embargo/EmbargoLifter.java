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
package au.edu.anu.datacommons.embargo;

import java.text.SimpleDateFormat;
import java.util.Iterator;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.data.db.dao.FedoraObjectDAO;
import au.edu.anu.datacommons.data.db.dao.FedoraObjectDAOImpl;
import au.edu.anu.datacommons.data.db.model.FedoraObject;
import au.edu.anu.datacommons.data.solr.dao.SolrSearchDAO;
import au.edu.anu.datacommons.data.solr.dao.SolrSearchDAOImpl;
import au.edu.anu.datacommons.data.solr.model.SolrSearchResult;

import com.ibm.icu.util.Calendar;

/**
 * EmbargoLifter
 *
 * Australian National University Data Commons
 * 
 * Sets the fedora_object.is_files_public when an items embargo date is up.
 *
 * JUnit coverage:
 * EmbargoLifterTest
 * 
 * @author Genevieve Turner
 *
 */
public class EmbargoLifter implements Runnable {
	static final Logger LOGGER = LoggerFactory.getLogger(EmbargoLifter.class);

	@Override
	public void run() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String date = sdf.format(cal.getTime());
		String fromDate = date + "T00:00:00Z";
		String toDate = date + "T23:23:59.999Z";
		LOGGER.debug("Finding embargoed items where the lift date is between {} and {}", fromDate, toDate);
		String queryString = "published.calcEmbargoDate:[" + fromDate + " TO " + toDate + "]";
		
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery(queryString);
		solrQuery.addField("id");
		solrQuery.addField("published.name");
		solrQuery.addField("published.email");
		
		SolrSearchDAO solrSearchDAO = new SolrSearchDAOImpl();
		try {
			SolrSearchResult solrSearchResult = solrSearchDAO.executeSearch(solrQuery);
			SolrDocumentList docList = solrSearchResult.getDocumentList();
			if (docList.getNumFound() > 0) {
				Iterator<SolrDocument> it = docList.iterator();
				FedoraObjectDAO fedoraObjectDAO = new FedoraObjectDAOImpl();
				while (it.hasNext()) {
					SolrDocument doc = it.next();
					String pid = (String) doc.getFieldValue("id");
					String name = (String) doc.getFieldValue("published.name");
					LOGGER.info("Embargo lifted for {} - {}", pid, name);
					FedoraObject fedoraObject = fedoraObjectDAO.getSingleByName(pid);
					if (fedoraObject != null && !fedoraObject.isFilesPublic()) {
						LOGGER.info("Updating fedora_object.is_files_public to true for {} - {}", pid, name);
						fedoraObject.setFilesPublic(Boolean.TRUE);
						fedoraObjectDAO.update(fedoraObject);
					}
				}
			}
		}
		catch (SolrServerException e) {
			
		}
	}

}
