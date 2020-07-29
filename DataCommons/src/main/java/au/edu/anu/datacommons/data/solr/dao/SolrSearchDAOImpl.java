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

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import au.edu.anu.datacommons.data.solr.SolrManager;
import au.edu.anu.datacommons.data.solr.SolrUtils;
import au.edu.anu.datacommons.data.solr.dao.query.AbstractSolrQuery;
import au.edu.anu.datacommons.data.solr.dao.query.AllSolrQuery;
import au.edu.anu.datacommons.data.solr.dao.query.PublishedSolrQuery;
import au.edu.anu.datacommons.data.solr.dao.query.TeamSolrQuery;
import au.edu.anu.datacommons.data.solr.dao.query.TemplateSolrQuery;
import au.edu.anu.datacommons.data.solr.model.SolrSearchResult;
import au.edu.anu.datacommons.search.SearchTerm;
import au.edu.anu.datacommons.security.service.GroupService;

/**
 * SolrSearchImpl
 *
 * Australian National University Data Commons
 * 
 * Implementation class for searches
 *
 * JUnit coverage:
 * SolrSearchDAOTest
 * 
 * @author Genevieve Turner
 *
 */
@Repository("solrSearchDAOImpl")
public class SolrSearchDAOImpl implements SolrSearchDAO {
	static final Logger LOGGER = LoggerFactory.getLogger(SolrSearchDAOImpl.class);
	
	@Resource(name="groupServiceImpl")
	GroupService groupService;

	@Override
	public SolrSearchResult executeSearch(String q, int offset, int limit,
			String filter) throws SolrServerException, IOException {
		q = SolrUtils.escapeSpecialCharacters(q);
		
		Object[] list = {q, offset, limit};
		LOGGER.debug("Values to use to generate query for Solr - Query Limiter: {} Query Term: {}, Offset: {}, Limit: {}", list);
		AbstractSolrQuery query = getQueryTerms(q, filter);
		query.setStart(offset);
		query.setRows(limit);
		
		return executeSearch(query.getSolrQuery());
	}

	@Override
	public SolrSearchResult executeSearch(String q, int offset, int limit,
			String filter, String sortField, ORDER sortOrder) throws SolrServerException, IOException {
		q = SolrUtils.escapeSpecialCharacters(q);
		
		Object[] list = {filter, q, offset, limit, sortField, sortOrder};
		LOGGER.debug("Values to use to generate query for Solr - Query Limiter: {} Query Term: {}, Offset: {}, Limit: {}, Sort Field: {}, Sort Order: {}", list);
		
		AbstractSolrQuery query = getQueryTerms(q, filter);
		
		query.setStart(offset);
		query.setRows(limit);
		query.setSortField(sortField, sortOrder);
		
		return executeSearch(query.getSolrQuery());
	}
	
	@Override
	public SolrSearchResult executeSearch(String q, String facetField, String facetSelected, int offset, int limit, String filter) 
			throws SolrServerException, IOException {
		if (q != null && !"".equals(q)) {
			q = SolrUtils.escapeSpecialCharacters(q);
		}
		else {
			q = "*";
		}
		
		AbstractSolrQuery query = getQueryTerms(q, filter);
		query.setStart(offset);
		query.setRows(limit);
		query.addFacet(facetField, facetSelected);
		query.setFacetSort("index");
//		query.setfac
//		FacetField facet = new FacetField(facetField);
		
		
		
		return executeSearch(query.getSolrQuery(), true);
	}

	@Override
	public SolrSearchResult executeSearch(List<SearchTerm> terms, int offset,
			int limit, String filter) throws SolrServerException, IOException {
		AbstractSolrQuery query = getQueryTerms(terms, filter);
		query.setStart(offset);
		query.setRows(limit);
		
		return executeSearch(query.getSolrQuery());
	}
	
	@Override
	public SolrSearchResult executeSearch(SolrQuery solrQuery) throws SolrServerException, IOException {
		return executeSearch(solrQuery, false);
	}
	
	/**
	 * Execute a search query
	 * 
	 * @param solrQuery The solr query to perform
	 * @param hasFacet Indicates whether the solr query has facets
	 * @return The search result
	 * @throws SolrServerException
	 */
	private SolrSearchResult executeSearch(SolrQuery solrQuery, boolean hasFacet) throws SolrServerException, IOException {
		LOGGER.debug("Query to send to Solr: {}", solrQuery.toString());
		SolrClient solrClient = SolrManager.getInstance().getSolrClient();
		QueryResponse queryResponse = solrClient.query(solrQuery);
		if (hasFacet) {
			return new SolrSearchResult(queryResponse.getResults(), queryResponse.getFacetFields());
		}
		else {
			return new SolrSearchResult(queryResponse.getResults());
		}
	}
	
	/**
	 * Get the query terms
	 * 
	 * @param q The query string
	 * @param filter The filter that defines which records to retrieve
	 * @return The class that generates the solr query
	 */
	private AbstractSolrQuery getQueryTerms(String q, String filter) {
		LOGGER.trace("Get the query terms with the search value '{}', and filter '{}'", q, filter);
		AbstractSolrQuery absSolrQuery = null;
		
		if ("team".equals(filter)) {
			absSolrQuery = new TeamSolrQuery(groupService, q);
		}
		else if ("published".equals(filter)) {
			absSolrQuery = new PublishedSolrQuery(groupService, q);
		}
		else if ("template".equals(filter)) {
			absSolrQuery = new TemplateSolrQuery(groupService, q);
		}
		else {
			absSolrQuery = new AllSolrQuery(groupService, q);
		}
		return absSolrQuery;
	}
	
	/**
	 * Get the query terms
	 * 
	 * @param searchTerms The search terms
	 * @param filter The filter that defines which  records to retrieve
	 * @return THe class that generates the solr query
	 */
	private AbstractSolrQuery getQueryTerms(List<SearchTerm> searchTerms, String filter) {
		AbstractSolrQuery absSolrQuery = null;
		
		if ("team".equals(filter)) {
			absSolrQuery = new TeamSolrQuery(groupService, searchTerms);
		}
		else if ("published".equals(filter)) {
			absSolrQuery = new PublishedSolrQuery(groupService, searchTerms);
		}
		else if ("template".equals(filter)) {
			absSolrQuery = new TemplateSolrQuery(groupService, searchTerms);
		}
		else {
			absSolrQuery = new AllSolrQuery(groupService, searchTerms);
		}
		return absSolrQuery;
	}
}
