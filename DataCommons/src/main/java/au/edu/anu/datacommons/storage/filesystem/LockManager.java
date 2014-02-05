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

package au.edu.anu.datacommons.storage.filesystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Rahul Khanna
 *
 */
public class LockManager<T> {
	private static final Logger LOGGER = LoggerFactory.getLogger(LockManager.class);
	
	private Map<T, ReentrantReadWriteLock> locks = new WeakHashMap<>();
	
	public T readLock(T obj) {
		verifyNotNull(obj);
		T cachedObj = getOrAdd(obj);
		ReentrantReadWriteLock lock = locks.get(cachedObj);
		LOGGER.trace("Read lock on {}: Waiting", obj.toString());
		lock.readLock().lock();
		LOGGER.trace("Read lock on {}: Obtained", obj.toString());
		return cachedObj;
	}
	
	public T readUnlock(T obj) {
		verifyNotNull(obj);
		T cachedObj = getOrAdd(obj);
		ReentrantReadWriteLock lock = locks.get(cachedObj);
		lock.readLock().unlock();
		LOGGER.trace("Read lock on {}: Released", obj.toString());
		return cachedObj;
	}
	
	public T writeLock(T obj) {
		verifyNotNull(obj);
		T cachedObj = getOrAdd(obj);
		ReentrantReadWriteLock lock = locks.get(cachedObj);
		LOGGER.trace("Write lock on {}: Waiting", obj.toString());
		lock.writeLock().lock();
		LOGGER.trace("Write lock on {}: Obtained", obj.toString());
		return cachedObj;
	}
	
	public T writeUnlock(T obj) {
		verifyNotNull(obj);
		T cachedObj = getOrAdd(obj);
		ReentrantReadWriteLock lock = locks.get(cachedObj);
		lock.writeLock().unlock();
		LOGGER.trace("Write lock on {}: Released", obj.toString());
		return cachedObj;
	}

	public synchronized T getOrAdd(T obj) {
		for (T cacheKey : locks.keySet()) {
			if (obj.equals(cacheKey)) {
				return cacheKey;
			}
		}
		locks.put(obj, new ReentrantReadWriteLock());
		return obj;
	}
	
	private void verifyNotNull(T obj) {
		if (obj == null) {
			throw new NullPointerException("Cannot lock/unlock a null object");
		}
	}
}
