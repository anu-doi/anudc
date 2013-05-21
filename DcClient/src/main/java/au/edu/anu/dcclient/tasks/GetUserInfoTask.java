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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.dcclient.Global;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.WebResource;

/**
 * This class represents a task that gets information about a user from DataCommons.
 */
public class GetUserInfoTask extends AbstractDcBagTask<String[], Object> {
	private static final Logger LOGGER = LoggerFactory.getLogger(GetUserInfoTask.class);

	@Override
	protected String[] doInBackground() throws Exception {
		String[] userInfo = null;

		ClientResponse response = null;
		try {
			stopWatch.start();
			setProgress(10);
			WebResource webResource = client.resource(getUserInfoUri());
			setProgress(20);
			response = webResource.get(ClientResponse.class);
			setProgress(80);
			LOGGER.info("Server returned: HTTP {}", response.getStatus());
			if (response.getClientResponseStatus() == Status.OK) {
				userInfo = extractUserInfo(response);
			}
		} finally {
			try {
				response.close();
			} catch (Exception e) {
				// No op
			}
			stopWatch.end();
			LOGGER.info("Time - Get User Info Task: {}", stopWatch.getFriendlyElapsed());
		}

		return userInfo;
	}
	
	@Override
	protected void done() {
		setProgress(100);
		super.done();
	}
	
	private String[] extractUserInfo(ClientResponse response) {
		String[] userInfo;
		String respStr = response.getEntity(String.class);
		int separatorIndex = respStr.indexOf(':');
		userInfo = new String[] { respStr.substring(0, separatorIndex), respStr.substring(separatorIndex + 1) };
		return userInfo;
	}
	
	protected URI getUserInfoUri() {
		return Global.getUserInfoUri();
	}
}
