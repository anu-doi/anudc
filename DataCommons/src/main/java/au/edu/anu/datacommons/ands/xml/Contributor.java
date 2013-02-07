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

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * Contributor
 * 
 * Australian National University Data Commons
 * 
 * Class for the contributor element in the ANDS RIF-CS schema
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
public class Contributor {
	private Long seq;
	private List<NamePart> nameParts;
	
	/**
	 * Constructor
	 * 
	 * Constructor for the Contributor Class
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		12/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 *
	 */
	public Contributor() {
		nameParts = new ArrayList<NamePart>();
	}
	
	/**
	 * getSeq
	 *
	 * Get the sequence number for the contributor
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the seq
	 */
	@XmlAttribute
	public Long getSeq() {
		return seq;
	}
	
	/**
	 * setSeq
	 *
	 * Set the sequence number of the contributor
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param seq the seq to set
	 */
	public void setSeq(Long seq) {
		this.seq = seq;
	}
	
	/**
	 * getNameParts
	 *
	 * Get the name parts of the contributor
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the nameParts
	 */
	@Size(min=1, message="A contributor needs at least one name part")
	@XmlElement(name="namePart", namespace=Constants.ANDS_RIF_CS_NS)
	public List<NamePart> getNameParts() {
		return nameParts;
	}
	
	/**
	 * setNameParts
	 *
	 * Set the name parts of the contributor
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param nameParts the nameParts to set
	 */
	public void setNameParts(List<NamePart> nameParts) {
		this.nameParts = nameParts;
	}
}
