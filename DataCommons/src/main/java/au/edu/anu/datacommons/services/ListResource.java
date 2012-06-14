package au.edu.anu.datacommons.services;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import au.edu.anu.datacommons.data.solr.SolrManager;
import au.edu.anu.datacommons.properties.GlobalProps;
import au.edu.anu.datacommons.search.ExternalPoster;
import au.edu.anu.datacommons.search.SolrSearchResult;
import au.edu.anu.datacommons.search.SparqlQuery;
import au.edu.anu.datacommons.util.Util;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.view.Viewable;

/**
 * ListResource
 * 
 * Australian National University Data Commons
 * 
 * Gets lists of items
 * 
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		04/05/2012	Genevieve Turner (GT)	Initial
 * 0.2		14/05/2012	Genevieve Turner (GT)	Updated to include a JSON search for items
 * 0.3		08/06/2012	Genevieve Turner (GT)	Updated for changes to post
 * 0.4		14/06/2012	Genevieve Turner (GT)	Updated for new templates to search solr
 * </pre>
 * 
 */
@Component
@Path("list")
public class ListResource {
	static final Logger LOGGER = LoggerFactory.getLogger(ListResource.class);
	
	@Resource(name="riSearchJSONService")
	ExternalPoster riSearchJSONService;
	
	/**
	 * getTemplates
	 * 
	 * Returns a list of templates to display create new records
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		04/05/2012	Genevieve Turner (GT)	Initial
	 * 0.3		08/06/2012	Genevieve Turner (GT)	Updated for changes to post
	 * </pre>
	 * 
	 * Returns a list of templates 
	 */
	@GET
	@PreAuthorize("hasRole('ROLE_ANU_USER')")
	@Produces(MediaType.TEXT_HTML)
	@Path("template")
	public Response getTemplates() {
		Response response = null;
		SolrServer solrServer = SolrManager.getInstance().getSolrServer();
		
		SolrQuery solrQuery = new SolrQuery();
		
		solrQuery.setQuery("*:*");
		solrQuery.addFilterQuery("template.type:template");
		
		solrQuery.addField("id");
		
		String returnFields = GlobalProps.getProperty(GlobalProps.PROP_SEARCH_SOLR_RETURNFIELDS);
		String[] splitReturnFields = returnFields.split(",");
		for (String field : splitReturnFields) {
			solrQuery.addField("template." + field);
		}

		Map<String, Object> model = new HashMap<String, Object>();

		try {
			QueryResponse queryResponse = solrServer.query(solrQuery);

			SolrDocumentList resultList = queryResponse.getResults();
			LOGGER.info("Number of results: {}",resultList.getNumFound());
			SolrSearchResult solrSearchResult = new SolrSearchResult(resultList);
			model.put("resultSet", solrSearchResult);
			response = Response.ok(new Viewable("/listtemplate.jsp", model)).build();
		}
		catch(SolrServerException e) {
			LOGGER.error("Exception querying field");
			Response.status(Status.INTERNAL_SERVER_ERROR);
		}
		
		return response;
	}

	/**
	 * getItems
	 * 
	 * Returns a list of items with the given title name part and type of object
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.2		14/05/2012	Genevieve Turner (GT)	Initial
	 * 0.3		08/06/2012	Genevieve Turner (GT)	Updated for changes to post
	 * </pre>
	 * 
	 * @param title Part of the title to search for
	 * @param type The type of item to search in
	 * @return The list of items
	 */
	@GET
	@PreAuthorize("hasRole('ROLE_ANU_USER')")
	@Produces(MediaType.APPLICATION_JSON)
	@Path("items")
	public Response getItems(@QueryParam("title") String title, @QueryParam("type") String type) {
		Response response = null;
		if (Util.isNotEmpty(title)) {
			SparqlQuery sparqlQuery = new SparqlQuery();
	
			sparqlQuery.addVar("?item");
			sparqlQuery.addVar("?title");
			sparqlQuery.addTriple("?item", "<dc:title>", "?title", false);
			sparqlQuery.addTriple("?item", "<dc:type>", "?type", false);
			String titleFilterString = "regex(str(?title), '" + title + "', 'i')";
			sparqlQuery.addFilter(titleFilterString, "");
			String typeFilterString = "regex(str(?type), '" + type +"', 'i')";
			sparqlQuery.addFilter(typeFilterString.toString(), "&&");
			
			ClientResponse riSearchResponse = riSearchJSONService.post("query", sparqlQuery.generateQuery());
			LOGGER.info("Return statis is: {}", riSearchResponse.getStatus());
			String jsonArray = riSearchResponse.getEntity(String.class);
			LOGGER.info("JSON Response: {}", jsonArray);
			response = Response.ok(jsonArray, MediaType.APPLICATION_JSON).build();
		}
		else {
			response = Response.ok("", MediaType.APPLICATION_JSON).build();
		}
		return response;
	}
}
