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

import au.edu.anu.datacommons.storage.completer.fido.FidoParser;
import au.edu.anu.datacommons.storage.info.PronomFormat;

/**
 * @author Rahul Khanna
 * 
 */
public class FidoTask implements Callable<PronomFormat> {
	private static final Logger LOGGER = LoggerFactory.getLogger(FidoTask.class);
	
	private BagFile bagfile;
	
	public FidoTask(BagFile bagfile) {
		super();
		this.bagfile = bagfile;
	}

	@Override
	public PronomFormat call() throws Exception {
		FidoParser fido;
		InputStream fileStream = null;
		try {
			fileStream = bagfile.newInputStream();
			fido = new FidoParser(fileStream);
			LOGGER.trace("Fido result for {}: {}", bagfile.getFilepath(), fido.getFidoStr());
		} finally {
			IOUtils.closeQuietly(fileStream);
		}
		
		return fido.getFileFormat();
	}

}
