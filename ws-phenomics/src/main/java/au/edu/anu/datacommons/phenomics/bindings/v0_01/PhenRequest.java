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
import au.edu.anu.datacommons.phenomics.Processable;
import au.edu.anu.datacommons.webservice.bindings.Activity;
import au.edu.anu.datacommons.webservice.bindings.Collection;
import au.edu.anu.datacommons.webservice.bindings.DcRequest;

@XmlRootElement(name = "request")
public class PhenRequest implements Processable
{
	private static final Logger LOGGER = LoggerFactory.getLogger(PhenRequest.class);
	private static PropertiesFile constants;
	
	static
	{
		try
		{
			constants = new PropertiesFile(new File(Config.DIR, "phenomics-ws/constants.properties"));
		}
		catch (IOException e)
		{
			LOGGER.warn("Unable to read constants file.", e);
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
	public List<DcRequest> generateActivityRequests()
	{
		List<DcRequest> genericRequests = new ArrayList<DcRequest>();
		
		Activity activity = new Activity();
		activity.setTitle(this.getProject().getTitle());
		activity.setBriefDesc(this.getProject().getBriefDesc());
		
		activity.setSubType(constants.getProperty("activity.subType"));
		activity.setOwnerGroup(constants.getProperty("activity.ownerGroup"));
		activity.setTemplate(constants.getProperty("activity.tmplt"));
		
		String[] emails = constants.getProperty("activity.email").split(";");
		activity.setEmails(Arrays.asList(emails));
		
		String[] anzforCodes = constants.getProperty("activity.anzforSubject").split(";");
		activity.setAnzForCodes(Arrays.asList(anzforCodes));
		
		DcRequest genReq = new DcRequest();
		genReq.setActivity(activity);
	
		genericRequests.add(genReq);
		
		return genericRequests;
	}
	
	@Override
	public List<DcRequest> generateCollectionRequests()
	{
		List<DcRequest> genericRequests = new ArrayList<DcRequest>();
		
		List<Strain> strains = this.getProject().getStrains();
		for (Strain iStrain : strains)
		{
			Collection strainColl = new Collection();
		}
		
		return genericRequests;
	}
	
	@Override
	public Map<DcRequest, Map<String, DcRequest>> generateDcRequests()
	{
		Map<DcRequest, Map<String, DcRequest>> dcReqMap = new HashMap<DcRequest, Map<String, DcRequest>>();
		
		// Activity
		Activity activity = new Activity();
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
		dcReqMap.put(dcReqActivity, null);
		
		/*
		// Collection DcRequest for each Strain
		List<Strain> strains = this.getProject().getStrains();
		for (Strain iStrain : strains)
		{
			// Strain as collection
			Collection strainColl = new Collection();
			strainColl.setTitle(iStrain.getTitle());
			strainColl.setBriefDesc(iStrain.getBriefDesc());

			// Strain constants
			strainColl.setOwnerGroup(constants.getProperty("strain.ownerGroup"));
			strainColl.setSubType(constants.getProperty("strain.subType"));
			strainColl.setTemplate(constants.getProperty("strain.tmplt"));
			strainColl.setEmails(Arrays.asList(constants.getProperty("strain.emails").split(";")));
			strainColl.setAnzForCodes(Arrays.asList(constants.getProperty("strain.anzforSubjects").split(";")));
			
			// Relation to project.
			Map<String, DcRequest> projectRel = new HashMap<String, DcRequest>();
			projectRel.put(constants.getProperty("strain.relToProject"), dcReqActivity);
			
			// Create DcRequest for strain collection.
			DcRequest dcReqStrainColl = new DcRequest();
			dcReqStrainColl.setCollection(strainColl);
			dcReqMap.put(dcReqStrainColl, projectRel);
			
			// Collection DcRequest for each barcode within this strain.
			List<Barcode> barcodes = iStrain.getBarcodes();
			for (Barcode iBarcode : barcodes)
			{
				Collection barcodeColl = new Collection();
				barcodeColl.setTitle(iBarcode.getTitle());
				barcodeColl.setBriefDesc(iBarcode.getBriefDesc());
				// TODO Constants
				
				// Relation to strain.
				Map<String, DcRequest> strainRel = new HashMap<String, DcRequest>();
				strainRel.put("isPartOf", dcReqStrainColl);
				
				// Create DcRequest for barcode collection.
				DcRequest dcReqBcColl = new DcRequest();
				dcReqBcColl.setCollection(barcodeColl);
				dcReqMap.put(dcReqBcColl, strainRel);
			}
		}
		*/
		return dcReqMap;
	}
}
