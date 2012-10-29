package au.edu.anu.datacommons.phenomics.bindings;

import java.util.List;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlRootElement;

import org.w3c.dom.Element;

@XmlRootElement (name = "response")
public class PhenResponse
{
	private Status status;
	private String msg;
	private List<Element> statusElements;
	
	public enum Status
	{
		@XmlEnumValue("success")
		SUCCESS,
		
		@XmlEnumValue("partial")
		PARTIAL,
		
		@XmlEnumValue("failure")
		FAILURE;
		
		@Override
		public String toString()
		{
			return super.toString().toLowerCase();
		}
	}

	@XmlAttribute(name = "status")
	public Status getStatus()
	{
		return status;
	}

	public void setStatus(Status status)
	{
		this.status = status;
	}

	@XmlAttribute(name = "message")
	public String getMsg()
	{
		return msg;
	}

	public void setMsg(String msg)
	{
		this.msg = msg;
	}

	@XmlAnyElement
	public List<Element> getStatusElements()
	{
		return statusElements;
	}

	public void setNodes(List<Element> statusElements)
	{
		this.statusElements = statusElements;
	}
}
