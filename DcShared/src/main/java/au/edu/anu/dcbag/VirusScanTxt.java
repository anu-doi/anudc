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

package au.edu.anu.dcbag;

import gov.loc.repository.bagit.BagFile;
import gov.loc.repository.bagit.utilities.namevalue.impl.AbstractNameValueBagFile;

/**
 * Represents the tag file containing virus scan results for each payload file in a specified Bag.
 */
public class VirusScanTxt extends AbstractNameValueBagFile
{
	private static final long serialVersionUID = 1L;
	
	private static final String TYPE = "VirusScan";
	public static final String FILEPATH = "virus-scan.txt";
	
	public VirusScanTxt(String filepath, BagFile bagFile, String encoding)
	{
		super(filepath, bagFile, encoding);
	}

	public VirusScanTxt(String filepath, String encoding)
	{
		super(filepath, encoding);
	}

	@Override
	public String getType()
	{
		return TYPE;
	}
}
