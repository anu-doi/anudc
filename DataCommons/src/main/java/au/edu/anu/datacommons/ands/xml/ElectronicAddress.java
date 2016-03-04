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

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import au.edu.anu.datacommons.ands.check.ActivityCheck;
import au.edu.anu.datacommons.ands.check.CollectionCheck;
import au.edu.anu.datacommons.ands.check.PartyCheck;

/**
 * ElectronicAddress
 * 
 * Australian National University Data Commons
 * 
 * Class for the electronic element in the ANDS RIF-CS schema
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
@XmlRootElement(name="electronic")
public class ElectronicAddress {
	private String type;
	private String target;
	private String value;
	private String title;
	private List<String> notes;
	private List<String> mediaType;
	private String byteSize;
	private List<ElectronicAddressArgument> args;
	//TODO filter out types for electronic addresses
	public ElectronicAddress() {
		args = new ArrayList<ElectronicAddressArgument>();
	}
	
	/**
	 * getType
	 *
	 * Get the type of electronic address
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		12/10/2012	Genevieve Turner(GT)	Initial
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
	 * Set the type of electronic address
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		12/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	@XmlAttribute
	public String getTarget() {
		return target;
	}
	
	public void setTarget(String target) {
		this.target = target;
	}
	
	/**
	 * getValue
	 *
	 * Get the value
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the value
	 */
	@NotNull(message="Electronic Address value may not be null")
	@XmlElement(namespace=Constants.ANDS_RIF_CS_NS)
	public String getValue() {
		return value;
	}
	
	/**
	 * setValue
	 *
	 * Set the value
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
	
	@XmlElement(namespace=Constants.ANDS_RIF_CS_NS)
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@XmlElement(namespace=Constants.ANDS_RIF_CS_NS)
	public List<String> getNotes() {
		return notes;
	}

	public void setNotes(List<String> notes) {
		this.notes = notes;
	}

	@XmlElement(namespace=Constants.ANDS_RIF_CS_NS)
	public List<String> getMediaType() {
		return mediaType;
	}

	public void setMediaType(List<String> mediaType) {
		this.mediaType = mediaType;
	}

	@XmlElement(namespace=Constants.ANDS_RIF_CS_NS)
	public String getByteSize() {
		return byteSize;
	}

	public void setByteSize(String byteSize) {
		this.byteSize = byteSize;
	}

	/**
	 * getArgs
	 *
	 * Get the arguments
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return the args
	 */
	//TODO make sure that these can only be there for services
	@Valid
	@Size(max=0, message="There should be no electronic address arguments", groups={CollectionCheck.class, ActivityCheck.class, PartyCheck.class})
	@XmlElement(name="arg", namespace=Constants.ANDS_RIF_CS_NS)
	public List<ElectronicAddressArgument> getArgs() {
		return args;
	}
	
	/**
	 * setArgs
	 *
	 * Set the arguments
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		03/10/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param args the args to set
	 */
	public void setArgs(List<ElectronicAddressArgument> args) {
		this.args = args;
	}
}
