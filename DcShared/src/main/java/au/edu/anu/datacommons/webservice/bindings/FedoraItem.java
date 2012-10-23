package au.edu.anu.datacommons.webservice.bindings;

import java.util.List;
import java.util.Map;

public interface FedoraItem
{
	public Map<String, List<String>> generateDataMap();
	
	public String getTemplate();
	
	public void setTemplate(String template);
	
	public String getPid();
	
	public void setPid(String pid);
	
	public String getType();
}
