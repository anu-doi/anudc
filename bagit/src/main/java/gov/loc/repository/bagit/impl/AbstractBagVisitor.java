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

package gov.loc.repository.bagit.impl;

import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.BagFile;
import gov.loc.repository.bagit.BagVisitor;
import gov.loc.repository.bagit.Cancellable;

public abstract class AbstractBagVisitor implements BagVisitor, Cancellable
{
	private boolean isCancelled = false;
	
	@Override
	public void cancel()
	{
		this.isCancelled = true;
	}
	
	@Override
	public boolean isCancelled()
	{
		return this.isCancelled;
	}
	
	@Override
	public void endBag() {
	}

	@Override
	public void endPayload() {
	}

	@Override
	public void endTags() {
	}

	@Override
	public void startBag(Bag bag) {
	}

	@Override
	public void startPayload() {
	}

	@Override
	public void startTags() {
	}

	@Override
	public void visitPayload(BagFile bagFile) {
	}

	@Override
	public void visitTag(BagFile bagFile) {
	}

}
