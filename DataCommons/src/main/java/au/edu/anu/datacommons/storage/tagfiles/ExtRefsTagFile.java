package au.edu.anu.datacommons.storage.tagfiles;

import java.io.IOException;
import java.io.InputStream;

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

	public ExtRefsTagFile(InputStream stream) throws IOException {
		super(stream);
	}

	@Override
	public String getFilepath() {
		return FILEPATH;
	}
}
