package au.edu.anu.datacommons.data.solr;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
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
	
	protected SolrServer solrServer;
	
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
	public SolrServer getSolrServer() {
		if (solrServer == null) {
			createSolrServer();
		}
		return solrServer;
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
	protected void createSolrServer() {
		String url = GlobalProps.getProperty(GlobalProps.PROP_SEARCH_SOLR);
		this.solrServer = new HttpSolrServer(url);
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
		if (solrServer instanceof HttpSolrServer) {
			((HttpSolrServer)solrServer).shutdown();
		}
		LOGGER.info("Solr Server connection shutdown at " + new java.util.Date());
	}
}
