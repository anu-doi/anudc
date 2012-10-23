package au.edu.anu.datacommons.phenomics.bindings.v0_01;

import static java.text.MessageFormat.format;
import static org.junit.Assert.*;

import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.ArrayList;
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

import au.edu.anu.datacommons.webservice.bindings.Link;

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
	public void testPhenRequest()
	{
		try
		{
			JAXBContext context = JAXBContext.newInstance("au.edu.anu.datacommons.phenomics.bindings.v0_01");
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			int counts[] = {1, 1, 1}; 
			
			// Project
			PhenProject proj = new PhenProject();
			proj.setTitle("Title - Project");
			proj.setBriefDesc("Brief Description - Project");
			proj.setExtId("PhenProj");
			proj.setStrains(new ArrayList<Strain>());
			
			for (int i = 0; i < counts[0]; i++)
			{
				Strain strain = new Strain();
				strain.setTitle(format("Title - Strain {0}", i));
				strain.setBriefDesc(format("Brief Description - Strain {0}", i));
				strain.setExtId(format("ExtId - Strain {0}", i));
				strain.setAnimals(new ArrayList<Animal>());
				proj.getStrains().add(strain);

				
				for (int j = 0; j < counts[1]; j++)
				{
					Animal animal = new Animal();
					animal.setTitle(format("Title - Animal {0} Strain {1}", j, i));
					animal.setBriefDesc(format("Brief Description - Animal {0} Strain {1}", j, i));
					animal.setExtId(format("ExtId - Animal {0} Strain {1}", j, i));
					animal.setInstruments(new ArrayList<Instrument>());
					strain.getAnimals().add(animal);
					
					for (int k = 0; k < counts[1]; k++)
					{
						Instrument instrument = new Instrument();
						instrument.setTitle(format("Title - Instrument {0} Animal {1} Strain {2}", k, j, i));
						instrument.setBriefDesc(format("Brief Description - Instrument {0} Animal {1} Strain {2}", k, j, i));
						instrument.setExtId(format("ExtId - Instrument {0} Animal {1} Strain {2}", k, j, i));
						
						Link file1 = new Link();
						file1.setUrl("http://online.wsj.com/public/resources/documents/Reprint_Samples.pdf");
						file1.setFilename("Reprint samples.pdf");
						Link file2 = new Link();
						file2.setUrl("http://www.stluciadance.com/prospectus_file/sample.pdf");
						file2.setRefOnly(Boolean.TRUE);
						instrument.setFileUrlList(Arrays.asList(file1, file2));

						animal.getInstruments().add(instrument);
					}
				}
			}
			
			PhenRequest req = new PhenRequest();
			req.setFunction("phenomics-project");
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
