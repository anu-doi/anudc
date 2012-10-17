package au.edu.anu.datacommons.webservice.bindings;

import javax.xml.bind.annotation.XmlElement;

public class DateCoverage
{
	private String dateFrom;
	private String dateTo;

	@XmlElement(name = "dateFrom")
	public String getDateFrom()
	{
		return dateFrom;
	}

	public void setDateFrom(String dateFrom)
	{
		this.dateFrom = dateFrom;
	}

	@XmlElement(name = "dateTo")
	public String getDateTo()
	{
		return dateTo;
	}

	public void setDateTo(String dateTo)
	{
		this.dateTo = dateTo;
	}
}
