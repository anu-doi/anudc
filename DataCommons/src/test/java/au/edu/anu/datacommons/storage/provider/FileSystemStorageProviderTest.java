/*******************************************************************************
 * Australian National University Data Commons
 * Copyright (C) 2013  The Australian National University
 * 
 * This file is part of Australian National University Data Commons.
 * 
 * Australian National University Data Commons is free software: you
 * can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package au.edu.anu.datacommons.storage.provider;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.storage.datafile.StagedDataFile;
import au.edu.anu.datacommons.storage.info.FileInfo;
import au.edu.anu.datacommons.storage.messagedigest.FileMessageDigests.Algorithm;
import au.edu.anu.datacommons.storage.temp.UploadedFileInfo;
import au.edu.anu.datacommons.test.util.TestUtil;

/**
 * @author Rahul Khanna
 *
 */
public class FileSystemStorageProviderTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(FileSystemStorageProviderTest.class);
	
	private static final String PID = "test:123";
	
	@Rule
	public TemporaryFolder tempDir = new TemporaryFolder();
	
	@Rule
	public TemporaryFolder rootDir = new TemporaryFolder();
	
	private AbstractStorageProvider provider;
	

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		provider = new FileSystemStorageProvider(rootDir.getRoot().toPath());
		
		LOGGER.info("Root dir: {}", rootDir.getRoot().getAbsolutePath());
		LOGGER.info("Temp dir: {}", tempDir.getRoot().getAbsolutePath());
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testAddFileToRoot() throws Exception {
		StagedDataFile source = createTempFile(10, 20, FileUtils.ONE_KB);
		String filename = "file name.txt";
		
		FileInfo addedFile = provider.addFile(PID, filename, source);
		assertThat(addedFile, is(notNullValue()));
		assertThat(addedFile.getFilename(), is(filename));
		assertThat(addedFile.getSize(), is(source.getSize()));
	}
	
	@Test
	public void testAddFileToSubdir() throws Exception {
		StagedDataFile source = createTempFile(10, 20, FileUtils.ONE_KB);
		String filepath = "dir1/dir2/file name.txt";
		
		FileInfo addedFile = provider.addFile(PID, filepath, source);
		assertThat(addedFile, is(notNullValue()));
		assertThat(addedFile.getFilename(), is(filepath.substring(filepath.lastIndexOf("/") + 1)));
		assertThat(addedFile.getRelFilepath(), is(filepath));
		assertThat(addedFile.getSize(), is(source.getSize()));
	}
	
	@Test
	public void testRenameFileInSameDir() throws Exception {
		StagedDataFile source = createTempFile(10, 20, FileUtils.ONE_KB);
		String filepath = "Dir 1/Dir 2/old file name.txt";
		
		FileInfo addedFile = provider.addFile(PID, filepath, source);
		assertThat(addedFile, is(notNullValue()));
		assertThat(addedFile.getRelFilepath(), is(filepath));
		
		String newFilepath = "Dir 1/Dir 2/new file name.txt";
		FileInfo renamedFile = provider.renameFile(PID, filepath, newFilepath);
		assertThat(renamedFile, is(notNullValue()));
		assertThat(renamedFile.getRelFilepath(), is(newFilepath));
	}
	
	@Test
	public void testRenameFileInDifferentDir() throws Exception {
		StagedDataFile source = createTempFile(10, 20, FileUtils.ONE_KB);
		String filepath = "Dir 1/Dir 2/old file name.txt";
		
		FileInfo addedFile = provider.addFile(PID, filepath, source);
		assertThat(addedFile, is(notNullValue()));
		assertThat(addedFile.getRelFilepath(), is(filepath));
		
		String newFilepath = "Dir 2/Dir 4/new file name.txt";
		FileInfo renamedFile = provider.renameFile(PID, filepath, newFilepath);
		assertThat(renamedFile, is(notNullValue()));
		assertThat(renamedFile.getRelFilepath(), is(newFilepath));
	}
	
	@Test
	public void testDeleteFileInRoot() throws Exception {
		StagedDataFile source = createTempFile(10, 20, FileUtils.ONE_KB);
		String filepath = "file name.txt";

		FileInfo addedFile = provider.addFile(PID, filepath, source);
		provider.deleteFile(PID, addedFile.getRelFilepath());
		assertThat(provider.fileExists(PID, filepath), is(false));
	}
	
	@Test
	public void testDeleteFileInSubdir() throws Exception {
		StagedDataFile source = createTempFile(10, 20, FileUtils.ONE_KB);
		String filepath = "dir 1/dir 2/file name.txt";
		
		FileInfo addedFile = provider.addFile(PID, filepath, source);
		provider.deleteFile(PID, addedFile.getRelFilepath());
		assertThat(provider.fileExists(PID, filepath), is(false));
	}
	
	@Test
	public void testFileExists() throws Exception {
		StagedDataFile source = createTempFile(10, 20, FileUtils.ONE_KB);
		String filepath = "dir 1/dir 2/file name.txt";

		// expected false as the file's not been added.
		assertThat(provider.fileExists(PID, filepath), is(false));
		// add the file
		FileInfo addedFile = provider.addFile(PID, filepath, source);
		// expected true now that the file's added.
		assertThat(provider.fileExists(PID, filepath), is(true));
	}
	
	@Test
	public void testGetStream() throws Exception {
		StagedDataFile source = createTempFile(10, 20, FileUtils.ONE_KB);
		String filepath = "dir 1/dir 2/file name.txt";

		FileInfo addedFile = provider.addFile(PID, filepath, source);
		assertThat(provider.fileExists(PID, addedFile.getRelFilepath()), is(true));
		String calculatedMd5 = calcMd5(addedFile);
		LOGGER.info("MD5: {}", calculatedMd5);
	}

	@Test
	public void testConcurrentAddSameFile() throws Exception {
		List<StagedDataFile> files = new ArrayList<>();
		final int nFiles = 20;
		for (int i = 0; i < nFiles; i++) {
			files.add(createTempFile(10, 20, FileUtils.ONE_MB));
		}
		ExecutorService pool = Executors.newCachedThreadPool();
		
		
		final String relativePath = "File.bin";
		final ArrayList<Future<?>> futures = new ArrayList<>();
		for (StagedDataFile sdf : files) {
			final StagedDataFile fSdf = sdf;
			futures.add(pool.submit(new Callable<Void>() {

				@Override
				public Void call() throws Exception {
					provider.addFile(PID, relativePath, fSdf);
					return null;
				}
			}));
		}
		
		while (!provider.fileExists(PID, relativePath));
		InputStream stream = provider.readStream(PID, relativePath);
		Thread.sleep(2000);
		IOUtils.closeQuietly(stream);
		
		pool.shutdown();
		pool.awaitTermination(1, TimeUnit.MINUTES);
		for (Future<?> f : futures) {
			f.get();
		}
		
		FileInfo addedFile = provider.getFileInfo(PID, relativePath);
		
		boolean md5Match = false;
		for (StagedDataFile sdf : files) {
			String origMd5 = sdf.getMessageDigests().getMessageDigest(Algorithm.MD5).getMessageDigestAsHex();
			if (origMd5.equals(calcMd5(addedFile))) {
				md5Match = true;
				break;
			}
		}
		
		assertThat(addedFile, is(notNullValue()));
		assertThat(md5Match, is(true));
		LOGGER.trace("{} {}", addedFile.getSize(), calcMd5(addedFile));
	}

	private String calcMd5(FileInfo addedFile) throws NoSuchAlgorithmException, IOException {
		MessageDigest digest = MessageDigest.getInstance("MD5");
		DigestInputStream inStream = new DigestInputStream(provider.readStream(PID, addedFile.getRelFilepath()), digest);
		for (int byteRead = inStream.read(); byteRead != -1; byteRead = inStream.read());
		
		byte[] calcDigest = digest.digest();
		String calculatedMd5 = Hex.encodeHexString(calcDigest);
		return calculatedMd5;
	}
	
	private StagedDataFile createTempFile(long minBytes, long maxBytes, long unit) throws IOException {
		Path tempFile = tempDir.newFile().toPath();
		String md5 = TestUtil.createFileOfSizeInRange(tempFile.toFile(), minBytes, maxBytes, unit);
		StagedDataFile sdf = new UploadedFileInfo(tempFile, Files.size(tempFile), md5);
		return sdf;
	}
}
