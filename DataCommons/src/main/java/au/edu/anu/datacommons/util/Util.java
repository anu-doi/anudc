/**
 * 
 */
package au.edu.anu.datacommons.util;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

/**
 * Util
 * 
 * Australian National University Data Commons
 * 
 * Utility class
 * 
 * JUnit coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.2		19/03/2012	Genevieve Turner (GT)	Added isNotEmpty function.
 * 0.3		26/04/2012	Genevieve Turner (GT)	Added convertArrayValueToList
 * </pre>
 */
public final class Util
{
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
	 * Version	Date		Developer			Description
	 * 0.1		13/03/2012	Genevieve Turner	Initial build
	 * 
	 * @param value The value to check if the field is empty
	 * @return Returns true if the value is not blank
	 */
	public static boolean isNotEmpty(String value){
		if(value == null){
			return false;
		}
		if(value.trim().equals("")){
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
	 * @param map A map to convert
	 * @return The converted map
	 */
	public static Map<String, List<String>> convertArrayValueToList(Map<String, String[]> map) {
		Map convertedMap = new HashMap<String, List<String>>();
		
		for (String key : map.keySet()) {
			String[] value = map.get(key);
			convertedMap.put(key, Arrays.asList(value));
		}
		
		return convertedMap;
	}
}
