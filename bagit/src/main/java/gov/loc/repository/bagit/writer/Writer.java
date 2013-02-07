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

package gov.loc.repository.bagit.writer;

import java.io.File;

import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.Cancellable;
import gov.loc.repository.bagit.ProgressListenable;

public interface Writer extends Cancellable, ProgressListenable {
	/*
	 * Write the bag.
	 * @param	Bag	the bag to be written
	 * @return		the newly-written bag
	 */
	Bag write(Bag bag, File file);
}
