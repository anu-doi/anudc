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

import java.text.MessageFormat;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.dcclient.collection.CollectionInfo;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

/**
 * Represents a task that creates/updates a collection in Data Commons.
 */
public class CreateCollectionTask extends AbstractDcBagTask<String, Void> {
	private static final Logger LOGGER = LoggerFactory.getLogger(CreateCollectionTask.class);

	private final CollectionInfo collInfo;

	public CreateCollectionTask(CollectionInfo collInfo) {
		this.collInfo = collInfo;
	}

	@Override
	protected String doInBackground() throws Exception {
		String createdPid = null;

		if (collInfo.getPid() == null) {
			// The parameter file doesn't contain a pid, create an object and store the pid of newly created object in
			// parameter file.
			stopWatch.start();
			try {
				createdPid = postCreateRequest();
				postRelsRequest();
			} finally {
				stopWatch.end();
				LOGGER.info("Time - Create Collection Task: {}", stopWatch.getFriendlyElapsed());
			}
		} else {
			// Sync the collection details.
			// TODO Implement syncing.
		}

		return createdPid;
	}

	private String postCreateRequest() throws Exception {
		String createdPid;
		WebResource webResource = client.resource(getCreateUri());
		ClientResponse response = webResource.accept(MediaType.TEXT_PLAIN_TYPE)
				.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
				.post(ClientResponse.class, collInfo.getCreateCollMap());
		if (response.getClientResponseStatus() != Status.CREATED)
			throw new Exception("Unable to create a collection. Server returned HTTP " + response.getStatus());
		createdPid = response.getEntity(String.class);
		collInfo.setPid(createdPid);
		LOGGER.info("Created object with pid: {}", createdPid);
		return createdPid;
	}
	
	private void postRelsRequest() {
		ClientResponse response;
		WebResource webResource = client.resource(getAddLinkUri(collInfo.getPid()));
		for (String[] rel : collInfo.getRelationSet()) {
			MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
			formData.add("linkType", rel[0]);
			formData.add("itemId", rel[1]);
			response = webResource.type(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
					.accept(MediaType.TEXT_PLAIN_TYPE).post(ClientResponse.class, formData);
			if (response.getClientResponseStatus() == Status.OK)
				LOGGER.info(MessageFormat.format("Created {0} relationship with {1}.", rel[0], rel[1]));
			else
				LOGGER.error(MessageFormat.format("Unable to set {0} relation with {1}.", rel[0], rel[1]));
		}
	}
}
