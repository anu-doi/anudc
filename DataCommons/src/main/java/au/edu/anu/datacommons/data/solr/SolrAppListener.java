package au.edu.anu.datacommons.data.solr;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * SolrAppListener
 * 
 * Australian National University Data Commons
 * 
 * Placeholder
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		19/06/2012	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
public class SolrAppListener implements ServletContextListener {
	/**
	 * contextDestroyed
	 * 
	 * Called when the servlet is uninitialised.  Shuts down the connnections the SolrManager
	 * is currently using.
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		19/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param arg0 The event initiated
	 * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
	 */
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		SolrManager.getInstance().shutdown();
	}

	/**
	 * contextInitialized
	 * 
	 * Called when the servlet is initialised
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		19/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param arg0 The event initiated
	 * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
	 */
	@Override
	public void contextInitialized(ServletContextEvent arg0) {
	}
}
