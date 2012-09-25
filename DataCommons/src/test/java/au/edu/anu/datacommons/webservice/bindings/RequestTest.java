package au.edu.anu.datacommons.webservice.bindings;

import static org.junit.Assert.*;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.JAXBIntrospector;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class RequestTest
{
	private static final Logger LOGGER = LoggerFactory.getLogger(RequestTest.class);
	
	private JAXBContext context;
	private Marshaller m;
	private Unmarshaller um;
	private StringWriter strWriter = null;
	private StringReader strReader = null;
	private JAXBIntrospector introspector; 

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
		context = JAXBContext.newInstance("au.edu.anu.datacommons.webservice.bindings");
		introspector = context.createJAXBIntrospector();
		m = context.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		um = context.createUnmarshaller();
	}

	@After
	public void tearDown() throws Exception
	{
	}

	@Test
	public void testMarshall()
	{
		// Request object.
		Request request = new Request();
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
		emails.add("rahul.khanna@anu.edu.au");
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
		List<Related> relatedList = new ArrayList<Related>();
		Related rel1 = new Related();
		rel1.setRelatedWebTitle("Google");
		rel1.setRelatedWebUrl("http://www.google.com.au");
		relatedList.add(rel1);
		Related rel2 = new Related();
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
	
	private void failOnException(Throwable e)
	{
		LOGGER.error(e.getMessage(), e);
		fail(e.getMessage());
	}
}
