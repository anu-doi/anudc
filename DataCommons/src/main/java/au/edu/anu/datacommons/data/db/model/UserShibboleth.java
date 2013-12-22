package au.edu.anu.datacommons.data.db.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="user_shibboleth")
public class UserShibboleth extends UserExtra {
	private String displayName;
	private String email;
	private String institution;
	
	public UserShibboleth() {
		
	}
	
	public UserShibboleth(Long id, String displayName, String email, String institution) {
		super.setId(id);
		this.displayName = displayName;
		this.email = email;
		this.institution = institution;
	}

	@Column(name="display_name")
	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	@Column(name="email")
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Column(name="institution")
	public String getInstitution() {
		return institution;
	}

	public void setInstitution(String institution) {
		this.institution = institution;
	}
}
