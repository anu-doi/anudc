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
package au.edu.anu.datacommons.data.solr.dao;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.Iterator;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.data.solr.model.SolrSearchResult;

public class SolrSearchDAOTest {
	static final Logger LOGGER = LoggerFactory.getLogger(SolrSearchDAOTest.class);
	
	@Test
	public void test() {
		SolrSearchDAO searchDAO = new SolrSearchDAOImpl();
		try {
			SolrSearchResult results = searchDAO.executeSearch("*", "keyword", null, 0, 10, "published");
			assertNotNull("Unexpected results value", results);
			assertThat("Unexpected number of resutls", results.getNumFound(), greaterThan(new Long(0)));
			assertNotNull("Unexpected facet fields value", results.getFacetFields());
			assertThat("Unexpected number of keywords", results.getFacetFields().size(), greaterThan(0));
			assertNotNull("Unexpected facet value", results.getFacetFields().get(0));
			assertThat("Unexpected number of facet field counts", results.getFacetFields().get(0).getValues().size(), greaterThan(0));
			
			LOGGER.debug("Number of results: {}", results.getNumFound());
			SolrDocumentList docList = results.getDocumentList();
			LOGGER.debug("Objects returned:");
			Iterator<SolrDocument> it = docList.iterator();
			while (it.hasNext()) {
				SolrDocument doc = it.next();
				Object object = doc.getFieldValue("id");
				LOGGER.debug("Id value: {}", object);
			}
			LOGGER.debug("Keywords found:");
			for (FacetField field : results.getFacetFields()) {
				LOGGER.debug("Facet field name: {}", field.getName());
				for (Count count : field.getValues()) {
					LOGGER.debug("Value: {}, Count: {}", count.getName(), count.getCount());
				}
			}
		}
		catch (SolrServerException e) {
			LOGGER.error("Exception executing solr search", e);
			fail("Exception executing test");
		}
	}
}
