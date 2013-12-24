package au.edu.anu.datacommons.data.db.model;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class AuthoritiesPK implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String username_;
	private String authority_;
	
	public String getUsername() {
		return username_;
	}
	
	public void setUsername(String username) {
		this.username_ = username;
	}
	
	public String getAuthority() {
		return authority_;
	}
	
	public void setAuthority(String authority) {
		this.authority_ = authority;
	}
	
	public int hashCode() {
		int hashCode = 0;
		if (username_ != null) {
			hashCode = 17 * hashCode + username_.hashCode();
		}
		if (authority_ != null) {
			hashCode = 17 * hashCode + authority_.hashCode();
		}
		return hashCode;
	}
	
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		
		if (!(obj instanceof AuthoritiesPK)) {
			return false;
		}
		AuthoritiesPK pk = (AuthoritiesPK) obj;
		return (	((this.getUsername() == null && pk.getUsername() == null) || (this.getUsername() != null && this.getUsername().equals(pk.getUsername()))
							&& ((this.getAuthority() == null && pk.getAuthority() == null) || (this.getAuthority() != null && this.getAuthority().equals(pk.getAuthority())))));
				
	}
}
