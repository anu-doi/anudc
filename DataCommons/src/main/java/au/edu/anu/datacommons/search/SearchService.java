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

package au.edu.anu.datacommons.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.solr.client.solrj.SolrServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import au.edu.anu.datacommons.data.solr.dao.SolrSearchDAO;
import au.edu.anu.datacommons.data.solr.model.SolrSearchResult;
import au.edu.anu.datacommons.security.service.GroupService;
import au.edu.anu.datacommons.util.Util;

import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.view.Viewable;

/**
 * SearchService
 * 
 * Australian National University Data Commons
 * 
 * Class provides a REST service using Jersey for searching the Fedora repository
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		26/03/2012	Rahul Khanna (RK)		Initial.
 * 0.2		04/05/2012	Genevieve Turner (GT)	Updated for the removal of the method 'runRiSearch'
 * 0.4		13/06/2012	Genevieve Turner (GT)	Updated for changes to use solr as the search engine
 * 0.5		13/06/2012	Genevieve Turner (GT)	Updated for varying search filters
 * 0.6		18/06/2012	Genevieve Turner (GT)	Fixed an issue where there is an error in the query if the user has no associated groups
 * 0.7		23/07/2012	Genevieve Turner (GT)	Added Solr query character escaping
 * </pre>
 * 
 */
@Component
@Path("/search")
public class SearchService
{
	private static final Logger LOGGER = LoggerFactory.getLogger(SearchService.class);
	private static final String SEARCH_JSP = "/search.jsp";
	private static final String SEARCH_ADVANCED_JSP = "/search_advanced.jsp";
	private static final String BROWSE_JSP = "/browse.jsp";
	private static final String BROWSE_RESULTS_JSP = "/browse_results.jsp";
	
	@Resource(name="groupServiceImpl")
	GroupService groupService;
	
	@Resource(name="solrSearchDAOImpl")
	SolrSearchDAO solrSearch;
	
	/**
	 * doGetAsXml
	 * 
	 * Australian National University Data Commons
	 * 
	 * This method is called when the search service is accessed and the type requested is XML.
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		23/03/2012	Rahul Khanna (RK)		Initial
	 * 0.2		04/05/2012	Genevieve Turner (GT)	Updated for the removal of the method 'runRiSearch'
	 * 0.4		13/06/2012	Genevieve Turner (GT)	Updated for changes to use solr as the search engine
	 * </pre>
	 * 
	 * @return XML containing search results as a Response object.
	 */
	@GET
	@Produces(MediaType.TEXT_XML)
	public Response doGetAsXml(@QueryParam("q") String q, @QueryParam("offset") int offset, @QueryParam("limit") int limit, @QueryParam("filter") String filter)
	{
		//TODO Test this function
		Response response = null;
		
		try {
			SolrSearchResult resultList = solrSearch.executeSearch(q, offset, limit, filter);
			response = Response.ok(resultList).build();
		}
		catch (SolrServerException e) {
			LOGGER.error("Exception retrieving results", e);
			response = Response.status(Status.BAD_REQUEST).build();
		}
		return response;
	}

	/**
	 * doGetAsHtml
	 * 
	 * Australian National University Data Commons
	 * 
	 * This method is called when the search service is accessed and the type requested is HTML.
	 * 
	 * @return Response to display the Search JSP and passing an object to it.
	 * 
	 *         <pre>
	 * Version	Date		Developer				Description
	 * 0.1		23/03/2012	Rahul Khanna (RK)		Initial
	 * 0.2		04/05/2012	Genevieve Turner (GT)	Updated for the removal of the method 'runRiSearch'
	 * 0.3		8/05/2012	Rahul Khanna (RK)		Updated for RiSearchRequest object.
	 * 0.4		13/06/2012	Genevieve Turner (GT)	Updated for changes to use solr as the search engine
	 * </pre>
	 */
	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response doGetAsHtml(@QueryParam("q") String q, @QueryParam("offset") int offset, @QueryParam("limit") int limit, @QueryParam("filter") String filter)
	{
		Response response = null;
		
		if (Util.isNotEmpty(q)) {
			Map<String, Object> model = new HashMap<String, Object>();
			try {
				LOGGER.info("User {} submitted search query [{}]", getCurUsername(), q);
				SolrSearchResult solrSearchResult = solrSearch.executeSearch(q, offset, limit, filter);
				model.put("resultSet", solrSearchResult);
			}
			catch (SolrServerException e) {
				LOGGER.error("Exception querying solr", e);
			}
			response = Response.ok(new Viewable(SEARCH_JSP, model)).build();
		}
		else {
			response = Response.ok(new Viewable(SEARCH_JSP)).build();
		}
		return response;
	}

	/**
	 * doGetAsJson
	 * 
	 * Australian National University Data Commons
	 * 
	 * Submits a request to RI Search Service. Returns response as JSON.
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		08/05/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 * @param q
	 *            Search terms.
	 * @param offset
	 *            Offset as int.
	 * @param limit
	 *            Limit as int.
	 * @param filter
	 *            Filter as String.
	 * @return HTTP OK Response with JSON string as its entity.
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response doGetAsJson(@QueryParam("q") String q, @QueryParam("offset") int offset, @QueryParam("limit") int limit, @QueryParam("filter") String filter)
	{
		//TODO Test this function
		Response response = null;
		
		try {
			SolrSearchResult resultList = solrSearch.executeSearch(q, offset, limit, filter);
			response = Response.ok(resultList).build();
		}
		catch (SolrServerException e) {
			LOGGER.error("Exception retrieving results", e);
			response = Response.status(Status.BAD_REQUEST).build();
		}
		return response;
	}
	
	/**
	 * Browse the provided facet
	 * 
	 * @param q The value to query
	 * @param facetField The field to use as a facet
	 * @param facetSelected The selected value within the field
	 * @param offset The offset
	 * @param limit The number of items to find
	 * @param filter The filter (i.e. all, team, or published)
	 * @return The response
	 */
	@GET
	@Path("/browse")
	@Produces(MediaType.TEXT_HTML)
	public Response doGetbrowseAsHtml(@QueryParam("q") String q, @QueryParam("field") String facetField
			, @QueryParam("field-select") String facetSelected, @QueryParam("offset") int offset
			, @QueryParam("limit") int limit, @QueryParam("filter") String filter) {
		if (Util.isNotEmpty(facetField)) {
			try {
				SolrSearchResult solrSearchResult = solrSearch.executeSearch(q, facetField, facetSelected, offset, limit, filter);
				Map<String, Object> model = new HashMap<String, Object>();
				model.put("resultSet", solrSearchResult);
				return Response.ok(new Viewable(BROWSE_JSP, model)).build();
			}
			catch(SolrServerException e) {
				LOGGER.error("Exception querying solr", e);
			}
		}
		else {
			return Response.ok(new Viewable(BROWSE_JSP)).build();
		}
		
		return Response.status(Status.BAD_REQUEST).build();
	}
	
	/**
	 * Get the results from a further refined browse search
	 * 
	 * @param q The value to query
	 * @param facetField The field to use as a facet
	 * @param facetSelected The selected value within the field
	 * @param offset The offset
	 * @param limit The number of items to find
	 * @param filter The filter (i.e. all, team, or published)
	 * @return The response
	 */
	@GET
	@Path("/browse/results")
	@Produces(MediaType.TEXT_HTML)
	public Response doGetBrowseResultsAsHtml(@QueryParam("q") String q, @QueryParam("field") String facetField
			, @QueryParam("field-select") String facetSelected, @QueryParam("offset") int offset
			, @QueryParam("limit") int limit, @QueryParam("filter") String filter) {
		if (Util.isNotEmpty(facetField)) {
			try {
				SolrSearchResult solrSearchResult = solrSearch.executeSearch(q, facetField, facetSelected, offset, limit, filter);
				Map<String, Object> model = new HashMap<String, Object>();
				model.put("resultSet", solrSearchResult);
				return Response.ok(new Viewable(BROWSE_RESULTS_JSP, model)).build();
			}
			catch(SolrServerException e) {
				LOGGER.error("Exception querying solr", e);
			}
		}
		else {
			return Response.ok(new Viewable(BROWSE_RESULTS_JSP)).build();
		}
		
		return Response.status(Status.BAD_REQUEST).build();
	}
	
	@GET
	@Path("/advanced")
	@Produces(MediaType.TEXT_HTML)
	public Response doGetAdvancedSearchAsHtml(@QueryParam("value-type") List<String> valueTypes
			, @QueryParam("search-val") List<String> searchValues, @QueryParam("offset") int offset
			, @QueryParam("limit") int limit, @QueryParam("filter") String filter) {
		List<SearchTerm> searchTerms = new ArrayList<SearchTerm>();
		if (searchValues != null && searchValues.size() > 0) {
			for (int i = 0; i < searchValues.size(); i++) {
				String value = searchValues.get(i);
				String key = valueTypes.get(i);
				if (value != null && !"".equals(value)) {
					SearchTerm term = new SearchTerm(key, value);
					searchTerms.add(term);
				}
			}
			if (searchTerms.size() > 0) {
				try {
					SolrSearchResult solrSearchResult =  solrSearch.executeSearch(searchTerms, offset, limit, filter);
					Map<String, Object> model = new HashMap<String, Object>();
					model.put("resultSet", solrSearchResult);
					return Response.ok(new Viewable(SEARCH_ADVANCED_JSP, model)).build();
				}
				catch(SolrServerException e) {
					LOGGER.error("Exception querying solr", e);
				}
			}
		}
		else {
			return Response.ok(new Viewable(SEARCH_ADVANCED_JSP)).build();
		}
		
	//	return Response.ok(new Viewable(SEARCH_ADVANCED_JSP)).build();
		return Response.status(Status.INTERNAL_SERVER_ERROR).build();
	}
	
	private String getCurUsername() {
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}
}
