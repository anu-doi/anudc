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

package au.edu.anu.datacommons.phenomics.bindings.v0_01;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

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
public class PhenRequest implements Processable
{
	private static final Logger LOGGER = LoggerFactory.getLogger(PhenRequest.class);
	private static PropertiesFile constants;

	static
	{
		try
		{
			constants = new PropertiesFile(new File(Config.getAppHome(), "config/ws-phenomics/constants.properties"));
		}
		catch (IOException e)
		{
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	private String version;
	private String function;
	private PhenProject project;

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

	@XmlElement(name = "project")
	public PhenProject getProject()
	{
		return project;
	}

	public void setProject(PhenProject project)
	{
		this.project = project;
	}

	@Override
	public Map<DcRequest, Map<String, FedoraItem>> generateDcRequests()
	{
		Map<DcRequest, Map<String, FedoraItem>> dcRequestsMap = new HashMap<DcRequest, Map<String, FedoraItem>>();

		// Activity
		Activity activity = new Activity();
		if (this.getProject().getAnudcId() != null)
			activity.setPid(this.getProject().getAnudcId());
		activity.setExtIds(Arrays.asList(this.getProject().getExtId()));
		activity.setTitle(this.getProject().getTitle());
		activity.setBriefDesc(this.getProject().getBriefDesc());

		// Activity constants
		activity.setSubType(constants.getProperty("project.subType"));
		activity.setOwnerGroup(constants.getProperty("project.ownerGroup"));
		activity.setTemplate(constants.getProperty("project.tmplt"));
		activity.setEmails(Arrays.asList(constants.getProperty("project.emails").split(";")));
		activity.setAnzForCodes(Arrays.asList(constants.getProperty("project.anzforSubjects").split(";")));

		// Generate a DcRequest for the Activity.
		DcRequest dcReqActivity = new DcRequest();
		dcReqActivity.setActivity(activity);
		dcRequestsMap.put(dcReqActivity, null);

		// Collection DcRequest for each Strain
		List<Strain> strains = this.getProject().getStrains();
		if (strains != null)
		{
			for (Strain iStrain : strains)
			{
				// Strain as collection
				Collection strainColl = new Collection();
				if (iStrain.getAnudcId() != null)
					strainColl.setPid(iStrain.getAnudcId());
				strainColl.setExtIds(Arrays.asList(iStrain.getExtId()));
				strainColl.setTitle(iStrain.getTitle());
				strainColl.setBriefDesc(iStrain.getBriefDesc());

				// Strain constants
				strainColl.setOwnerGroup(constants.getProperty("strain.ownerGroup"));
				strainColl.setSubType(constants.getProperty("strain.subType"));
				strainColl.setTemplate(constants.getProperty("strain.tmplt"));
				strainColl.setEmails(Arrays.asList(constants.getProperty("strain.emails").split(";")));
				strainColl.setAnzForCodes(Arrays.asList(constants.getProperty("strain.anzforSubjects").split(";")));

				// Relation to project.
				Map<String, FedoraItem> projectRel = new HashMap<String, FedoraItem>();
				projectRel.put(constants.getProperty("strain.relToProject"), activity);

				// Create DcRequest for strain collection.
				DcRequest dcReqStrainColl = new DcRequest();
				dcReqStrainColl.setCollection(strainColl);
				dcRequestsMap.put(dcReqStrainColl, projectRel);

				// Collection DcRequest for each animal within this strain.
				List<Animal> animals = iStrain.getAnimals();
				if (animals != null)
				{
					for (Animal iAnimal : animals)
					{
						// Animal as collection
						Collection animalColl = new Collection();
						if (iAnimal.getAnudcId() != null)
							animalColl.setPid(iAnimal.getAnudcId());
						animalColl.setExtIds(Arrays.asList(iAnimal.getExtId()));
						animalColl.setTitle(iAnimal.getTitle());
						animalColl.setBriefDesc(iAnimal.getBriefDesc());

						// Animal constants
						animalColl.setOwnerGroup(constants.getProperty("animal.ownerGroup"));
						animalColl.setSubType(constants.getProperty("animal.subType"));
						animalColl.setTemplate(constants.getProperty("animal.tmplt"));
						animalColl.setAnzForCodes(Arrays.asList(constants.getProperty("animal.anzforSubjects").split(";")));
						animalColl.setEmails(Arrays.asList(constants.getProperty("animal.emails").split(";")));

						// Relation to strain.
						Map<String, FedoraItem> strainRel = new HashMap<String, FedoraItem>();
						strainRel.put(constants.getProperty("animal.relToStrain"), strainColl);

						// Create DcRequest for animal collection.
						DcRequest dcReqAnimalColl = new DcRequest();
						dcReqAnimalColl.setCollection(animalColl);
						dcRequestsMap.put(dcReqAnimalColl, strainRel);

						List<Instrument> instruments = iAnimal.getInstruments();
						if (instruments != null)
						{
							for (Instrument iInstr : iAnimal.getInstruments())
							{
								// Instrument as collection.
								Collection instrumentColl = new Collection();
								if (iInstr.getAnudcId() != null)
									instrumentColl.setPid(iInstr.getAnudcId());
								instrumentColl.setExtIds(Arrays.asList(iInstr.getExtId()));
								instrumentColl.setTitle(iInstr.getTitle());
								instrumentColl.setBriefDesc(iInstr.getBriefDesc());
								instrumentColl.setFileUrlList(iInstr.getFileUrlList());

								// Instrument constants.
								instrumentColl.setOwnerGroup(constants.getProperty("instrument.ownerGroup"));
								instrumentColl.setSubType(constants.getProperty("instrument.subType"));
								instrumentColl.setTemplate(constants.getProperty("instrument.tmplt"));
								instrumentColl.setAnzForCodes(Arrays.asList(constants.getProperty("instrument.anzforSubjects").split(";")));
								instrumentColl.setEmails(Arrays.asList(constants.getProperty("instrument.emails").split(";")));

								// Relation to strain.
								Map<String, FedoraItem> animalRel = new HashMap<String, FedoraItem>();
								animalRel.put(constants.getProperty("instrument.relToAnimal"), animalColl);

								// Create DcRequest for instrument collection.
								DcRequest dcReqInstrColl = new DcRequest();
								dcReqInstrColl.setCollection(instrumentColl);
								dcRequestsMap.put(dcReqInstrColl, animalRel);
							}
						}
					}
				}
			}
		}

		return dcRequestsMap;
	}
}
