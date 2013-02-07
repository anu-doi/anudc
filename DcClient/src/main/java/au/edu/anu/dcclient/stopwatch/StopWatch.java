package au.edu.anu.dcclient.stopwatch;

import static java.text.MessageFormat.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class enables measuring the time elapsed by a task. This is done by calling the start method just before the code whose times are to be measured, and the end method
 * just after. This class measures the time elapsed in milliseconds by using <code>System.currentTimeMillis()</code>. The difference in start and end times is the time it
 * took for the code to execute. This time elapsed value can be returned as a String in the format <code>1 hours 2 minutes 3 seconds 4 ms</code>
 * 
 * It is recommended that <code>end()</code> is called in a finally block of the code being measured.
 * 
 * @see {@link System#currentTimeMillis()}
 */
public class StopWatch
{
	private static final Logger LOGGER = LoggerFactory.getLogger(StopWatch.class);
	long startTimeInMs = -1;
	long endTimeInMs = -1;

	/**
	 * Starts the stopwatch.
	 */
	public void start()
	{
		startTimeInMs = System.currentTimeMillis();
	}

	/**
	 * Ends the stopwatch.
	 * 
	 * @throws RuntimeException
	 *             when called before <code>start()</code>
	 */
	public void end()
	{
		if (startTimeInMs == -1)
			throw new RuntimeException("end() cannot be called before start()");
		endTimeInMs = System.currentTimeMillis();
	}
	
	/**
	 * Returns the time this stopwatch was started.
	 * 
	 * @return Start time in millis
	 */
	public long getStartTimeInMs()
	{
		return startTimeInMs;
	}

	/**
	 * Returns the time this stopwatch was stopped.
	 * 
	 * @return End time in millis
	 */
	public long getEndTimeInMs()
	{
		return endTimeInMs;
	}

	/**
	 * Returns if the <code>end()</code> method has been called.
	 * 
	 * @return true if this stopwatch has been stopped, false otherwise.
	 */
	public boolean hasEnded()
	{
		return endTimeInMs != -1;
	}

	/**
	 * Gets the time elapsed in millis.
	 * 
	 * @return the difference between end time and start time in millis
	 */
	public long getElapsedInMillis()
	{
		if (endTimeInMs == -1)
			return System.currentTimeMillis() - this.startTimeInMs;  
		return endTimeInMs - startTimeInMs;
	}

	/**
	 * Gets the time elapsed in seconds.
	 * 
	 * @return the difference between end time and start time in seconds
	 */
	public long getElapsedInSeconds()
	{
		return (getElapsedInMillis() / 1000);
	}

	/**
	 * Gets the time elapsed in the human readable format <code>1 hours 2 minutes 3 seconds 4 ms</code>.
	 * 
	 * @return Time elapsed as String
	 */
	public String getFriendlyElapsed()
	{
		return getFriendlyTime(getElapsedInMillis());
	}
	
	/**
	 * Formats milliseconds into a human readable String in the format <code>1 hours 2 minutes 3 seconds 4 ms</code>.
	 * 
	 * @param totalMillis
	 *            milliseconds to convert into human readable String
	 * 
	 * @return String representing the time
	 */
	private String getFriendlyTime(long totalMillis)
	{
		long millis = getElapsedInMillis() % 1000;
		long secs = ((getElapsedInMillis() % (1000 * 60 * 60)) % (1000 * 60)) / 1000;
		long mins = (getElapsedInMillis() % (1000 * 60 * 60)) / (1000 * 60);
		long hours = getElapsedInMillis() / (1000 * 60 * 60);
		return format("{0,number,integer} hours {1,number,integer} mins {2,number,integer} sec {3,number,integer} ms", hours, mins, secs, millis);
	}
	
	@Override
	public String toString()
	{
		return getFriendlyElapsed();
	}
}
