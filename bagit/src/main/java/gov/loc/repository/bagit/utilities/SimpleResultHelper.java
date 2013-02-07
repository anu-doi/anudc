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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import gov.loc.repository.bagit.verify.CompleteVerifier;
import gov.loc.repository.bagit.verify.ManifestVerifier;

public class SimpleResultHelper {
	
	public static void missingPayloadFile(SimpleResult result, String manifest, String filepath) {
		result.setSuccess(false);
		result.addMessage(CompleteVerifier.CODE_PAYLOAD_MANIFEST_CONTAINS_MISSING_FILE, "Payload manifest {0} contains missing file(s): {1}", manifest, filepath);
	}
	
	public static void missingTagFile(SimpleResult result, String manifest, String filepath) {
		result.setSuccess(false);
		result.addMessage(CompleteVerifier.CODE_TAG_MANIFEST_CONTAINS_MISSING_FILE, "Tag manifest {0} contains missing file(s): {1}", manifest, filepath);
	}

	public static void invalidPayloadFile(SimpleResult result, String manifest, String filepath) {
		result.setSuccess(false);
		result.addMessage(ManifestVerifier.CODE_PAYLOAD_MANIFEST_CONTAINS_INVALID_FILE, "Payload manifest {0} contains invalid file(s): {1}", manifest, filepath);
	}
	
	public static void invalidTagFile(SimpleResult result, String manifest, String filepath) {
		result.setSuccess(false);
		result.addMessage(ManifestVerifier.CODE_TAG_MANIFEST_CONTAINS_INVALID_FILE, "Tag manifest {0} contains invalid files: {1}", manifest, filepath);
	}

	public static boolean isMissingOrInvalid(SimpleResult result, String filepath) {
		if(containsObject(result, CompleteVerifier.CODE_TAG_MANIFEST_CONTAINS_MISSING_FILE, filepath))
			return true;
		if(containsObject(result, CompleteVerifier.CODE_PAYLOAD_MANIFEST_CONTAINS_MISSING_FILE, filepath))
			return true;
		if(containsObject(result, ManifestVerifier.CODE_TAG_MANIFEST_CONTAINS_INVALID_FILE, filepath))
			return true;
		if(containsObject(result, ManifestVerifier.CODE_PAYLOAD_MANIFEST_CONTAINS_INVALID_FILE, filepath))
			return true;
		return false;
		
	}
	
	public static boolean containsObject(SimpleResult result, String code, String object) {
		List<SimpleMessage> messages = result.getSimpleMessagesByCode(code);
		for(SimpleMessage message : messages) {
			if (message.getObjects().contains(object)) return true;
		}
		return false;
	}

	public static Set<String> aggregateObjects(SimpleResult result, String code) {
		Set<String> objects = new HashSet<String>();
		List<SimpleMessage> messages = result.getSimpleMessagesByCode(code);
		for(SimpleMessage message : messages) {
			objects.addAll(message.getObjects());
		}
		return objects;
	}

	public static Set<String> aggregateSubjects(SimpleResult result, String code) {
		Set<String> subjects = new HashSet<String>();
		List<SimpleMessage> messages = result.getSimpleMessagesByCode(code);
		for(SimpleMessage message : messages) {
			subjects.add(message.getSubject());
		}
		return subjects;
		
	}
	
	public static Set<String> getInvalidTagFiles(SimpleResult result) {
		return aggregateObjects(result, ManifestVerifier.CODE_TAG_MANIFEST_CONTAINS_INVALID_FILE);
	}

	public static Set<String> getInvalidPayloadFiles(SimpleResult result) {
		return aggregateObjects(result, ManifestVerifier.CODE_PAYLOAD_MANIFEST_CONTAINS_INVALID_FILE);
	}

	public static Set<String> getMissingTagFiles(SimpleResult result) {
		return aggregateObjects(result, CompleteVerifier.CODE_TAG_MANIFEST_CONTAINS_MISSING_FILE);
	}

	public static Set<String> getMissingPayloadFiles(SimpleResult result) {
		return aggregateObjects(result, CompleteVerifier.CODE_PAYLOAD_MANIFEST_CONTAINS_MISSING_FILE);
	}

	public static Set<String> getExtraPayloadFiles(SimpleResult result) {
		return aggregateSubjects(result, CompleteVerifier.CODE_PAYLOAD_FILE_NOT_IN_PAYLOAD_MANIFEST);
	}
	
}
