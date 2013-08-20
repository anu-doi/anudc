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

package au.edu.anu.datacommons.storage.completer.metadata;

import java.io.IOException;
import java.io.InputStream;
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

/**
 * This class implements the Apache Tika Parser interface allowing parsing of FITS files. A FITS file's header consists
 * of keys, values, and comments. Because Apache Tika requires a key and a value only, the comment (if any) is
 * contatenated to the value of its respective key using the slash '/' character is a separator.
 * 
 * @see <a href="http://en.wikipedia.org/wiki/FITS">http://en.wikipedia.org/wiki/FITS</a>
 * @author Rahul Khanna
 */
public class FitsParser implements Parser {
	private static final long serialVersionUID = 1L;
	private static final Set<MediaType> SUPPORTED_TYPES = Collections.singleton(MediaType.application("fits"));
	public static final String FITS_MIME_TYPE = "application/fits";

	@Override
	public Set<MediaType> getSupportedTypes(ParseContext context) {
		return SUPPORTED_TYPES;
	}

	@Override
	public void parse(InputStream stream, ContentHandler handler, Metadata metadata, ParseContext context)
			throws IOException, SAXException, TikaException {
		metadata.set(Metadata.CONTENT_TYPE, FITS_MIME_TYPE);

		Pattern p = Pattern.compile("^= \'([^\']*)\'[\\s]+/\\s(.*)$");
		Fits fits;

		try {
			fits = new Fits(stream);
			Header header = fits.getHDU(0).getHeader();
			Cursor cursor = header.iterator();

			while (cursor.hasNext()) {
				HeaderCard obj = (HeaderCard) cursor.next();
				String key = obj.getKey();
				String value = obj.getValue();
				String comment = obj.getComment();

				if (isKeyBlank(key)) {
					continue;
				}

				// If the value's null, sometimes the comment contains the value and the comment separated by a '/'
				if (value == null) {
					Matcher m = p.matcher(comment);
					if (m.groupCount() == 2 && m.find()) {
						value = m.group(1);
						comment = m.group(2);
					}
				}

				if (comment != null) {
					comment = comment.trim();
				}
					
				StringBuilder formattedValue = new StringBuilder();
				if (value != null) {
					formattedValue.append(value);
				}

				// Because metadata can take only a key and a value, appending the comment to the value string.
				if (comment != null) {
					if (formattedValue.length() > 0) {
						formattedValue.append(" / ");
					}
					formattedValue.append(comment);
				}

				metadata.add(key, formattedValue.toString());
			}
		} catch (FitsException e) {
			throw new IOException(e);
		}
	}

	/**
	 * Checks if a key is null, zero-length or the literal "end".
	 * 
	 * @param key
	 *            Key to check
	 * @return true if null, zero-length or the literal "end", false otherwise
	 */
	private boolean isKeyBlank(String key) {
		return (key == null || key.length() == 0 || key.equalsIgnoreCase("end"));
	}
}
