package au.edu.anu.datacommons.digitalhumanities.bindings.v0_01;

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
public class DhRequest implements Processable
{
	private static final Logger LOGGER = LoggerFactory.getLogger(DhRequest.class);
	private static PropertiesFile constants;

	static
	{
		try
		{
			constants = new PropertiesFile(new File(Config.DIR, "ws-digitalhumanities/constants.properties"));
		}
		catch (IOException e)
		{
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	private String version;
	private String function;
	private DhProject project;

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
	public DhProject getProject()
	{
		return project;
	}

	public void setProject(DhProject project)
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
				strainColl.setFileUrlList(iStrain.getFileUrlList());

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
