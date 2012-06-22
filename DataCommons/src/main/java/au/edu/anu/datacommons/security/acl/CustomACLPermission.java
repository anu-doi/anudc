package au.edu.anu.datacommons.security.acl;

import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.Permission;

/**
 * CustomACLPermission
 * 
 * Australian National University Data Commons
 * 
 * Adds additional custom permissions to those already impelemented by ACL.
 * Current Added Permissions are:
 * REVIEW
 * PUBLISH
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
public class CustomACLPermission extends BasePermission {
	private static final long serialVersionUID = 1L;
	
	public static final Permission REVIEW = new CustomACLPermission (1<<5,'V');
	public static final Permission PUBLISH = new CustomACLPermission (1<<6,'P');
	
	/**
	 * Constructor
	 * 
	 * Constructure for the additional permissions
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		22/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param mask Integer representing the permission
	 */
	protected CustomACLPermission (int mask) {
		super(mask);
	}
	
	/**
	 * Constructor
	 * 
	 * Constructure for the additional permissions
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		22/06/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param mask Integer representing the permission
	 * @param code One character code representing the permission
	 */
	protected CustomACLPermission (int mask, char code) {
		super(mask, code);
	}
}
