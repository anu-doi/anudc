/**
 * 
 */
package au.edu.anu.datacommons.ldap;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;

import au.edu.anu.datacommons.properties.GlobalProps;

/**
 * @author Rahul Khanna
 * 
 */
public class LdapPerson
{
	private Attributes attributes;

	/**
	 * Constructor
	 * @param attrs
	 * Attributes object containing attribute-value pairs of an LDAP entry.
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
	 * @return true if the attribute was pulled from the LDAP server, false otherwise.
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
	 *  
	 * @return
	 * The display name of the LDAP Person.
	 */
	public String getDisplayName()
	{
		String displayName;
		
		try
		{
			displayName = (String) attributes.get(GlobalProps.getProperty(GlobalProps.PROP_LDAPATTR_DISPLAYNAME, "displayName")).get(0);
		}
		catch (NamingException e)
		{
			displayName = "";
		}
		
		return displayName;
	}
	
	/**
	 * 
	 * @return
	 * Given name from the LDAP person entry.
	 */
	public String getGivenName()
	{
		String givenName;
		
		try
		{
			givenName = (String) attributes.get(GlobalProps.getProperty(GlobalProps.PROP_LDAPATTR_GIVENNAME, "givenName")).get(0);
		}
		catch (NamingException e)
		{
			givenName = "";
		}
		
		return givenName;
	}
}
