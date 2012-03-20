/**
 * 
 */
package au.edu.anu.datacommons.util;

import java.io.StringWriter;
import java.io.Writer;

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
 * @author Rahul Khanna
 *
 */
public final class Utils
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
}
