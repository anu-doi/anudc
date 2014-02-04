package au.edu.anu.dcclient.shibboleth.idp;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/*
 * IdentityProvider
 *
 * Australian National University Data Commons
 * 
 * Identity Provider Object
 *
 * JUnit coverage:
 * ShibbolethIdpControllerTest
 * 
 * @author Genevieve Turner
 *
 */
@XmlRootElement(name="identity-provider")
@XmlAccessorType(XmlAccessType.FIELD)
public class IdentityProvider {
	@XmlElement(name="entityID")
	private String entityID;
	
	@XmlElement(name="ecp-url")
	private String ecpURL;
	
	@XmlElement(name="display-name")
	private String displayName;

	/**
	 * Get the Identity Providers' entityID
	 * 
	 * @return The entityID
	 */
	public String getEntityID() {
		return entityID;
	}

	/**
	 * Set the Identity Providers' entityID
	 * 
	 * @param entityID The entityID
	 */
	public void setEntityID(String entityID) {
		this.entityID = entityID;
	}

	/**
	 * Get the Identity Providers' Enhanced Client or Proxy end point URL
	 * 
	 * @return The ECP URL
	 */
	public String getEcpURL() {
		return ecpURL;
	}

	/**
	 * Set the Identity Providers' Enhanced Client or Proxy end point URL
	 * 
	 * @param ecpURL The ECP URL
	 */
	public void setEcpURL(String ecpURL) {
		this.ecpURL = ecpURL;
	}

	/**
	 * Get the display name
	 * 
	 * @return The display name
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * Set the display name
	 * 
	 * @param displayName The display name
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	@Override
	public String toString() {
		return displayName;
	}
}
