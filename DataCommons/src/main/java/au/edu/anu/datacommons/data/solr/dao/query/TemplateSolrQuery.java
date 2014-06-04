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
 * TemplateSolrQuery
 *
 * Australian National University Data Commons
 * 
 * Query for templates
 *
 * JUnit coverage:
 * None
 * 
 * @author Genevieve Turner
 *
 */
public class TemplateSolrQuery extends AbstractSolrQuery {
	String query;
	
	/**
	 * Constructor
	 * 
	 * @param groupService The group service
	 * @param q The query string
	 */
	public TemplateSolrQuery(GroupService groupService, String q) {
		super(groupService);
		if (q == null || "".equals(q)) {
			query = "*:*";
		}
		else {
			//TODO update this to do actual query
			q = "template.name:*";
		}
	}
	
	/**
	 * Constructor
	 * 
	 * @param groupService THe group service
	 * @param searchTerms THe search terms
	 */
	public TemplateSolrQuery(GroupService groupService, List<SearchTerm> searchTerms) {
		super(groupService);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < searchTerms.size(); i++) {
			SearchTerm term = searchTerms.get(i);
			sb.append("(");
			sb.append(addTerm("template", term.getKey(), term.getValue()));
			sb.append(")");
			if (i < searchTerms.size() - 1) {
				sb.append(" && ");
			}
		}
		query = sb.toString();
	}

	@Override
	protected void setQuery(SolrQuery solrQuery) {
		// TODO Auto-generated method stub
		solrQuery.setQuery(query);
	}

	@Override
	protected void setReturnFields(SolrQuery solrQuery) {
		solrQuery.addField("id");
		setReturnFields("template", solrQuery);
	}

	@Override
	protected void setFilters(SolrQuery solrQuery) {
		solrQuery.addFilterQuery("template.type:template");
		setQueryFacetFilters(solrQuery);
	}

}
