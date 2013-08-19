package au.edu.anu.datacommons.test.util;

import gov.loc.repository.bagit.Manifest;
import gov.loc.repository.bagit.Manifest.Algorithm;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import org.apache.commons.codec.binary.Hex;
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

	/**
	 * Writes random data to a file until file reaches specified size in MB.
	 * 
	 * @param file
	 *            File to write to.
	 * @param sizeInMB
	 *            Number of megabytes to write
	 * @return MD5 as String
	 * @throws IOException
	 */
	public static String fillRandomData(File file, long sizeInMB) throws IOException {
		OutputStream fileStream = null;
		MessageDigest md;
		try {
			md = MessageDigest.getInstance(Algorithm.MD5.javaSecurityAlgorithm);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
		
		try {
			fileStream = new DigestOutputStream(new BufferedOutputStream(new FileOutputStream(file), (int) FileUtils.ONE_MB), md) ;
			for (int i = 0; i < sizeInMB; i++)
				fileStream.write(TestUtil.getRandomByteArray((int) FileUtils.ONE_MB));
		} finally {
			IOUtils.closeQuietly(fileStream);
		}
		
		return new String(Hex.encodeHex(md.digest(), true));
	}
}
