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

package gov.loc.repository.bagit.writer.impl;

import java.io.File;
import java.util.ArrayList;

import gov.loc.repository.bagit.BagFactory;
import gov.loc.repository.bagit.ProgressListener;
import gov.loc.repository.bagit.Bag.Format;
import gov.loc.repository.bagit.impl.AbstractBagVisitor;
import gov.loc.repository.bagit.utilities.TempFileHelper;
import gov.loc.repository.bagit.writer.Writer;

public abstract class AbstractWriter extends AbstractBagVisitor implements Writer {

	private ArrayList<ProgressListener> progressListeners = new ArrayList<ProgressListener>();
	protected BagFactory bagFactory;
	
	public AbstractWriter(BagFactory bagFactory) {
		this.bagFactory = bagFactory;
	}
	
	@Override
	public void addProgressListener(ProgressListener progressListener) {
		this.progressListeners.add(progressListener);
	}

	@Override
	public void removeProgressListener(ProgressListener progressListener) {
		this.progressListeners.remove(progressListener);
	}
	
	protected void progress(String activity, String item, long count, long total)
	{
		for (ProgressListener listener : this.progressListeners)
		{
			listener.reportProgress(activity, item, count, total);
		}
	}

	protected File getTempFile(File file) {
		return TempFileHelper.getTempFile(file);
	}

	protected abstract Format getFormat();
	
	protected void switchTemp(File file) {
		TempFileHelper.switchTemp(file);

	}
}
