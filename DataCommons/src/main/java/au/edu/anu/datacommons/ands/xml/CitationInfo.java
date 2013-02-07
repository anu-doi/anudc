/*******************************************************************************
 * Australian National University Data Commons
 * Copyright (C) 2013  The Australian National University
 * 
 * This file is part of Australian National University Data Commons.
 * 
 * Australian National University Data Commons is free software: you
 * can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

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
