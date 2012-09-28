package au.edu.anu.datacommons.report;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

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

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;

import au.edu.anu.datacommons.util.ExtensionFileFilter;
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
 * </pre>
 *
 */
@Path("/report")
public class ReportResource {
	static final Logger LOGGER = LoggerFactory.getLogger(ReportResource.class);
	private static final String JASPER_UNCOMPILED_EXTENSION = "jrxml";
	private static final String JASPER_COMPILED_EXTENSION = "jasper";
	
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
	 * </pre>
	 * 
	 * @param context
	 * @param request
	 * @return
	 */
	@POST
	@PreAuthorize("hasRole('ROLE_ANU_USER')")
	public Response getReport(@Context ServletContext context, @Context HttpServletRequest request) {
		ReportGenerator report = new ReportGenerator(request, context.getRealPath("/"));
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
		//TODO filter out with having administrative permissions
		
		String reportPath = context.getRealPath("/WEB-INF/reports");
		File file = new File(reportPath);
		if (!file.exists()) {
			LOGGER.error("Report path does not exist: {}", file.getAbsolutePath());
			throw new WebApplicationException(Response.status(500).entity("Report path does not exist").build());
		}
		File[] files = file.listFiles(new ExtensionFileFilter(JASPER_UNCOMPILED_EXTENSION));
		for (File jrxmlFile : files) {
			String outputFilename = String.format("%s.%s", stripExtension(jrxmlFile.getAbsolutePath()), JASPER_COMPILED_EXTENSION);
			LOGGER.info(outputFilename);
			File outputFile = new File(outputFilename);
			try {
				InputStream is = new FileInputStream(jrxmlFile);
				OutputStream os = new FileOutputStream(outputFile);
				try {
					JasperCompileManager.compileReportToStream(is, os);
				}
				catch(JRException e) {
					LOGGER.error("Exception compiling report: {}", jrxmlFile.getName(), e);
				}
				is.close();
				os.close();
			}
			catch (IOException e) {
				throw new WebApplicationException(Response.status(500).entity("Error reloading reports").build());
			}
		}
		UriBuilder uriBuilder = UriBuilder.fromPath("/reload");
		return Response.seeOther(uriBuilder.build()).build();
	}
	
	/**
	 * stripExtension
	 *
	 * Strip the extension from a file name
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		27/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param filename The filename to strip
	 * @return The striped filename
	 */
	private String stripExtension(String filename) {
		int index = filename.lastIndexOf(".");
		if (index == -1) {
			return filename;
		}
		return filename.substring(0, index);
	}
}
