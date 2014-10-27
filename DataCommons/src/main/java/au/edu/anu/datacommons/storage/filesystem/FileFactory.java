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

import java.io.File;
import java.util.Iterator;
import java.util.concurrent.LinkedBlockingDeque;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates and stores File objects in a cache for reuse. Allows locking for multithreaded operations
 * by returning the same instance of File object from cache for the same underlying file system resource.
 * Old entries are moved using Least Recently Used strategy.
 * 
 * @author Rahul Khanna
 */
public class FileFactory<T> {
	private static final Logger LOGGER = LoggerFactory.getLogger(FileFactory.class);

	LinkedBlockingDeque<T> cache;

	/**
	 * Constructor for this class with specified capacity.
	 * 
	 * @param capacity
	 *            Capacity of cache as int
	 */
	public FileFactory(int capacity) {
		cache = new LinkedBlockingDeque<T>(capacity);
	}

	/**
	 * Searches the cache for an instance of File object equal to the one specified. If one exists, the existing object
	 * in cache is returned. If not the specified instance is stored in the cache and the same instance is returned.
	 * 
	 * @param f
	 *            File object
	 * @return Returns the File object that should be used. Will be the same as the one provided as parameter if an
	 *         equivalent instance didn't already exist in cache.
	 */
	public synchronized T getFromCache(T f) {
		T retF = null;
		Iterator<T> iter = cache.iterator();
		while (iter.hasNext()) {
			T cachedFile = iter.next();
			if (cachedFile.equals(f)) {
				retF = cachedFile;
				break;
			}
		}
	
		if (retF == null) {
			while (cache.remainingCapacity() == 0) {
				cache.removeLast();
			}
			cache.addFirst(f);
			retF = f;
		} else {
			cache.remove(retF);
			cache.addFirst(retF);
		}
		
		return retF;
	}
}
