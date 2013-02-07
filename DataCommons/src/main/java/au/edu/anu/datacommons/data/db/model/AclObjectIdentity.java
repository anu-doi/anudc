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

package au.edu.anu.datacommons.data.db.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * AclObjectIdentity
 * 
 * Australian National University Data Comons
 * 
 * Entity class for the acl_object_identity database table
 * 
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		04/05/2012	Genevieve Turner (GT)	Initial
 * </pre>
 * 
 */
@Entity
@Table(name="acl_object_identity")
public class AclObjectIdentity {
	private Long id;
	private Long object_id_class;
	private Long object_id_identity;
	private Long owner_sid;
	private Long parent_object;
	private Boolean entries_inheriting;
	
	/**
	 * getId
	 * 
	 * Gets the identifier of the class
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		04/05/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @return The identifier
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}
	
	/**
	 * setId
	 * 
	 * Sets the identifier of the class
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		04/05/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @param id The identifier
	 */
	public void setId(Long id) {
		this.id = id;
	}
	
	/**
	 * getObject_id_class
	 * 
	 * Gets the id of the class for which the object relates
	 * The class ids can be found in the table 'acl_class'
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		04/05/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @return The id of the class to which the object relates.
	 */
	@Column(name="object_id_class")
	public Long getObject_id_class() {
		return object_id_class;
	}
	
	/**
	 * getEntries_inheriting
	 * 
	 * Sets the id of the class for which the object relates
	 * The class ids can be found in the table 'acl_class'
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		04/05/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @param object_id_class The id of the class to which the object relates.
	 */
	public void setObject_id_class(Long object_id_class) {
		this.object_id_class = object_id_class;
	}

	/**
	 * getObject_id_identity
	 * 
	 * The id of the object to which the row relates.
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		04/05/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @return The id of the object to which the row relates.
	 */
	@Column(name="object_id_identity")
	public Long getObject_id_identity() {
		return object_id_identity;
	}
	
	/**
	 * setObject_id_identity
	 * 
	 *  The id of the object to which the row relates.
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		04/05/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @param object_id_identity The id of the object to which the row relates.
	 */
	public void setObject_id_identity(Long object_id_identity) {
		this.object_id_identity = object_id_identity;
	}

	/**
	 * getOwner_sid
	 * 
	 * Gets who owns the object
	 * The id can be found from the class 'acl_sid'
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		04/05/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @return The id of the owner
	 */
	@Column(name="owner_sid")
	public Long getOwner_sid() {
		return owner_sid;
	}
	
	/**
	 * setOwner_sid
	 * 
	 * Sets who owns the object
	 * The id can be found from the class 'acl_sid'
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		04/05/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @param owner_sid
	 */
	public void setOwner_sid(Long owner_sid) {
		this.owner_sid = owner_sid;
	}

	/**
	 * getParent_object
	 * 
	 * Gets the id of the parent object
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		04/05/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @return The id of the parent object
	 */
	@Column(name="parent_object")
	public Long getParent_object() {
		return parent_object;
	}
	
	/**
	 * setParent_object
	 * 
	 * Sets the id of the parent object
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		04/05/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @param parent_object The id of the parent object
	 */
	public void setParent_object(Long parent_object) {
		this.parent_object = parent_object;
	}

	/**
	 * getEntries_inheriting
	 * 
	 * Gets whether the entries inherit or not
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		04/05/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @return Boolean of inheritance
	 */
	@Column(name="entries_inheriting")
	public Boolean getEntries_inheriting() {
		return entries_inheriting;
	}
	
	/**
	 * setEntries_inheriting
	 * 
	 * Sets whether the entries inherit or not
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		04/05/2012	Genevieve Turner (GT)	Initial
	 * </pre>
	 * 
	 * @param entries_inheriting Boolean of inheritance
	 */
	public void setEntries_inheriting(Boolean entries_inheriting) {
		this.entries_inheriting = entries_inheriting;
	}
}
