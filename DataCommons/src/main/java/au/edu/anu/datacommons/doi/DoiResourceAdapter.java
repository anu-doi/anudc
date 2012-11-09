package au.edu.anu.datacommons.doi;

import org.datacite.schema.kernel_2.RelatedIdentifierType;
import org.datacite.schema.kernel_2.Resource;
import org.datacite.schema.kernel_2.Resource.Creators;
import org.datacite.schema.kernel_2.Resource.Creators.Creator;
import org.datacite.schema.kernel_2.Resource.Identifier;
import org.datacite.schema.kernel_2.Resource.Titles;
import org.datacite.schema.kernel_2.Resource.Titles.Title;
import org.datacite.schema.kernel_2.TitleType;

import au.edu.anu.datacommons.xml.data.Data;
import au.edu.anu.datacommons.xml.data.DataItem;

public class DoiResourceAdapter
{
	private Resource doiResource = new Resource();

	public DoiResourceAdapter(Data itemData) throws DoiException
	{
		generateResource(itemData);
	}

	public void generateResource(Data itemData) throws DoiException
	{
		// Mandatory fields.
		Creators creators = getCreators(itemData);
		if (creators.getCreator().size() == 0)
			throw new DoiException("No creators provided.");
		doiResource.setCreators(creators);
		
		Titles titles = getTitles(itemData);
		if (titles.getTitle().size() == 0)
			throw new DoiException("No titles provided.");
		doiResource.setTitles(titles);
		
		String publisher = getPublisher(itemData);
		if (publisher == null || publisher.length() == 0)
			throw new DoiException("No publisher provided.");
		doiResource.setPublisher(publisher);
		
		String publicationYear = getPublicationYear(itemData);
		if (publicationYear == null || publicationYear.length() == 0)
			throw new DoiException("No publication year provided.");
		doiResource.setPublicationYear(publicationYear);
		
		// Optional fields.
		String doi = getDoi(itemData);
		if (doi != null && doi.length() > 0)
		{
			Identifier identifier = new Identifier();
			identifier.setIdentifierType("DOI");
			identifier.setValue(doi);
			doiResource.setIdentifier(identifier);
		}
	}

	private Creators getCreators(Data itemData)
	{
		Creators creators = new Creators();

		DataItem citationCreators = itemData.getFirstElementByName("citationCreator");
		String[] creatorsStr = citationCreators.getValue().split(";");
		for (int i = 0; i < creatorsStr.length; i++)
		{
			Creator creator = new Creator();
			creator.setCreatorName(creatorsStr[i].trim());
			creators.getCreator().add(creator);
		}
		
		return creators;
	}
	
	private Titles getTitles(Data itemData)
	{
		Titles titles = new Titles();

		DataItem titleDataItem = itemData.getFirstElementByName("name");
		if (titleDataItem != null)
		{
			Title title = new Title();
			title.setValue(titleDataItem.getValue());
			titles.getTitle().add(title);
		}
		
		DataItem altTitleDataItem = itemData.getFirstElementByName("altName");
		if (altTitleDataItem != null)
		{
			Title title = new Title();
			title.setTitleType(TitleType.ALTERNATIVE_TITLE);
			title.setValue(altTitleDataItem.getValue());
			titles.getTitle().add(title);
		}
		
		return titles;
	}

	private String getPublisher(Data itemData)
	{
		String publisher = null;
		
		DataItem publisherDI = itemData.getFirstElementByName("citationPublisher");
		if (publisherDI != null)
			publisher = publisherDI.getValue();
		
		return publisher;
	}

	private String getPublicationYear(Data itemData)
	{
		String publicationYear = null;

		DataItem publicationYearDI = itemData.getFirstElementByName("citationYear");
		if (publicationYearDI != null)
			publicationYear = publicationYearDI.getValue();
		return publicationYear;
	}
	
	private String getDoi(Data itemData)
	{
		String doi = null;
		
		DataItem doiDI = itemData.getFirstElementByName("doi");
		if (doiDI != null)
			doi = doiDI.getValue();
		return doi;
	}

	public Resource getDoiResource()
	{
		return this.doiResource;
	}
}
