package au.edu.anu.datacommons.storage;

import static java.text.MessageFormat.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.Random;
import java.util.concurrent.Callable;

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

	public TempFileTask(URL fileUrl) {
		this.fileUrl = fileUrl;
	}
	
	public TempFileTask(InputStream inputStream) {
		this.inputStream = inputStream;
	}
	
	@Override
	public File call() throws Exception {
		if (fileUrl != null) {
			saveUrlToTempFile();
		} else if (inputStream != null) {
			saveInputStreamToTempFile();
		}
		return savedFile;
	}

	public File saveUrlToTempFile() throws IOException {
		savedFile = createTempFile();
		try {
			LOGGER.debug("Downloading {} and saving as {}...", fileUrl.toString(), savedFile.getAbsolutePath());
			FileUtils.copyURLToFile(fileUrl, savedFile, CONNECTION_TIMEOUT_MS, READ_TIMEOUT_MS);
			LOGGER.debug("Downloaded {} successfully and saved as {} ({}).", new Object[] { fileUrl.toString(),
					savedFile.getAbsolutePath(), FileUtils.byteCountToDisplaySize(savedFile.length()) });
		} catch (IOException e) {
			LOGGER.error("Unable to download {}. {}", fileUrl.toString(), e.getMessage());
			if (!savedFile.delete()) {
				LOGGER.warn("Unable to delete {}", savedFile.getAbsolutePath());
			}
			throw e;
		}
		return savedFile;
	}

	public File saveInputStreamToTempFile() throws IOException {
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
			LOGGER.error("Unable to save InputStream.", e.getMessage());
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

		return savedFile;
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
