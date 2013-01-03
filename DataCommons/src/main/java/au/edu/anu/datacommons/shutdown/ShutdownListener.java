package au.edu.anu.datacommons.shutdown;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import au.edu.anu.datacommons.storage.DcStorage;

/**
 * Application Lifecycle Listener implementation class ShutdownListener
 *
 */
@WebListener
public final class ShutdownListener implements ServletContextListener {

    /**
     * Default constructor. 
     */
    public ShutdownListener() {
        // TODO Auto-generated constructor stub
    }

	/**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent sce) {
        // TODO Auto-generated method stub
    }

	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent sce) {
    	DcStorage.getInstance().close();
    }
	
}
