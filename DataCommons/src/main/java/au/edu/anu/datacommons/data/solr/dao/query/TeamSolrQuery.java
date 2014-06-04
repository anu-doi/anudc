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

import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;

import au.edu.anu.datacommons.search.SearchTerm;
import au.edu.anu.datacommons.security.service.GroupService;

/**
 * TeamSolrQuery
 *
 * Australian National University Data Commons
 * 
 * Class to generate queries for solr
 *
 * JUnit coverage:
 * None
 * 
 * @author Genevieve Turner
 *
 */
public class TeamSolrQuery extends AbstractSolrQuery {
	private String query;

	/**
	 * Constructor
	 * 
	 * @param groupService The group service
	 * @param q The query string
	 */
	public TeamSolrQuery(GroupService groupService, String q) {
		super(groupService);
		
		query = "unpublished.all:(" + q + ")";
	}
	
	/**
	 * Constructor
	 * 
	 * @param groupService THe group service
	 * @param searchTerms THe search terms
	 */
	public TeamSolrQuery(GroupService groupService, List<SearchTerm> searchTerms) {
		super(groupService);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < searchTerms.size(); i++) {
			SearchTerm term = searchTerms.get(i);
			sb.append("(");
			sb.append(addTerm("unpublished", term.getKey(), term.getValue()));
			sb.append(")");
			if (i < searchTerms.size() - 1) {
				sb.append(" && ");
			}
			
		}
		query = sb.toString();
		LOGGER.info("Query: {}", query);
	}

	@Override
	protected void setQuery(SolrQuery solrQuery) {
		solrQuery.setQuery(query);
	}

	@Override
	protected void setReturnFields(SolrQuery solrQuery) {
		solrQuery.addField("id");
		setReturnFields("unpublished", solrQuery);
	}

	@Override
	protected void setFilters(SolrQuery solrQuery) {
		String filterGroups = getGroupsString();
		if (filterGroups == null || "".equals(filterGroups)) {
			filterGroups = "0";
		}
		String filter = "unpublished.ownerGroup:(" + filterGroups + ")";
		solrQuery.addFilterQuery(filter);
		setQueryFacetFilters(solrQuery);
	}
}
