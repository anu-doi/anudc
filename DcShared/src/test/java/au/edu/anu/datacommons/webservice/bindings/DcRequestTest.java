package au.edu.anu.datacommons.webservice.bindings;

import static org.junit.Assert.*;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DcRequestTest
{
	private static final Logger LOGGER = LoggerFactory.getLogger(DcRequestTest.class);

	private JAXBContext context;
	private Marshaller m;
	private Unmarshaller um;
	private StringWriter strWriter = null;
	private StringReader strReader = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception
	{
	}

	@Before
	public void setUp() throws Exception
	{
		ClassLoader cl = this.getClass().getClassLoader();
		context = JAXBContext.newInstance("au.edu.anu.datacommons.webservice.bindings", cl);
		m = context.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		um = context.createUnmarshaller();
	}

	@After
	public void tearDown() throws Exception
	{
	}

	@Test
	public void testActivity()
	{
		// Request object.
		DcRequest request = new DcRequest();
		request.setMethodName("createProject");

		Activity activity = new Activity();

		activity.setTemplate("tmplt:2");

		// Tab - General.
		activity.setSubType("project");
		activity.setOwnerGroup("1");
		activity.setTitle("Test Activity");
		activity.setAbbrTitle("Abbr Title");
		activity.setAltTitle("Alt Title");
		activity.setArcNumber("123456");
		// Funding bodies.
		List<String> fBodies = new ArrayList<String>();
		fBodies.add("Some random funding body.");
		activity.setFundingBodies(fBodies);

		// Tab - Description.
		activity.setBriefDesc("Brief description.");
		activity.setFullDesc("Full description.");

		// Tab - Contact
		List<String> emails = new ArrayList<String>();
		emails.add("test@anu.edu.au");
		emails.add("second@anu.edu.au");
		activity.setEmails(emails);
		activity.setContactAddress("123 Some Rd, Acton ACT 2601");
		// Phones
		List<String> phones = new ArrayList<String>();
		phones.add("(02) 1234-5678");
		phones.add("(03) 9874-5612");
		activity.setPhones(phones);
		// Faxes
		List<String> faxes = new ArrayList<String>();
		faxes.add("(02) 7896-1234");
		faxes.add("(02) 4563-1234");
		activity.setFaxes(faxes);
		// Websites
		List<String> websites = new ArrayList<String>();
		websites.add("http://projectname.anu.edu.au/");
		websites.add("http://proj2.anu.edu.au/");
		activity.setWebsites(websites);

		// Tab - Subject.
		// Anz For codes.
		List<String> anzForCodes = new ArrayList<String>();
		anzForCodes.add("50305");
		anzForCodes.add("12345");
		activity.setAnzForCodes(anzForCodes);
		// Anz Seo codes.
		List<String> anzSeoCodes = new ArrayList<String>();
		anzSeoCodes.add("45677");
		anzSeoCodes.add("78945");
		activity.setAnzSeoCodes(anzSeoCodes);
		// Keywords.
		List<String> keywords = new ArrayList<String>();
		keywords.add("kw1");
		keywords.add("kw2");
		activity.setKeywords(keywords);
		// Type of Research
		activity.setResearchType("sbr");

		// Tab - Related Information
		// Publications.
		List<Publication> pubList = new ArrayList<Publication>();
		Publication pub1 = new Publication();
		pub1.setIdType("issn");
		pub1.setTitle("Pub1 title");
		pub1.setId("Pub1 identifier");
		pubList.add(pub1);
		activity.setPublications(pubList);
		// Related
		List<RelatedWebsites> relatedList = new ArrayList<RelatedWebsites>();
		RelatedWebsites rel1 = new RelatedWebsites();
		rel1.setRelatedWebTitle("Google");
		rel1.setRelatedWebUrl("http://www.google.com.au");
		relatedList.add(rel1);
		RelatedWebsites rel2 = new RelatedWebsites();
		rel2.setRelatedWebTitle("Facebook");
		rel2.setRelatedWebUrl("http://www.facebook.com");
		relatedList.add(rel2);
		activity.setRelated(relatedList);

		request.setActivity(activity);

		try
		{
			// Marshall the request object.
			strWriter = new StringWriter();
			m.marshal(request, strWriter);
			strWriter.flush();
			LOGGER.trace(strWriter.toString());
		}
		catch (JAXBException e)
		{
			failOnException(e);
		}
	}

	/**
	 * Marshalls and unshalls a Request object containing a collection.
	 */
	@Test
	public void testCollection()
	{
		DcRequest request = new DcRequest();

		Collection coll = new Collection();
		coll.setTemplate("tmplt:1");
		coll.setTitle("Title 1");
		coll.setBriefTitle("Brief Title 1");
		coll.setAltTitle("Alternate Title 1");
		coll.setSubType("collection");
		coll.setOwnerGroup("1");
		coll.setMetadataLanguage("en");
		coll.setDataLanguage("en");

		DateCoverage dateCov1 = new DateCoverage();
		dateCov1.setDateFrom("1/1/2010");
		dateCov1.setDateTo("31/21/2010");

		DateCoverage dateCov2 = new DateCoverage();
		dateCov2.setDateFrom("1/1/2011");
		dateCov2.setDateTo("31/12/2011");
		coll.setDateCoverage(Arrays.asList(dateCov1, dateCov2));

		String covDateTxt1 = "Coverage of WW1";
		String covDateTxt2 = "Coverage of WW2";
		coll.setCoverageDateTextList(Arrays.asList(covDateTxt1, covDateTxt2));

		GeospatialLocation gl1 = new GeospatialLocation();
		gl1.setCovAreaType("text");
		gl1.setCovAreaValue("Some location");

		GeospatialLocation gl2 = new GeospatialLocation();
		gl2.setCovAreaType("text");
		gl2.setCovAreaValue("Some location 2");
		coll.setGeospatialLocations(Arrays.asList(gl1, gl2));

		coll.setSignificanceStatement("This collection is significant");
		coll.setBriefDesc("Brief Description of this collection.");
		coll.setFullDesc("Full description of this collection.");
		coll.setCitationType("AMA");
		coll.setCitationText("Some citation text as per AMA.");

		Publication pub1 = new Publication();
		pub1.setIdType("issn");
		pub1.setTitle("Pub1 title");
		pub1.setId("Pub1 identifier");

		coll.setPublications(Arrays.asList(pub1));

		RelatedWebsites rel1 = new RelatedWebsites();
		rel1.setRelatedWebTitle("Google");
		rel1.setRelatedWebUrl("http://www.google.com.au");

		RelatedWebsites rel2 = new RelatedWebsites();
		rel2.setRelatedWebTitle("Facebook");
		rel2.setRelatedWebUrl("http://www.facebook.com");
		coll.setRelated(Arrays.asList(rel1, rel2));
		
		coll.setExtIds(Arrays.asList("ExtId1", "ExtId2"));

		coll.setEmails(Arrays.asList("email1@anu.edu.au", "email2.anu.edu.au"));
		coll.setContactAddress("Some place for contact.");
		coll.setPhones(Arrays.asList("123456789", "987654321"));
		coll.setFaxes(Arrays.asList("321654987", "789456123"));
		coll.setWebsites(Arrays.asList("http://test.anu.edu.au", "http://test2.anu.edu.au"));
		coll.setPrincipalInvestigators(Arrays.asList("Principal Investigator 1", "Principal Investigator 2", "Principal Investigator 3"));
		coll.setSupervisors(Arrays.asList("Super 1", "Super 2"));
		coll.setCollaborators(Arrays.asList("Collaborator 1", "Collaborator 2"));

		coll.setAnzForCodes(Arrays.asList("060203", "080299"));
		coll.setAnzSeoCodes(Arrays.asList("961003", "909899"));
		coll.setKeywords(Arrays.asList("Keyword 1", "Keyword 2"));
		coll.setResearchType("ar");

		coll.setAccessRights("Access Rights for this collection");
		coll.setRightsStatement("Rights Statement for this collection");
		coll.setLicenceType("CC-BY-ND");
		coll.setLicence("Licence for this collection.");

		coll.setDataLocation("USB stick under my desk");
		coll.setRetentionPeriod("Indefinitely");
		coll.setDisposalDate("31/12/2015");
		coll.setDataExtent("100 files");
		coll.setDataSize("100 GB");
		coll.setDataMgmtPlan(Boolean.TRUE);
		
		Link file1 = new Link();
		file1.setUrl("http://online.wsj.com/public/resources/documents/Reprint_Samples.pdf");
		file1.setFilename("Reprint samples.pdf");
		Link file2 = new Link();
		file2.setUrl("http://www.stluciadance.com/prospectus_file/sample.pdf");
		file2.setRefOnly(Boolean.TRUE);
		coll.setFileUrlList(Arrays.asList(file1, file2));

		request.setCollection(coll);

		StringWriter xmlStr = new StringWriter();
		try
		{
			m.marshal(request, xmlStr);
			LOGGER.trace(xmlStr.toString());

			StringReader strReader = new StringReader(xmlStr.toString());
			DcRequest recreatedRequest = (DcRequest) um.unmarshal(strReader);
			StringWriter recreatedXmlStr = new StringWriter();
			m.marshal(recreatedRequest, recreatedXmlStr);
			LOGGER.trace(recreatedXmlStr.toString());
			assertEquals(xmlStr.toString(), recreatedXmlStr.toString());
			
			Map<String, List<String>> dataMap = request.getCollection().generateDataMap();
			for (Entry<String, List<String>> entry : dataMap.entrySet())
			{
				LOGGER.trace(entry.getKey());
				for (String value : entry.getValue())
					LOGGER.trace("\t{}", value);
			}
		}
		catch (JAXBException e)
		{
			failOnException(e);
		}

	}

	private void failOnException(Throwable e)
	{
		LOGGER.error(e.getMessage(), e);
		fail(e.getMessage());
	}
}
