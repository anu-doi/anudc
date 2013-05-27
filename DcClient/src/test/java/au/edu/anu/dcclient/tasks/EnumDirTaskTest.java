package au.edu.anu.dcclient.tasks;

import static java.text.MessageFormat.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.dcclient.tasks.EnumDirTask;

public class EnumDirTaskTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(EnumDirTaskTest.class);
	
	@Rule
	public TemporaryFolder tempDir = new TemporaryFolder();
	
	private Random rand = new Random();
	
	private EnumDirTask enumDirTask;
	private int expectedFilesCount = 0;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		LOGGER.debug("Creating directory tree in {}", tempDir.getRoot().getAbsolutePath());
		createRandomDirTree();
		enumDirTask = new EnumDirTask(tempDir.getRoot(), true);
	}

	private void createRandomDirTree() throws IOException {
		// Create 1-3 folders in tempDir.
		int numDirsInRoot = rand.nextInt(3) + 1;
		for (int i = 0; i < numDirsInRoot; i++) {
			expectedFilesCount += createFiles(tempDir.newFolder());
		}
		expectedFilesCount += createFiles(tempDir.getRoot());
	}

	private int createFiles(File dir) throws IOException {
		// Create 1-4 files in specified directory.
		int filesInDir = rand.nextInt(4) + 1;
		for (int i = 0; i < filesInDir; i++) {
			File newTempFile = tempDir.newFile();
			if (!dir.equals(tempDir.getRoot())) {
				if (!newTempFile.renameTo(new File(dir, newTempFile.getName()))) {
					throw new IOException(format("Unable to move {0} to {1}", newTempFile.getAbsolutePath(),
							dir.getAbsolutePath()));
				}
			}
		}
		return filesInDir;
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws InterruptedException, ExecutionException {
		enumDirTask.execute();
		Collection<File> files = enumDirTask.get();
		assertNotNull(files);
		assertEquals(expectedFilesCount, files.size());
		for (File iFile : files) {
			LOGGER.info(iFile.getAbsolutePath());
		}
	}

}
