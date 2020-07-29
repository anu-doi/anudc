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

package au.edu.anu.datacommons.geoscience.bindings.v0_01;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.config.Config;
import au.edu.anu.datacommons.config.PropertiesFile;
import au.edu.anu.datacommons.webservice.Processable;
import au.edu.anu.datacommons.webservice.bindings.Activity;
import au.edu.anu.datacommons.webservice.bindings.Collection;
import au.edu.anu.datacommons.webservice.bindings.DcRequest;
import au.edu.anu.datacommons.webservice.bindings.FedoraItem;

@XmlRootElement(name = "request")
public class GeoRequest implements Processable
{
	private static final Logger LOGGER = LoggerFactory.getLogger(GeoRequest.class);
	private static PropertiesFile constants;

	static
	{
		try
		{
			constants = new PropertiesFile(new File(Config.getAppHome(), "config/ws-geoscience/constants.properties"));
		}
		catch (IOException e)
		{
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	private String version;
	private String function;
	private List<GeoCollection> geoCollections;

	@XmlAttribute(name = "version")
	public String getVersion()
	{
		return version;
	}

	public void setVersion(String version)
	{
		this.version = version;
	}

	@XmlAttribute(name = "function")
	public String getFunction()
	{
		return function;
	}

	public void setFunction(String function)
	{
		this.function = function;
	}

	@XmlElement(name = "collection")
	public List<GeoCollection> getGeoCollections()
	{
		return geoCollections;
	}

	public void setGeoCollections(List<GeoCollection> geoCollections)
	{
		this.geoCollections = geoCollections;
	}

	@Override
	public Map<DcRequest, Map<String, FedoraItem>> generateDcRequests()
	{
		Map<DcRequest, Map<String, FedoraItem>> dcRequestsMap = new HashMap<DcRequest, Map<String, FedoraItem>>();

		// Activity
		Activity activity = new Activity();
		activity.setPid(constants.getProperty("project.anudcid"));
		
		// Generate a DcRequest for the Activity.
		DcRequest dcReqActivity = new DcRequest();
		dcReqActivity.setActivity(activity);
		dcRequestsMap.put(dcReqActivity, null);

		// Collection DcRequest for each GeoCollection
		List<GeoCollection> geoCollections = this.getGeoCollections();
		if (geoCollections != null)
		{
			for (GeoCollection iGeoColl : geoCollections)
			{
				// Strain as collection
				Collection dcColl = new Collection();
				if (iGeoColl.getAnudcId() != null)
					dcColl.setPid(iGeoColl.getAnudcId());
				dcColl.setExtIds(Arrays.asList(iGeoColl.getExtId()));
				dcColl.setTitle(iGeoColl.getTitle());
				dcColl.setBriefDesc(iGeoColl.getBriefDesc());

				// Strain constants
				dcColl.setOwnerGroup(constants.getProperty("collection.ownerGroup"));
				dcColl.setSubType(constants.getProperty("collection.subType"));
				dcColl.setTemplate(constants.getProperty("collection.tmplt"));
				dcColl.setEmails(Arrays.asList(constants.getProperty("collection.emails").split(";")));
				dcColl.setAnzForCodes(Arrays.asList(constants.getProperty("collection.anzforSubjects").split(";")));

				// Relation to project.
				Map<String, FedoraItem> projectRel = new HashMap<String, FedoraItem>();
				projectRel.put(constants.getProperty("collection.relToProject"), activity);

				// Create DcRequest for strain collection.
				DcRequest dcReqStrainColl = new DcRequest();
				dcReqStrainColl.setCollection(dcColl);
				dcRequestsMap.put(dcReqStrainColl, projectRel);
			}
		}

		return dcRequestsMap;
	}
}
