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

import java.text.DecimalFormat;

public class SizeHelper {

	public static final double KB = Math.pow(2, 10);
	public static final double MB = Math.pow(2, 20);
	public static final double GB = Math.pow(2, 30);
	public static final double TB = Math.pow(2, 40);
	
	public static String getSize(long octets) {
		String unit;
		double div;
		if (octets < MB) {
			//Return KB
			unit = "KB";
			div = KB;			
		} else if (octets < GB) {
			//Return MB
			unit = "MB";
			div = MB;
		} else if (octets < TB) {
			//Return GB
			unit = "GB";
			div = GB;
		} else {
			//Return TB
			unit = "TB";
			div = TB;
		}
		String format = "#.#";
		double size = octets/div;
		String sizeString = (new DecimalFormat(format)).format(size);
		while (sizeString.endsWith("0")) {
			format += "#";
			sizeString = (new DecimalFormat(format)).format(size);
		}
		return sizeString + " " + unit;
	}
	
}
