/**
 * 
 */
package au.edu.anu.datacommons.external;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import au.edu.anu.datacommons.services.DisplayResource;
import au.edu.anu.datacommons.webservice.bindings.FedoraItem;

import com.sun.jersey.api.view.Viewable;

/**
 * REST API endpoint that accepts requests:
 * <ul>
 * <li>to display a page that accepts parameter values for selected metadata provider.
 * <li>to call the selected metadata provider passing on given parameters to it. When the provider returns the fedora
 * item it passes those metadata values to the create new item page so the form can be prepopulated.
 * </ul>
 * 
 * @author Rahul Khanna
 *
 */
@Component
@Scope("request")
@Path("/extmetadata")
public class ExternalMetadataResource {
	private static final Logger LOGGER = LoggerFactory.getLogger(ExternalMetadataResource.class);

	private static final String EXT_METADATA_JSP = "/extdata.jsp";

	/**
	 * List of external metadata providers
	 */
	@Autowired
	List<ExternalMetadataProvider> extMetadataProviders;
	
	@Context
	private HttpServletRequest request;
	
	/**
	 * 
	 * @param uriInfo
	 * @return
	 */
	@GET
	@Produces(MediaType.TEXT_HTML)
	@PreAuthorize("hasRole('ROLE_ANU_USER')")
	public Response getExternalMetadataSources(@Context UriInfo uriInfo) {
		Response resp = null;
		MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();

		String metadataProvider = queryParams.getFirst("provider");
		if (metadataProvider == null || metadataProvider.length() == 0) {
			resp = generateInputFormPage();
		} else {
			LOGGER.info("User {} ({}) requested metadata from {} with params {}", getCurUsername(), getRemoteIp(),
					metadataProvider, queryParams);
			resp = createRetrieveMetadataResponse(resp, queryParams, metadataProvider);
			if (resp == null) {
				resp = generateInputFormPage();
			}
		}
		return resp;
	}

	private Response createRetrieveMetadataResponse(Response resp, MultivaluedMap<String, String> queryParams,
			String extMetadataSource) {
		for (ExternalMetadataProvider provider : extMetadataProviders) {
			if (provider.getClass().getName().equals(extMetadataSource)) {
				try {
					resp = retrieveMetadata(provider, queryParams);
				} catch (ExternalMetadataException e) {
					resp = Response.serverError().entity(e.getMessage()).build();
				}
				break;
			}
		}
		return resp;
	}

	private Response retrieveMetadata(ExternalMetadataProvider provider, MultivaluedMap<String, String> formParams)
			throws ExternalMetadataException {
		Response resp = null;
		FedoraItem retrievedItem = provider.retrieveMetadata(formParams);

		UriBuilder newItemRedirUrl = UriBuilder.fromResource(DisplayResource.class).path(DisplayResource.class,
				"newItemPage");
		for (Entry<String, List<String>> entry : retrievedItem.generateDataMap().entrySet()) {
			if (entry.getValue() != null && !entry.getValue().isEmpty()) {
				for (String iValue : entry.getValue()) {
					if (iValue != null && iValue.length() > 0) {
						newItemRedirUrl = newItemRedirUrl.queryParam(entry.getKey(), iValue);
					}
				}
			}
		}
		resp = Response.seeOther(newItemRedirUrl.build()).build();
		return resp;
	}

	private Response generateInputFormPage() {
		Map<String, Object> model = new HashMap<>();
		model.put("providers", extMetadataProviders);
		Response resp = Response.ok(new Viewable(EXT_METADATA_JSP, model)).build();
		return resp;
	}
	
	/**
	 * Gets the username of the currently logged-in user.
	 * 
	 * @return Username as String
	 */
	private String getCurUsername() {
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}

	/**
	 * Gets the IP address of the logged-in user.
	 * @return
	 */
	private String getRemoteIp() {
		return request.getRemoteAddr();
	}

}
