package au.edu.anu.dcclient;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class provides a common thread pool for worker threads to which tasks can be assigned.
 */
public class ThreadPoolManager
{
	private static ExecutorService es = null;
	
	/**
	 * Protected constructor so instantiation can only happen through getExecSvc.
	 */
	protected ThreadPoolManager()
	{
	}
	
	/**
	 * Returns the only instance of the Executor Service.
	 * 
	 * @return ExecutorService
	 */
	public synchronized static ExecutorService getExecSvc()
	{
		if (es == null)
			es = Executors.newSingleThreadExecutor();
		return es;
	}
}
