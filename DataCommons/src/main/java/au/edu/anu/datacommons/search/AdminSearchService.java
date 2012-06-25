package au.edu.anu.datacommons.search;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import au.edu.anu.datacommons.util.Util;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.view.Viewable;

/**
 * AdminSearchService
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
 * 0.1		13/06/2012	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
@Path("/search/admin")
@Component
public class AdminSearchService {
	private static final Logger LOGGER = LoggerFactory.getLogger(AdminSearchService.class);

	@Resource(name = "riSearchService")
	ExternalPoster riSearchService;

	@Resource(name = "gsearchUpdateService")
	ExternalPoster gsearchUpdateService;

	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response doGetAsHTML(@QueryParam("q") String q) {
		Response response = null;
		
		Map<String, Object> model = getResultSet(q);
		
		response = Response.ok(new Viewable("/adminsearch.jsp", model)).build();
		return response;
	}
	
	@POST
	@Produces(MediaType.TEXT_HTML)
	public Response goPostAsHTML(@QueryParam("q") String q, @Context HttpServletRequest request) {
		StringBuffer message = new StringBuffer();
		String[] itemList = request.getParameterValues("itemList");
		if (Util.isNotEmpty(request.getParameter("updateIndex"))) {
			message.append(updateIndex(itemList));
		}
		Response response = null;
		Map<String, Object> model = getResultSet(q);
		model.put("message", message.toString());
		response = Response.ok(new Viewable("/adminsearch.jsp", model)).build();
		return response;
	}
	
	private String updateIndex(String[] itemList) {
		StringBuffer message = new StringBuffer();
		if (gsearchUpdateService != null) {
			for (String item : itemList) {
				ClientResponse clientResponse = gsearchUpdateService.post("value", item);
				message.append(item);
				if (clientResponse.getStatus() == 200) {
					message.append(" - update successful<br />");
				}
				else {
					message.append(" - update failed<br />");
				}
			}
		}
		return message.toString();
	}
	
	private Map<String, Object> getResultSet(String q) {
		Map<String, Object> model = new HashMap<String, Object>();
		if (Util.isNotEmpty(q)) {
			if (riSearchService == null) {
				LOGGER.error("riSearchService is null");
			}
			else {
				String query = new SparqlQuery(q).generateQuery();
				ClientResponse riSearchResponse = riSearchService.post("query", query);
				try {
					String responseString = riSearchResponse.getEntity(String.class);
					LOGGER.debug("riSearchResponse: {}", responseString);
					// For some reason XPath doesn't work properly if you directly get the document from the stream
					DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
					Document resultsXmlDoc = factory.newDocumentBuilder().parse(new InputSource(new StringReader(responseString)));
					SparqlResultSet resultSet = new SparqlResultSet(resultsXmlDoc);

					model.put("resultSet", resultSet);
				}
				catch (SAXException e)
				{
					LOGGER.error("Error creating document", e);
				}
				catch (ParserConfigurationException e)
				{
					LOGGER.error("Error creating document", e);
				}
				catch (IOException e)
				{
					LOGGER.error("Error creating document", e);
				}
			}
		}
		return model;
	}
}
