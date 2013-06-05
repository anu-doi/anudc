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

package au.edu.anu.datacommons.web.context;

import static java.text.MessageFormat.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.apache.tika.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.properties.GlobalProps;
import au.edu.anu.datacommons.storage.DcStorage;

import com.sun.jersey.core.util.Base64;

/**
 * Application Lifecycle Listener implementation class ShutdownListener
 * 
 */
@WebListener
public final class WebContextListener implements ServletContextListener {
	private static final Logger LOGGER = LoggerFactory.getLogger(WebContextListener.class);
	protected static final int CONNECTION_TIMEOUT_MS = 30000;
	protected static final int READ_TIMEOUT_MS = 30000;

	/**
	 * Default constructor.
	 */
	public WebContextListener() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see ServletContextListener#contextInitialized(ServletContextEvent)
	 */
	public void contextInitialized(ServletContextEvent sce) {
		checkPythonPath();
		checkFidoPath();
		// Disabling checks for other web applications as they may not start before this web application.
//		checkFedoraServer();
//		checkSolrServer();
	}

	/**
	 * @see ServletContextListener#contextDestroyed(ServletContextEvent)
	 */
	public void contextDestroyed(ServletContextEvent sce) {
		shutdownDcStorage();
	}

	private void checkPythonPath() {
		String pythonPath = GlobalProps.getPythonPath();
		File pythonBin = new File(pythonPath);
		if (!pythonBin.isFile() || !pythonBin.canExecute()) {
			throw new IllegalStateException(format("Python executable {0} doesn't exist or is not executable.",
					pythonBin.getAbsolutePath()));
		}
		LOGGER.debug("Python executable exists and is executable.");
	}

	private void checkFidoPath() {
		String fidoPath = GlobalProps.getFidoPath();
		File fidoScriptFile = new File(fidoPath);
		if (!fidoScriptFile.exists()) {
			throw new IllegalStateException(format("Fido script not found at {0}.", fidoScriptFile.getAbsolutePath()));
		}
		LOGGER.debug("Fido script exists.");
	}

	private void checkFedoraServer() {
		String fedoraUrl = GlobalProps.getProperty(GlobalProps.PROP_FEDORA_URI);
		try {
			checkService(fedoraUrl, "<meta http-equiv=\"refresh\" content=\"0;url=describe\">", "UTF-8");
		} catch (IOException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
		LOGGER.debug("Fedora accessible at {}", fedoraUrl);
	}

	private void checkSolrServer() {
		String solrUrl = GlobalProps.getProperty(GlobalProps.PROP_SEARCH_SOLR);
		try {
			checkService(solrUrl, "<title>Welcome to Solr</title>", "ISO-8859-1");
		} catch (IOException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
		LOGGER.debug("Solr accessible at {}", solrUrl);
	}

	private void checkService(String urlStr, String respContains, String respEncoding) throws IOException {
		checkService(urlStr, respContains, respEncoding, null);
	}

	private void checkService(String urlStr, String respContains, String respEncoding, String[] credentials)
			throws IOException {
		URL url = null;
		url = createUrl(urlStr);

		// Check if the URL is reachable.
		InputStream netstream = null;
		try {
			netstream = getInputStream(url);
		} catch (IOException e) {
			IOUtils.closeQuietly(netstream);
			throw e;
		}

		// Check the response received from the server.
		String svcResp;
		try {
			svcResp = getResponse(netstream, respEncoding);
		} finally {
			IOUtils.closeQuietly(netstream);
		}

		if (!svcResp.contains(respContains)) {
			throw new IllegalStateException(format("Service returned unexpected response: {0}", svcResp));
		}
	}

	private URL createUrl(String urlStr) throws MalformedURLException {
		if (urlStr == null || urlStr.length() == 0) {
			throw new NullPointerException();
		}
		URL url = new URL(urlStr);
		return url;
	}

	private InputStream getInputStream(URL url) throws IOException {
		return getInputStream(url, null);
	}

	private InputStream getInputStream(URL url, String[] credentials) throws IOException {
		InputStream stream = null;

		URLConnection conn = url.openConnection();
		conn.setConnectTimeout(CONNECTION_TIMEOUT_MS);
		conn.setReadTimeout(READ_TIMEOUT_MS);
		if (credentials != null && credentials.length == 2) {
			String userpass = format("{0}:{1}", credentials[0], credentials[1]);
			String basicAuth = format("Basic {0}", new String(Base64.encode(userpass)));
			conn.setRequestProperty("Authorization", basicAuth);
		}
		stream = conn.getInputStream();

		return stream;
	}

	private String getResponse(InputStream netstream, String encoding) throws IOException {
		StringWriter writer = new StringWriter();
		IOUtils.copy(netstream, writer, encoding);
		return writer.toString();
	}

	private void shutdownDcStorage() {
		DcStorage.getInstance().close();
	}

}
