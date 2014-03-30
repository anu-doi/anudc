/*******************************************************************************
 * Australian National University Data Commons
 * Copyright (C) 2013  The Australian National University
 * 
 * This file is part of Australian National University Data Commons.
 * 
 * Australian National University Data Commons is free software: you
 * can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package au.edu.anu.datacommons.util;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Utility class useful for measuring the time difference between 2 units of code by calling the <code>start</code>
 * before the unit of code to be measured and calling <code>stop</code> after it.
 * 
 * @author Rahul Khanna
 * 
 */
public class StopWatch {
	Date start;
	Date stop;
	
	/**
	 * Starts the stopwatch.
	 */
	public void start() {
		this.start = new Date();
	}
	
	/**
	 * Stops the stopwatch.
	 * 
	 * @throws IllegalStateException
	 *             when this method is called before <code>start</code>
	 */
	public void stop() {
		verifyStarted();
		this.stop = new Date();
	}

	/**
	 * Returns number of milliseconds since start or milliseconds between start and stop if stopwatch is stopped.
	 * 
	 * @return Milliseconds as long
	 */
	public long getTimeElapsedMillis() {
		verifyStarted();
		if (this.stop != null) {
			return this.stop.getTime() - this.start.getTime();
		} else {
			return new Date().getTime() - this.start.getTime();
		}
	}
	
	/**
	 * Returns the time elapsed as a formatted string. For example:
	 * <p>
	 * <ul>
	 * <li>1:02:03.456
	 * <li>2:03.456
	 * <li>3.456
	 * </ul>
	 * 
	 * @return Time elapsed as String
	 */
	public String getTimeElapsedFormatted() {
		String formatted;
		long hr = TimeUnit.MILLISECONDS.toHours(getTimeElapsedMillis());
		long min = TimeUnit.MILLISECONDS.toMinutes(getTimeElapsedMillis() - TimeUnit.HOURS.toMillis(hr));
		long sec = TimeUnit.MILLISECONDS.toSeconds(getTimeElapsedMillis() - TimeUnit.HOURS.toMillis(hr)
				- TimeUnit.MINUTES.toMillis(min));
		long ms = TimeUnit.MILLISECONDS.toMillis(getTimeElapsedMillis() - TimeUnit.HOURS.toMillis(hr)
				- TimeUnit.MINUTES.toMillis(min) - TimeUnit.SECONDS.toMillis(sec));
		if (hr > 0) {
			formatted = String.format("%d:%02d:%02d.%03d", hr, min, sec, ms);
		} else if (min > 0) {
			formatted = String.format("%d:%02d.%03d", min, sec, ms);
		} else {
			formatted = String.format("%d.%03d", sec, ms);
		}
		return formatted;
	}
	
	/**
	 * Returns a formatted string representing the rate at which specified bytes has been processed.
	 * 
	 * @param bytes
	 *            Number of bytes processed as long
	 * @return Process rate as String. For example, 1.00 MB/sec
	 */
	public String getRate(long bytes) {
		long rate;
		if (getTimeElapsedMillis() > 0) {
			rate = bytes * 1000L / getTimeElapsedMillis();
		} else {
			rate = 0;
		}
		return Util.byteCountToDisplaySize(rate) + "/sec";
	}

	private void verifyStarted() {
		if (this.start == null) {
			throw new IllegalStateException("Stopwatch not started.");
		}
	}
}
