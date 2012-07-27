package au.edu.anu.dcbag.metadata;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

public class MetadataExtractorImpl implements MetadataExtractor
{
	private static final Logger LOGGER = LoggerFactory.getLogger(MetadataExtractorImpl.class);
	
	private Metadata metadata = new Metadata();
	private Map<String, String[]> metadataMap = null;
	
	public MetadataExtractorImpl(InputStream inStream)
	{
		BodyContentHandler textHandler = new BodyContentHandler();
		AutoDetectParser parser = new AutoDetectParser();
		try
		{
			parser.parse(inStream, textHandler, this.metadata);
			inStream.close();
			LOGGER.debug("Parsing using Tika successful.");
			for (String key : metadata.names())
			{
				LOGGER.trace("Property: {}", key);
				for (String value : metadata.getValues(key))
					LOGGER.trace("\tValue: {}", value);
			}
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (SAXException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (TikaException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public MetadataExtractorImpl(File inFile) throws FileNotFoundException
	{
		this(new FileInputStream(inFile));
	}
	
	public Metadata getMetadata()
	{
		return this.metadata;
	}

	@Override
	public Map<String, String[]> getMetadataMap()
	{
		if (metadataMap == null)
		{
			metadataMap = new HashMap<String, String[]>();
			for (String key : metadata.names())
				metadataMap.put(key, metadata.getValues(key));
		}
		
		LOGGER.debug("Returning metadata map with {} values.", metadataMap.size());
		return metadataMap;
	}
}
