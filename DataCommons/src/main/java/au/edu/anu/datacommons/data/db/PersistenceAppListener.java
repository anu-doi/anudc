package au.edu.anu.datacommons.data.db;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * PersistenceAppListener
 * 
 * Australian National University Data Commons
 * 
 * A listener class that will close the entity manager factory when the web server is closed
 * 
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		26/04/2012	Genevieve Turner (GT)	Initial
 * </pre>
 * 
 */
public class PersistenceAppListener implements ServletContextListener {
	
	/**
	 * contextDestroyed
	 * 
	 * Called when the servlet is uninitialised
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/05/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @see ServletContextListener#contextDestroyed(ServletContextEvent)
	 */
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		PersistenceManager.getInstance().closeEntityManagerFactory();
	}

	/**
	 * contextInitialized
	 * 
	 * Called when the servlet is initialised
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/05/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @see ServletContextListener#contextInitialized(ServletContextEvent)
	 */
	@Override
	public void contextInitialized(ServletContextEvent event) {
	}
}
