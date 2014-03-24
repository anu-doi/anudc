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

import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;

import au.edu.anu.datacommons.data.solr.model.SolrSearchResult;

/**
 * SolrSearch
 *
 * Australian National University Data Commons
 * 
 * Interface for searching against the solr search system
 *
 * JUnit coverage:
 * None
 * 
 * @author Genevieve Turner
 *
 */
public interface SolrSearchDAO {
	/**
	 * Execute a search against Solr
	 * 
	 * @param q Search terms
	 * @param offset offset amount (i.e. the value to start the search from)
	 * @param limit How many rows to return
	 * @param filter Indicator for which type of search to do (e.g. 'team','published','template','all')
	 * @return The search results
	 * @throws SolrServerException
	 */
	public SolrSearchResult executeSearch(String q, int offset, int limit, String filter) throws SolrServerException;
	
	/**
	 * Execute a search against Solr with a specific sort order
	 * 
	 * @param q Search terms
	 * @param offset offset amount (i.e. the value to start the search from)
	 * @param limit How many rows to return
	 * @param filter Indicator for which type of search to do
	 * @param sortField The field to sort on
	 * @param sortOrder The order to sort by (i.e. ascending or descending)
	 * @return The search results
	 * @throws SolrServerException
	 */
	public SolrSearchResult executeSearch(String q, int offset, int limit, String filter, String sortField, ORDER sortOrder) throws SolrServerException;

	/**
	 * Execute a search against solr with the provided query
	 * 
	 * @param solrQuery The query to execute
	 * @return The search results
	 * @throws SolrServerException
	 */
	public SolrSearchResult executeSearch(SolrQuery solrQuery) throws SolrServerException;
}
