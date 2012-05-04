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
 * 0.1		26/04/2012	Genevieve Turner (GT)	Initial
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
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name="object_id_class")
	public Long getObject_id_class() {
		return object_id_class;
	}
	
	public void setObject_id_class(Long object_id_class) {
		this.object_id_class = object_id_class;
	}

	@Column(name="object_id_identity")
	public Long getObject_id_identity() {
		return object_id_identity;
	}
	
	public void setObject_id_identity(Long object_id_identity) {
		this.object_id_identity = object_id_identity;
	}

	@Column(name="owner_sid")
	public Long getOwner_sid() {
		return owner_sid;
	}
	
	public void setOwner_sid(Long owner_sid) {
		this.owner_sid = owner_sid;
	}

	@Column(name="parent_object")
	public Long getParent_object() {
		return parent_object;
	}
	
	public void setParent_object(Long parent_object) {
		this.parent_object = parent_object;
	}

	@Column(name="entries_inheriting")
	public Boolean getEntries_inheriting() {
		return entries_inheriting;
	}
	
	public void setEntries_inheriting(Boolean entries_inheriting) {
		this.entries_inheriting = entries_inheriting;
	}
}
