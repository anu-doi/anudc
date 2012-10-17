package au.edu.anu.datacommons.webservice.bindings;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "request")
public class DcRequest
{
	private String methodName;
	private Activity activity;
	private Collection collection;
	
	@XmlAttribute
	public String getMethodName()
	{
		return methodName;
	}

	public void setMethodName(String methodName)
	{
		this.methodName = methodName;
	}
	
	@XmlElement
	public Activity getActivity()
	{
		return activity;
	}

	public void setActivity(Activity activity)
	{
		this.activity = activity;
	}
	
	@XmlElement
	public Collection getCollection()
	{
		return collection;
	}
	
	public void setCollection(Collection collection)
	{
		this.collection = collection;
	}

	@Override
	public String toString()
	{
		String newLine = System.getProperty("line.separator");
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append(MessageFormat.format("methodName: {0}{1}", methodName, newLine));
		if (activity != null)
			strBuilder.append("Contains Activity object.");
		
		return strBuilder.toString();
	}

}
