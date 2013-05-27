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

package au.edu.anu.dcclient.tasks;

import java.net.URI;

import javax.swing.SwingWorker;
import javax.ws.rs.core.UriBuilder;

import au.edu.anu.dcclient.CustomClient;
import au.edu.anu.dcclient.Global;
import au.edu.anu.dcclient.stopwatch.StopWatch;

import com.sun.jersey.api.client.Client;

/**
 * Represents an abstract task that performs an action related to a bag containing a set of files.
 * 
 * @param <T>
 *            the type of the object returned by the task which is specific to each class that extends this class.
 */
public abstract class AbstractDcBagTask<T, V> extends SwingWorker<T, V> {
	protected StopWatch stopWatch = new StopWatch();
	protected Client client = CustomClient.getInstance();

	public StopWatch getStopWatch() {
		return this.stopWatch;
	}
	
	protected URI getBagFileUri(String pid, String filepath) {
		UriBuilder ub = UriBuilder.fromUri(getBagBaseUri()).path(pid);
		ub = ub.path(filepath);
		return ub.build();
	}
	
	protected URI getBagBaseUri() {
		return Global.getBagUploadUri();
	}
	
	protected URI getCreateUri() {
		return UriBuilder.fromUri(Global.getCreateUri()).queryParam("layout", "def:display").queryParam("tmplt", "tmplt:1").build();
	}
	
	protected URI getAddLinkUri(String pid) {
		return UriBuilder.fromUri(Global.getAddLinkUri()).path(pid).build();
	}
	
	protected void taskStart() {
		stopWatch.start();
	}
	
	protected void taskEnd() {
		stopWatch.end();
	}
}
