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
package au.edu.anu.datacommons.metadatastores;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import au.edu.anu.datacommons.properties.GlobalProps;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

/**
 * 
 * DataGenerator
 * 
 * Australian National University Data Commons
 * 
 * This class generates information from external sources.
 * 
 * DataGeneratorTest
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		27/05/2013	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
public class DataGenerator {
	static final Logger LOGGER = LoggerFactory.getLogger(DataGenerator.class);
	
	/**
	 * getLinkReference
	 *
	 * Checks if there is a valid Australian Research Council or National Health and Medical Research Council
	 * PURL.
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		27/05/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param fundsProvider The funds provider
	 * @param referenceNumber The reference number
	 * @return The purl
	 */
	public String getLinkReference(String fundsProvider, String referenceNumber) {
		String arc = GlobalProps.getProperty("arc.grant.title");
		String nhmrc = GlobalProps.getProperty("nhmrc.grant.title");
		boolean hasResource = false;
		String reference = null;
		LOGGER.debug("Checking urls for: {}, {}", fundsProvider, referenceNumber);
		if (arc.equals(fundsProvider) && referenceNumber != null && referenceNumber.trim().length() > 0) {
			String arcPrefix = GlobalProps.getProperty("arc.prefix");
			hasResource = checkURLResolves(arcPrefix + referenceNumber);
			if (hasResource) {
				reference = arcPrefix + referenceNumber;
			}
		}
		else if (nhmrc.equals(fundsProvider) && referenceNumber != null && referenceNumber.trim().length() > 0) {
			String nhmrcPrefix = GlobalProps.getProperty("nhmrc.prefix");
			hasResource = checkURLResolves(nhmrcPrefix + referenceNumber);
			if (hasResource) {
				reference = nhmrcPrefix + referenceNumber;
			}
		}
		
		return reference;
	}
	
	/**
	 * checkURLResolves
	 *
	 * Find out if the URL has an appropriate record in Research Data Australia
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		27/05/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param url The url to check
	 * @return An indicator if the purl exists
	 */
	private boolean checkURLResolves(String url) {
		LOGGER.debug("Verifying reference number: {}", url);
		
		String andsUrl = GlobalProps.getProperty("ands.service.url");
		String andsKey = GlobalProps.getProperty("ands.service.key");
		
		try {
			String andsSearchUrl = andsUrl + andsKey + "/getMetadata.xml?" + "q=" + URLEncoder.encode("key:\"" + url + "\"", "UTF-8");
			LOGGER.debug("URL: {}", andsSearchUrl);
			
			ClientConfig config = new DefaultClientConfig();
			Client client = Client.create(config);
			
			WebResource webService = client.resource(UriBuilder.fromUri(andsSearchUrl).build());
			
			ClientResponse clientResponse = webService.get(ClientResponse.class);
			int status = clientResponse.getStatus();
			if (status == 200) {
				Document doc = clientResponse.getEntity(Document.class);
				NodeList nodeList = doc.getElementsByTagName("numFound");
				if (nodeList.getLength() > 0) {
					Node node = nodeList.item(0);
					if (node.getNodeType() == Node.ELEMENT_NODE) {
						Element numRowElement = (Element) node;
						String numRows = numRowElement.getTextContent();
						//Verify that the number of rows is more than 0
						if (!"0".equals(numRows)) {
							return true;
						}
					}
				}
			}
			
		}
		catch (UnsupportedEncodingException e) {
			LOGGER.error("Exception", e);
		}
		
		return false;
	}
	
	/**
	 * generateActivityFromMetadataStores
	 *
	 * Retrieve the grant information from metadata stores
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		27/05/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param contractCode The contract code of the grant to retrieve information for
	 * @return The grant information
	 */
	public Map<String, List<String>> generateActivityFromMetadataStores(String contractCode) {
		String url = GlobalProps.getProperty("ms.url") + "grant/info/" + contractCode;
		
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		
		WebResource webService = client.resource(UriBuilder.fromUri(url).build());
		ClientResponse clientResponse = webService.type(MediaType.APPLICATION_FORM_URLENCODED).accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
		String grantStr = clientResponse.getEntity(String.class);
		
		ObjectMapper mapper = new ObjectMapper();
		try {
			Grant grant = mapper.readValue(grantStr, Grant.class);
			
			Map<String, List<String>> activity = new HashMap<String, List<String>>();
			
			activity.put("type", getValueAsList("Activity"));
			activity.put("subType", getValueAsList("program"));
			String group = GlobalProps.getProperty("ms.default.group");
			activity.put("ownerGroup", getValueAsList(group)); // find group
			activity.put("name", getValueAsList(grant.getTitle()));
			activity.put("fundingBody", getValueAsList(grant.getFundsProvider()));
			activity.put("existenceStart", getValueAsList(grant.getStartDate()));
			activity.put("existenceEnd", getValueAsList(grant.getEndDate()));
			activity.put("fullDesc", getValueAsList(grant.getDescription()));
			List<String> subjects = new ArrayList<String>();
			for (Subject subject : grant.getAnzforSubjects()) {
				subjects.add(subject.getCode());
			}
			if (subjects.size() > 0) {
				activity.put("anzforSubject", subjects);
			}
			return activity;
		}
		catch (JsonMappingException e) {
			LOGGER.error("Exception mapping grant", e);
		}
		catch (JsonParseException e) {
			LOGGER.error("Exception parsing grant", e);
		}
		catch (IOException e) {
			LOGGER.error("Exception reading grant", e);
		}
		
		return null;
	}
	
	/**
	 * getValueAsList
	 *
	 * Get a single value and return it as a list
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		27/05/2013	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param value The value to get as a list
	 * @return The value in a list
	 */
	private List<String> getValueAsList(String value) {
		if (value != null && value.trim().length() > 0) {
			return Arrays.asList(value);
		}
		return null;
	}
}
