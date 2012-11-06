package au.edu.anu.datacommons.config;

import java.io.File;

public class Config
{
	public static final File DIR;
	public static final String NEWLINE = System.getProperty("line.separator");
	public static final String CHARSET = "UTF-8";
	
	static
	{
		String osName = System.getProperty("os.name"); 
		if (osName.toLowerCase().startsWith("windows"))
			DIR = new File("C:\\AnuDc");
		else
			DIR = new File("/etc/anudc");
		
		if (!DIR.exists())
			DIR.mkdirs();
	}
}
