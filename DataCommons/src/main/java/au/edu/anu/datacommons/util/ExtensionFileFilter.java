package au.edu.anu.datacommons.util;

import java.io.File;
import java.io.FilenameFilter;

/**
 * ExtensionFileFilter
 * 
 * Australian National University Data Commons
 * 
 * Placeholder
 *
 * JUnit Coverage:
 * None
 * 
 * <pre>
 * Version	Date		Developer				Description
 * 0.1		26/09/2012	Genevieve Turner (GT)	Initial
 * </pre>
 *
 */
public class ExtensionFileFilter implements FilenameFilter {
	private String extension;
	
	/**
	 * Constructor
	 * 
	 * Constructor that passes in the extension that will be filtered out
	 * 
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		26/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param extension The extension name to filter
	 */
	public ExtensionFileFilter(String extension) {
		this.extension = "." + extension;
	}
	
	/**
	 * accept
	 * 
	 * Verifies if the file ends with the extension
	 *
	 * <pre>
	 * Version	Date		Developer				Description
	 * 0.1		26/09/2012	Genevieve Turner(GT)	Initial
	 * </pre>
	 * 
	 * @param dir The directory of the file to filter
	 * @param name The name of the file to filter
	 * @return True if the file name ends with the extension
	 * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
	 */
	@Override
	public boolean accept(File dir, String name) {
		return name.endsWith(extension);
	}
}
