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

package au.edu.anu.datacommons.webservice.bindings;

import java.util.List;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlRootElement;

import org.w3c.dom.Element;

@XmlRootElement (name = "response")
public class CombinedStatusResponse
{
	private Status status;
	private String msg;
	private List<Element> statusElements;
	
	public enum Status
	{
		@XmlEnumValue("success")
		SUCCESS,
		
		@XmlEnumValue("partial")
		PARTIAL,
		
		@XmlEnumValue("failure")
		FAILURE;
		
		@Override
		public String toString()
		{
			return super.toString().toLowerCase();
		}
	}

	@XmlAttribute(name = "status")
	public Status getStatus()
	{
		return status;
	}

	public void setStatus(Status status)
	{
		this.status = status;
	}

	@XmlAttribute(name = "message")
	public String getMsg()
	{
		return msg;
	}

	public void setMsg(String msg)
	{
		this.msg = msg;
	}

	@XmlAnyElement
	public List<Element> getStatusElements()
	{
		return statusElements;
	}

	public void setNodes(List<Element> statusElements)
	{
		this.statusElements = statusElements;
	}
}
