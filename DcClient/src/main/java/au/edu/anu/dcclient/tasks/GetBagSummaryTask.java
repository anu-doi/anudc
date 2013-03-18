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

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.dcbag.BagSummary;
import au.edu.anu.dcbag.FileSummary;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * Represents a task that gets information about a bag associated with a record in Data Commons.
 */
public final class GetBagSummaryTask extends AbstractDcBagTask<BagSummary, List<FileSummary>> {
	private static final Logger LOGGER = LoggerFactory.getLogger(GetBagSummaryTask.class);

	private final URI pidBagUri;

	/**
	 * 
	 * GetInfoTask
	 * 
	 * Australian National University Data Commons
	 * 
	 * Constructor for GetInfoTask
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		26/06/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 * @param pidBagUri
	 */
	public GetBagSummaryTask(URI bagBaseUri, String pid) {
		super();
		this.pidBagUri = UriBuilder.fromUri(bagBaseUri).path(pid).build();
	}

	@Override
	protected BagSummary doInBackground() throws Exception {
		BagSummary bagSummary = null;
		try {
			stopWatch.start();
			WebResource webResource = client.resource(UriBuilder.fromUri(pidBagUri).build());
			ClientResponse response = webResource.accept(MediaType.APPLICATION_JSON_TYPE).get(ClientResponse.class);
			String entity = response.getEntity(String.class);
			bagSummary = mapJsonToBagSummary(entity);
			System.out.println(bagSummary.getNumFiles());
		} finally {
			stopWatch.end();
			LOGGER.info("Time - Get Bag Info Task: {}", stopWatch.getFriendlyElapsed());
		}

		return bagSummary;
	}
	
	@Override
	protected void done() {
		super.done();
		try {
			get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	BagSummary mapJsonToBagSummary(String jsonStr) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(org.codehaus.jackson.map.DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		BagSummary bagSummary = mapper.readValue(jsonStr, BagSummary.class);
		return bagSummary;
	}
}
