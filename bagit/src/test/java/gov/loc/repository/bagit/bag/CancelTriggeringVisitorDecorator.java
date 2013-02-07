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

package gov.loc.repository.bagit.bag;

import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.BagFile;
import gov.loc.repository.bagit.BagVisitor;
import gov.loc.repository.bagit.Cancellable;

public class CancelTriggeringVisitorDecorator extends CancelThresholdBase implements BagVisitor
{
	private BagVisitor realVisitor;
	
	public CancelTriggeringVisitorDecorator(BagVisitor realVisitor, int threshold, Cancellable processToCancel)
	{
		super(threshold, processToCancel);
		this.realVisitor = realVisitor;
	}

	public void endBag()
	{
		this.increment();
		realVisitor.endBag();
	}

	public void endPayload()
	{
		this.increment();
		realVisitor.endPayload();
	}

	public void endTags()
	{
		this.increment();
		realVisitor.endTags();
	}

	public void startBag(Bag bag)
	{
		this.increment();
		realVisitor.startBag(bag);
	}

	public void startPayload()
	{
		this.increment();
		realVisitor.startPayload();
	}

	public void startTags()
	{
		this.increment();
		realVisitor.startTags();
	}

	public void visitPayload(BagFile bagFile)
	{
		this.increment();
		realVisitor.visitPayload(bagFile);
	}

	public void visitTag(BagFile bagFile)
	{
		this.increment();
		realVisitor.visitTag(bagFile);
	}
}
