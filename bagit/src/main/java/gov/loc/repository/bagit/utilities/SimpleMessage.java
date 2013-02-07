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

import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class SimpleMessage {
	public static final String MESSAGE_TYPE_ERROR = "error";
	public static final String MESSAGE_TYPE_WARNING = "warning";
	public static final String MESSAGE_TYPE_INFO = "info";
	
	private String code = null;
	private String message = null;
	private String subject = null;
	private Set<String> objects = null;
	private String messageType = MESSAGE_TYPE_ERROR;
	
	public SimpleMessage() {
	}

	public SimpleMessage(String message) {
		this.message = message;
	}
	
	public SimpleMessage(String code, String message) {
		this.code = code;
		this.message = message;
	}

	public SimpleMessage(String code, String message, String subject) {
		this.code = code;
		this.message = message;
		this.subject = subject;
	}

	public SimpleMessage(String code, String message, String subject, String object) {
		this.code = code;
		this.message = message;
		this.subject = subject;
		this.objects = new HashSet<String>();
		this.objects.add(object);
	}

	public SimpleMessage(String code, String message, String subject, String object, String messageType) {
		this.code = code;
		this.message = message;
		this.subject = subject;
		this.objects = new HashSet<String>();
		this.objects.add(object);
		this.messageType = messageType;
	}

	
	public SimpleMessage(String code, String message, String subject, Collection<String> objects) {
		this.code = code;
		this.message = message;
		this.subject = subject;
		this.objects = new HashSet<String>();
		this.objects.addAll(objects);
	}

	public SimpleMessage(String code, String message, String subject, Collection<String> objects, String messageType) {
		this.code = code;
		this.message = message;
		this.subject = subject;
		this.objects = new HashSet<String>();
		this.objects.addAll(objects);
		this.messageType = messageType;
	}

	
	public String getCode() {
		return code;
	}
	
	public void setCode(String code) {
		this.code = code;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public Set<String> getObjects() {
		return objects;
	}
	
	public void addObject(String object) {
		if (this.objects == null) {
			this.objects = new HashSet<String>();				
		}
		this.objects.add(object);
	}

	public void addObjects(Collection<String> objects) {
		if (this.objects == null) {
			this.objects = new HashSet<String>();				
		}
		this.objects.addAll(objects);
	}

	
	public void setObjects(Set<String> objects) {
		this.objects = objects;
	}

	public String getSubject() {
		return subject;
	}
	
	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	@Override
	public String toString() {
		if (this.message != null) {
			if (this.subject == null) {
				return MessageFormat.format("({0}) ", this.messageType) + this.message;
			}
			if (this.objects == null) {
				return MessageFormat.format("({0}) ", this.messageType) + MessageFormat.format(this.message, this.subject);
			}
			return MessageFormat.format("({0}) ", this.messageType) + MessageFormat.format(this.message, this.subject, this.objects);
		}				
		return super.toString();
	}

	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}
}
