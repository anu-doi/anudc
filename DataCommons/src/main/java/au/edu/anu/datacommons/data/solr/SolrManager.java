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

package au.edu.anu.datacommons.data.solr;

import java.io.IOException;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.properties.GlobalProps;

/**
 * SolrManager
 * 
 * Australian National University Data Commons
 * 
 * A class that manages the connection to the Solr instance.
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		13/06/2012	Genevieve Turner (GT)	Initial
 * 0.2		19/06/2012	Genevieve Turner (GT)	Updates to shutdown the solrServer and to change the connection method
 * </pre>
 *
 */
public class SolrManager {
	static final Logger LOGGER = LoggerFactory.getLogger(SolrManager.class);
	
	private static final SolrManager solrManager = new SolrManager();
	
//	protected SolrServer solrServer;
	protected SolrClient solrClient;
	
	/**
	 * getInstance
	 *
	 * Returns an instance of the SolrManager
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		13/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return
	 */
	public static SolrManager getInstance() {
		return solrManager;
	}
	
	/**
	 * Constructor
	 * 
	 * Constructor for SolrManager
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		13/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 *
	 */
	private SolrManager() {
		
	}
	
	/**
	 * getSolrServer
	 *
	 * Returns the SolrServer instance
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		13/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return
	 */
//	public SolrServer getSolrServer() {
//		if (solrServer == null) {
//			createSolrServer();
//		}
//		return solrServer;
//	}
	
	public SolrClient getSolrClient() {
		if (solrClient == null) {
			createSolrClient();
		}
		return solrClient;
	}
	
	/**
	 * createSolrServer
	 *
	 * Creates the SolrServer instance
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		13/06/2012	Genevieve Turner(GT)	Initial
	 * 0.2		19/06/2012	Genevieve Turner (GT)	Updated to use HttpSolrServer rather than a deprecated method
	 * </pre>
	 *
	 */
//	protected void createSolrServer() {
//		String url = GlobalProps.getProperty(GlobalProps.PROP_SEARCH_SOLR);
//		this.solrServer = new HttpSolrServer(url);
//		LOGGER.info("Solr Server connection started at " + new java.util.Date());
//	}
	
	protected void createSolrClient() {
		String url = GlobalProps.getProperty(GlobalProps.PROP_SEARCH_SOLR);
		this.solrClient = new HttpSolrClient(url);
		LOGGER.info("Solr Server connection started at " + new java.util.Date());
	}
	
	/**
	 * shutdown
	 *
	 * Shuts down the current solrServer instance.
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.2		19/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 *
	 */
	public void shutdown() {
//		if (solrServer instanceof HttpSolrServer) {
//			((HttpSolrServer)solrServer).shutdown();
//		}
		try {
			if (solrClient != null) {
				solrClient.close();
				LOGGER.info("Solr Server connection shutdown at " + new java.util.Date());
			}
		}
		catch (IOException e) {
			LOGGER.error("Error closing connection to solr");
		}
	}
}
