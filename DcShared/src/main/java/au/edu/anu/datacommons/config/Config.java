package au.edu.anu.datacommons.config;

import java.io.File;

/**
 * Provides configuration and environment information:
 * 
 * <ul>
 * <li>Directory where configuration files are location</li>
 * <li>New Line String specific to the platform on which the JVM is running</li>
 * <li>Character Set constant</li>
 * <ul>
 */
public class Config
{
	/**
	 * Location of configuration files. Individual projects may store configuration within a subdirectory within the returned subdirectory. Returns C:\AnuDc if
	 * on Windows, /etc/anudc on *nix.
	 */
	public static final File DIR;
	
	/**
	 * Returns the value of system property line.separator
	 */
	public static final String NEWLINE = System.getProperty("line.separator");

	/**
	 * Returns the default character set to be used.
	 */
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
