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

import java.text.MessageFormat;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(name = "request")
public class DcRequest
{
	private String methodName;
	private Activity activity;
	private Collection collection;
	
	@XmlAttribute
	public String getMethodName()
	{
		return methodName;
	}

	public void setMethodName(String methodName)
	{
		this.methodName = methodName;
	}
	
	@XmlElement
	public Activity getActivity()
	{
		return activity;
	}

	public void setActivity(Activity activity)
	{
		this.activity = activity;
	}
	
	@XmlElement
	public Collection getCollection()
	{
		return collection;
	}
	
	public void setCollection(Collection collection)
	{
		this.collection = collection;
	}

	@Override
	public String toString()
	{
		String newLine = System.getProperty("line.separator");
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append(MessageFormat.format("methodName: {0}{1}", methodName, newLine));
		if (activity != null)
			strBuilder.append("Contains Activity object.");
		
		return strBuilder.toString();
	}

	@XmlTransient
	public FedoraItem getFedoraItem()
	{
		FedoraItem item;
		if (this.activity != null)
			item = this.activity;
		else if (this.collection != null)
			item = this.collection;
		else
			throw new NullPointerException("Fedora Item in this collection is null.");
		
		return item;
	}
}
