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

package au.edu.anu.datacommons.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import au.edu.anu.datacommons.properties.GlobalProps;

/**
 * Util
 * 
 * Australian National University Data Commons
 * 
 * Utility class
 * 
 * JUnit coverage: None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.2		19/03/2012	Genevieve Turner (GT)	Added isNotEmpty function.
 * 0.3		26/04/2012	Genevieve Turner (GT)	Added convertArrayValueToList
 * 0.4		4/05/2012	Rahul Khanna (RK)		Added generatePassword.
 * 0.5		24/07/2012	Genevieve Turner (GT)	Added listToStringWithNewline
 * </pre>
 */
public final class Util
{
	static final Logger LOGGER = LoggerFactory.getLogger(Util.class);
	
	public static void writeXmlToWriter(Document inDoc, Writer xmlWriter)
	{
		Transformer transformer;
		try
		{
			transformer = TransformerFactory.newInstance().newTransformer();
			DOMSource source = new DOMSource(inDoc);

			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.transform(source, new StreamResult(xmlWriter));
		}
		catch (TransformerConfigurationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (TransformerFactoryConfigurationError e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (TransformerException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return;
	}

	public static String getXmlAsString(Document inDoc)
	{
		StringWriter stringWriter = new StringWriter();
		writeXmlToWriter(inDoc, stringWriter);

		return stringWriter.toString();
	}

	/**
	 * isNotEmpty
	 * 
	 * Sets the options elements of the item
	 * 
	 * Version Date Developer Description 0.1 13/03/2012 Genevieve Turner Initial build
	 * 
	 * @param value
	 *            The value to check if the field is empty
	 * @return Returns true if the value is not blank
	 */
	public static boolean isNotEmpty(String value)
	{
		if (value == null)
		{
			return false;
		}
		if (value.trim().equals(""))
		{
			return false;
		}
		return true;
	}

	/**
	 * convertArrayValueToList
	 * 
	 * Converts a map with a string array as a value to a list of strings
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.3		26/04/2012	Genevieve Turner (GT)	Initial add
	 * </pre>
	 * 
	 * @param map
	 *            A map to convert
	 * @return The converted map
	 */
	public static Map<String, List<String>> convertArrayValueToList(Map<String, String[]> map)
	{
		Map convertedMap = new HashMap<String, List<String>>();

		for (String key : map.keySet())
		{
			String[] value = map.get(key);
			convertedMap.put(key, Arrays.asList(value));
		}

		return convertedMap;
	}

	/**
	 * generatePassword
	 * 
	 * Australian National University Data Commons
	 * 
	 * Generates a password of a specified length using characters specified in global properties.
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		4/05/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 * @param length
	 *            Number of characters in password.
	 * 
	 * @return Password as String.
	 */
	public static String generateRandomString(int length)
	{
		Random rand = new Random();
		StringBuilder password = new StringBuilder();

		// Get valid list of characters from global properties that the password can comprise of.
		char[] passwordChars = GlobalProps.getProperty(GlobalProps.PROP_PASSWORDGENERATOR_CHARS).toCharArray();

		// Pick a random character from the character array and add it to the password until desired password length is reached.
		for (int i = 0; i < length; i++)
			password.append(passwordChars[rand.nextInt(passwordChars.length)]);

		return password.toString();
	}
	
	/**
	 * decodeUrlEncoded
	 * 
	 * Decodes a url encoded string for example 'test%3a92' would return 'test:92'
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.5		08/05/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @param encoded
	 *            Encoded string
	 * @return Decoded string
	 */
	public static String decodeUrlEncoded(String encoded) {
		String decoded = null;
		try {
			decoded = URLDecoder.decode(encoded, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error("Error urldecoding string: {}. Error: {}", encoded, e.getMessage());
		}
		return decoded;
	}
	
	/**
	 * computeMessageDigest
	 * 
	 * Australian National University Data Commons
	 * 
	 * Computes the message digest of a file using a specified algorithm.
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		11/05/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 * @param sourceFile
	 *            The file whose digest is to be calculated as File object.
	 * @param algorithm
	 *            Digest algorithm. Valid values: "MD5", "SHA-1", "SHA-256", "SHA-512". Ref
	 *            http://docs.oracle.com/javase/7/docs/technotes/guides/security/StandardNames.html#MessageDigest
	 * @return Computed digest value as String.
	 * @throws IOException
	 *             When the file object cannot be found or cannot be read.
	 * @throws NoSuchAlgorithmException
	 *             When the algorithm specified doesn't exist.
	 */
	public static String computeMessageDigest(File sourceFile, String algorithm) throws IOException, NoSuchAlgorithmException
	{
		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(sourceFile));
		MessageDigest digest = MessageDigest.getInstance(algorithm);
		byte[] buffer = new byte[8192];
		int numBytesRead = 0;
	
		// Read through the file, updating the digest value.
		while ((numBytesRead = bis.read(buffer)) > 0)
		{
			digest.update(buffer, 0, numBytesRead);
		}
	
		// Complete the hash computation and get the digest as byte[].
		byte[] hashValue = digest.digest();
		
		// Convert hash value to a hex string and return it. 
		return new BigInteger(1, hashValue).toString(16);
	}
	
	/**
	 * listToStringWithNewline
	 *
	 * Converts a list of strings to a single string with a newline as a seperator
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.5		24/07/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param messages
	 * @return
	 */
	public static String listToStringWithNewline(List<String> messages) {
		StringBuffer stringBuffer = new StringBuffer();
		for (String message : messages) {
			stringBuffer.append(message);
			stringBuffer.append("\n");
		}
		return stringBuffer.toString();
	}
}
