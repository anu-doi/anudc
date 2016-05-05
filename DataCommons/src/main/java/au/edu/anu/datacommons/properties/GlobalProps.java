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

package au.edu.anu.datacommons.properties;

import static java.text.MessageFormat.*;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.ws.rs.core.UriBuilder;

import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.config.Config;
import au.edu.anu.datacommons.config.PropertiesFile;

/**
 * GlobalProps
 * 
 * Australian National University Data Commons
 * 
 * Contains static methods that reads (no write functions) values for keys in the global.properties file.
 * 
 * Usage example: To return the value for the key containing Fedora's URI.
 * 
 * <pre>
 * GlobalProps.getProperty(GlobalProps.PROP_FEDORA_SERVER);
 * 
 * <pre>
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		8/03/2012	Rahul Khanna (RK)		Initial.
 * 0.2		14/03/2012	Rahul Khanna (RK)		Added Search properties.
 * 0.3		20/03/2012	Rahul Khanna (RK)		Added File Upload properties.
 * 0.4		4/05/2012	Rahul Khanna (RK)		Added Random Password chars property.
 * 0.5		12/05/2012	Genevieve Turner (GT)	Changed case of properties, related uri and save namespace
 * 0.6		08/06/2012	Genevieve Turner (GT)	Added solr location
 * 0.7		13/06/2012	Genevieve Turner (GT)	Added solr standard return fields
 * 
 * <pre>
 * 
 */
public final class GlobalProps {
	private static final PropertiesFile globalProperties;
	private static final Logger LOGGER = LoggerFactory.getLogger(GlobalProps.class);

	// List of valid Keys in global.properties file that can be used as parameters in the methods of this class.
	public static final String PROP_FEDORA_URI = "fedora.baseURI";
	public static final String PROP_FEDORA_USERNAME = "fedora.username";
	public static final String PROP_FEDORA_PASSWORD = "fedora.password";
	public static final String PROP_FEDORA_RISEARCHURL = "fedora.riSearchURI";
	public static final String PROP_FEDORA_RELATEDURI = "fedora.relatedURI";
	public static final String PROP_FEDORA_SAVENAMESPACE = "fedora.saveNamespace";
	public static final String PROP_FEDORA_OAIPROVIDER_URL = "fedora.oaiprovider.url";
	public static final String PROP_FEDORA_NAMEFIELDS = "fedora.nameFields";
	public static final String PROP_LDAP_URI = "ldap.uri";
	public static final String PROP_LDAP_BASEDN = "ldap.baseDn";
	public static final String PROP_LDAP_ATTRLIST = "ldap.person.attrList";
	public static final String PROP_LDAPATTR_UNIID = "ldap.attr.uniId";
	public static final String PROP_LDAPATTR_DISPLAYNAME = "ldap.attr.displayName";
	public static final String PROP_LDAPATTR_GIVENNAME = "ldap.attr.givenName";
	public static final String PROP_LDAPATTR_FAMILYNAME = "ldap.attr.familyName";
	public static final String PROP_LDAPATTR_EMAIL = "ldap.attr.email";
	public static final String PROP_LDAPATTR_PHONE = "ldap.attr.phone";
	public static final String PROP_SEARCH_SEARCHFIELDS = "search.dcSearchFields";
	public static final String PROP_SEARCH_RETURNFIELDS = "search.dcReturnFields";
	public static final String PROP_SEARCH_URIREPLACE = "search.uriReplace";
	public static final String PROP_SEARCH_SOLR = "search.solr";
	public static final String PROP_SEARCH_SOLR_RETURNFIELDS = "search.solr.returnFields";
	public static final String PROP_UPLOAD_DIR = "upload.uploadDir";
	public static final String PROP_UPLOAD_ARCHIVEBASEDIR = "upload.archiveBaseDir";
	public static final String PROP_UPLOAD_MAXSIZEINMEM = "upload.maxSizeInMemInBytes";
	public static final String PROP_UPLOAD_HTTPBASEURI = "upload.uploadHttpBaseURI";
	public static final String PROP_UPLOAD_BAGSDIR = "upload.bagsDir";
	public static final String PROP_PASSWORDGENERATOR_CHARS = "passwordGenerator.chars";
	public static final String PROP_DROPBOX_PASSWORDLENGTH = "dropbox.passwordLength";
	public static final String PROP_EMAIL_DEBUG_SEND = "email.debug.sendmail";
	public static final String PROP_CAS_SERVER = "cas.server";
	public static final String PROP_APP_SERVER = "app.server";
	public static final String PROP_REVIEW_REJECTED_TITLE = "review.rejected.title";
	public static final String PROP_REVIEW_READY_TITLE = "review.reviewready.title";
	public static final String PROP_PUBLISH_READY_TITLE = "review.publishready.title";
	public static final String PROP_ORCA_RIFCS = "orca.rifcs.location";
	public static final String PROP_ORCA_XSL = "orca.transform.xsl";
	public static final String PROP_PYTHON_PATH = "python.path";
	public static final String PROP_FIDO_PATH = "fido.path";
	public static final String PROP_REPORT_EMAIL_SUBJECT = "report.email.subject";

	static {
		try {
			globalProperties = new PropertiesFile(new File(Config.getAppHome(), "config/datacommons.properties"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see Properties.getProperty()
	 * @param key
	 * @return value of the key, null if the key doesn't exist.
	 */
	public static String getProperty(String key) {
		return globalProperties.getProperty(key);
	}

	/**
	 * @see Properties.getProperty()
	 * @param key
	 * @param defaultValue
	 * @return value of the key or defaultValue if key doesn't exist.
	 */
	public static String getProperty(String key, String defaultValue) {
		return globalProperties.getProperty(key, defaultValue);
	}

	public static String getUploadDirAsString() {
		return getProperty(PROP_UPLOAD_DIR);
	}

	public static File getUploadDirAsFile() {
		File uploadDir = new File(getUploadDirAsString());
		createIfNotExists(uploadDir);
		return uploadDir;
	}

	public static String getBagsDirAsString() {
		return getProperty(PROP_UPLOAD_BAGSDIR);
	}

	public static File getBagsDirAsFile() {
		File bagsDir = new File(getBagsDirAsString());
		createIfNotExists(bagsDir);
		return bagsDir;
	}

	public static String getArchiveBaseDirAsString() {
		return getProperty(PROP_UPLOAD_ARCHIVEBASEDIR);
	}

	public static File getArchiveBaseDirAsFile() {
		String archiveBaseDirPath = getArchiveBaseDirAsString();
		File archiveBaseDir = null;
		if (archiveBaseDirPath != null && archiveBaseDirPath.length() > 0) {
			archiveBaseDir = new File(archiveBaseDirPath);
			createIfNotExists(archiveBaseDir);
		}
		return archiveBaseDir;
	}

	public static boolean getEmailDebugSend() {
		String emailDebugSendString = getProperty(PROP_EMAIL_DEBUG_SEND);
		boolean emailDebugSend = false;

		if (emailDebugSendString != null) {
			emailDebugSend = Boolean.parseBoolean(getProperty(PROP_EMAIL_DEBUG_SEND));
		} else {
			LOGGER.warn("Property {} not specified in Global Properties. Using default: false", PROP_EMAIL_DEBUG_SEND);
		}

		return emailDebugSend;
	}

	public static int getMaxSizeInMem() {
		try {
			return Integer.parseInt(getProperty(PROP_UPLOAD_MAXSIZEINMEM));
		} catch (NumberFormatException e) {
			LOGGER.warn("Property " + PROP_UPLOAD_MAXSIZEINMEM + " not specified in Global Properties.");
			return DiskFileItemFactory.DEFAULT_SIZE_THRESHOLD;
		}
	}

	public static URI getCasServerUri() {
		URI casUri = UriBuilder.fromUri(getProperty(PROP_CAS_SERVER)).build();
		return casUri;
	}

	public static String getClamScanHost() {
		return getProperty("clamscan.host");
	}

	public static int getClamScanPort() {
		return Integer.parseInt(getProperty("clamscan.port"));
	}

	public static int getClamScanTimeout() {
		int timeout;
		try {
			timeout = Integer.parseInt(getProperty("clamscan.timeout", "500000"));
		} catch (NumberFormatException e) {
			timeout = 500000;
		}
		return timeout;
	}

	public static String getPythonPath() {
		return getProperty(PROP_PYTHON_PATH);
	}

	public static String getFidoPath() {
		return getProperty(PROP_FIDO_PATH);
	}
	
	public static String getStorageSolrUrl() {
		return getProperty("storage.search.url");
	}
	
	public static Path getClamScanPath() {
		return Paths.get(getProperty("clamscan.path"));
	}

	public static Path getNicePath() {
		return Paths.get(getProperty("processes.nice.path"));
	}
	
	public static String getNiceness() {
		return getProperty("processes.niceness");
	}

	private static void createIfNotExists(File dir) {
		if (!dir.exists()) {
			if (!dir.mkdirs()) {
				throw new RuntimeException(format("The directory {0} doesn't exist. Unable to create it.",
						dir.getAbsolutePath()));
			}
		}
	}
}
