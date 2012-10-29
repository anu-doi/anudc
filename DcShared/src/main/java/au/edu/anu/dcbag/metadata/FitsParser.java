package au.edu.anu.dcbag.metadata;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nom.tam.fits.Fits;
import nom.tam.fits.FitsException;
import nom.tam.fits.Header;
import nom.tam.fits.HeaderCard;
import nom.tam.util.Cursor;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.XHTMLContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class FitsParser implements Parser
{
	private static final long serialVersionUID = 1L;
	private static final Set<MediaType> SUPPORTED_TYPES = Collections.singleton(MediaType.application("fits"));
	public static final String FITS_MIME_TYPE = "application/fits";

	@Override
	public Set<MediaType> getSupportedTypes(ParseContext context)
	{
		return SUPPORTED_TYPES;
	}

	@Override
	public void parse(InputStream stream, ContentHandler handler, Metadata metadata, ParseContext context) throws IOException, SAXException, TikaException
	{
		metadata.set(Metadata.CONTENT_TYPE, FITS_MIME_TYPE);

		Pattern p = Pattern.compile("^= \'([^\']*)\'[\\s]+/\\s(.*)$");
		Fits fits;

		try
		{
			fits = new Fits(stream);
			Header header = fits.getHDU(0).getHeader();
			Cursor cursor = header.iterator();
			
			while (cursor.hasNext())
			{
				HeaderCard obj = (HeaderCard) cursor.next();
				String key = obj.getKey();
				String value = obj.getValue();
				String comment = obj.getComment();
				
				if (key == null || key.length() == 0 || key.equalsIgnoreCase("end"))
					continue;
				
				if (value == null)
				{
					Matcher m = p.matcher(comment);
					if (m.groupCount() == 2 && m.find())
					{
						value = m.group(1);
						comment = m.group(2);
					}
				}
				
				if (comment != null)
					comment = comment.trim();
				
				StringBuilder formattedValue = new StringBuilder();
				if (value != null)
				{
					formattedValue.append(value);
				}
				
				if (comment != null)
				{
					if (formattedValue.length() > 0)
						formattedValue.append(" / ");
					formattedValue.append(comment);
				}
				
				metadata.add(key, formattedValue.toString());
			}
			
			XHTMLContentHandler xhtml = new XHTMLContentHandler(handler, metadata);
			xhtml.startDocument();
			xhtml.endDocument();
		}
		catch (FitsException e)
		{
			throw new IOException(e);
		}

	}

}
