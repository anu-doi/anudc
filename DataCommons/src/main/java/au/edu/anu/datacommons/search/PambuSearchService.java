package au.edu.anu.datacommons.search;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import au.edu.anu.datacommons.data.solr.SolrManager;
import au.edu.anu.datacommons.util.Util;

import com.sun.jersey.api.view.Viewable;

/**
 * PambuSearchService
 * 
 * Australian National University Data Commons
 * 
 * Placeholder
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		08/06/2012	Genevieve Turner (GT)	Initial
 * 0.2		13/06/2012	Genevieve Turner (GT)	Updated to use solrj classes and added and/or query functionality
 * 0.3		10/08/2012	Genevieve Turner (GT)	Updated to provide more get options.
 * </pre>
 *
 */
@Component
@Path("/search/pambu")
public class PambuSearchService {
	private static final Logger LOGGER = LoggerFactory.getLogger(PambuSearchService.class);
	
	/**
	 * doGetPAMBUHTML
	 *
	 * Returns a html response for a get for pambu
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		08/06/2012	Genevieve Turner(GT)	Initial
	 * 0.3		10/08/2012	Genevieve Turner (GT)	Updated to provide more get options.
	 * </pre>
	 * 
	 * @return The html response
	 */
	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response doGetPAMBUHTML(@Context HttpServletRequest request) {
		Response response = null;

		if (Util.isNotEmpty(request.getParameter("submit"))) {
			response = doPostPAMBUHTML(request);
		}
		else if (Util.isNotEmpty(request.getParameter("browseAll"))) {
			response = doPostPAMBUHTML(request);
		}
		else {
			response = Response.ok(new Viewable("/pambu/pambusearch.jsp")).build();
		}
		
		return response;
	}
	
	/**
	 * doPostPAMBUHTML
	 *
	 * Returns a html response for a get for pambu
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		08/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param request Request information
	 * @return The html response
	 */
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_HTML)
	public Response doPostPAMBUHTML(@Context HttpServletRequest request) {
		int numRows = 1000;
		String selection = request.getParameter("selection");
		String pmbHolding = request.getParameter("pmbHolding");
		String modifier = request.getParameter("modifier");
		String preferredOrder = request.getParameter("preferredOrder");
		String output = request.getParameter("output");
		String entry = request.getParameter("entry");
		String page = request.getParameter("page");
		
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setRows(numRows);
		if (Util.isNotEmpty(page)) {
			int pageNum = Integer.valueOf(page);
			int start = (pageNum - 1) * numRows;
			solrQuery.setStart(start);
		}
		
		
		if (Util.isNotEmpty(request.getParameter("submit"))) {
			entry = processModifier(modifier, entry, solrQuery);
		}
		else if (Util.isNotEmpty(request.getParameter("clear"))) {
			return Response.ok(new Viewable("/pambu/pambusearch.jsp")).build();
		}
		else if (Util.isNotEmpty(request.getParameter("browseAll"))) {
			selection = "browseAll";
		}
		
		Response response = null;
		Map<String, Object> model = new HashMap<String, Object>();
		
		// Set fields to search
		setPAMBUQueryFields(selection, entry, solrQuery);
		
		// Filter out documents and manuscripts
		setPAMBUFilterFields(pmbHolding, solrQuery);
		
		// Sort items
		setPAMBUSortFields(preferredOrder, solrQuery);
		
		// Set the values to be returned
		setPAMBUReturnFields(output, solrQuery);
		
		SolrServer solrServer = SolrManager.getInstance().getSolrServer();
		try {
			QueryResponse queryResponse = solrServer.query(solrQuery);
			SolrDocumentList resultList = queryResponse.getResults();
			LOGGER.debug("Number of results: {}",resultList.getNumFound());
			
			SolrSearchResult solrSearchResult = new SolrSearchResult(resultList);
			model.put("resultSet", solrSearchResult);
		}
		catch (SolrServerException e) {
			LOGGER.error("Exception querying solr", e);
		}
		response = Response.ok(new Viewable("/pambu/pambu.jsp", model)).build();
		return response;
	}
	
	/**
	 * processModifier
	 *
	 * Adds a filter to make sure that the field contains all fields when AND has been selected.
	 * It also ensures that all fields selected are searched for
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.2		12/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param value The value of the search criteria filter
	 * @param entry The value of the entry field in the request
	 * @param solrQuery The solrQuery to execute
	 * @return A string to add to the query
	 */
	private String processModifier(String value, String entry, SolrQuery solrQuery) {
		if ("AND".equals(value)) {
			String[] entries = entry.split(" ");
			for (String filter : entries) {
				solrQuery.addFilterQuery("published.all:" + filter);
			}
		}
		return "(" + entry + ")";
	}
	
	/**
	 * setPAMBUQueryFields
	 *
	 * Sets the query fields for PAMBU
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		08/06/2012	Genevieve Turner(GT)	Initial
	 * 0.2		13/06/2012	Genevieve Turner (GT)	Updated to use solrj classes and added and/or query functionality
	 * </pre>
	 * 
	 * @param value The value of the search criteria filter
	 * @param entry The value of the entry field in the request
	 * @param solrQuery The SolrQuery to add fields to
	 */
	private void setPAMBUQueryFields(String value, String entry, SolrQuery solrQuery) {
		StringBuffer query = new StringBuffer();
		if ("author".equals(value) || "all".equals(value)) {
			addQueryField(query, "published.combinedAuthors", entry);
		}
		if ("title".equals(value) || "all".equals(value)) {
			addQueryField(query, "published.name", entry);
		}
		if ("serial".equals(value) || "all".equals(value)) {
			addQueryField(query, "published.serialNum", entry);
		}
		if ("notes".equals(value) || "all".equals(value)) {
			addQueryField(query, "published.briefDesc", entry);
			addQueryField(query, "published.fullDesc", entry);
		}
		if ("browseAll".equals(value)) {
			addQueryField(query, "*","*");
		}
		solrQuery.setQuery(query.toString());
	}
	
	/**
	 * addQueryField
	 *
	 * Add the field to the query fields string
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		13/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param query
	 * @param field
	 * @param value
	 */
	private void addQueryField(StringBuffer query, String field, String value) {
		if (!query.toString().equals("")) {
			query.append(" ");
		}
		query.append(field);
		query.append(":");
		query.append(value);
	}
	
	/**
	 * setPAMBUReturnFields
	 *
	 * Sets the fields to be returned for the PAMBU search
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		08/06/2012	Genevieve Turner(GT)	Initial
	 * 0.2		13/06/2012	Genevieve Turner (GT)	Updated to use solrj classes and added and/or query functionality
	 * </pre>
	 * 
	 * @param value The value of the return criteria filter
	 * @param solrQuery The SolrQuery to add fields to
	 */
	private void setPAMBUReturnFields(String value, SolrQuery solrQuery) {
		solrQuery.addField("published.combinedAuthors");
		solrQuery.addField("published.name");
		solrQuery.addField("published.combinedDates.formatted");
		solrQuery.addField("published.holdingLocation");
		solrQuery.addField("published.numReels");
		solrQuery.addField("published.serialNum");
		solrQuery.addField("published.accessRights");
		solrQuery.addField("published.format");
		
		if ("long".equals(value)) {
			solrQuery.addField("published.briefDesc");
			solrQuery.addField("published.fullDesc");
		}
	}
	
	/**
	 * setPAMBUFilterFields
	 *
	 * Set the filter fields for the PAMBU search
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		08/06/2012	Genevieve Turner(GT)	Initial
	 * 0.2		13/06/2012	Genevieve Turner (GT)	Updated to use solrj classes and added and/or query functionality
	 * </pre>
	 * 
	 * @param value The value of the filter fields criteria filter
	 * @param solrQuery The SolrQuery to add fields to
	 */
	private void setPAMBUFilterFields(String value, SolrQuery solrQuery) {
		solrQuery.addFilterQuery("location.published:PAMBU");
		if (value.equals("doc")) {
			solrQuery.addFilterQuery("published.holdingType:doc");
		}
		if (value.equals("ms")) {
			solrQuery.addFilterQuery("published.holdingType:ms");
		}
		else {
			solrQuery.addFilterQuery("published.holdingType:doc,ms");
		}
	}
	
	/**
	 * setPAMBUSortFields
	 *
	 * Set the sort order for the PAMBU search
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		08/06/2012	Genevieve Turner(GT)	Initial
	 * 0.2		13/06/2012	Genevieve Turner (GT)	Updated to use solrj classes and added and/or query functionality
	 * </pre>
	 * 
	 * @param value The value of the sort order
	 * @param solrQuery The SolrQuery to add fields to
	 */
	private void setPAMBUSortFields(String value, SolrQuery solrQuery) {
		if ("sortVal".equals(value)) {
			solrQuery.addSortField("published.sortVal", ORDER.asc);
		}
		if ("author".equals(value)) {
			solrQuery.addSortField("published.combinedAuthors", ORDER.asc);
		}
		if ("date".equals(value)) {
			solrQuery.addSortField("published.combinedDates", ORDER.asc);
		}
	}
}
