package au.edu.anu.datacommons.test.util;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import gov.loc.repository.bagit.Manifest.Algorithm;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.util.Util;

public class TestUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(TestUtil.class);
	private static final int BUFFER_SIZE = 8192;
	
	public static final Random rand = new Random();
	
	@Rule
	public TemporaryFolder tempDir = new TemporaryFolder();
	
	public static byte[] getRandomByteArray(int size) {
		byte[] bytes = new byte[size];
		rand.nextBytes(bytes);
		return bytes;
	}

	/**
	 * Creates a file of size in range between minUnits and maxUnits.
	 * 
	 * @param file
	 *            File to write
	 * @param minUnits
	 *            Minimum possible size of file
	 * @param maxUnits
	 *            Maximum possible size of file
	 * @param unit
	 *            Unit multiplier. E.g. FileUtils.ONE_KB, FileUtils.ONE_MB
	 * @return MD5 of the contents of the file
	 * @throws IOException
	 *             when unable to write file
	 */
	public static String createFileOfSizeInRange(File file, long minUnits, long maxUnits, long unit) throws IOException {
		return createFileOfSizeInRange(file, minUnits * unit, maxUnits * unit);
	}
	
	/**
	 * Creates a file of size in range between minBytes and maxBytes. The size is a random value between the range
	 * specified.
	 * 
	 * @param file
	 *            File to write
	 * @param minBytes
	 *            Minimum possible size of file
	 * @param maxBytes
	 *            Maximum possible size of file
	 * @return MD5 of the contents of the file
	 * @throws IOException
	 *             when unable to write file
	 */
	public static String createFileOfSizeInRange(File file, long minBytes, long maxBytes) throws IOException {
		if (minBytes < 0 || maxBytes < 0) {
			throw new IllegalArgumentException();
		}
		if (minBytes > maxBytes) {
			throw new IllegalArgumentException();
		}
		long size = (long) (minBytes + (long) (rand.nextDouble() * (maxBytes - minBytes + 1L)));
		return createFileOfSize(file, size);
	}

	/**
	 * Creates a file of size nUnits * unit.
	 * 
	 * @param file
	 *            File to write
	 * @param nUnits
	 *            Number of units
	 * @param unit
	 *            FileUtils.ONE_KB, FileUtils.ONE_MB etc.
	 * @return MD5 of the contents of the file
	 * @throws IOException
	 *             when unable to write the file
	 */
	public static String createFileOfSize(File file, long nUnits, long unit) throws IOException {
		return createFileOfSize(file, nUnits * unit);
	}
	
	/**
	 * Creates a file of size sizeInBytes and fills it with random data.
	 * 
	 * @param file
	 *            File to write
	 * @param sizeInBytes
	 *            size of the file in bytes
	 * @return MD5 hash of the contents of the file
	 * @throws IOException
	 *             when unable to write to file
	 */
	public static String createFileOfSize(File file, long sizeInBytes) throws IOException {
		MessageDigest digester = createMd5Digester();
		OutputStream os = null;
		try {
			os = new DigestOutputStream(new FileOutputStream(file), digester);
			WritableByteChannel channel = Channels.newChannel(os);

			byte[] buffer = new byte[BUFFER_SIZE];
			for (long i = 0; i <= sizeInBytes; i += BUFFER_SIZE) {
				rand.nextBytes(buffer);
				int length;
				if (sizeInBytes < i + BUFFER_SIZE) {
					length = (int) (sizeInBytes % ((long) BUFFER_SIZE));
				} else {
					length = BUFFER_SIZE;
				}
				ByteBuffer bb = ByteBuffer.wrap(buffer, 0, length);
				channel.write(bb);
			}
		} finally {
			IOUtils.closeQuietly(os);
		}
		String md5 = new String(Hex.encodeHex(digester.digest(), true));
		LOGGER.info("Created {} ({}) ({} bytes) MD5: {}", file.getAbsolutePath(), Util.byteCountToDisplaySize(sizeInBytes), sizeInBytes, md5);
		return md5;
	}
	
	private static MessageDigest createMd5Digester() {
		MessageDigest digester;
		try {
			digester = MessageDigest.getInstance(Algorithm.MD5.javaSecurityAlgorithm);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
		return digester;
	}
	
	@Test
	public void testCreateRangedFile() throws IOException {
		File file = tempDir.newFile();
		long minBytes = 1024L;
		long maxBytes = 1078L;
		createFileOfSizeInRange(file, minBytes, maxBytes);
		LOGGER.info("Created file of size: {} bytes", file.length());
		assertThat(file.length(), allOf(greaterThanOrEqualTo(minBytes), lessThanOrEqualTo(maxBytes)));
	}
	
	@Test
	public void testCreateFile() throws IOException {
		long[] sizes = {0L, 10L, BUFFER_SIZE, BUFFER_SIZE + 1L, BUFFER_SIZE * 4L + 128L, 20L * 1024L * 1024L};
		for (int i = 0; i < sizes.length; i++) {
			File file = tempDir.newFile();
			String md = createFileOfSize(file, sizes[i]);
			LOGGER.info("Created file of size: {} bytes with MD5: {}", file.length(), md);
			assertThat(file.length(), is(sizes[i]));
		}
	}
	
}
