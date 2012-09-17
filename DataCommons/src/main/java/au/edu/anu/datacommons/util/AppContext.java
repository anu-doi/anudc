package au.edu.anu.datacommons.util;

import org.springframework.context.ApplicationContext;

/**
 * AppContext
 * 
 * Australian National University Data Commons
 * 
 * This class makes available the ApplicationContext so that spring beans can be retrieved
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		17/09/2012	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
public class AppContext {
	private static ApplicationContext ctx;
	
	/**
	 * setApplicationContext
	 *
	 * Set the application context
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		17/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param applicationContext The application context to set
	 */
	public static void setApplicationContext(ApplicationContext applicationContext) {
		ctx = applicationContext;
	}
	
	/**
	 * getApplicationContext
	 *
	 * Get the application context
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		17/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return The application context
	 */
	public static ApplicationContext getApplicationContext() {
		return ctx;
	}
}
