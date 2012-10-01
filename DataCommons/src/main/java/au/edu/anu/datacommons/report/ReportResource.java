package au.edu.anu.datacommons.report;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
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

import com.sun.jersey.api.view.Viewable;

/**
 * ReportResource
 * 
 * Australian National University Data Commons
 * 
 * Resource for reporting purposes
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		27/09/2012	Genevieve Turner (GT)	Initial
 * 0.2		02/10/2012	Genevieve Turner (GT)	MOved reload of reports functionality to ReportGenerator class
 * 0.3		02/10/2012	Genevieve Turner (GT)	Updated to verify the user has permissions to execute a report prior to generating the report
 * </pre>
 *
 */
@Component
@Scope("request")
@Path("/report")
public class ReportResource {
	static final Logger LOGGER = LoggerFactory.getLogger(ReportResource.class);

	@Resource(name = "fedoraObjectServiceImpl")
	private FedoraObjectService fedoraObjectService;
	
	/**
	 * getReportPage
	 *
	 * Get the page to ask questions about the report to generate
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		27/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return
	 */
	@GET
	@Produces(MediaType.TEXT_HTML)
	@PreAuthorize("hasRole('ROLE_ANU_USER')")
	public Response getReportPage(@QueryParam("pid") String pid) {
		Map<String, Object> model = new HashMap<String, Object>();
		if (Util.isNotEmpty(pid)) {
			model.put("pid", pid);
		}
		
		return Response.ok(new Viewable("/reports.jsp", model)).build();
	}
	
	/**
	 * getReport
	 *
	 * Get the actual report
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		27/09/2012	Genevieve Turner(GT)	Initial
	 * 0.3		02/10/2012	Genevieve Turner (GT)	Updated to verify the user has permissions to execute a report prior to generating the report
	 * </pre>
	 * 
	 * @param context The context information
	 * @param request The request information
	 * @return
	 */
	@POST
	@PreAuthorize("hasRole('ROLE_ANU_USER')")
	public Response getReport(@Context ServletContext context, @Context HttpServletRequest request) {
		ReportGenerator report = new ReportGenerator(request, context.getRealPath("/"));
		String pid = request.getParameter("pid");
		if (Util.isNotEmpty(pid)) {
			FedoraObject fedoraObject = fedoraObjectService.getItemByPid(pid);
			fedoraObjectService.hasReportPermission(fedoraObject);
		}
		Response response = null;
		try {
			String format = request.getParameter("format");
			response = report.generateReport(format);
		}
		catch (Exception e) {
			LOGGER.error("Exception processing report", e);
			throw new WebApplicationException(Response.status(500).entity("Exception processing report").build());
		}
		if (response == null) {
			LOGGER.error("Response is null?");
		}
		return response;
	}
	
	/**
	 * reloadReports
	 *
	 * Reload the reports
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		27/09/2012	Genevieve Turner(GT)	Initial
	 * 0.2		02/10/2012	Genevieve Turner (GT)	Moved reload to ReportGenerator class
	 * </pre>
	 * 
	 * @param context The servlet context information
	 * @return The reponse
	 */
	@GET
	@Produces(MediaType.TEXT_HTML)
	@Path("/reload")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public Response reloadReports(@Context ServletContext context) {
		ReportGenerator.reloadReports(context);
		
		UriBuilder uriBuilder = UriBuilder.fromPath("/reload");
		return Response.seeOther(uriBuilder.build()).build();
	}
}
