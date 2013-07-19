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

package au.edu.anu.datacommons.storage;

import gov.loc.repository.bagit.BagFile;

import java.io.InputStream;
import java.util.concurrent.Callable;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.datacommons.properties.GlobalProps;
import au.edu.anu.datacommons.storage.completer.virusscan.ClamScan;
import au.edu.anu.datacommons.storage.info.ScanResult;

/**
 * 
 * @author Rahul Khanna
 *
 */
public class AvScanTask implements Callable<ScanResult> {
	private static final Logger LOGGER = LoggerFactory.getLogger(AvScanTask.class);

	private BagFile bagfile;
	
	public AvScanTask(BagFile bagfile) {
		super();
		this.bagfile = bagfile;
	}

	@Override
	public ScanResult call() throws Exception {
		ScanResult sr = null;
		ClamScan cs = new ClamScan(GlobalProps.getClamScanHost(), GlobalProps.getClamScanPort(), GlobalProps.getClamScanTimeout());
		InputStream stream = null;
		try {
			stream = bagfile.newInputStream();
			sr = cs.scan(stream);
			LOGGER.trace("AV Scan results for {}: {}", bagfile.getFilepath(), sr.getStatus());
		} finally {
			IOUtils.closeQuietly(stream);
		}
		return sr;
	}
}
