package au.edu.anu.datacommons.doi;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.StringWriter;
import java.net.URI;

import javax.ws.rs.core.UriBuilder;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.datacite.schema.kernel_2.Resource;
import org.datacite.schema.kernel_2.Resource.Creators;
import org.datacite.schema.kernel_2.Resource.Creators.Creator;
import org.datacite.schema.kernel_2.Resource.Identifier;
import org.datacite.schema.kernel_2.Resource.Titles;
import org.datacite.schema.kernel_2.Resource.Titles.Title;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.test.framework.JerseyTest;

public class DoiClientTest extends JerseyTest
{
	private static final Logger LOGGER = LoggerFactory.getLogger(DoiClientTest.class);

	private DoiClient doiClient;
	private String sampleDoi = "10.5072/13/50639BFE25F18";
	
	private static JAXBContext context;
	
	private Marshaller marshaller;
	private Unmarshaller unmarshaller;

	public DoiClientTest()
	{
		super("au.edu.anu.datacommons.doi");
		//		LOGGER.trace("In Constructor");
		//		WebResource webResource = resource();
		//		DoiConfig doiConfig = new DoiConfigImpl(webResource.getURI().toString(), appId);
		//		doiClient = new DoiClient(doiConfig);
		
		doiClient = new DoiClient();
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		context = JAXBContext.newInstance(Resource.class);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception
	{
	}

	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "http://datacite.org/schema/kernel-2.2 http://schema.datacite.org/meta/kernel-2.2/metadata.xsd");
	}

	@After
	public void tearDown() throws Exception
	{
		super.tearDown();
	}

	@Ignore
	public void testMint()
	{
		try
		{
			doiClient.mint("https://datacommons.anu.edu.au:8443/DataCommons/item/anudc:3320", generateSampleResource());
			String respStr = doiClient.getDoiResponseAsString();
			LOGGER.trace(respStr);
		}
		catch (Exception e)
		{
			failOnException(e);
		}
	}

	@Ignore
	public void testUpdate()
	{
		try
		{
			Resource res = new Resource();
			Creators creators = new Creators();
			Creator creator = new Creator();
			creator.setCreatorName("Creator 1");
			creators.getCreator().add(creator);
			res.setCreators(creators);
			
			Titles titles = new Titles();
			Title title = new Title();
			title.setValue("Title 1");
			titles.getTitle().add(title);
			res.setTitles(titles);
			
			res.setPublisher("Publisher 1");
			res.setPublicationYear("1987");
			
			Identifier id = new Identifier();
			id.setValue(sampleDoi);
			id.setIdentifierType("DOI");
			res.setIdentifier(id);
			
			doiClient.update(sampleDoi, null, res);
			Resource newRes = doiClient.getMetadata(sampleDoi);
			String resAsStr = getResourceAsString(newRes);
			LOGGER.trace(resAsStr);
		}
		catch (Exception e)
		{
			failOnException(e);
		}
	}

	@Ignore
	public void testDeactivate()
	{
		try
		{
			doiClient.deactivate(sampleDoi);
			assertTrue(doiClient.getDoiResponseAsString().indexOf("AbsolutePath:" + resource().getURI().toString() + "deactivate.xml/") != -1);
//			assertTrue(doiClient.getDoiResponseAsString().indexOf("QueryParam:app_id=TEST" + appId) != -1);
			assertTrue(doiClient.getDoiResponseAsString().indexOf("QueryParam:doi=" + sampleDoi) != -1);
		}
		catch (Exception e)
		{
			failOnException(e);
		}
	}

	@Ignore
	public void testActivate()
	{
		try
		{
			doiClient.activate(sampleDoi);
			assertTrue(doiClient.getDoiResponseAsString().indexOf("AbsolutePath:" + resource().getURI().toString() + "activate.xml/") != -1);
//			assertTrue(doiClient.getDoiResponseAsString().indexOf("QueryParam:app_id=TEST" + appId) != -1);
			assertTrue(doiClient.getDoiResponseAsString().indexOf("QueryParam:doi=" + sampleDoi) != -1);
		}
		catch (Exception e)
		{
			failOnException(e);
		}
	}

	@Test
	public void testGetDoiMetaData()
	{
		try
		{
			Resource res = doiClient.getMetadata(sampleDoi);
			StringWriter strW = new StringWriter();
			marshaller.marshal(res, strW);
//			assertTrue(doiClient.getDoiResponseAsString().indexOf("AbsolutePath:" + resource().getURI().toString() + "xml.xml/") != -1);
//			assertTrue(doiClient.getDoiResponseAsString().indexOf("QueryParam:doi=" + sampleDoi) != -1);
		}
		catch (Exception e)
		{
			failOnException(e);
		}
	}
	
	private Resource generateSampleResource()
	{
		Resource metadata = new Resource();

		Titles titles = new Titles();
		Title title1 = new Title();
		title1.setValue("Some title without a type");

		titles.getTitle().add(title1);

		metadata.setTitles(titles);

		Creators creators = new Creators();
		metadata.setCreators(creators);
		Creator creator = new Creator();
		creator.setCreatorName("Professor John Smith");

		metadata.getCreators().getCreator().add(creator);

		metadata.setPublisher("Some random publisher");
		metadata.setPublicationYear("2010");

		return metadata;
	}
	
	private String getResourceAsString(Resource res) throws JAXBException
	{
		StringWriter strW = new StringWriter();
		marshaller.marshal(res, strW);
		return strW.toString();
	}

	private void failOnException(Throwable e)
	{
		LOGGER.error(e.getMessage(), e);
		fail(e.getMessage());
	}
}
