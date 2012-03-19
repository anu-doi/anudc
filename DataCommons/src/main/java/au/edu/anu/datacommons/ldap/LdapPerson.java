/**
 * 
 */
package au.edu.anu.datacommons.ldap;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;

import au.edu.anu.datacommons.properties.GlobalProps;

/**
 * LdapPerson
 * 
 * Autralian National University Data Commons
 * 
 * This class contains the attributes of a person who has a record in the LDAP server. The list of attributes is limited to what's extracted by the LDAP query.
 * For example, if the LDAP query requests only givenName and UniId to be retrieved for records that match the criteria, other attributes will not be stored.
 * 
 * <pre>
 * Version	Date		Developer			Description
 * 0.1		16/03/2012	Rahul Khanna (RK)	Initial
 * </pre>
 */
public class LdapPerson
{
	private Attributes attributes;

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
	public LdapPerson(Attributes attrs)
	{
		this.attributes = attrs;
	}

	/**
	 * Gets the value of an attribute.
	 * 
	 * @param attrName
	 *            The attribute whose value is to be returned.
	 * @return The value of the attribute.
	 */
	public String getAttribute(String attrName)
	{
		String attrValue;

		try
		{
			attrValue = (String) attributes.get(attrName).get(0);
		}
		catch (Exception e)
		{
			attrValue = null;
		}
		return attrValue;
	}

	/**
	 * Checks if an attribute was retrieved for this person.
	 * 
	 * @param attrName
	 *            The LDAP attribute name. E.g. displayName.
	 * @return true if the attribute was pulled from the LDAP server as part of the query, false otherwise.
	 */
	public boolean attrExists(String attrName)
	{
		NamingEnumeration<String> attrList = attributes.getIDs();
		boolean attrExists = false;

		try
		{
			while (attrList.hasMore())
			{
				if (attrList.next().equalsIgnoreCase(attrName))
				{
					attrExists = true;
					break;
				}
			}
		}
		catch (NamingException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return attrExists;
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
	public String getDisplayName()
	{
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
	 * @return Given name from the LDAP person entry.
	 */
	public String getGivenName()
	{
		return getAttribute(GlobalProps.getProperty(GlobalProps.PROP_LDAPATTR_GIVENNAME, "givenName"));
	}
}
