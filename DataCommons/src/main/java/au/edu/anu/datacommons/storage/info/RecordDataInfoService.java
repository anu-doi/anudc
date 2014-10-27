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

import gov.loc.repository.bagit.impl.BagInfoTxtImpl;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import au.edu.anu.datacommons.storage.tagfiles.BagInfoTagFile;
import au.edu.anu.datacommons.storage.tagfiles.ExtRefsTagFile;
import au.edu.anu.datacommons.storage.tagfiles.TagFilesService;

/**
 * A service class that generates a {@link RecordDataSummary} object containing details about the files in a collection
 * record.
 * 
 * @author Rahul Khanna
 * 
 */
@Component
public class RecordDataInfoService {
	private static final Logger LOGGER = LoggerFactory.getLogger(RecordDataInfoService.class);

	@Autowired(required = true)
	private TagFilesService tagFilesSvc;

	public RecordDataSummary createRecordDataSummary(String pid) throws IOException {
		RecordDataSummary rdi = new RecordDataSummary();
		rdi.setPid(pid);
		setNumSizeOfFilesInRecord(rdi, pid);
		rdi.setExtRefs(tagFilesSvc.getAllEntries(pid, ExtRefsTagFile.class).values());
		return rdi;
	}
	
	
	/**
	 * Sets aggregated information in a RecordDataInfo object about files in a collection record.
	 * 
	 * @param rdi
	 *            RecordDataInfo object to which aggregated information will be added.
	 * @param pid
	 *            Identifier of collection record.
	 * @throws IOException
	 */
	private void setNumSizeOfFilesInRecord(RecordDataSummary rdi, String pid) throws IOException {
		String payloadOxum = tagFilesSvc.getEntryValue(pid, BagInfoTagFile.class, BagInfoTxtImpl.FIELD_PAYLOAD_OXUM);
		if (payloadOxum != null) {
			String[] payloadOxumParts = payloadOxum.split("\\.");
			try {
				rdi.setRecordSize(Long.parseLong(payloadOxumParts[0], 10));
			} catch (NumberFormatException | IndexOutOfBoundsException e) {
				LOGGER.warn("{}/{} contains invalid value {} for key {}", pid, BagInfoTagFile.FILEPATH, payloadOxum,
						BagInfoTxtImpl.FIELD_PAYLOAD_OXUM);
			}
			try {
				rdi.setRecordNumFiles(Long.parseLong(payloadOxumParts[1], 10));
			} catch (NumberFormatException | IndexOutOfBoundsException e) {
				LOGGER.warn("{}/{} contains invalid value {} for key {}", pid, BagInfoTagFile.FILEPATH, payloadOxum,
						BagInfoTxtImpl.FIELD_PAYLOAD_OXUM);
			}
		}
	}
}
