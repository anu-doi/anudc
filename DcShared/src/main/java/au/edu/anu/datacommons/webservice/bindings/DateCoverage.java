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

import javax.xml.bind.annotation.XmlElement;

public class DateCoverage
{
	private String dateFrom;
	private String dateTo;

	@XmlElement(name = "dateFrom")
	public String getDateFrom()
	{
		return dateFrom;
	}

	public void setDateFrom(String dateFrom)
	{
		this.dateFrom = dateFrom;
	}

	@XmlElement(name = "dateTo")
	public String getDateTo()
	{
		return dateTo;
	}

	public void setDateTo(String dateTo)
	{
		this.dateTo = dateTo;
	}
}
