package au.edu.anu.datacommons.data.db.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.Transient;

import au.edu.anu.datacommons.ldap.LdapPerson;
import au.edu.anu.datacommons.ldap.LdapRequest;

@Entity
@Table(name = "users")
public class Users
{
	private Long id;
	private String username;
	private String password;
	private Boolean enabled;

	private String displayName;
	private String givenName;
	private String familyName;
	private String email;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId()
	{
		return id;
	}

	protected void setId(Long id)
	{
		this.id = id;
	}

	@Column(name = "username")
	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	@Column(name = "password")
	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	@Column(name = "enabled")
	public Boolean getEnabled()
	{
		return enabled;
	}

	public void setEnabled(Boolean enabled)
	{
		this.enabled = enabled;
	}

	@Transient
	public String getDisplayName()
	{
		return displayName;
	}

	@Transient
	public String getGivenName()
	{
		return givenName;
	}

	@Transient
	public String getFamilyName()
	{
		return familyName;
	}

	@Transient
	public String getEmail()
	{
		return email;
	}

	@PostLoad
	public void getLdapPerson()
	{
		LdapPerson ldapPerson;
		LdapRequest ldapReq = new LdapRequest();
		try
		{
			ldapPerson = ldapReq.searchUniId(username);
			displayName = ldapPerson.getDisplayName();
			givenName = ldapPerson.getGivenName();
			familyName = ldapPerson.getFamilyName();
			email = ldapPerson.getEmail();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			displayName = "";
			givenName = "";
			familyName = "";
			email = "";
		}
	}
}
