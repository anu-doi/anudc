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

package au.edu.anu.dcbag.metadata;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.xmp.XMPMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

public class MetadataExtractorImpl implements MetadataExtractor
{
	private static final Logger LOGGER = LoggerFactory.getLogger(MetadataExtractorImpl.class);

	private Metadata metadata = new Metadata();
	private Map<String, String[]> metadataMap = null;

	public MetadataExtractorImpl(InputStream dataStream) throws IOException, SAXException, TikaException
	{
		parse(dataStream);
	}

	public MetadataExtractorImpl(File inFile) throws IOException, SAXException, TikaException
	{
		this(new FileInputStream(inFile));
	}

	private void parse(InputStream dataStream) throws IOException, SAXException, TikaException
	{
		try
		{
			BodyContentHandler textHandler = new BodyContentHandler(-1);
			AutoDetectParser parser = new AutoDetectParser(new AutoDetectParser(), new FitsParser());
			ParseContext parseContext = new ParseContext();
			parser.parse(dataStream, textHandler, this.metadata, parseContext);
			LOGGER.debug("Parsing using Tika successful. {} metadata elements extracted.", metadata.size());

			if (LOGGER.isTraceEnabled())
			{
				for (String key : metadata.names())
				{
					LOGGER.trace("Property: {}", key);
					for (String value : metadata.getValues(key))
						LOGGER.trace("\tValue: {}", value);
				}
			}
		}
		finally
		{
			dataStream.close();
		}
	}

	@Override
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
		return metadataMap;
	}

	@Override
	public XMPMetadata getXmpMetadata() throws TikaException
	{
		XMPMetadata xmp = null;
		xmp = new XMPMetadata(getMetadata());
		return xmp;
	}
}
