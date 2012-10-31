package au.edu.anu.datacommons.doi;

import java.text.MessageFormat;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "response")
public class DoiResponse
{
	private String type;
	private String code;
	private String message;
	private String doi;
	private String url;
	private String appId;
	private String verboseMsg;

	@XmlAttribute(name = "type")
	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	@XmlElement(name = "responsecode")
	public String getCode()
	{
		return code;
	}

	public void setCode(String code)
	{
		this.code = code;
	}

	@XmlElement(name = "message")
	public String getMessage()
	{
		return message;
	}

	public void setMessage(String message)
	{
		this.message = message;
	}

	@XmlElement(name = "doi")
	public String getDoi()
	{
		return doi;
	}

	public void setDoi(String doi)
	{
		this.doi = doi;
	}

	@XmlElement(name = "doi")
	public String getUrl()
	{
		return url;
	}

	public void setUrl(String url)
	{
		this.url = url;
	}

	@XmlElement(name = "app_id")
	public String getAppId()
	{
		return appId;
	}

	public void setAppId(String appId)
	{
		this.appId = appId;
	}

	@XmlElement(name = "verbosemessage")
	public String getVerboseMsg()
	{
		return verboseMsg;
	}

	public void setVerboseMsg(String verboseMsg)
	{
		this.verboseMsg = verboseMsg;
	}

	@Override
	public String toString()
	{
		return MessageFormat.format("DOI Service Response: type={0}, code={1}, message={2}, doi={3}, url={4}, app_id={5}, verbosemessage={6}.", this.getType(),
				this.getCode(), this.getMessage(), this.getDoi(), this.getUrl(), this.getAppId(), this.getVerboseMsg());
	}
}
