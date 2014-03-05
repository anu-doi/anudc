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

package au.edu.anu.datacommons.tasks;

import static java.text.MessageFormat.format;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Rahul Khanna
 *
 */
public class ThreadPoolFactory implements ThreadFactory {
	private static Set<String> poolIds = new HashSet<>();
	
	private final ThreadGroup group;
	private final String poolId;
	private final AtomicInteger threadNumber = new AtomicInteger(1);
	private final int priority;
	
	public ThreadPoolFactory(String poolId, int priority) {
		super();
		addId(poolId);
		this.poolId = poolId;
		this.priority = priority;
		SecurityManager s = System.getSecurityManager();
		group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
	}

	@Override
	public Thread newThread(Runnable r) {
		String threadName = "p-" + poolId + "-t-" + threadNumber.getAndIncrement();
		Thread t = new Thread(group, r, threadName);
		if (t.isDaemon()) {
			t.setDaemon(false);
		}
		t.setPriority(priority);
		return t;
	}

	private synchronized void addId(String poolId) {
		if (poolIds.contains(poolId)) {
			throw new IllegalArgumentException(format("Pool ID {0} already in use", poolId));
		}
		poolIds.add(poolId);
	}
}
