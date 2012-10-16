package au.edu.anu.datacommons.phenomics.bindings.v0_01;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

public class Strain
{
	private String title;
	private String briefDesc;
	private List<Barcode> barcodes;

	@XmlElement(name = "title")
	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	@XmlElement(name = "briefDesc")
	public String getBriefDesc()
	{
		return briefDesc;
	}

	public void setBriefDesc(String briefDesc)
	{
		this.briefDesc = briefDesc;
	}

	@XmlElementWrapper(name = "barcodes")
	@XmlElement(name = "barcode")
	public List<Barcode> getBarcodes()
	{
		return barcodes;
	}

	public void setBarcodes(List<Barcode> barcodes)
	{
		this.barcodes = barcodes;
	}
}
