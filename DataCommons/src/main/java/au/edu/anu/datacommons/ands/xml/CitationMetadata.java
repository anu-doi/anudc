package au.edu.anu.datacommons.ands.xml;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlElement;

/**
 * CitationMetadata
 * 
 * Australian National University Data Commons
 * 
 * Class for the citationMetadata element in the ANDS RIF-CS schema
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		12/10/2012	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
public class CitationMetadata {
	private Identifier identifier;
	private List<Contributor> contributors;
	private String title;
	private String edition;
	private String publisher;
	private String placePublished;
	private ANDSDate date;
	private String url;
	private String context;

	/**
	 * Constructor
	 * 
	 * Constructor class for Citation Metadata
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		12/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 *
	 */
	public CitationMetadata() {
		contributors = new ArrayList<Contributor>();
	}
	
	/**
	 * getIdentifier
	 *
	 * Get the identifier
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		12/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the identifier
	 */
	@Valid
	@NotNull(message="Citation metadata requires an identifier")
	@XmlElement(namespace=Constants.ANDS_RIF_CS_NS)
	public Identifier getIdentifier() {
		return identifier;
	}
	
	/**
	 * setIdentifier
	 *
	 * Set the identifier
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		12/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param identifier the identifier to set
	 */
	public void setIdentifier(Identifier identifier) {
		this.identifier = identifier;
	}

	/**
	 * getContributors
	 *
	 * Get the contributors
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the contributors
	 */
	@Valid
	@Size(min=1, message="Citation Metadata requires at least one contributor")
	@XmlElement(name="contributor", namespace=Constants.ANDS_RIF_CS_NS)
	public List<Contributor> getContributors() {
		return contributors;
	}

	/**
	 * setContributors
	 *
	 * Set the contributors
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param contributors the contributors to set
	 */
	public void setContributors(List<Contributor> contributors) {
		this.contributors = contributors;
	}

	/**
	 * getTitle
	 *
	 * Get the title
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the title
	 */
	@NotNull(message="Citation metadata requires a contributor")
	@XmlElement(namespace=Constants.ANDS_RIF_CS_NS)
	public String getTitle() {
		return title;
	}

	/**
	 * setTitle
	 *
	 * Set the title
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * getEdition
	 *
	 * Get the edition
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the edition
	 */
	@NotNull(message="Citation metadata requires an edition")
	@XmlElement(namespace=Constants.ANDS_RIF_CS_NS)
	public String getEdition() {
		return edition;
	}

	/**
	 * setEdition
	 *
	 * Set the edition
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param edition the edition to set
	 */
	public void setEdition(String edition) {
		this.edition = edition;
	}

	/**
	 * getPublisher
	 *
	 * Get the publisher
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the publisher
	 */
	@XmlElement(namespace=Constants.ANDS_RIF_CS_NS)
	public String getPublisher() {
		return publisher;
	}

	/**
	 * setPublisher
	 *
	 * Set the publisher
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param publisher the publisher to set
	 */
	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	/**
	 * getPlacePublished
	 *
	 * Get the place published
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the placePublished
	 */
	@NotNull(message="Citation metadata requires a location where it was published")
	@XmlElement(namespace=Constants.ANDS_RIF_CS_NS)
	public String getPlacePublished() {
		return placePublished;
	}

	/**
	 * setPlacePublished
	 *
	 * SEt the place published
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param placePublished the placePublished to set
	 */
	public void setPlacePublished(String placePublished) {
		this.placePublished = placePublished;
	}

	/**
	 * getDate
	 *
	 * Get the date
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the date
	 */
	@Valid
	@XmlElement(namespace=Constants.ANDS_RIF_CS_NS)
	public ANDSDate getDate() {
		return date;
	}

	/**
	 * setDate
	 *
	 * Set the date
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param date the date to set
	 */
	public void setDate(ANDSDate date) {
		this.date = date;
	}

	/**
	 * getUrl
	 *
	 * Get the url
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the url
	 */
	@NotNull(message="Citation metadata requires a url")
	@XmlElement(namespace=Constants.ANDS_RIF_CS_NS)
	public String getUrl() {
		return url;
	}

	/**
	 * setUrl
	 *
	 * Set the url
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * getContext
	 *
	 * Get the context
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the context
	 */
	@NotNull(message="Citation metadata requires a context")
	@XmlElement(namespace=Constants.ANDS_RIF_CS_NS)
	public String getContext() {
		return context;
	}

	/**
	 * setContext
	 *
	 * Set the context
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param context the context to set
	 */
	public void setContext(String context) {
		this.context = context;
	}
}
