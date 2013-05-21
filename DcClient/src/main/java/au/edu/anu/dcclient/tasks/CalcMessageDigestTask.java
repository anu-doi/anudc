package au.edu.anu.dcclient.tasks;

import gov.loc.repository.bagit.Manifest;
import gov.loc.repository.bagit.Manifest.Algorithm;
import gov.loc.repository.bagit.utilities.MessageDigestHelper;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.dcclient.progress.ProgressInputStream;

public class CalcMessageDigestTask extends AbstractDcBagTask<String, Void> {
	private static final Logger LOGGER = LoggerFactory.getLogger(CalcMessageDigestTask.class);

	private File sourceFile;
	private Manifest.Algorithm algorithm;
	
	public CalcMessageDigestTask(File sourceFile) {
		this.sourceFile = sourceFile;
		this.algorithm = Algorithm.MD5;
	}
	
	public CalcMessageDigestTask(File sourceFile, Manifest.Algorithm algorithm) {
		this.sourceFile = sourceFile;
		this.algorithm = algorithm;
	}
	
	@Override
	protected String doInBackground() throws Exception {
		return calcMd();
	}

	public String calcMd() throws FileNotFoundException {
		ProgressInputStream fileStream = null;
		String md5sum;
		try {
			fileStream = new ProgressInputStream(new FileInputStream(sourceFile), sourceFile.length());
			fileStream.addPropertyChangeListener(new PropertyChangeListener() {
				
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					if ("percentComplete".equals(evt.getPropertyName())) {
						setProgress((Integer) evt.getNewValue());
					}
				}
			});
			md5sum = MessageDigestHelper.generateFixity(fileStream, algorithm);
			LOGGER.trace("{}, MD5: {}", sourceFile.getAbsolutePath(), md5sum);
		} finally {
			IOUtils.closeQuietly(fileStream);
		}
		return md5sum;
	}
}
