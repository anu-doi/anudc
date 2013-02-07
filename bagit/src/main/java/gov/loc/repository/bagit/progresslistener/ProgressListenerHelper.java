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

package gov.loc.repository.bagit.progresslistener;

import java.text.MessageFormat;

public class ProgressListenerHelper {
		
	public static String format(String activity, Object item, Long count, Long total) {
		String msg;
		
		if (count != null)
		{
			if (total != null)
			{
				msg = MessageFormat.format("{0} {1} ({2} of {3})", activity, item, count, total);
			}
			else
			{
				msg = MessageFormat.format("{0} {1} ({2})", activity, item, count);
			}
		}
		else
		{
			msg = MessageFormat.format("{0} {1}", activity, item);
		}
		return msg;
	}
}
