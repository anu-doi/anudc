package au.edu.anu.datacommons.ands.xml;

import javax.validation.Valid;
import javax.xml.bind.annotation.XmlElement;

import au.edu.anu.datacommons.validator.AtLeastOneOf;

/**
 * CitationInfo
 * 
 * Australian National University Data Commons
 * 
 * Class fro the citationInfo element in the ANDS RIF-CS schema
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
@AtLeastOneOf(fieldNames={"fullCitation", "citationMetadata"}, message="Quality Level 3 - Either a full citation or citation metadata is required")
public class CitationInfo {
	private FullCitation fullCitation;
	private CitationMetadata citationMetadata;
	
	/**
	 * getFullCitation
	 *
	 * Get the full citation
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		04/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the fullCitation
	 */
	@Valid
	@XmlElement(namespace=Constants.ANDS_RIF_CS_NS)
	public FullCitation getFullCitation() {
		return fullCitation;
	}
	
	/**
	 * setFullCitation
	 *
	 * Set the full citation
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		04/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param fullCitation the fullCitation to set
	 */
	public void setFullCitation(FullCitation fullCitation) {
		this.fullCitation = fullCitation;
	}
	
	/**
	 * getCitationMetadata
	 *
	 * Get the citation metadata
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		04/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the citationMetadata
	 */
	@Valid
	@XmlElement(namespace=Constants.ANDS_RIF_CS_NS)
	public CitationMetadata getCitationMetadata() {
		return citationMetadata;
	}
	
	/**
	 * setCitationMetadata
	 *
	 * Set the citation metadata
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		04/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param citationMetadata the citationMetadata to set
	 */
	public void setCitationMetadata(CitationMetadata citationMetadata) {
		this.citationMetadata = citationMetadata;
	}
}
