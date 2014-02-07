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

package au.edu.anu.datacommons.storage.info;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;

import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.wsdl.extensions.http.HTTPAddressSerializer;

import au.edu.anu.datacommons.storage.info.FileInfo.Type;

/**
 * @author Rahul Khanna
 *
 */
public class RecordDataInfoTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(RecordDataInfoTest.class);
	
	private RecordDataInfo rdi;
	
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
		rdi = new RecordDataInfo();
		Collection<FileInfo> files = new TreeSet<>();
		files.add(createFileInfo("d1/"));
		files.add(createFileInfo("d1/d2/"));
		files.add(createFileInfo("d1/d2/d3/"));
		files.add(createFileInfo("d1/d2/d3/d4/"));
		files.add(createFileInfo("d1/d2/d3/d4/d5/"));
		files.add(createFileInfo("d1/d2/d3/d4/d5/d6/"));
		files.add(createFileInfo("d1/d2/d3/d4/d5/d6/d7/"));
		files.add(createFileInfo("f1.txt"));
		files.add(createFileInfo("d1/f1.txt"));
		files.add(createFileInfo("d1/d2/f1.txt"));
		files.add(createFileInfo("d1/d2/d3/f1.txt"));
		files.add(createFileInfo("d1/d2/d3/d4/f1.txt"));
		files.add(createFileInfo("d1/d2/d3/d4/d5/f1.txt"));
		files.add(createFileInfo("d1/d2/d3/d4/d5/d6/f1.txt"));
		files.add(createFileInfo("d1/d2/d3/d4/d5/d6/d7/f1.txt"));
		rdi.setFiles(files);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetFilesInDir() {
		Map<String, Integer> expecteds = new HashMap<String, Integer>();
		expecteds.put("", 2);
		expecteds.put("/", 2);
		expecteds.put("d1", 2);
		expecteds.put("d1/", 2);
		expecteds.put("d1/d2/d3/d4/d5", 2);
		expecteds.put("d1/d2/d3/d4/d5/", 2);
		for (Entry<String, Integer> expected : expecteds.entrySet()) {
			assertThat(rdi.getFiles(expected.getKey()), hasSize(expected.getValue().intValue()));
		}
	}
	
	@Test
	public void testGetParents() {
		Map<String, Integer> expecteds = new HashMap<String, Integer>();

		expecteds.put("", 0);
		expecteds.put("/", 0);
		expecteds.put("d1/d2/d3/d4/d5/d6/d7", 7);
		expecteds.put("d1/d2/d3/d4/d5/d6/d7/", 7);
		expecteds.put("d1/d2/d3/d4/d5/d6/d7/f1.txt", 8);
		
		for (Entry<String, Integer> expected : expecteds.entrySet()) {
			assertThat(rdi.getParents(expected.getKey()), hasSize(expected.getValue().intValue()));
		}
	}
	

	private FileInfo createFileInfo(String path) {
		FileInfo fi = new FileInfo();
		fi.setFilename(Paths.get(path).getFileName().toString());
		fi.setRelFilepath(path);
		if (path.endsWith("/")) {
			fi.setType(Type.DIR);
		} else {
			fi.setType(Type.FILE);
		}
		return fi;
	}
}
