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

package au.edu.anu.datacommons.ldap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

import au.edu.anu.datacommons.properties.GlobalProps;

/**
 * LdapPerson
 * 
 * Autralian National University Data Commons
 * 
 * This class contains the attributes of a person who has a record in the LDAP server. The list of attributes is limited
 * to what's extracted by the LDAP query. For example, if the LDAP query requests only givenName and UniId to be
 * retrieved for records that match the criteria, other attributes will not be stored.
 * 
 * <pre>
 * Version	Date		Developer			Description
 * 0.1		16/03/2012	Rahul Khanna (RK)	Initial
 * </pre>
 */
public class LdapPerson {
	private Map<String, List<String>> attrs;

	/**
	 * Constructor that accepts an Attributes object as parameter.
	 * 
	 * Autralian National University Data Commons
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		16/03/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 * @param attrs
	 *            Attributes object containing attribute-value pairs of an LDAP entry.
	 */
	public LdapPerson(Attributes attrs) {
		generateMap(attrs);
	}

	private void generateMap(Attributes attrs) {
		this.attrs = new HashMap<String, List<String>>(attrs.size());
		NamingEnumeration<? extends Attribute> keysEnum = attrs.getAll();
		try {
			while (keysEnum.hasMore()) {
				Attribute i = keysEnum.next();
				String key = i.getID();
				List<String> values = new ArrayList<String>(i.size());
				NamingEnumeration<?> valuesEnum = i.getAll();
				while (valuesEnum.hasMore()) {
					values.add((String) valuesEnum.next());
				}
				this.attrs.put(key, values);
			}
		} catch (NamingException e) {
			// No op. No attributes resolved.
		}
	}

	/**
	 * Gets the value of an attribute.
	 * 
	 * @param attrName
	 *            The attribute whose value is to be returned.
	 * @return The value of the attribute.
	 */
	public String getAttribute(String attrName) {
		return this.attrs.get(attrName).get(0);
	}

	/**
	 * Checks if an attribute was retrieved for this person.
	 * 
	 * @param attrName
	 *            The LDAP attribute name. E.g. displayName.
	 * @return true if the attribute was pulled from the LDAP server as part of the query, false otherwise.
	 */
	public boolean attrExists(String attrName) {
		return this.attrs.containsKey(attrName);
	}

	/**
	 * getDisplayName
	 * 
	 * Autralian National University Data Commons
	 * 
	 * Convenience method that returns Display Name property of an LDAP entry. Used in JSP files:
	 * 
	 * <c:out value="${user.displayName} />
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		16/03/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 * @return The display name of the LDAP Person.
	 */
	public String getDisplayName() {
		return getAttribute(GlobalProps.getProperty(GlobalProps.PROP_LDAPATTR_DISPLAYNAME));
	}

	/**
	 * getGivenName
	 * 
	 * Autralian National University Data Commons
	 * 
	 * Convenience method that returns Display Name property of an LDAP entry. Used in JSP files:
	 * 
	 * <c:out value="${user.givenName}" />
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		16/03/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 * @return Given name from the LDAP person entry.
	 */
	public String getGivenName() {
		return getAttribute(GlobalProps.getProperty(GlobalProps.PROP_LDAPATTR_GIVENNAME, "givenName"));
	}

	/**
	 * getFamilyName
	 * 
	 * Australian National University Data Commons
	 * 
	 * Returns the Family name of this person.
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		17/05/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 * @return Family Name as String.
	 */
	public String getFamilyName() {
		return getAttribute(GlobalProps.getProperty(GlobalProps.PROP_LDAPATTR_FAMILYNAME));
	}

	/**
	 * getEmail
	 * 
	 * Australian National University Data Commons
	 * 
	 * Returns the email address of this person.
	 * 
	 * <pre>
	 * Version	Date		Developer			Description
	 * 0.1		17/05/2012	Rahul Khanna (RK)	Initial
	 * </pre>
	 * 
	 * @return Email address as String.
	 */
	public String getEmail() {
		return getAttribute(GlobalProps.getProperty(GlobalProps.PROP_LDAPATTR_EMAIL));
	}

	public String getUniId() {
		return getAttribute(GlobalProps.getProperty(GlobalProps.PROP_LDAPATTR_UNIID));
	}
}
