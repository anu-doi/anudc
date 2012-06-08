package au.edu.anu.datacommons.search;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import au.edu.anu.datacommons.util.Util;
import au.edu.anu.datacommons.xml.solr.SolrResponse;
import au.edu.anu.datacommons.xml.transform.JAXBTransform;

import com.sun.jersey.api.client.ClientResponse;
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
 * </pre>
 *
 */
@Component
@Path("/search/pambu")
public class PambuSearchService {
	private static final Logger LOGGER = LoggerFactory.getLogger(PambuSearchService.class);

	@Resource(name = "solrSearchService")
	ExternalPoster solrSearchService;
	
	/**
	 * doGetPAMBUHTML
	 *
	 * Returns a html response for a get for pambu
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		08/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return The html response
	 */
	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response doGetPAMBUHTML() {
		Response response = null;
		
		response = Response.ok(new Viewable("/pambu/pambusearch.jsp")).build();
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
		String selection = request.getParameter("selection");
		String pmbHolding = request.getParameter("pmbHolding");
		String modifier = request.getParameter("modifier");
		String preferredOrder = request.getParameter("preferredOrder");
		String output = request.getParameter("output");
		String entry = request.getParameter("entry");
		
		SolrQuery solrQuery = new SolrQuery();
		
		if (Util.isNotEmpty(request.getParameter("submit"))) {
			entry = processModifier(modifier, entry, solrQuery);
		}
		else if (Util.isNotEmpty(request.getParameter("clear"))) {
			return Response.ok(new Viewable("/pambu/pambusearch.jsp")).build();
		}
		else if (Util.isNotEmpty(request.getParameter("browseAll"))) {
			//submit = "browseAll";
			selection = "browseAll";
		}
		
		Response response = null;
		Map<String, Object> values = new HashMap<String, Object>();
		
		LOGGER.info("Text to search");
		
		// Set fields to search
		setPAMBUQueryFields(selection, entry, solrQuery);
		
		// Filter out documents and manuscripts
		setPAMBUFilterFields(pmbHolding, solrQuery);
		
		// Sort items
		setPAMBUSortFields(preferredOrder, solrQuery);
		
		//TODO figure out what search logic is?
		
		// Set the values to be returned
		setPAMBUReturnFields(output, solrQuery);
		
		ClientResponse searchResponse = solrSearchService.post(solrQuery.generateMultivaluedMap());
		SolrResponse solrResponse = transformResponse(searchResponse);
		if (solrResponse != null) {
			values.put("resultSet", solrResponse.getResult());
		}
		
		response = Response.ok(new Viewable("/pambu/pambu.jsp", values)).build();
		return response;
	}
	
	/**
	 * transformResponse
	 *
	 * Transforms the xml response into an object response
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		08/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param response The client response to process
	 * @return The object of the response
	 */
	private SolrResponse transformResponse(ClientResponse response) {
		SolrResponse result = null;
		Map<String, Object> values = new HashMap<String, Object>();
		JAXBTransform transform = new JAXBTransform();
		try {
			result = (SolrResponse) transform.unmarshalStream(response.getEntityInputStream(), SolrResponse.class);
			if (result.getResult().getDocs() != null) {
				LOGGER.info("Number of results {}", result.getResult().getDocs().size());
			}
			else {
				LOGGER.info("No results returned");
			}
			values.put("resultSet", result.getResult());
		}
		catch (JAXBException e) {
			LOGGER.error("Exception transforming document", e);
		}
		return result;
	}
	
	private String processModifier(String value, String entry, SolrQuery solrQuery) {
		if ("AND".equals(value)) {
			String[] entries = entry.split(" ");
			for (String filter : entries) {
				solrQuery.addFilterField("published.all", filter);
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
	 * </pre>
	 * 
	 * @param value The value of the search criteria filter
	 * @param entry The value of the entry field in the request
	 * @param solrQuery The SolrQuery to add fields to
	 */
	private void setPAMBUQueryFields(String value, String entry, SolrQuery solrQuery) {
		String[] values = null;
		
		if ("author".equals(value) || "all".equals(value)) {
			solrQuery.addQueryField("published.combinedAuthors", entry);
		}
		if ("title".equals(value) || "all".equals(value)) {
			solrQuery.addQueryField("published.name", entry);
		}
		if ("serial".equals(value) || "all".equals(value)) {
			solrQuery.addQueryField("published.serialNum", entry);
		}
		if ("notes".equals(value) || "all".equals(value)) {
			solrQuery.addQueryField("published.briefDesc", entry);
			solrQuery.addQueryField("published.fullDesc", entry);
		}
	}
	
	/**
	 * setPAMBUReturnFields
	 *
	 * Placeholder
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		08/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param value The value of the return criteria filter
	 * @param solrQuery The SolrQuery to add fields to
	 */
	private void setPAMBUReturnFields(String value, SolrQuery solrQuery) {
		solrQuery.addReturnField("published.combinedAuthors");
		solrQuery.addReturnField("published.name");
		solrQuery.addReturnField("published.combinedDates");
		solrQuery.addReturnField("published.holdingLocation");
		solrQuery.addReturnField("published.numReels");
		solrQuery.addReturnField("published.serialNum");
		solrQuery.addReturnField("published.accessRights");
		solrQuery.addReturnField("published.format");
		
		if ("long".equals(value)) {
			solrQuery.addReturnField("published.briefDesc");
			solrQuery.addReturnField("published.fullDesc");
		}
	}
	
	/**
	 * setPAMBUFilterFields
	 *
	 * Placeholder
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		08/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param value The value of the filter fields criteria filter
	 * @param solrQuery The SolrQuery to add fields to
	 */
	private void setPAMBUFilterFields(String value, SolrQuery solrQuery) {
		solrQuery.addFilterField("location.published", "PAMBU");
		if (value.equals("doc")) {
			solrQuery.addFilterField("published.holdingType", "doc");
		}
		if (value.equals("ms")) {
			solrQuery.addFilterField("published.holdingType", "ms");
		}
		else {
			solrQuery.addFilterField("published.holdingType", "doc,ms");
		}
	}
	
	/**
	 * setPAMBUSortFields
	 *
	 * Placeholder
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		08/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param value The value of the sort order
	 * @param solrQuery The SolrQuery to add fields to
	 */
	private void setPAMBUSortFields(String value, SolrQuery solrQuery) {
		if ("sortVal".equals(value)) {
			solrQuery.setSortOrder("published.sortVal asc");
		}
		if ("author".equals(value)) {
			solrQuery.setSortOrder("published.combinedAuthors asc");
		}
		if ("date".equals(value)) {
			solrQuery.setSortOrder("published.combinedDates asc");
		}
	}
}
