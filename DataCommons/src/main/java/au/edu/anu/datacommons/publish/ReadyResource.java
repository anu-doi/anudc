package au.edu.anu.datacommons.publish;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import au.edu.anu.datacommons.data.db.model.FedoraObject;
import au.edu.anu.datacommons.security.service.FedoraObjectService;
import au.edu.anu.datacommons.util.Util;
import au.edu.anu.datacommons.xml.sparql.Result;

import com.sun.jersey.api.view.Viewable;

/**
 * ReadyResource
 * 
 * Australian National University Data Commons
 * 
 * Resources for indicating ready for review, publish or rejection
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		25/07/2012	Genevieve Turner (GT)	Initial
 * 0.2		01/08/2012	Genevieve Turner (GT)	Updated the return values for getting lists of objects in a particular status
 * 0.3		12/10/2012	Genevieve Turner (GT)	Updated to add a title to the return values
 * </pre>
 *
 */
@Path("ready")
@Component
@Scope("request")
public class ReadyResource {
	static final Logger LOGGER = LoggerFactory.getLogger(ReadyResource.class);

	@Resource(name="fedoraObjectServiceImpl")
	private FedoraObjectService fedoraObjectService;

	/**
	 * readyForReview
	 *
	 * Sets the item as ready for review
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		25/07/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param pid The item to perform actions on
	 * @param layout The layout to use with display (i.e. the xsl stylesheet)
	 * @param tmplt The template that determines the fields on the screen
	 * @return The html response
	 */
	@POST
	@Path("review/{id}")
	@Produces(MediaType.TEXT_HTML)
	@PreAuthorize("hasRole('ROLE_ANU_USER')")
	public Response readyForReview(@PathParam("id") String pid, @QueryParam("layout") String layout, @QueryParam("tmplt") String tmplt) {
		FedoraObject fedoraObject = fedoraObjectService.getItemByPid(pid);
		fedoraObjectService.setReadyForReview(fedoraObject);
		
		return buildDisplayResponse(pid, layout, tmplt);
	}
	
	/**
	 * rejectReview
	 *
	 * Rejects the item for publishing
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		25/07/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param pid The item to perform actions on
	 * @param layout The layout to use with display (i.e. the xsl stylesheet)
	 * @param tmplt The template that determines the fields on the screen
	 * @param request The http request
	 * @return The html response
	 */
	@POST
	@Path("reject/{id}")
	@Produces(MediaType.TEXT_HTML)
	@PreAuthorize("hasRole('ROLE_ANU_USER')")
	public Response rejectReview(@PathParam("id") String pid, @QueryParam("layout") String layout, @QueryParam("tmplt") String tmplt, @Context HttpServletRequest request) {
		FedoraObject fedoraObject = fedoraObjectService.getItemByPid(pid);
		
		Map<String, List<String>> form = Util.convertArrayValueToList(request.getParameterMap());
		List<String> reasons = form.get("rejectReason");
		
		fedoraObjectService.setRejected(fedoraObject, reasons);
		
		return buildDisplayResponse(pid, layout, tmplt);
	}
	
	/**
	 * readyForPublish
	 *
	 * Sets the item as ready for publish
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		25/07/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param pid The item to perform actions on
	 * @param layout The layout to use with display (i.e. the xsl stylesheet)
	 * @param tmplt The template that determines the fields on the screen
	 * @return The html response
	 */
	@POST
	@Path("publish/{id}")
	@Produces(MediaType.TEXT_HTML)
	@PreAuthorize("hasRole('ROLE_ANU_USER')")
	public Response readyForPublish(@PathParam("id") String pid, @QueryParam("layout") String layout, @QueryParam("tmplt") String tmplt) {
		FedoraObject fedoraObject = fedoraObjectService.getItemByPid(pid);
		fedoraObjectService.setReadyForPublish(fedoraObject);
		
		return buildDisplayResponse(pid, layout, tmplt);
	}
	
	/**
	 * listReadyForReview
	 *
	 * Gets a list of objects that are ready for review
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		25/07/2012	Genevieve Turner(GT)	Initial
	 * 0.2		01/08/2012	Genevieve Turner (GT)	Updated the return values for getting lists of objects in a particular status
	 * 0.3		12/10/2012	Genevieve Turner (GT)	Updated to add a title to the return values
	 * 0.4		15/10/2012	Genevieve Turner (GT)	Added title to return fields
	 * </pre>
	 * 
	 * @return The html response
	 */
	@GET
	@Path("list/review")
	@Produces(MediaType.TEXT_HTML)
	@PreAuthorize("hasRole('ROLE_ANU_USER')")
	public Response listReadyForReview() {
		List<FedoraObject> reviewReadyList = fedoraObjectService.getReadyForReview();
		List<Result> results = fedoraObjectService.getListInformation(reviewReadyList);
		
		Map<String, Object> values = new HashMap<String, Object>();
		values.put("itemList", reviewReadyList);
		values.put("resultList", results);
		values.put("title", "Ready for Review");
		
		Viewable viewable = new Viewable("/status_lists.jsp", values);
		return Response.ok(viewable).build();
	}
	
	/**
	 * listRejected
	 *
	 * Gets a list of objects that have been rejected
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		25/07/2012	Genevieve Turner(GT)	Initial
	 * 0.2		01/08/2012	Genevieve Turner (GT)	Updated the return values for getting lists of objects in a particular status
	 * 0.3		12/10/2012	Genevieve Turner (GT)	Updated to add a title to the return values
	 * 0.4		15/10/2012	Genevieve Turner (GT)	Added title to return fields
	 * </pre>
	 * 
	 * @return The html response
	 */
	@GET
	@Path("list/rejected")
	@Produces(MediaType.TEXT_HTML)
	@PreAuthorize("hasRole('ROLE_ANU_USER')")
	public Response listRejected() {
		List<FedoraObject> rejectedList = fedoraObjectService.getRejected();
		List<Result> results = fedoraObjectService.getListInformation(rejectedList);
		
		Map<String, Object> values = new HashMap<String, Object>();
		values.put("itemList", rejectedList);
		values.put("resultList", results);
		values.put("title", "Rejected");
		
		Viewable viewable = new Viewable("/status_lists.jsp", values);
		return Response.ok(viewable).build();
	}
	
	/**
	 * listReadyForPublish
	 *
	 * Gets a list of objects that are ready for publishing
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		25/07/2012	Genevieve Turner(GT)	Initial
	 * 0.2		01/08/2012	Genevieve Turner (GT)	Updated the return values for getting lists of objects in a particular status
	 * 0.3		12/10/2012	Genevieve Turner (GT)	Updated to add a title to the return values
	 * 0.4		15/10/2012	Genevieve Turner (GT)	Added title to return fields
	 * </pre>
	 * 
	 * @return The html response
	 */
	@GET
	@Path("list/publish")
	@Produces(MediaType.TEXT_HTML)
	@PreAuthorize("hasRole('ROLE_ANU_USER')")
	public Response listReadyForPublish() {
		List<FedoraObject> publishReadyList = fedoraObjectService.getReadyForPublish();
		List<Result> results = fedoraObjectService.getListInformation(publishReadyList);
		
		Map<String, Object> values = new HashMap<String, Object>();
		values.put("itemList", publishReadyList);
		values.put("resultList", results);
		values.put("title", "Ready for Publish");
		
		Viewable viewable = new Viewable("/status_lists.jsp", values);
		return Response.ok(viewable).build();
	}
	
	/**
	 * buildDisplayResponse
	 *
	 * Placeholder
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		25/07/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param pid The item to perform actions on
	 * @param layout The layout to use with display (i.e. the xsl stylesheet)
	 * @param tmplt The template that determines the fields on the screen
	 * @return The html response
	 */
	private Response buildDisplayResponse(String pid, String layout, String tmplt) {
		UriBuilder uriBuilder = UriBuilder.fromPath("/display").path(pid).queryParam("layout", layout);
		if (Util.isNotEmpty(tmplt)) {
			uriBuilder = uriBuilder.queryParam("tmplt", tmplt);
		}
		LOGGER.info("Address: {}", uriBuilder.build().toString());
		return Response.seeOther(uriBuilder.build()).build();
	}
}
