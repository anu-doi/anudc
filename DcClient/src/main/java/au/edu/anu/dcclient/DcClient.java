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

package au.edu.anu.dcclient;

import java.awt.EventQueue;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.swing.UIManager;

import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.parse.BasicParserPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.dcclient.cli.CmdMgr;
import au.edu.anu.dcclient.gui.MainWindow;
import au.edu.anu.dcclient.shibboleth.auth.Registry;

/**
 * Entry class for the DcClient application.
 */
public class DcClient {
	private static final Logger LOGGER = LoggerFactory.getLogger(DcClient.class);

	/**
	 * main
	 * 
	 * Australian National University Data Commons
	 * 
	 * The main entry point for this desktop application.
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		22/06/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 * @param args
	 *            Command line arguments
	 */
	public static void main(String[] args) {
		// If no command line arguments specified, start GUI.
		if (args.length == 0) {
			try {
				Registry.initialise();
				BasicParserPool ppMgr = new BasicParserPool();
				ppMgr.setNamespaceAware(true);
				LOGGER.info("Registry Initialised");
			}
			catch (ConfigurationException e) {
				LOGGER.error("Exception configuring bootstrap", e);
			}
			//TODO remove the certificate disabling
			disableCertificateValidation();
			startGui();
		} else {
			CmdMgr cmdMgr = new CmdMgr(args);
			System.exit(cmdMgr.getExitCode());
		}
	}

	/**
	 * Starts the GUI interface of the desktop client.
	 */
	private static void startGui() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Throwable e) {
			// No op
		}
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					MainWindow window = new MainWindow();
					window.setVisible(true);
				} catch (Exception e) {
					LOGGER.error("Unable to start " + MainWindow.class.getName(), e);
					System.exit(1);
				}
			}
		});
	}

	public static void disableCertificateValidation() {
		TrustManager[] trustAllCerts = new TrustManager[] {
			new X509TrustManager() {
				public X509Certificate[] getAcceptedIssuers() {
					return new X509Certificate[0];
				}
				public void checkClientTrusted(X509Certificate[] certs, String authType) {}
				public void checkServerTrusted(X509Certificate[] certs, String authType) {}
			}
		};
		
		/*HostnameVerifier hv = new HostnameVerifier() {
			public boolean verify(String hostname, SSLSession session) { return true; }
		};*/
		
		try {
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		//	HttpsURLConnection.setDefaultHostnameVerifier(hv);
		}
		catch (Exception e) {
		}
	}
}
