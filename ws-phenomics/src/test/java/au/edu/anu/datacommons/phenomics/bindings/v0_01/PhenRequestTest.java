package au.edu.anu.datacommons.phenomics.bindings.v0_01;

import static org.junit.Assert.*;

import java.io.StringWriter;
import java.util.Arrays;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PhenRequestTest
{
	private static final Logger LOGGER = LoggerFactory.getLogger(PhenRequestTest.class);
	
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
	}

	@After
	public void tearDown() throws Exception
	{
	}

	@Test
	public void test()
	{
		try
		{
			JAXBContext context = JAXBContext.newInstance("au.edu.anu.datacommons.phenomics.bindings.v0_01");
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			
			PhenProject proj = new PhenProject();
			proj.setTitle("Some Title");
			proj.setBriefDesc("Brief descr");
			
			Strain strain1 = new Strain();
			strain1.setTitle("Strain title 1");
			strain1.setBriefDesc("Strain Brief Description 1");
			
			Barcode bc11 = new Barcode();
			bc11.setTitle("Barcode 1 Title for strain 1");
			bc11.setBriefDesc("Barcode 1 Brief Description for strain 1");
			
			Barcode bc12 = new Barcode();
			bc12.setTitle("Barcode 2 Title for strain 1");
			bc12.setBriefDesc("Barcode 2 Brief Description for strain 1");
			strain1.setBarcodes(Arrays.asList(bc11, bc12));
			
			Strain strain2 = new Strain();
			strain2.setTitle("Strain title 2");
			strain2.setBriefDesc("Strain Brief Description 2");
			
			Barcode bc21 = new Barcode();
			bc21.setTitle("Barcode 1 Title for strain 2");
			bc21.setBriefDesc("Barcode 1 Brief Description for strain 2");
			
			Barcode bc22 = new Barcode();
			bc22.setTitle("Barcode 2 Title for strain 2");
			bc22.setBriefDesc("Barcode 2 Brief Description for strain 2");
			strain2.setBarcodes(Arrays.asList(bc21, bc22));
			
			proj.setStrains(Arrays.asList(strain1, strain2));
			
			PhenRequest req = new PhenRequest();
			req.setFunction("sync");
			req.setVersion("0.01");
			req.setProject(proj);
			
			StringWriter str = new StringWriter();
			m.marshal(req, str);
			LOGGER.trace(str.toString());
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
