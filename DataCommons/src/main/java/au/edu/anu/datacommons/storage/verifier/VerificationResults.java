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

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Rahul Khanna
 * 
 */
@XmlRootElement(name = "verificationResults")
@XmlType(propOrder = {"timeStamp", "messages"})
public class VerificationResults implements Iterable<ResultMessage> {

	private String bagId;
	private List<ResultMessage> messages = new ArrayList<ResultMessage>();
	private Date timestamp;

	VerificationResults() {
		this.timestamp = new Date();
	}
	
	public VerificationResults(String bagId) {
		this.bagId = bagId;
		this.timestamp = new Date();
	}

	public void addMessage(ResultMessage msg) {
		messages.add(msg);
	}

	@XmlAttribute(name = "bagId")
	public String getBagId() {
		return bagId;
	}
	
	@XmlElement
	public Date getTimestamp() {
		return timestamp;
	}
	
	@XmlElement(name="message")
	public List<ResultMessage> getMessages() {
		return messages;
	}
	
	@Override
	public Iterator<ResultMessage> iterator() {
		return messages.iterator();
	}
}
