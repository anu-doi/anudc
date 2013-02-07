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

import java.util.Arrays;

import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.BagFactory;
import gov.loc.repository.bagit.BagInfoTxt;
import gov.loc.repository.bagit.Manifest;
import gov.loc.repository.bagit.transformer.Completer;

public class UpdatePayloadOxumCompleter implements Completer {

	private CompleterHelper helper;
	private BagFactory bagFactory;
	
	//Not bothering with extending LongRunningOperation since this should be fast
	//Not bothering with configuration of threadcount
	
	public UpdatePayloadOxumCompleter(BagFactory bagFactory) {
		this.bagFactory = bagFactory;
		this.helper = new CompleterHelper();
	}
	

	
	@Override
	public Bag complete(Bag bag) {
		
		Bag newBag = this.bagFactory.createBag(bag);
		newBag.putBagFiles(bag.getPayload());
		newBag.putBagFiles(bag.getTags());
		
			BagInfoTxt bagInfo = newBag.getBagInfoTxt();
			if (bagInfo != null) {
				if (bagInfo.getPayloadOxum() != null) {
					bagInfo.generatePayloadOxum(newBag);
				}
			
			//Regenerate the tag manifests
			for(Manifest manifest : newBag.getTagManifests()) {
				this.helper.regenerateManifest(newBag, manifest, true, Arrays.asList(new String[] { newBag.getBagConstants().getBagInfoTxt()}), null);
			}
		}
		return newBag;
	}


}
