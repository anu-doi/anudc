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
package au.edu.anu.datacommons.data.solr.dao.query;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.data.db.model.Groups;
import au.edu.anu.datacommons.properties.GlobalProps;
import au.edu.anu.datacommons.security.service.GroupService;

/**
 * AbstractSolrQuery
 *
 * Australian National University Data Commons
 * 
 * Abstract class for generating solr queries
 *
 * JUnit coverage:
 * None
 * 
 * @author Genevieve Turner
 *
 */
public abstract class AbstractSolrQuery {
	static final Logger LOGGER = LoggerFactory.getLogger(AbstractSolrQuery.class);
	
	GroupService groupService;
	protected int start;
	protected int rows;
	protected String sortField;
	protected ORDER sortOrder;
	private List<String> facetFilters = new ArrayList<String>();
	private Set<String> facetFields = new HashSet<String>();
	
	/**
	 * Constructor
	 * 
	 * @param groupService The group service
	 */
	public AbstractSolrQuery(GroupService groupService) {
		this.groupService = groupService;
	}
	
	/**
	 * Set the solr query string (i.e. the 'q' field on a solr search)
	 * 
	 * @param solrQuery The solr query
	 */
	abstract protected void setQuery(SolrQuery solrQuery);
	
	/**
	 * Set the return fields
	 * 
	 * @param solrQuery The solr query
	 */
	abstract protected void setReturnFields(SolrQuery solrQuery);
	
	/**
	 * Set the filters
	 * 
	 * @param solrQuery The solr query
	 */
	abstract protected void setFilters(SolrQuery solrQuery);
	
	/**
	 * Set the start point
	 * 
	 * @param start The start value
	 */
	public void setStart(int start) {
		this.start = start;
	}
	
	/**
	 * Set the start point in the query
	 * 
	 * @param query The solr query
	 */
	protected void setQueryStart(SolrQuery query) {
		query.setStart(start);
	}
	
	/**
	 * Set the number of rows to return
	 * 
	 * @param rows The number of rows
	 */
	public void setRows(int rows) {
		this.rows = rows;
	}
	
	/**
	 * Set the number of rows for the query
	 * 
	 * @param solrQuery The solr query
	 */
	public void setQueryRows(SolrQuery solrQuery) {
		solrQuery.setRows(rows);
	}
	
	/**
	 * Set the sort field and order
	 * 
	 * @param sortField The sort field
	 * @param sortOrder The sort order
	 */
	public void setSortField(String sortField, ORDER sortOrder) {
		this.sortField = sortField;
		this.sortOrder = sortOrder;
	}
	
	/**
	 * Set the query sort field
	 * 
	 * @param solrQuery The solr query
	 */
	public void setQuerySortField(SolrQuery solrQuery) {
		if (sortField != null) {
			solrQuery.setSortField(sortField, sortOrder);
		}
	}
	
	/**
	 * Add a facet to the query
	 * 
	 * @param facetField The facet field
	 * @param facetSelected The facet selected
	 */
	public void addFacet(String facetField, String facetSelected) {
		if (facetField == null) {
			return;
		}
		if (facetSelected != null && !"".equals(facetSelected)) {
			facetFilters.add(facetField + ":" + facetSelected);
		}
		facetFields.add(facetField);
	}
	
	/**
	 * Set the facet filters
	 * 
	 * @param solrQuery The solr query
	 */
	protected void setQueryFacetFilters(SolrQuery solrQuery) {
		for (String filter : facetFilters) {
			solrQuery.addFilterQuery(filter);
		}
	}
	
	/**
	 * Set the query facet fields
	 * 
	 * @param solrQuery The solr query
	 */
	protected void setQueryFacetFields(SolrQuery solrQuery) {
		if (facetFields != null && facetFields.size() > 0) {
			for (String facetField : facetFields) {
				solrQuery.addFacetField(facetField);
			}
			solrQuery.setFacetMinCount(1);
		}
	}
	
	/**
	 * Get the solr query
	 * 
	 * @return The solr query
	 */
	public SolrQuery getSolrQuery() {
		SolrQuery solrQuery = new SolrQuery();
		setQuery(solrQuery);
		setReturnFields(solrQuery);
		setFilters(solrQuery);
		setQueryStart(solrQuery);
		setQueryRows(solrQuery);
		setQuerySortField(solrQuery);
		setQueryFacetFields(solrQuery);
		
		return solrQuery;
	}

	/**
	 * Get a string of groups that the user has access to
	 * 
	 * @return A string of the groups
	 */
	protected String getGroupsString() {
		StringBuffer filterGroups = new StringBuffer();
		List<Groups> groups = groupService.getAll();
		for (Groups group : groups) {
			filterGroups.append(group.getId());
			filterGroups.append(" ");
		}
		LOGGER.trace("Filter Groups: {}", filterGroups.toString());
		return filterGroups.toString();
	}
	
	/**
	 * Set the fields to return
	 * 
	 * @param type Add the prefix for the returned field type (e.g. 'unpublished','published','template')
	 * @param solrQuery The query to set the return fields for
	 */
	protected void setReturnFields(String type, SolrQuery solrQuery) {
		String returnFields = GlobalProps.getProperty(GlobalProps.PROP_SEARCH_SOLR_RETURNFIELDS);
		String[] splitReturnFields = returnFields.split(",");
		for (String field : splitReturnFields) {
			solrQuery.addField(type + "." + field);
		}
	}
	
	/**
	 * Add a term to the search
	 * 
	 * @param type The type (i.e. published, unpublished, tmplt)
	 * @param key THe key to search
	 * @param value THe value to search
	 * @return The string representaition of the search term
	 */
	protected String addTerm(String type, String key, String value) {
		StringBuilder sb = new StringBuilder();
		sb.append(type);
		sb.append(".");
		sb.append(key);
		sb.append(":(");
		sb.append(value);
		sb.append(") ");
		return sb.toString();
	}
}
