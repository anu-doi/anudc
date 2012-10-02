package au.edu.anu.datacommons.report;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.data.db.PersistenceManager;
import au.edu.anu.datacommons.data.db.dao.GenericDAO;
import au.edu.anu.datacommons.data.db.dao.GenericDAOImpl;
import au.edu.anu.datacommons.data.db.model.Report;
import au.edu.anu.datacommons.data.db.model.ReportParam;
import au.edu.anu.datacommons.data.solr.SolrManager;
import au.edu.anu.datacommons.data.solr.SolrUtils;
import au.edu.anu.datacommons.util.ExtensionFileFilter;
import au.edu.anu.datacommons.util.Util;

/**
 * ReportGenerator
 * 
 * Australian National University Data Commons
 * 
 * Generates reports with Jasper reports
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		27/09/2012	Genevieve Turner (GT)	Initial
 * 0.2		02/10/2012	Genevieve Turner (GT)	Moved the recompile reports functionality to this class
 * 0.3		03/10/2012	Genevieve Turner (GT)	Added the retrieval of the reports object name
 * </pre>
 *
 */
public class ReportGenerator {
	static final Logger LOGGER = LoggerFactory.getLogger(ReportGenerator.class);
	
	private static final String REPORT_PATH = "/WEB-INF/reports/";
	private static final String JASPER_UNCOMPILED_EXTENSION = "jrxml";
	private static final String JASPER_COMPILED_EXTENSION = "jasper";
	
	private String filePath_;
	private Map<String, Object> params_;
	
	/**
	 * Constructor
	 * 
	 * Sets parameters and the report file path
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		27/09/2012	Genevieve Turner(GT)	Initial
	 * 0.3		03/10/2012	Genevieve Turner (GT)	Added the retrieval of the reports object name
	 * </pre>
	 * 
	 * @param request the http request information
	 * @param serverPath the path for the server
	 */
	public ReportGenerator(HttpServletRequest request, String serverPath) {
		String reportParam = request.getParameter("report");
		Long reportId = Long.valueOf(reportParam);
		filePath_ = serverPath + REPORT_PATH + "report_tmplt.jasper";
		
		GenericDAO<Report, Long> reportDAO = new GenericDAOImpl<Report, Long>(Report.class);
		Report report = reportDAO.getSingleById(reportId);
		
		params_ = new HashMap<String, Object>();
		params_.put("baseURL", serverPath + REPORT_PATH);
		if (Util.isNotEmpty(report.getSubReport())) {
			params_.put("sub_rpt", report.getSubReport());
		}
		for (ReportParam rptParam : report.getReportParams()) {
			if ("name".equals(rptParam.getParamName())) {
				String pid = request.getParameter("pid");
				if (Util.isNotEmpty(pid)) {
					String name = getReportName(pid);
					if (Util.isNotEmpty(name)) {
						params_.put("name", name);
					}
				}
			}
			else if (Util.isNotEmpty(rptParam.getRequestParam())) {
				String value = request.getParameter(rptParam.getRequestParam());
				params_.put(rptParam.getParamName(), value);
			}
			else {
				params_.put(rptParam.getParamName(), rptParam.getDefaultValue());
			}
		}
	}
	
	/**
	 * generateReport
	 *
	 * Generate the report
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		27/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param format the report format
	 * @return the reponse with the generate report
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws JRException
	 * @throws IOException
	 */
	public Response generateReport(String format)
			throws ClassNotFoundException, SQLException, JRException, IOException {
		Response response = null;
		if ("pdf".equals(format)) {
			response = generatePDF();
		}
		else if ("html".equals(format)) {
			response = generateHTML();
		}
		
		return response;
	}
	
	/**
	 * generatePDF
	 *
	 * Generates a pdf report
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		27/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return a response that contains a pdf report
	 * @throws JRException
	 * @throws SQLException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private Response generatePDF() throws JRException, SQLException, IOException, ClassNotFoundException {
		InputStream inputStream = new FileInputStream(new File(filePath_));
		
		byte[] bytes = JasperRunManager.runReportToPdf(inputStream, params_, getConnection());
		return Response.ok(bytes).type("application/pdf").build();
	}
	
	/**
	 * generateHTML
	 *
	 * Generates a html report
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		27/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return a response that contains a html report
	 * @throws JRException
	 * @throws SQLException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private Response generateHTML() throws JRException, SQLException, IOException, ClassNotFoundException {
		InputStream inputStream = new FileInputStream(new File(filePath_));
		Writer writer = new StringWriter();
		JasperPrint jasperPrint = JasperFillManager.fillReport(inputStream, params_, getConnection());
		
		JRHtmlExporter htmlExporter = new JRHtmlExporter();
		
		Map imagesMap = new HashMap();
		
		htmlExporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
		htmlExporter.setParameter(JRExporterParameter.OUTPUT_WRITER, writer);
		htmlExporter.setParameter(JRHtmlExporterParameter.IMAGES_MAP, imagesMap);
		htmlExporter.setParameter(JRHtmlExporterParameter.IMAGES_URI, "../servlets/image?image=");
		htmlExporter.exportReport();
		return Response.ok(writer.toString()).type("text/html").build();
	}
	
	/**
	 * getConnection
	 *
	 * Get a JDBC connection
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		27/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return The connection
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	private Connection getConnection() throws ClassNotFoundException, SQLException {
		//TODO further investigate options for getting information from hibernate?
		
		Map<String, Object> entityManagerProperties = PersistenceManager.getInstance().getEntityManagerFactory().getProperties();
		
		String dbUrl = (String) entityManagerProperties.get("hibernate.connection.url");
		String dbDriver = (String) entityManagerProperties.get("hibernate.connection.driver_class");
		String dbUser = (String) entityManagerProperties.get("hibernate.connection.user");
		String dbPassword = (String) entityManagerProperties.get("hibernate.connection.password");
		
		Class.forName(dbDriver);
		
		Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
		
		return conn;
	}
	
	/**
	 * reloadReports
	 *
	 * Recompile the reports
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.2		02/10/2012	Genevieve Turner(GT)	Initial - Moved from ReportResource class
	 * </pre>
	 * 
	 * @param context The servlet context information
	 */
	public static void reloadReports(ServletContext context) {
		String reportPath = context.getRealPath(REPORT_PATH);
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
	}
	
	/**
	 * stripExtension
	 *
	 * Strip the extension from a file name
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.2		2/10/2012	Genevieve Turner(GT)	Initial - Moved from ReportResource class
	 * </pre>
	 * 
	 * @param filename The filename to strip
	 * @return The striped filename
	 */
	private static String stripExtension(String filename) {
		int index = filename.lastIndexOf(".");
		if (index == -1) {
			return filename;
		}
		return filename.substring(0, index);
	}
	
	/**
	 * getReportName
	 *
	 * Retrieves the name of the record associated with the pid
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.3		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param pid
	 * @return
	 */
	private String getReportName(String pid) {
		SolrServer solrServer = SolrManager.getInstance().getSolrServer();
		
		String escapedPid = SolrUtils.escapeSpecialCharacters(pid);
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery("id:" + escapedPid);
		solrQuery.addField("id");
		solrQuery.addField("unpublished.name");
		String name = null;
		try {
			QueryResponse queryResponse = solrServer.query(solrQuery);
			SolrDocumentList resultList = queryResponse.getResults();
			if (resultList.getNumFound() > 0) {
				SolrDocument doc = resultList.get(0);
				name = (String) doc.getFirstValue("unpublished.name");
			}
		}
		catch (SolrServerException e) {
			LOGGER.error("Error executing query", e);
			throw new WebApplicationException(500);
		}
		
		return name;
	}
}
