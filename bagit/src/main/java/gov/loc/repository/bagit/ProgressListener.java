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

package gov.loc.repository.bagit;

/**
 * Receives progress reports from other components.
 * This interface is implemented by any components that receive progress
 * updates during a potentially long-running operation.</p>
 * 
 * <p>To receive progress
 * updates, pass an implementation of the the {@link ProgressListener}
 * interface to the {@link #addProgressListener(ProgressListener)} method.
 * If you no longer wish to receive updates, object may be passed to the
 * {@link #removeProgressListener(ProgressListener)} method.</p>
 * 
 * <p>It is the
 * responsibility of the listener implementations to ensure that concurrent
 * invocations of the {@link ProgressListener#reportProgress(String, Object, Long, Long)}
 * method are thread-safe.</p> 
 *
 * @see ProgressListenable
 * @see gov.loc.repository.bagit.utilities.LongRunningOperationBase
 */
public interface ProgressListener
{
	/**
	 * Receives a progress report.
	 * 
	 * @param activity Describes the current activity of the operation.  Will never be null.
	 * @param item The item currently being processed.  May be null.
	 * @param count The index of the current item being processed.  May be null.
	 * @param total The total number of items to be processed.  May be null.
	 */
	void reportProgress(String activity, Object item, Long count, Long total);
}
