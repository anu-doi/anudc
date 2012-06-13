package au.edu.anu.datacommons.data.solr;

import java.net.MalformedURLException;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
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
	 * </pre>
	 *
	 */
	protected void createSolrServer() {
		String url = GlobalProps.getProperty(GlobalProps.PROP_SEARCH_SOLR);
		try {
			this.solrServer = new CommonsHttpSolrServer(url);
		}
		catch (MalformedURLException e) {
			LOGGER.info("Exception connecting to url " + url, e);
		}
		LOGGER.info("Solr Server connection started at " + new java.util.Date());
	}
}
