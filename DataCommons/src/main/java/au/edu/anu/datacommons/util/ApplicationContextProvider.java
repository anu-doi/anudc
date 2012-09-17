package au.edu.anu.datacommons.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * ApplicationContextProvider
 * 
 * Australian National University Data Commons
 * 
 * This class setse the application context after it has been loaded
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
public class ApplicationContextProvider implements ApplicationContextAware {

	/**
	 * setApplicationContext
	 * 
	 * Set the application context so that it can be retrieved.
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		17/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param ctx The application context to set
	 * @throws BeansException
	 * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
	 */
	@Override
	public void setApplicationContext(ApplicationContext ctx)
			throws BeansException {
		AppContext.setApplicationContext(ctx);
	}
}
