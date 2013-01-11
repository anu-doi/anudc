package au.edu.anu.datacommons.doi;

import org.datacite.schema.kernel_2.Resource;
import org.datacite.schema.kernel_2.Resource.Creators;
import org.datacite.schema.kernel_2.Resource.Creators.Creator;
import org.datacite.schema.kernel_2.Resource.Identifier;
import org.datacite.schema.kernel_2.Resource.Titles;
import org.datacite.schema.kernel_2.Resource.Titles.Title;
import org.datacite.schema.kernel_2.TitleType;

import au.edu.anu.datacommons.xml.data.Data;
import au.edu.anu.datacommons.xml.data.DataItem;

/**
 * This class accepts a Data item and creates a new {@link Resource} object with the details of the record. The Resource object can then be
 * marshalled into XML as per DataCite's schema used in DOI requests.
 */
public class DoiResourceAdapter
{
	private Resource doiResource = new Resource();

	/**
	 * Constructor specifying the Data object to adapt into a Resource object.
	 * 
	 * @param itemData
	 *            Data object to read metadata values of a record from.
	 * @throws DoiException
	 *             when unable to generate a Resource object.
	 * 
	 */
	public DoiResourceAdapter(Data itemData) throws DoiException
	{
		generateResource(itemData);
	}

	/**
	 * Generates a Resource object from a specified Data object.
	 * 
	 * @param itemData
	 *            Data object to read metadata values of a record from.
	 * 
	 * @throws DoiException
	 *             When at least one creator and one title, a publisher and publication year has not been specified in the record.
	 */
	public void generateResource(Data itemData) throws DoiException
	{
		// Mandatory fields.
		Creators creators = getCreators(itemData);
		if (creators.getCreator().size() == 0)
			throw new DoiException("No creators provided. Creators, titles, publisher and publication year are required for a DOI to be minted.");
		doiResource.setCreators(creators);

		Titles titles = getTitles(itemData);
		if (titles.getTitle().size() == 0)
			throw new DoiException("No titles provided. Creators, titles, publisher and publication year are required for a DOI to be minted.");
		doiResource.setTitles(titles);

		String publisher = getPublisher(itemData);
		if (publisher == null || publisher.length() == 0)
			throw new DoiException("No publisher provided. Creators, titles, publisher and publication year are required for a DOI to be minted.");
		doiResource.setPublisher(publisher);

		String publicationYear = getPublicationYear(itemData);
		if (publicationYear == null || publicationYear.length() == 0)
			throw new DoiException("No publication year provided. Creators, titles, publisher and publication year are required for a DOI to be minted.");
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

	/**
	 * Returns a Creators object consisting of a list of Creator objects created from the specified Data object. The creators must be separated by a semicolon
	 * ';'. For example:
	 * 
	 * <ul>
	 * <li>John Smith; Bob Smith</li>
	 * <li>Smith, John; Smith, Bob</li>
	 * </ul>
	 * 
	 * @param itemData
	 *            Data object containing a record's metadata.
	 * @return Creators object containing a List of Creator objects
	 */
	private Creators getCreators(Data itemData)
	{
		Creators creators = new Creators();

		DataItem citationCreators = itemData.getFirstElementByName("citationCreator");
		if (citationCreators != null && citationCreators.getValue() != null)
		{
			String[] creatorsStr = citationCreators.getValue().split(";");
			for (int i = 0; i < creatorsStr.length; i++)
			{
				Creator creator = new Creator();
				creator.setCreatorName(creatorsStr[i].trim());
				creators.getCreator().add(creator);
			}
		}

		return creators;
	}

	/**
	 * Returns a Titles object consisting of a list of Title objects from the specified Data object.
	 * 
	 * @param itemData
	 *            Data object to read title details from
	 * @return Titles object containing a List of Title objects.
	 */
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

	/**
	 * Gets the publisher of the record from the Data object.
	 * 
	 * @param itemData
	 *            Data object from which the publisher value will be read
	 * @return Name of Publisher as String
	 */
	private String getPublisher(Data itemData)
	{
		String publisher = null;

		DataItem publisherDI = itemData.getFirstElementByName("citationPublisher");
		if (publisherDI != null)
			publisher = publisherDI.getValue();

		return publisher;
	}

	/**
	 * Gets the year of publication from the Data Object.
	 * 
	 * @param itemData
	 *            Data object from which the year of publication will be read
	 * @return Year of publication as String
	 */
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

	/**
	 * Returns the generated Resource object containing values read from the Data object for use in DOI requests.
	 * 
	 * @return Resource object containing metadata values of a record
	 */
	public Resource getDoiResource()
	{
		return this.doiResource;
	}
}
