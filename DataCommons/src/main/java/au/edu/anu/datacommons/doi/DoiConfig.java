package au.edu.anu.datacommons.doi;


public interface DoiConfig
{
	public String getBaseUri();
	
	public String getAppId();
	
	public boolean useTestPrefix();
	
	public boolean isDebug();
	
	public boolean useProxy();
	
	public String getProxyServer();
	
	public String getProxyPort();
	
	public String getProxyUsername();
	
	public String getProxyPassword();
}
