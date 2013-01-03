package au.edu.anu.datacommons.services;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.data.solr.SolrManager;
import au.edu.anu.datacommons.exception.DataCommonsException;
import au.edu.anu.datacommons.search.SolrSearchResult;

import com.sun.jersey.api.view.Viewable;

/**
 * AdminResource
 * 
 * Australian National University Data Commons
 * 
 * Administrative functions
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		14/08/2012	Genevieve Turner (GT)	Initial
 * 0.2		02/01/2012	Genevieve Turner (GT)	Updated to reflect changes in error handling
 * </pre>
 *
 */
@Path("/admin")
public class AdminResource {
	private static final Logger LOGGER = LoggerFactory.getLogger(AdminResource.class);
	
	/**
	 * listAllANUPublished
	 *
	 * Lists all the data commons records published to the ANU
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		14/08/2012	Genevieve Turner(GT)	Initial
	 * 0.2		02/01/2012	Genevieve Turner (GT)	Updated to reflect changes in error handling
	 * </pre>
	 * 
	 * @return
	 */
	@GET
	@Path("/anupublished")
	@Produces(MediaType.TEXT_HTML)
	public Response listAllANUPublished() {
		int numResults = 1000;
		
		Response response = null;
		SolrServer solrServer = SolrManager.getInstance().getSolrServer();
		
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery("published.all:*");
		solrQuery.addField("id");
		solrQuery.addField("published.name");
		solrQuery.addFilterQuery("location.published:ANU");
		solrQuery.setRows(numResults);
		
		try {
			SolrDocumentList documentList = new SolrDocumentList();
			QueryResponse queryResponse = solrServer.query(solrQuery);
			SolrDocumentList resultList = queryResponse.getResults();
			long numFound = resultList.getNumFound();
			documentList.addAll(resultList);
			for (int i = numResults; i < numFound; i = i + numResults) {
				solrQuery.setStart(i);
				queryResponse = solrServer.query(solrQuery);
				resultList = queryResponse.getResults();
				documentList.addAll(resultList);
			}
			
			SolrSearchResult solrSearchResult = new SolrSearchResult(documentList);
			Map<String, Object> model = new HashMap<String, Object>();
			model.put("resultSet", solrSearchResult);
			response = Response.ok(new Viewable("/sitemap.jsp", model)).build();
		}
		catch (SolrServerException e) {
			LOGGER.error("Error retrieving results for page", e);
			throw new DataCommonsException(502, "Error retrieving results for page");
		}
		
		return response;
	}
}
