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

package au.edu.anu.datacommons.security.acl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
 * 0.2		16/08/2012	Genevieve Turner (GT)	Added a list of permissions
 * 0.3		11/12/2012	Genevieve Turner (GT)	Added the PUBLISH_MULTI and SET_PERMISSION permission levels
 * </pre>
 *
 */
public class CustomACLPermission extends BasePermission {
	private static final long serialVersionUID = 1L;
	
	public static final Permission REVIEW = new CustomACLPermission (1<<5,'V');
	public static final Permission PUBLISH = new CustomACLPermission (1<<6,'P');
	public static final Permission PUBLISH_MULTI = new CustomACLPermission (1<<7,'M');
	public static final Permission SET_PERMISSION = new CustomACLPermission (1<<8,'S');

	// 
	private static List<Permission> listOfPermissions_;
	static {
		ArrayList<Permission> tmp = new ArrayList<Permission>();
		tmp.add(CustomACLPermission.READ);
		tmp.add(CustomACLPermission.CREATE);
		tmp.add(CustomACLPermission.WRITE);
		tmp.add(CustomACLPermission.DELETE);
		tmp.add(CustomACLPermission.ADMINISTRATION);
		tmp.add(CustomACLPermission.REVIEW);
		tmp.add(CustomACLPermission.PUBLISH);
		tmp.add(CustomACLPermission.PUBLISH_MULTI);
		tmp.add(CustomACLPermission.SET_PERMISSION);
		listOfPermissions_ = Collections.unmodifiableList(tmp);
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
	
	/**
	 * getPermissionList
	 *
	 * Gets a list of permissions for the CustomACLPermission class
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.2		16/08/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @return
	 */
	public static List<Permission> getPermissionList() {
		return listOfPermissions_;
	}
}
