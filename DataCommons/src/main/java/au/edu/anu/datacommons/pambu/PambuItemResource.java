package au.edu.anu.datacommons.pambu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import au.edu.anu.datacommons.data.solr.SolrManager;
import au.edu.anu.datacommons.data.solr.SolrUtils;
import au.edu.anu.datacommons.search.SolrSearchResult;
import au.edu.anu.datacommons.util.Util;

import com.sun.jersey.api.view.Viewable;

/**
 * PambuItemResource
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
 * 0.1		28/11/2012	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
@Path("pambu/item")
@Component
@Scope("request")
public class PambuItemResource {
	private static final Logger LOGGER = LoggerFactory.getLogger(PambuAdminResource.class);
	
	/**
	 * getPambuItemList
	 *
	 * Retrieve the Item List page for a record in the Pacific Manuscripts Bureau
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		28/11/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param pambuId The pacific manuscripts bureau serial number
	 * @return The page with a list of items for the serial number
	 */
	@GET
	@Path("list/{pambuId}")
	public Response getPambuItemList(@PathParam("pambuId")String pambuId) {
		Map<String, Object> model = new HashMap<String, Object>();
		
		String pambuIdDecoded = Util.decodeUrlEncoded(pambuId);
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.addField("id");
		solrQuery.addField("published.name");
		solrQuery.addField("published.related.hasPart");
		solrQuery.addField("published.serialNum");
		solrQuery.addField("published.holdingType");
		
		StringBuilder queryString = new StringBuilder();
		queryString.append("published.serialNum:\"");
		queryString.append(pambuIdDecoded);
		queryString.append("\"");
		
		LOGGER.debug("Query String: {}", queryString.toString());
		
		solrQuery.setQuery(queryString.toString());
		
		SolrServer solrServer = SolrManager.getInstance().getSolrServer();
		try {
			QueryResponse queryResponse = solrServer.query(solrQuery);
			SolrDocumentList solrDocumentList = queryResponse.getResults();
			LOGGER.info("Number of records found: {}", solrDocumentList.size());
			if (solrDocumentList.size() > 0) {
				SolrDocument doc = solrDocumentList.get(0);
				List<String> hasPart = (ArrayList<String>) doc.get("published.related.hasPart");
				SolrSearchResult results = getItemList(hasPart);
				model.put("document", doc);
				model.put("items", results);
			}
		}
		catch (SolrServerException e) {
			throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
		}
		
		return Response.ok(new Viewable("/pambu/pambuitemlist.jsp", model)).build();
	}
	
	/**
	 * getItemList
	 *
	 * Get the item list values
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		28/11/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param parts A list of items to find
	 * @return
	 * @throws SolrServerException
	 */
	private SolrSearchResult getItemList(List<String> parts) throws SolrServerException {
		// This query may be better handled upgrading from solr 3.6 to 4.0 as solr 4.0 has joins
		
		SolrQuery solrQuery = new SolrQuery();
		
		solrQuery.setQuery("*:*");
		solrQuery.setRows(1000);
		
		solrQuery.addField("id");
		solrQuery.addField("unpublished.name");
		solrQuery.addField("unpublished.briefDesc");
		solrQuery.addField("unpublished.serialNum");

		StringBuilder fq = new StringBuilder();
		for (String id : parts) {
			fq.append("id:\"");
			fq.append(SolrUtils.escapeSpecialCharacters(id));
			fq.append("\" ");
		}
		solrQuery.addFilterQuery(fq.toString());
		SolrServer solrServer = SolrManager.getInstance().getSolrServer();
		QueryResponse queryResponse = solrServer.query(solrQuery);
		SolrDocumentList solrDocumentList = queryResponse.getResults();
		SolrSearchResult solrSearchResult = new SolrSearchResult(solrDocumentList);
		
		LOGGER.debug("Number of Items found: {}", solrSearchResult.getNumFound());
		
		return solrSearchResult;
	}
}
