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

package gov.loc.repository.bagit.verify.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.ProgressListener;
import gov.loc.repository.bagit.ProgressListenable;
import gov.loc.repository.bagit.utilities.CancelUtil;
import gov.loc.repository.bagit.utilities.LongRunningOperationBase;
import gov.loc.repository.bagit.utilities.SimpleResult;
import gov.loc.repository.bagit.verify.CompleteVerifier;
import gov.loc.repository.bagit.verify.FailModeSupporting;
import gov.loc.repository.bagit.verify.ManifestChecksumVerifier;
import gov.loc.repository.bagit.verify.ValidVerifier;

public class ValidVerifierImpl extends LongRunningOperationBase implements ValidVerifier, FailModeSupporting {

	private static final Log log = LogFactory.getLog(ValidVerifierImpl.class);
	
	private CompleteVerifier completeVerifier;
	private ManifestChecksumVerifier manifestVerifier;
	private FailMode failMode = FailMode.FAIL_STAGE;
	
	public ValidVerifierImpl(CompleteVerifier completeVerifier, ManifestChecksumVerifier manifestVerifier) {
		this.completeVerifier = completeVerifier;
		this.manifestVerifier = manifestVerifier;
	}
	
	@Override
	public void addProgressListener(ProgressListener progressListener) {
		super.addProgressListener(progressListener);
		
		if (completeVerifier instanceof ProgressListenable) {
			((ProgressListenable)completeVerifier).addProgressListener(progressListener);
		}
		if (manifestVerifier instanceof ProgressListenable) {
			((ProgressListenable)manifestVerifier).addProgressListener(progressListener);
		}
	}
	
	@Override
	public FailMode getFailMode() {
		return this.failMode;
	}
	
	@Override
	public void setFailMode(FailMode failMode) {
		this.failMode = failMode;
		if (completeVerifier instanceof FailModeSupporting) ((FailModeSupporting)completeVerifier).setFailMode(failMode);
		if (manifestVerifier instanceof FailModeSupporting) ((FailModeSupporting)manifestVerifier).setFailMode(failMode);
	}
	
	@Override
	public void cancel()
	{
		super.cancel();
		
		CancelUtil.cancel(this.completeVerifier);
		CancelUtil.cancel(this.manifestVerifier);
	}
	
	@Override
	public SimpleResult verify(Bag bag) {
		//Is complete
		SimpleResult result = this.completeVerifier.verify(bag);
		if (this.isCancelled()) return null;
		if(! result.isSuccess() && FailMode.FAIL_FAST == failMode) return result;
		if(! result.isSuccess() && FailMode.FAIL_STEP == failMode) return result;
		if(! result.isSuccess() && FailMode.FAIL_STAGE == failMode) return result;

		//Every checksum checks
		result.merge(this.manifestVerifier.verify(bag.getTagManifests(), bag));
		if (this.isCancelled()) return null;
		if(! result.isSuccess() && FailMode.FAIL_FAST == failMode) return result;
		if(! result.isSuccess() && FailMode.FAIL_STEP == failMode) return result;

		result.merge(this.manifestVerifier.verify(bag.getPayloadManifests(), bag));
		if (this.isCancelled()) return null;
		if(! result.isSuccess() && FailMode.FAIL_FAST == failMode) return result;
		if(! result.isSuccess() && FailMode.FAIL_STEP == failMode) return result;
		
		log.info("Completed verification that bag is valid.");
		log.info("Validity check: " + result.toString());				
		return result;

	}
	
}
