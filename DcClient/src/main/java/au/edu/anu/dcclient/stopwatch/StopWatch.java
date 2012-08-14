package au.edu.anu.dcclient.stopwatch;

import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StopWatch
{
	private static final Logger LOGGER = LoggerFactory.getLogger(StopWatch.class);
	long startTimeInMs = -1;
	long endTimeInMs = -1;

	public void start()
	{
		startTimeInMs = System.currentTimeMillis();
	}

	public void end()
	{
		if (startTimeInMs == -1)
			throw new RuntimeException("end() cannot be called before start()");
		endTimeInMs = System.currentTimeMillis();
		LOGGER.debug(getFriendlyElapsed());
	}
	
	public boolean hasEnded()
	{
		return endTimeInMs != -1;
	}

	public long getElapsedInMillis()
	{
		if (endTimeInMs == -1)
			return System.currentTimeMillis() - this.startTimeInMs;  
		return endTimeInMs - startTimeInMs;
	}

	public long getElapsedInSeconds()
	{
		return (getElapsedInMillis() / 1000);
	}

	public String getFriendlyElapsed()
	{
		return getFriendlyTime(getElapsedInMillis());
	}
	
	private String getFriendlyTime(long totalMillis)
	{
		long millis = getElapsedInMillis() % 1000;
		long secs = ((getElapsedInMillis() % (1000 * 60 * 60)) % (1000 * 60)) / 1000;
		long mins = (getElapsedInMillis() % (1000 * 60 * 60)) / (1000 * 60);
		long hours = getElapsedInMillis() / (1000 * 60 * 60);
		return MessageFormat.format("{0,number,integer} hours {1,number,integer} mins {2,number,integer} sec {3,number,integer} ms", hours, mins, secs, millis);
	}
	
	@Override
	public String toString()
	{
		return getFriendlyElapsed();
	}
}
