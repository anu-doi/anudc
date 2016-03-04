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

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * RelatedInfo
 * 
 * Australian National University Data Commons
 * 
 * Class for the relatedInfo element in the ANDS RIF-CS schema
 *
 * JUnit Coverage:
 * RegistryObjectsTest
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		15/10/2012	Genevieve Turner (GT)	Initial
 * 0.2		04/12/2012	Genevieve Turner (GT)	Updated to comply with rif-cs version 1.4
 * </pre>
 *
 */
public class RelatedInfo {
	private String type;
	private Identifier identifier;
	private List<Relation> relations;
	private Format format;
	private String title;
	private String notes;
	
	/**
	 * getType
	 *
	 * Get the related information type
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the type
	 */
	@XmlAttribute
	public String getType() {
		return type;
	}
	
	/**
	 * setType
	 *
	 * Set the related information type
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	/**
	 * getIdentifier
	 *
	 * Get the related object identifier
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the identifier
	 */
	@XmlElement(namespace=Constants.ANDS_RIF_CS_NS)
	public Identifier getIdentifier() {
		return identifier;
	}
	
	/**
	 * setIdentifier
	 *
	 * Set the related object identifier
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param identifier the identifier to set
	 */
	public void setIdentifier(Identifier identifier) {
		this.identifier = identifier;
	}

	/**
	 * getRelations
	 * 
	 * Get the relation types for the relation information.
	 * 
	 * @return The relations
	 */
	public List<Relation> getRelations() {
		return relations;
	}

	/**
	 * setRelations
	 * 
	 * Set the relation types for the relation information
	 * 
	 * @param relations The relations
	 */
	public void setRelations(List<Relation> relations) {
		this.relations = relations;
	}

	/**
	 * getFormat
	 *
	 * Get the related info format
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.2		04/12/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the format
	 */
	@XmlElement(namespace=Constants.ANDS_RIF_CS_NS)
	public Format getFormat() {
		return format;
	}

	/**
	 * setFormat
	 *
	 * Set the related info format
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.2		04/12/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param format the format to set
	 */
	public void setFormat(Format format) {
		this.format = format;
	}
	
	/**
	 * getTitle
	 *
	 * Get the related object title
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the title
	 */
	@XmlElement(namespace=Constants.ANDS_RIF_CS_NS)
	public String getTitle() {
		return title;
	}
	
	/**
	 * setTitle
	 *
	 * Set the related object title
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
	 * getNotes
	 *
	 * Get the related object notes
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the notes
	 */
	@XmlElement(namespace=Constants.ANDS_RIF_CS_NS)
	public String getNotes() {
		return notes;
	}
	
	/**
	 * setNotes
	 *
	 * Set the related object notes
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param notes the notes to set
	 */
	public void setNotes(String notes) {
		this.notes = notes;
	}
}
