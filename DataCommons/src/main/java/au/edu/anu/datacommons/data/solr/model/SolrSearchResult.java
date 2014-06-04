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

package au.edu.anu.datacommons.data.solr.model;

import java.util.List;

import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SolrSearchResult
 *
 * Australian National University Data Commons
 * 
 * Wrapper class for SolrDocumentList's so that they can be used for things such as JSP files.
 * This is needed as JSP converts it to a Collection object so for example the number of resutls found
 * cannot be accessed
 *
 * JUnit coverage:
 * SolrSearchDAOTest
 * 
 * @author Genevieve Turner
 *
 */
public class SolrSearchResult {
	static final Logger LOGGER = LoggerFactory.getLogger(SolrSearchResult.class);
	
	private SolrDocumentList resultDocuments = null;
	private List<FacetField> facetFields = null;
	
	/**
	 * Constructor
	 * 
	 * @param results The document list
	 */
	public SolrSearchResult(SolrDocumentList results) {
		this.resultDocuments = results;
	}
	
	/**
	 * Constructor
	 * 
	 * @param results The document list
	 * @param facetFields The facet fields
	 */
	public SolrSearchResult(SolrDocumentList results, List<FacetField> facetFields) {
		this.resultDocuments = results;
		this.facetFields = facetFields;
	}
	
	/**
	 * Get the number of results found for the search
	 * 
	 * @return The number of results
	 */
	public long getNumFound() {
		return resultDocuments.getNumFound();
	}
	
	/**
	 * Get the record to start at
	 * 
	 * @return The start record
	 */
	public long getStart() {
		return resultDocuments.getStart();
	}
	
	/**
	 * Get the maximum score
	 * 
	 * @return The maximum score
	 */
	public float getMaxScore() {
		return resultDocuments.getMaxScore();
	}
	
	/**
	 * Get the document list
	 * 
	 * @return The document list
	 */
	public SolrDocumentList getDocumentList() {
		return resultDocuments;
	}
	
	/**
	 * Set the document list
	 * 
	 * @param results The document list
	 */
	public void setDocumentList(SolrDocumentList results) {
		this.resultDocuments = results;
	}

	/**
	 * Get the facet fields
	 * 
	 * @return The list of facet fields
	 */
	public List<FacetField> getFacetFields() {
		return facetFields;
	}

	/**
	 * Set the list of facet fields
	 * 
	 * @param facetFields The list of facet fields
	 */
	public void setFacetFields(List<FacetField> facetFields) {
		this.facetFields = facetFields;
	}
}
