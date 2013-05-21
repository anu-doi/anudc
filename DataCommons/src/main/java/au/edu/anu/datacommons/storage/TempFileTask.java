package au.edu.anu.datacommons.storage;

import static java.text.MessageFormat.*;
import gov.loc.repository.bagit.Manifest;
import gov.loc.repository.bagit.Manifest.Algorithm;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.concurrent.Callable;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.properties.GlobalProps;

public class TempFileTask implements Callable<File> {
	private static final Logger LOGGER = LoggerFactory.getLogger(TempFileTask.class);

	private static final int CONNECTION_TIMEOUT_MS = 30000;
	private static final int READ_TIMEOUT_MS = 30000;
	private static final String FILENAME_PREFIX = "AnuDc";

	private static Random random = new Random();

	private URL fileUrl = null;
	private InputStream inputStream = null;
	private File savedFile = null;

	private Manifest.Algorithm mdAlgorithm = null;
	private String expectedMd = null;
	private String calculatedMd = null;
	private MessageDigest md = null;

	public TempFileTask(URL fileUrl) {
		this.fileUrl = fileUrl;
	}

	public TempFileTask(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public void setExpectedMessageDigest(Manifest.Algorithm mdAlgorithm, String messageDigest) {
		this.mdAlgorithm = mdAlgorithm;
		this.expectedMd = messageDigest.toLowerCase();
	}

	@Override
	public File call() throws Exception {
		InputStream digestInputStream = null;
		if (fileUrl != null) {
			digestInputStream = createDigestInputStream(fileUrl);
		} else if (inputStream != null) {
			digestInputStream = createDigestInputStream(inputStream);
		}
		saveInputStreamToTempFile(digestInputStream);

		if (expectedMd != null) {
			if (!expectedMd.equals(calculatedMd)) {
				String errorMsg = format("Calculated {0} {1} does not match expected {2}.", md.getAlgorithm(),
						calculatedMd, expectedMd);
				LOGGER.error(errorMsg);
				savedFile.delete();
				throw new IOException(errorMsg);
			} else {
				LOGGER.debug("Calculated {} {} matches expected {}", md.getAlgorithm(), calculatedMd, expectedMd);
			}
		} else {
			LOGGER.debug("Calculated {}: {}", md.getAlgorithm(), calculatedMd);
		}
		return savedFile;
	}

	public String getCalculatedMd() {
		return calculatedMd;
	}

	private InputStream createDigestInputStream(URL fileUrl) throws IOException {
		createMessageDigest();
		URLConnection connection = fileUrl.openConnection();
		connection.setConnectTimeout(CONNECTION_TIMEOUT_MS);
		connection.setReadTimeout(READ_TIMEOUT_MS);
		LOGGER.debug("Opened InputStream from {}", fileUrl);
		return new DigestInputStream(connection.getInputStream(), md);
	}

	private InputStream createDigestInputStream(InputStream inputStream) {
		createMessageDigest();
		return new DigestInputStream(inputStream, md);
	}

	private void createMessageDigest() {
		try {
			if (mdAlgorithm != null && expectedMd != null) {
				md = MessageDigest.getInstance(mdAlgorithm.javaSecurityAlgorithm);
			} else {
				md = MessageDigest.getInstance(Algorithm.MD5.javaSecurityAlgorithm);
			}
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	private File saveInputStreamToTempFile(InputStream inputStream) throws IOException {
		savedFile = createTempFile();

		FileChannel targetChannel = null;
		FileOutputStream fos = null;
		ReadableByteChannel sourceChannel = null;
		try {
			LOGGER.debug("Saving InputStream as {}...", savedFile.getAbsolutePath());
			fos = new FileOutputStream(savedFile);
			targetChannel = fos.getChannel();
			sourceChannel = Channels.newChannel(inputStream);
			ByteBuffer buffer = ByteBuffer.allocate((int) FileUtils.ONE_MB);
			while (sourceChannel.read(buffer) != -1) {
				buffer.flip();
				targetChannel.write(buffer);
				buffer.compact();
			}

			buffer.flip();
			while (buffer.hasRemaining())
				targetChannel.write(buffer);

			LOGGER.debug("Saved InputStream to {}. ({})", savedFile.getAbsolutePath(),
					FileUtils.byteCountToDisplaySize(savedFile.length()));
		} catch (IOException e) {
			LOGGER.error("Unable to save InputStream. {}", e.getMessage());
			if (!savedFile.delete()) {
				LOGGER.warn("Unable to delete {}", savedFile.getAbsolutePath());
			}
			throw e;
		} finally {
			IOUtils.closeQuietly(sourceChannel);
			IOUtils.closeQuietly(inputStream);
			IOUtils.closeQuietly(targetChannel);
			IOUtils.closeQuietly(fos);
		}
		calcMessageDigest();
		return savedFile;
	}

	private void calcMessageDigest() {
		calculatedMd = new String(Hex.encodeHex(md.digest())).toLowerCase();
	}

	private File createTempFile() {
		File tempFile;
		do {
			tempFile = new File(getTempDir(), generateRandomFilename());
		} while (tempFile.exists());
		return tempFile;
	}

	private String generateRandomFilename() {
		long n = random.nextLong();
		if (n == Long.MIN_VALUE) {
			n = 0; // corner case
		} else {
			n = Math.abs(n);
		}
		return FILENAME_PREFIX + Long.toString(n);
	}

	private File getTempDir() {
		File tempDir = new File(GlobalProps.getProperty(GlobalProps.PROP_UPLOAD_DIR));
		if (!tempDir.isDirectory()) {
			throw new RuntimeException(format("{0} is not a directory.", tempDir.getAbsolutePath()));
		}
		if (!tempDir.exists()) {
			if (!tempDir.mkdirs()) {
				throw new RuntimeException(format("{0} cannot be created. Check permissions.",
						tempDir.getAbsolutePath()));
			}
		}
		return tempDir;
	}
}
