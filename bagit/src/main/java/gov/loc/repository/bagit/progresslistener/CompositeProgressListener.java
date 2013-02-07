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

package gov.loc.repository.bagit.progresslistener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import gov.loc.repository.bagit.ProgressListener;

public class CompositeProgressListener implements ProgressListener {
	private List<ProgressListener> listeners = Collections.synchronizedList(new ArrayList<ProgressListener>());
	
	public CompositeProgressListener() {
	}

	public CompositeProgressListener(Collection<ProgressListener> listeners) {
		this.listeners.addAll(listeners);
	}

	public CompositeProgressListener(ProgressListener[] listeners) {
		this.listeners.addAll(Arrays.asList(listeners));
	}
	
	public List<ProgressListener> getProgressListeners() {
		return Collections.unmodifiableList(this.listeners);
	}
	
	public void addProgressListener(ProgressListener listener) {
		this.listeners.add(listener);
	}
	
	public void removeProgressListener(ProgressListener listener) {
		this.listeners.remove(listener);
	}
	
	@Override
	public void reportProgress(String activity, Object item, Long count,
			Long total) {
		for(ProgressListener listener : this.listeners) {
			listener.reportProgress(activity, item, count, total);
		}
	}

}
