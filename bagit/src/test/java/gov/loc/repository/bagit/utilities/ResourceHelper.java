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

package gov.loc.repository.bagit.utilities;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.AndFileFilter;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.apache.commons.io.filefilter.PrefixFileFilter;

public class ResourceHelper {
	/**
	 * The root of the project.  Some limited experiments show that the
	 * <c>user.dir</c> property is set to the project root by both
	 * Eclipse and Maven test harnesses.
	 */
	public static final File PROJECT_DIR = new File(System.getProperty("user.dir"));
	
	/**
	 * The <c>target</c> directory, under the project.
	 */
	public static final File TARGET_DIR = new File(PROJECT_DIR, "target");
	
	/**
	 * The unit test bag data directory.
	 */
	public static final File TEST_DATA_DIR = new File(TARGET_DIR, "unit-test-data");

	/**
	 * An {@link IOFileFilter} that filters out any hidden files,
	 * or any files starting with "." (the UNIX hidden file convention).
	 */
	private static final IOFileFilter REAL_FILE_FILTER = new AndFileFilter(HiddenFileFilter.VISIBLE, new NotFileFilter(new PrefixFileFilter(".")));
	
	private static boolean initialCopyCompleted = false;

	/* Removed, as these don't seem to be used.
	public static File getFile(Class<?> clazz, String filename) throws Exception
	{
		String resourceName = clazz.getPackage().getName().replace('.', '/');
		if (filename != null)
		{
			resourceName += "/" + filename;
		}
		return new File(clazz.getClassLoader().getResource(resourceName).toURI());
	}

	public static File getFile(Object obj, String filename) throws Exception
	{
		return getFile(obj.getClass(), filename);
	}
	*/
	
	public static File getFile(String filePath) throws Exception {
		copyUnitTestData();
		return new File(TEST_DATA_DIR, filePath);
	}
	
	private static void copyUnitTestData() throws IOException 
	{
		synchronized (TEST_DATA_DIR)
		{
			if (!initialCopyCompleted)
			{
				if (TEST_DATA_DIR.exists())
					FileUtils.deleteQuietly(TEST_DATA_DIR);
				
				FileUtils.copyDirectory(new File(PROJECT_DIR, "src/test/resources/bags"), new File(TEST_DATA_DIR, "bags"), REAL_FILE_FILTER, true);
				FileUtils.copyDirectory(new File(PROJECT_DIR, "src/test/resources/file_systems"), new File(TEST_DATA_DIR, "file_systems"), REAL_FILE_FILTER, true);
				
				initialCopyCompleted = true;
			}
		}
	}
}
