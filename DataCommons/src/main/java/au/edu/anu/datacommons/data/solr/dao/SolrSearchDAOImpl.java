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

import java.util.List;

import javax.annotation.Resource;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import au.edu.anu.datacommons.data.db.model.Groups;
import au.edu.anu.datacommons.data.solr.SolrManager;
import au.edu.anu.datacommons.data.solr.SolrUtils;
import au.edu.anu.datacommons.data.solr.model.SolrSearchResult;
import au.edu.anu.datacommons.properties.GlobalProps;
import au.edu.anu.datacommons.security.service.GroupService;
import au.edu.anu.datacommons.services.UserResource;
import au.edu.anu.datacommons.util.Util;

/**
 * SolrSearchImpl
 *
 * Australian National University Data Commons
 * 
 * Implementation class for searches
 *
 * JUnit coverage:
 * None
 * 
 * @author Genevieve Turner
 *
 */
@Repository("solrSearchDAOImpl")
public class SolrSearchDAOImpl implements SolrSearchDAO {
	static final Logger LOGGER = LoggerFactory.getLogger(UserResource.class);
	
	@Resource(name="groupServiceImpl")
	GroupService groupService;

	@Override
	public SolrSearchResult executeSearch(String q, int offset, int limit,
			String filter) throws SolrServerException {
		q = SolrUtils.escapeSpecialCharacters(q);
		
		Object[] list = {q, offset, limit};
		LOGGER.trace("Query Term: {}, Offset: {}, Limit: {}", list);
		
		SolrQuery solrQuery = new SolrQuery();
		setQueryTerms(solrQuery, q, filter);
		
		solrQuery.setStart(offset);
		solrQuery.setRows(limit);
		
		return executeSearch(solrQuery);
	}

	@Override
	public SolrSearchResult executeSearch(String q, int offset, int limit,
			String filter, String sortField, ORDER sortOrder) throws SolrServerException {
		q = SolrUtils.escapeSpecialCharacters(q);
		
		Object[] list = {q, offset, limit};
		LOGGER.trace("Query Term: {}, Offset: {}, Limit: {}", list);
		
		SolrQuery solrQuery = new SolrQuery();
		setQueryTerms(solrQuery, q, filter);
		
		solrQuery.setStart(offset);
		solrQuery.setRows(limit);
		solrQuery.setSortField(sortField, sortOrder);
		
		return executeSearch(solrQuery);
	}
	
	@Override
	public SolrSearchResult executeSearch(SolrQuery solrQuery) throws SolrServerException {
		LOGGER.debug("Solr Query: {}", solrQuery.toString());
		SolrServer solrServer = SolrManager.getInstance().getSolrServer();
		QueryResponse queryResponse = solrServer.query(solrQuery);
		SolrSearchResult result = new SolrSearchResult(queryResponse.getResults());
		
		return result;
	}
	
	/**
	 * Set the query 
	 * 
	 * @param solrQuery The query to set the query terms for
	 * @param q The value to query for
	 * @param filter Indicator for which type of search to do (e.g. 'team','published','template','all')
	 */
	private void setQueryTerms(SolrQuery solrQuery, String q, String filter) {
		if ("team".equals(filter)) {
			setTeamQuery(solrQuery, q);
		}
		else if ("published".equals(filter)) {
			setPublishedQuery(solrQuery, q);
		}
		else if ("template".equals(filter)) {
			setTemplateQuery(solrQuery, q);
		}
		else {
			setAllQuery(solrQuery, q);
		}
	}
	
	/**
	 * Query both published records and those unpublished records the user has permission to access.
	 * 
	 * @param solrQuery The query to set the query terms for
	 * @param q The value to query for
	 */
	private void setAllQuery(SolrQuery solrQuery, String q) {
		LOGGER.debug("Set all query");
		String filterGroups = getGroupsString();
		solrQuery.setQuery("published.all:(" + q + ") unpublished.all:(" + q + ")");
		
		solrQuery.addField("id");
		setReturnFields("published", solrQuery);
		setReturnFields("unpublished", solrQuery);
		if (Util.isNotEmpty(filterGroups)) {
			solrQuery.addFilterQuery("(location.published:ANU or unpublished.ownerGroup:(" + filterGroups + "))");
		}
		else {
			solrQuery.addFilterQuery("location.published:ANU");
		}
	}
	
	/**
	 * Query the unpublished records that the user has access to
	 * 
	 * @param solrQuery The query to set the query terms for
	 * @param q The value to query for
	 */
	private void setTeamQuery(SolrQuery solrQuery, String q) {
		LOGGER.debug("Set team query");

		if (groupService == null) {
			LOGGER.error("Group service is null");
		}
		else {
			String filterGroups = getGroupsString();
			solrQuery.setQuery("unpublished.all:(" + q + ")");
			
			solrQuery.addField("id");
			setReturnFields("unpublished", solrQuery);
			if (filterGroups == null || "".equals(filterGroups)) {
				filterGroups = "0";
			}
			solrQuery.addFilterQuery("unpublished.ownerGroup:(" + filterGroups + ")");
		}
	}
	
	/**
	 * Query the published records
	 * 
	 * @param solrQuery The query to set the query terms for
	 * @param q The value to query for
	 */
	private void setPublishedQuery(SolrQuery solrQuery, String q) {
		LOGGER.debug("Set published query");
		
		solrQuery.setQuery("published.all:(" + q + ")");
		
		solrQuery.addField("id");
		setReturnFields("published", solrQuery);
		
		solrQuery.addFilterQuery("location.published:ANU");
	}
	
	/**
	 * Query the template records
	 * 
	 * @param solrQuery The solr query to add fields/filter queries to
	 * @param q The value to query for. Please note that this is not currently used for template queries.
	 */
	private void setTemplateQuery(SolrQuery solrQuery, String q) {
		LOGGER.debug("Set template query");
		
		solrQuery.setQuery("*:*");
		
		solrQuery.addField("id");
		setReturnFields("template", solrQuery);
		
		solrQuery.addFilterQuery("template.type:template");
	}
	
	/**
	 * Get a string of groups that the user has access to
	 * 
	 * @return A string of the groups
	 */
	private String getGroupsString() {
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
	private void setReturnFields(String type, SolrQuery solrQuery) {
		String returnFields = GlobalProps.getProperty(GlobalProps.PROP_SEARCH_SOLR_RETURNFIELDS);
		String[] splitReturnFields = returnFields.split(",");
		for (String field : splitReturnFields) {
			solrQuery.addField(type + "." + field);
		}
	}
	
}
