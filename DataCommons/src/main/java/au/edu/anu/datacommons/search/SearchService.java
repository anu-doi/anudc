package au.edu.anu.datacommons.search;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import au.edu.anu.datacommons.data.solr.SolrManager;
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
 * </pre>
 * 
 */
@Component
@Path("/search")
public class SearchService
{
	private static final Logger LOGGER = LoggerFactory.getLogger(SearchService.class);
	private static final String SEARCH_JSP = "/search.jsp";
	
	// TODO Once determined how object info such as published flag, group etc. are stored use this parameter to filter out results.
	//	@QueryParam("filter") private String filter;

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
			QueryResponse queryResponse = executeQuery(q, offset, limit, filter);
			SolrDocumentList resultList = queryResponse.getResults();
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
				QueryResponse queryResponse = executeQuery(q, offset, limit, filter);
				
				SolrDocumentList resultList = queryResponse.getResults();
				LOGGER.info("Number of results: {}",resultList.getNumFound());
				SolrSearchResult solrSearchResult = new SolrSearchResult(resultList);
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
			QueryResponse queryResponse = executeQuery(q, offset, limit, filter);
			SolrDocumentList resultList = queryResponse.getResults();
			response = Response.ok(resultList).build();
		}
		catch (SolrServerException e) {
			LOGGER.error("Exception retrieving results", e);
			response = Response.status(Status.BAD_REQUEST).build();
		}
		return response;
	}
	
	
	/**
	 * executeQuery
	 *
	 * Executes a query on the solr search engine
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.4		13/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param q	Search terms
	 * @param offset Offset amount (i.e. the value to start the search from)
	 * @param limit How many rows to return
	 * @param filter Extra filters
	 * @return The response to the query
	 * @throws SolrServerException
	 */
	private QueryResponse executeQuery (String q, int offset, int limit, String filter) throws SolrServerException {
		Object[] list = {q, offset, limit};
		LOGGER.info("Query Term: {}, Offset: {}, Limit: {}", list);
		
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery("unpublished.all:(" + q + ")");
		
		solrQuery.addField("id");
		solrQuery.addField("unpublished.name");
		solrQuery.addField("unpublished.briefDesc");
		
		solrQuery.setStart(offset);
		solrQuery.setRows(limit);
		SolrServer solrServer = SolrManager.getInstance().getSolrServer();
		QueryResponse queryResponse = solrServer.query(solrQuery);
		return queryResponse;
	}
}
