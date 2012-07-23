package au.edu.anu.datacommons.upload;

import static org.junit.Assert.*;
import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.BagFactory;
import gov.loc.repository.bagit.BagFactory.LoadOption;
import gov.loc.repository.bagit.writer.Writer;
import gov.loc.repository.bagit.writer.impl.FileSystemWriter;
import gov.loc.repository.bagit.writer.impl.ZipWriter;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;

import javax.ws.rs.core.MediaType;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.multipart.FormDataMultiPart;
import com.sun.jersey.multipart.file.FileDataBodyPart;
import com.sun.jersey.test.framework.AppDescriptor;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.spi.container.TestContainer;

public class UploadServiceTest extends JerseyTest
{
	private static final Logger LOGGER = LoggerFactory.getLogger(UploadServiceTest.class);
	private static BagFactory bf;

	public UploadServiceTest()
	{
		super("au.edu.anu.datacommons.upload");
	}

	@BeforeClass
	public static void oneTimeSetUp()
	{
		bf = new BagFactory();
	}

	@Ignore
	public void testDoPostBag() throws IOException
	{
		// Load a Bag.
		Bag bag = createDummyBag();
		assertNotNull("Bag is null.", bag);

		bag.getBagInfoTxt().addExternalIdentifier("test:4");
		bag = bag.makeComplete();
		assertTrue("Bag incomplete.", bag.verifyComplete().isSuccess());
		assertTrue("Bag invalid.", bag.verifyValid().isSuccess());

		ZipWriter zipWriter = new ZipWriter(bf);
		zipWriter.setCompressionLevel(ZipWriter.DEFAULT_COMPRESSION_LEVEL);

		Writer writer = zipWriter;
		// File tempFile = File.createTempFile("DcTemp", "");
		bag = writer.write(bag, bag.getFile());
		// tempFile.renameTo(bag.getFile());

		// Post the zip file to Upload service.
		WebResource webResource = resource();
		FormDataMultiPart fdmp = new FormDataMultiPart();
		fdmp.bodyPart(new FileDataBodyPart("file", bag.getFile(), MediaType.APPLICATION_OCTET_STREAM_TYPE));

		ClientResponse response = webResource.path("upload").path("bag").path("test:4").type(MediaType.MULTIPART_FORM_DATA_TYPE)
				.post(ClientResponse.class, fdmp);
		assertEquals("HTTP Status should be 200.", 200, response.getStatus());
		LOGGER.info(String.valueOf(response.getStatus()));

		// Clean up
		bag.close();
		bag.getFile().delete();
	}

	@Ignore
	public void testDoGetBag() throws IOException
	{
		// System.in.read();
		WebResource webResource = resource();
		ClientResponse response = webResource.path("upload").path("bag").path("test:5").path("data").path("DcTest226622472490795340")
				.type(MediaType.APPLICATION_OCTET_STREAM_TYPE).get(ClientResponse.class);
		LOGGER.info("HTTP status: " + response.getStatus());
		// System.in.read();
	}
	
	@Test
	public void testServerStartup() throws IOException
	{
		System.in.read();
	}
	
	private Bag createDummyBag() throws IOException
	{
		File payloadFile1 = File.createTempFile("DcTest", "");
		long payloadFile1Size = 1 * 1024 * 1024;
		File payloadFile2 = File.createTempFile("DcTest", "");
		long payloadFile2Size = 2 * 1024 * 1024;

		writeFile(payloadFile1, payloadFile1Size);
		assertEquals("Payload File 1 not of expected size.", payloadFile1Size, payloadFile1.length());
		writeFile(payloadFile2, payloadFile2Size);
		assertEquals("Payload File 2 not of expected size.", payloadFile2Size, payloadFile2.length());

		File bagFile = File.createTempFile("DcTest", "");
		bagFile.renameTo(new File(bagFile.getParent(), "Bag.zip"));
		bagFile = new File(bagFile.getParent(), "Bag.zip");

		Bag bag = bf.createBag();
		bag.addFileToPayload(payloadFile1);
		bag.addFileToPayload(payloadFile2);
		bag = bag.makeComplete();

		assertTrue("Bag is not valid.", bag.verifyValid().isSuccess());

		ZipWriter zipWriter = new ZipWriter(bf);
		zipWriter.setCompressionLevel(ZipWriter.DEFAULT_COMPRESSION_LEVEL);
		bag = bag.write(zipWriter, bagFile);

		// Clean up.
		payloadFile1.delete();
		payloadFile2.delete();

		return bag;
	}
	
	private void writeFile(File file, long size) throws IOException
	{
		byte[] buffer = new byte[8192];
		BufferedOutputStream os = null;
		try
		{
			os = new BufferedOutputStream(new FileOutputStream(file));
			LOGGER.info("Writing file {} of size {}...", file.getAbsolutePath(), size);
			for (long i = 0; i < size / buffer.length; i++)
			{
				for (int j = 0; j < buffer.length; j++)
				{
					Double randomDbl = (Math.random() * 255D);
					buffer[j] = randomDbl.byteValue();
				}
				os.write(buffer);
			}
			LOGGER.info("File written.");
		}
		finally
		{
			if (os != null)
				os.close();
		}
	}
}
