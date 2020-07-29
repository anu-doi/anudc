/*******************************************************************************
 * Australian National University Data Commons
 * Copyright (C) 2013  The Australian National University
 * 
 * This file is part of Australian National University Data Commons.
 * 
 * Australian National University Data Commons is free software: you
 * can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package au.edu.anu.datacommons.report;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
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
import au.edu.anu.datacommons.data.db.model.ReportAuto;
import au.edu.anu.datacommons.data.db.model.ReportAutoParam;
import au.edu.anu.datacommons.data.db.model.ReportParam;
import au.edu.anu.datacommons.data.solr.SolrManager;
import au.edu.anu.datacommons.data.solr.SolrUtils;
import au.edu.anu.datacommons.properties.GlobalProps;
import au.edu.anu.datacommons.util.ExtensionFileFilter;
import au.edu.anu.datacommons.util.Util;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;

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
 * 0.4		26/10/2012	Genevieve Turner (GT)	Added solr parameter and fixed an issue where with parameters
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
	 * 0.4		26/10/2012	Genevieve Turner (GT)	Added solr parameter and fixed an issue where with parameters
	 * </pre>
	 * 
	 * @param request the http request information
	 * @param serverPath the path for the server
	 */
	public ReportGenerator(HttpServletRequest request, String serverPath) {
		String reportParam = request.getParameter("report");
		Long reportId = Long.valueOf(reportParam);
		Report report = getReport(reportId);
		filePath_ = getFilePath(serverPath, report.getReportTemplate());
		
		setBaseParams(report, serverPath);
		
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
			else if (Util.isNotEmpty(rptParam.getRequestParam()) && Util.isNotEmpty(request.getParameter(rptParam.getRequestParam()))) {
				String value = request.getParameter(rptParam.getRequestParam());
				LOGGER.debug("adding parameter '{}' with value '{}'", rptParam.getRequestParam(), value);
				params_.put(rptParam.getParamName(), value);
			}
			else {
				LOGGER.debug("adding parameter '{}' with value '{}'", rptParam.getRequestParam(), rptParam.getDefaultValue());
				params_.put(rptParam.getParamName(), rptParam.getDefaultValue());
			}
		}
	}
	
	/**
	 * Constructor
	 * 
	 * @param reportAuto The automated report object
	 * @param serverPath The server path
	 */
	public ReportGenerator(ReportAuto reportAuto, String serverPath) {
		Report report = getReport(reportAuto.getReportId());
		filePath_ = getFilePath(serverPath, report.getReportTemplate());
		setBaseParams(report, serverPath);
		
		for (ReportParam reportParam : report.getReportParams()) {
			boolean found = false;
			for (ReportAutoParam autoParam : reportAuto.getReportAutoParams()) {
				if (Util.isNotEmpty(reportParam.getParamName()) && reportParam.getParamName().equals(autoParam.getParam())) {
					params_.put(reportParam.getParamName(), autoParam.getParamVal());
					found = true;
					break;
				}
			}
			if (!found) {
				params_.put(reportParam.getParamName(), reportParam.getDefaultValue());
			}
		}
	}
	
	/**
	 * Get the file path
	 * 
	 * @param serverPath The server path
	 * @param reportTemplate The name of the report template
	 * @return The server path for the report file
	 */
	private String getFilePath(String serverPath, String reportTemplate) {
		return serverPath + REPORT_PATH + reportTemplate;
	}
	
	/**
	 * Set the parameters that are sent for all reports
	 * 
	 * @param report The report to generate
	 * @param serverPath The server path
	 */
	private void setBaseParams(Report report, String serverPath) {
		params_ = new HashMap<String, Object>();
		params_.put("baseURL", serverPath + REPORT_PATH);
		params_.put("SOLR_LOCATION", GlobalProps.getProperty(GlobalProps.PROP_SEARCH_SOLR));
		params_.put("LDAP_LOCATION", GlobalProps.getProperty(GlobalProps.PROP_LDAP_URI));
		params_.put("BAG_LOCATION", GlobalProps.getProperty(GlobalProps.PROP_UPLOAD_BAGSDIR));
		if (Util.isNotEmpty(report.getSubReport())) {
			LOGGER.debug("For report {} the subreport is: {}", report.getReportName(), report.getSubReport());
			params_.put("sub_rpt", report.getSubReport());
		}
	}
	
	/**
	 * Get the report row from the database
	 * 
	 * @param reportId The id of the report to retrieve
	 * @return The report database object
	 */
	private Report getReport(Long reportId) {
		GenericDAO<Report, Long> reportDAO = new GenericDAOImpl<Report, Long>(Report.class);
		Report report = reportDAO.getSingleById(reportId);
		return report;
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
		byte[] reportBytes;
		if ("pdf".equals(format)) {
			reportBytes = generatePDF();
			return generateResponse(reportBytes, "application/pdf");
		}
		else if ("html".equals(format)) {
			reportBytes = generateHTML();
			return generateResponse(reportBytes, "text/html");
		}
		else if ("xlsx".equals(format)) {
			reportBytes = generateXLSX();
			return generateResponse(reportBytes, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		}
		return response;
	}
	
	/**
	 * Generate a response for the web service
	 * 
	 * @param bytes The bytes of the response
	 * @param contentType The content type
	 * @return The response
	 */
	private Response generateResponse(byte[] bytes, String contentType) {
		return Response.ok(bytes).type(contentType).build();
	}
	
	/**
	 * Generate the report for email purposes
	 * 
	 * @param format The format of the report
	 * @return The report in bytes
	 * @throws JRException
	 * @throws SQLException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public byte[] generateReportForEmail(String format) throws JRException, SQLException, IOException, ClassNotFoundException {
		if ("xlsx".equals(format)) {
			return generateXLSX();
		}
		else if ("html".equals(format)) {
			return generateHTML();
		}
		return generatePDF();
	}
	
	/**
	 * Generate a pdf report
	 * 
	 * @return The bytes of the generated report
	 * @throws JRException
	 * @throws SQLException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private byte[] generatePDF() throws JRException, SQLException, IOException, ClassNotFoundException {
		InputStream inputStream = new FileInputStream(new File(filePath_));
		
		byte[] bytes = JasperRunManager.runReportToPdf(inputStream, params_, getConnection());
		return bytes;
	}
	
	/**
	 * Generate a html report
	 * 
	 * @return The bytes of the generated report
	 * @throws JRException
	 * @throws SQLException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private byte[] generateHTML() throws JRException, SQLException, IOException, ClassNotFoundException {
		InputStream inputStream = new FileInputStream(new File(filePath_));
		params_.put(JRParameter.IS_IGNORE_PAGINATION, Boolean.TRUE);
		JasperPrint jasperPrint = JasperFillManager.fillReport(inputStream, params_, getConnection());
		
		JRHtmlExporter htmlExporter = new JRHtmlExporter();
		
		@SuppressWarnings("rawtypes")
		Map imagesMap = new HashMap();

		ByteArrayOutputStream htmlReport = new ByteArrayOutputStream();
		
		htmlExporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
		htmlExporter.setParameter(JRExporterParameter.OUTPUT_STREAM, htmlReport);
		htmlExporter.setParameter(JRHtmlExporterParameter.IMAGES_MAP, imagesMap);
		htmlExporter.setParameter(JRHtmlExporterParameter.IMAGES_URI, "../servlets/image?image=");
		htmlExporter.exportReport();
		byte[] bytes = htmlReport.toByteArray();
		htmlReport.close();
		params_.remove(JRParameter.IS_IGNORE_PAGINATION);
		return bytes;
	}
	
	/**
	 * Generate an Excel spreadsheet report
	 * 
	 * @return The generated report in bytes
	 * @throws JRException
	 * @throws SQLException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private byte[] generateXLSX() throws JRException, SQLException, IOException, ClassNotFoundException {
		InputStream inputStream = new FileInputStream(new File(filePath_));
		params_.put(JRParameter.IS_IGNORE_PAGINATION, Boolean.TRUE);
		JasperPrint jasperPrint = JasperFillManager.fillReport(inputStream, params_, getConnection());
		
		JRXlsxExporter xlsxExporter = new JRXlsxExporter();
		
		ByteArrayOutputStream xlsReport = new ByteArrayOutputStream();

		xlsxExporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
		xlsxExporter.setParameter(JRExporterParameter.OUTPUT_STREAM, xlsReport);
		
		xlsxExporter.exportReport();
		byte[] bytes = xlsReport.toByteArray();
		xlsReport.close();
		params_.remove(JRParameter.IS_IGNORE_PAGINATION);
		return bytes;
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
		
		Map<String, Object> entityManagerProperties = PersistenceManager.getEntityManagerFactory().getProperties();
		
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
		reloadReports(context.getRealPath(REPORT_PATH));
	}

	/**
	 * Recompile the reports
	 * 
	 * @param reportPath The path the reports are located on
	 */
	public static void reloadReports(String reportPath) {
		File reportsDir = new File(reportPath);
		if (reportsDir.exists()) {
			File[] files = reportsDir.listFiles(new ExtensionFileFilter(JASPER_UNCOMPILED_EXTENSION));
			for (File jrxmlFile : files) {
				String outputFilename = String.format("%s.%s", stripExtension(jrxmlFile.getAbsolutePath()), JASPER_COMPILED_EXTENSION);
				File outputFile = new File(outputFilename);
				// compile report if not compiled already
				if (!outputFile.exists() || outputFile.length() == 0) {
					try (InputStream is = new BufferedInputStream(new FileInputStream(jrxmlFile));
							OutputStream os = new BufferedOutputStream(new FileOutputStream(outputFile))) {
						JasperCompileManager.compileReportToStream(is, os);
					} catch(JRException e) {
						LOGGER.error("Exception compiling report: {}", jrxmlFile.getName(), e);
					} catch (IOException e) {
						LOGGER.warn("Unable to read {} or write to {}", jrxmlFile.getName(), outputFile.getName());
					}
				}
			}
		} else {
			LOGGER.warn("Report path does not exist: {}", reportsDir.getAbsolutePath());
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
	 * @param pid The pid of the object the report is about
	 * @return The name of the object
	 */
	private String getReportName(String pid) {
		SolrClient solrClient = SolrManager.getInstance().getSolrClient();
		
		String escapedPid = SolrUtils.escapeSpecialCharacters(pid);
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery("id:" + escapedPid);
		solrQuery.addField("id");
		solrQuery.addField("unpublished.name");
		String name = null;
		try {
			QueryResponse queryResponse = solrClient.query(solrQuery);
			SolrDocumentList resultList = queryResponse.getResults();
			if (resultList.getNumFound() > 0) {
				SolrDocument doc = resultList.get(0);
				name = (String) doc.getFirstValue("unpublished.name");
			}
		}
		catch (SolrServerException | IOException e) {
			LOGGER.error("Error executing query", e);
			throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
		}
		
		return name;
	}
}
