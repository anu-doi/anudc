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

package gov.loc.repository.bagit.transformer.impl;

import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.Cancellable;
import gov.loc.repository.bagit.ProgressListenable;
import gov.loc.repository.bagit.transformer.Completer;
import gov.loc.repository.bagit.utilities.LongRunningOperationBase;

public class ChainingCompleter extends LongRunningOperationBase implements Completer {

	private Completer[] completers;
	
	public ChainingCompleter(Completer... completers) {
		this.completers = completers;
		for(Completer completer : completers) {
			if (completer instanceof Cancellable) this.addChainedCancellable((Cancellable)completer);
			if (completer instanceof ProgressListenable) this.addChainedProgressListenable((ProgressListenable)completer);
		}
	}
	
	@Override
	public Bag complete(Bag bag) {
		Bag newBag = bag;
		for(Completer completer : completers) {
			newBag = completer.complete(newBag);
		}
		return newBag;		
	}

}
