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

package au.edu.anu.datacommons.storage.completer;

import java.util.List;

import gov.loc.repository.bagit.transformer.Completer;

/**
 * @author Rahul Khanna
 *
 */
public abstract class AbstractCustomCompleter implements Completer {

	protected List<String> limitAddUpdatePayloadFilepaths = null;
	protected List<String> limitDeletePayloadFilepaths = null;

	public List<String> getLimitAddUpdatePayloadFilepaths() {
		return limitAddUpdatePayloadFilepaths;
	}

	public void setLimitAddUpdatePayloadFilepaths(List<String> limitAddUpdatePayloadFilepaths) {
		this.limitAddUpdatePayloadFilepaths = limitAddUpdatePayloadFilepaths;
	}

	public List<String> getLimitDeletePayloadFilepaths() {
		return limitDeletePayloadFilepaths;
	}

	public void setLimitDeletePayloadFilepaths(List<String> limitDeletePayloadFilepaths) {
		this.limitDeletePayloadFilepaths = limitDeletePayloadFilepaths;
	}

	protected boolean isLimited(List<String> limitList, String filepath) {
		boolean isLimited = false;
		if (limitList == null) {
			isLimited = true;
		} else {
			if (limitList.contains(filepath)) {
				isLimited = true;
			}
		}
		return isLimited;
	}

}
