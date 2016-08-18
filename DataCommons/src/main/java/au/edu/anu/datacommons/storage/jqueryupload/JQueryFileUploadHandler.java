/**
 * 
 */
package au.edu.anu.datacommons.storage.jqueryupload;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.WeakHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Rahul Khanna
 *
 */
public class JQueryFileUploadHandler {

	private static final Logger logger = LoggerFactory.getLogger(JQueryFileUploadHandler.class);

	private Path uploadDir;
	
	private WeakHashMap<Path, ReadWriteLock> fileLocks = new WeakHashMap<>();
	
	public JQueryFileUploadHandler(String uploadDir) {
		this(Paths.get(uploadDir));
	}
	
	public JQueryFileUploadHandler(Path uploadDir) {
		this.uploadDir = uploadDir;
	}
	

	public void processFile(InputStream fileStream, String id, String fileName, long expectedLength)
			throws IOException {
		Path targetFile = getTarget(id, fileName);
		long bytesCopied = Files.copy(fileStream, targetFile, StandardCopyOption.REPLACE_EXISTING);
		logger.info("Saved [{}] expected={},actual={}", targetFile.toString(), expectedLength, bytesCopied);
	}

	public void processFilePart(InputStream partStream, String id, String fileName, String contentRange)
			throws IOException {
		ContentRange cr = new ContentRange(contentRange);
		logger.trace("Processing part for filename={},content-range={}", fileName, cr.toString());

		Path partFile = null;
		try {
			// save the stream to a temp part file
			partFile = getPartFile(id, fileName);
			logger.trace("Saving part {}...", partFile.toString());
			long expectedChunkSize = cr.getLastBytePos() - cr.getFirstBytePos() + 1;
			long actualChunkSize = Files.copy(partStream, partFile, StandardCopyOption.REPLACE_EXISTING);

			// check if the part file is expected length
			if (actualChunkSize != expectedChunkSize) {
				throw new IOException(
						String.format("Chunk size expected=%d,actual=%d", expectedChunkSize, actualChunkSize));
			}
			logger.debug("Saved part {} (expected={},actual={} bytes)", partFile, expectedChunkSize, actualChunkSize);

			Path targetFile = getTarget(id, fileName);
			// check if the target file to be append is the correct size relative to the content range
			long targetSizePreAppend = cr.getFirstBytePos() != 0 && Files.isRegularFile(targetFile)
					? Files.size(targetFile) : 0L;
			if (cr.getFirstBytePos() > 0 && targetSizePreAppend != cr.getFirstBytePos()) {
				throw new IOException(String.format("Target file pre-append size expected=%d,actual=%d",
						cr.getFirstBytePos(), targetSizePreAppend));
			}

			// part file's good, now append its contents to the target file and
			// delete the part file
			logger.trace("Appending part {} ({} bytes) to target {} ({} bytes)...", partFile.toString(),
					actualChunkSize, targetFile.toString(), targetSizePreAppend);
			
			// reading the entire part file in memory to ensure there can't be any IOExceptions
			// from reading part file while writing to the target file.
			byte[] bytes = Files.readAllBytes(partFile);
			if (bytes.length != expectedChunkSize) {
				throw new IOException(String.format("Part file %s chunk size expected=%d,actual=%d",
						partFile.toString(), expectedChunkSize, actualChunkSize));
			}
			
			// append part file's contents to the target file
			Files.write(targetFile, bytes, StandardOpenOption.CREATE, StandardOpenOption.WRITE,
					cr.getFirstBytePos() == 0 ? StandardOpenOption.TRUNCATE_EXISTING : StandardOpenOption.APPEND);

			// check the target file size post-append
			long targetSizePostAppend = Files.size(targetFile);
			if (targetSizePostAppend != cr.getLastBytePos() + 1) {
				throw new IOException(String.format("Target file post-append size expected=%d,actual=%d",
						cr.getLastBytePos() + 1, targetSizePreAppend));
			}

			logger.debug("Appended part {} to target {} ({} bytes)", partFile.toString(), targetFile.toString(),
					targetSizePostAppend);
		} finally {
			IOUtils.closeQuietly(partStream);
			FileUtils.deleteQuietly(partFile.toFile());
		}
	}

	public JQueryFileUploadResponse generateResponse(String id, String fileName) {
		return generateResponse(id, fileName, null);
	}
	
	public JQueryFileUploadResponse generateResponse(String id, String fileName, Exception exception) {
		JQueryFileUploadResponse resp;
		Path target = getTarget(id, fileName);

		try {
			if (exception == null) {
				resp = new JQueryFileUploadResponse(fileName, Files.size(target));
			} else {
				resp = new JQueryFileUploadResponse(fileName, Files.size(target), exception.getMessage());
			}
		} catch (IOException e) {
			resp = new JQueryFileUploadResponse(fileName, 0L, exception.getMessage());
		}
		
		return resp;
	}
	

	public Path getTarget(String id, String fileName) {
		return uploadDir.resolve(String.format("%s#%s", id, fileName));
	}

	private Path getPartFile(String id, String fileName) {
		return uploadDir.resolve(generatePartFileName(id, fileName));
	}

	private String generatePartFileName(String id, String fileName) {
		return String.format("~%s#%s.part", id, fileName);
	}

	public boolean isFileComplete(String id, String fileName, String contentRange) throws IOException {
		long fileSize = Files.size(getTarget(id, fileName));
		ContentRange cr = new ContentRange(contentRange);
		return cr.getInstanceLength() == fileSize;
	}

}
