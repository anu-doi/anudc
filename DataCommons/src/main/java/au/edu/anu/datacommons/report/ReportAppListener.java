package au.edu.anu.datacommons.report;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * ReportAppListener
 * 
 * Australian National University Data Commons
 * 
 * Initiates the recompilation of reports on server start up.
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		02/10/2012	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
public class ReportAppListener implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent event) {
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		ReportGenerator.reloadReports(event.getServletContext());
	}

}
