package au.edu.anu.datacommons.webservice.bindings;

import java.util.List;
import java.util.Map;

public interface FedoraItem
{
	/**
	 * Generates a map of data values required for processing by Data Commons
	 * 
	 * @return {@code Map<String, List<String>>}
	 */
	public Map<String, List<String>> generateDataMap();
	
	/**
	 * Returns the template used by this item.
	 * 
	 * @return Template value as a Pid of the template record used.
	 */
	public String getTemplate();
	
	/**
	 * Sets the template to be used by this item.
	 * 
	 * @param template
	 *            Pid of the template to be used
	 */
	public void setTemplate(String template);
	
	/**
	 * Gets the pid of this item.
	 * 
	 * @return Pid as String
	 */
	public String getPid();
	
	/**
	 * Sets the pid of this item.
	 * 
	 * @param pid
	 *            Pid as String
	 */
	public void setPid(String pid);
	
	/**
	 * Gets the type of this item - activity, service, party or collection.
	 * 
	 * @return Type as String
	 */
	public String getType();
	
	/**
	 * Gets the owner group of this item.
	 * 
	 * @return Owner Group ID as String
	 */
	public String getOwnerGroup();
	
	/**
	 * Sets the owner group of this item.
	 * 
	 * @param ownerGroup
	 *            Owner Group as String
	 */
	public void setOwnerGroup(String ownerGroup);
}
