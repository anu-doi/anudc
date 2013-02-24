package au.edu.anu.datacommons.test.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class TestUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(TestUtil.class);
	
	public static final Random RANDOM = new Random();
	
	private TestUtil() {
		// Private constructor so this class cannot be instantiated.
	}
	
	public static byte[] getRandomByteArray(int size) {
		byte[] bytes = new byte[size];
		RANDOM.nextBytes(bytes);
		return bytes;
	}
	
	public static void fillRandomData(File file, long sizeInMB) throws IOException {
		BufferedOutputStream fileStream = null;
		try {
			fileStream = new BufferedOutputStream(new FileOutputStream(file), (int) FileUtils.ONE_MB);
			for (int i = 0; i < sizeInMB; i++)
				fileStream.write(TestUtil.getRandomByteArray((int) FileUtils.ONE_MB));
		} finally {
			IOUtils.closeQuietly(fileStream);
		}
	}
}
