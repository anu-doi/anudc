package au.edu.anu.datacommons.search;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;

import org.apache.commons.codec.binary.Base64;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import au.edu.anu.datacommons.properties.GlobalProps;
import au.edu.anu.datacommons.util.Utils;

/**
 * Servlet implementation class SearchServlet
 */
@WebServlet(name = "SearchServlet", urlPatterns = "/search/search.do")
public final class SearchServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;
	private static final int BUFFER_BYTES = 1024;
	private final Logger log = Logger.getLogger(this.getClass().getName());

	/**
	 * Default constructor.
	 */
	public SearchServlet()
	{
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		StringBuilder riSearchQuery = new StringBuilder();
		InputStream riSearchUrlInStream;
		OutputStream riSearchUrlOutStream;
		SparqlQuery sparqlQuery = new SparqlQuery();

		// Create the URL query string.
		Set<Entry<String, String[]>> paramSet = ((Map<String, String[]>) request.getParameterMap()).entrySet();
		for (Entry<String, String[]> iParam : paramSet)
		{
			if (iParam.getKey().toString().equalsIgnoreCase("terms") || iParam.getKey().toString().equalsIgnoreCase("format"))
				continue;

			riSearchQuery.append(iParam.getKey().toString());
			riSearchQuery.append("=");
			riSearchQuery.append(iParam.getValue()[0]);
			riSearchQuery.append("&");
		}

		sparqlQuery.setTerms(request.getParameter("terms"));
		riSearchQuery.append("query=");
		riSearchQuery.append(URLEncoder.encode(sparqlQuery.generateQuery(), Charset.defaultCharset().name()));

		// Open connection and send POST request.
		HttpURLConnection connection = (HttpURLConnection) new URL(GlobalProps.getProperty(GlobalProps.PROP_FEDORA_URI)
				+ GlobalProps.getProperty(GlobalProps.PROP_FEDORA_RISEARCHURL)).openConnection();
		connection.setDoOutput(true);
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Length", "" + riSearchQuery.length());
		connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		connection.setRequestProperty("Accept-Charset", Charset.defaultCharset().name());
		StringBuilder credentials = new StringBuilder();
		credentials.append(GlobalProps.getProperty(GlobalProps.PROP_FEDORA_USERNAME));
		credentials.append(":");
		credentials.append(GlobalProps.getProperty(GlobalProps.PROP_FEDORA_PASSWORD));
		connection.setRequestProperty("Authorization", "Basic " + Base64.encodeBase64String(credentials.toString().getBytes()));

		// Write the query string.
		riSearchUrlOutStream = connection.getOutputStream();
		log.info("riSearchQuery: " + riSearchQuery.toString());
		riSearchUrlOutStream.write(riSearchQuery.toString().getBytes(Charset.defaultCharset()));

		// Get the response stream.
		riSearchUrlInStream = connection.getInputStream();

		// Read from the risearch inputstream and send to servet's outputstream.
		if (request.getParameter("resultsfmt").equalsIgnoreCase("xml"))
		{
			ServletOutputStream servletOutStream = response.getOutputStream();
			int bytesRead;
			byte[] bytes = new byte[BUFFER_BYTES];
			while ((bytesRead = riSearchUrlInStream.read(bytes)) != -1)
			{
				servletOutStream.write(bytes, 0, bytesRead);
			}

			servletOutStream.flush();
			servletOutStream.close();
		}
		else if (request.getParameter("resultsfmt").equalsIgnoreCase("doc"))
		{
			Document resultsXmlDoc = null;
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			// factory.setNamespaceAware(true);
			try
			{
				resultsXmlDoc = factory.newDocumentBuilder().parse(riSearchUrlInStream);
				SparqlResultSet resultSet = new SparqlResultSet(resultsXmlDoc);

				request.setAttribute("resultSet", resultSet);
				request.getRequestDispatcher("/jsp/search.jsp").forward(request, response);
			}
			catch (SAXException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (ParserConfigurationException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		// Flush and close streams.
		riSearchUrlInStream.close();
		riSearchUrlOutStream.flush();
		riSearchUrlOutStream.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		// TODO Auto-generated method stub
	}

}
