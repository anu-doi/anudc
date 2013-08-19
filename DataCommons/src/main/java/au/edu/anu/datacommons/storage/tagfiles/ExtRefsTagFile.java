package au.edu.anu.datacommons.storage.tagfiles;

import java.io.File;
import java.io.IOException;

public class ExtRefsTagFile extends AbstractKeyValueFile {
	private static final long serialVersionUID = 1L;
	
	public static final String FILEPATH = "ext-refs.txt";

	public ExtRefsTagFile(File tagFile) throws IOException {
		super(tagFile);
	}
}
