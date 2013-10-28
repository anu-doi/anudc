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

package au.edu.anu.datacommons.test.util;

import static java.text.MessageFormat.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Rahul Khanna
 *
 */
public class TestStopWatch {
	private static final Logger LOGGER = LoggerFactory.getLogger(TestStopWatch.class);
	
	public enum State {WAITING, STARTED, STOPPED};
	
	private State state = State.WAITING;
	private long start;
	private long stop;
	
	public void start() {
		if (state != State.WAITING) {
			throw new IllegalStateException(format("Stopwatch in {0} state. Should be {1}.", state.toString(),
					State.WAITING.toString()));
		}
		start = System.nanoTime();
		state = State.STARTED;
	}
	
	public void stop() {
		if (state != State.STARTED) {
			throw new IllegalStateException(format("Stopwatch in {0} state. Should be {1}.", state.toString(),
					State.STARTED.toString()));
		}
		stop = System.nanoTime();
		state = State.STOPPED;
	}
	
	public double getMillisElapsed() {
		if (state != State.STOPPED) {
			throw new IllegalStateException(format("Stopwatch in {0} state. Should be {1}.", state.toString(),
					State.STOPPED.toString()));
		}
		return (stop - start) / 1000000.0;
	}
	
	public double getSecondsElapsed() {
		return getMillisElapsed() / 1000.0;
	}
}
