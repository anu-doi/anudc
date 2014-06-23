package au.edu.anu.datacommons.storage.tagfiles;

import java.io.File;
import java.io.IOException;

/**
 * Tag file class for ext-refs.txt . This is a custom tag file that stores links to resources hosted external to the
 * DataCommons storage.
 * 
 * @author Rahul Khanna
 *
 */
public class ExtRefsTagFile extends AbstractKeyValueFile {
	private static final long serialVersionUID = 1L;
	
	public static final String FILEPATH = "ext-refs.txt";

	public ExtRefsTagFile(File file) throws IOException {
		super(file);
	}

	@Override
	public String getFilepath() {
		return FILEPATH;
	}
}
