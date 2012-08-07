package au.edu.anu.dcclient;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPoolManager
{
	private static ExecutorService es = null;
	
	protected ThreadPoolManager()
	{
		// Protected so this class can only be instantiated by getExecSvc method. 
	}
	
	public static ExecutorService getExecSvc()
	{
		if (es == null)
			es = Executors.newSingleThreadExecutor();
		return es;
	}
}
