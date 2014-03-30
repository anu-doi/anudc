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

package au.edu.anu.datacommons.storage.verifier;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * POJO object for a single message generated while checking the completeness/integrity of a bag as specified in the
 * BagIt specification.
 * 
 * @author Rahul Khanna
 * 
 */
@XmlType(propOrder = { "severity", "category", "filepath", "message" })
public class ResultMessage {
	public enum Severity {
		INFO, WARN, ERROR
	};

	public enum Category {
		VALIDATION_EXCEPTION, PAYLOADFILE_NOTFOUND, TAGFILE_NOTFOUND, TAGFILE_ENTRY_MISSING, MANIFEST_ENTRY_MISSING, ARTIFACT_FOUND, OTHER, CHECKSUM_MISMATCH
	}

	private Severity severity;
	private Category category;
	private String filepath;
	private String message;

	ResultMessage() {

	}

	/**
	 * Creates an instance representing a single occurrance of error/warning/info.
	 * 
	 * @param severity
	 *            Severity of the message
	 * @param category
	 *            Category of the message
	 * @param filepath
	 *            Relative filepath to the payload file that the message is for
	 * @param message
	 */
	public ResultMessage(Severity severity, Category category, String filepath, String message) {
		super();
		this.severity = severity;
		this.category = category;
		this.filepath = filepath;
		this.message = message;
	}

	@XmlElement
	public Severity getSeverity() {
		return severity;
	}

	@XmlElement
	public Category getCategory() {
		return category;
	}

	@XmlElement
	public String getFilepath() {
		return filepath;
	}

	@XmlElement
	public String getMessage() {
		return message;
	}
}
