package au.edu.anu.dcclient.stopwatch;

/**
 * This interface describes a class that performs a task and uses a StopWatch object to measure the time elapsed in that task.
 */
public interface Timeable
{
	/**
	 * Returns the StopWatch object
	 * 
	 * @return StopWatch
	 */
	public StopWatch getStopWatch();	
}
