package au.edu.anu.datacommons.security.acl;

import org.springframework.security.acls.domain.DefaultPermissionFactory;

/**
 * CustomPermissionFactory
 * 
 * Australian National University Data Commons
 * 
 * Class to make the Custom ACL Permissions available
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		22/06/2012	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
public class CustomPermissionFactory extends DefaultPermissionFactory {
	/**
	 * Constructor
	 * 
	 * Constructor for the class that performs what the default Permission Factory does plus
	 * the permissions in the CustomACLPermission class
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		22/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 *
	 */
	public CustomPermissionFactory() {
		super();
		registerPublicPermissions(CustomACLPermission.class);
	}
}
