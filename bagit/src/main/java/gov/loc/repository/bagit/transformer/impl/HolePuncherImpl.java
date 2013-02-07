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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.BagFactory;
import gov.loc.repository.bagit.BagFile;
import gov.loc.repository.bagit.FetchTxt;
import gov.loc.repository.bagit.impl.AbstractBagVisitor;
import gov.loc.repository.bagit.transformer.HolePuncher;
import gov.loc.repository.bagit.utilities.SimpleResult;
import gov.loc.repository.bagit.utilities.SimpleResultHelper;
import gov.loc.repository.bagit.utilities.UrlHelper;
import gov.loc.repository.bagit.verify.FailModeSupporting.FailMode;

public class HolePuncherImpl extends AbstractBagVisitor implements HolePuncher {
	private static final Log log = LogFactory.getLog(HolePuncherImpl.class);
	private String baseUrl;
	private Bag originalBag;
	private Bag newBag;
	private FetchTxt fetch;
	private boolean includePayloadDirectory = false;
	private boolean includeTags = false;
	private BagFactory bagFactory;
	private boolean resume = false;
	private boolean leaveTags = true;
	private SimpleResult bagVerifyResult;
	
	public HolePuncherImpl(BagFactory bagFactory) {
		this.bagFactory = bagFactory;
	}

	@Override
	public Bag makeHoley(Bag bag, String baseUrl, boolean includePayloadDirectoryInUrl,
			boolean includeTags, boolean resume) {
		return this.makeHoley(bag, baseUrl, includePayloadDirectoryInUrl, includeTags, true, resume);
	}
		
	@Override
	public Bag makeHoley(Bag bag, String baseUrl, boolean includePayloadDirectoryInUrl,
			boolean includeTags, boolean leaveTags, boolean resume) {
		log.info("Making bag holey");
		this.originalBag = bag;
		this.resume = resume;
		this.baseUrl = baseUrl;
		if (! this.baseUrl.endsWith("/")) {
			this.baseUrl += "/";
		}
		this.includePayloadDirectory = includePayloadDirectoryInUrl;
		this.includeTags = includeTags;
		if (includeTags) {
			this.includePayloadDirectory = true;
		}
		this.leaveTags = leaveTags;
		bag.accept(this);
		return this.newBag;
	}

	@Override
	public void startBag(Bag bag) {
		this.newBag = this.bagFactory.createBag(bag.getBagConstants().getVersion());
		this.fetch = this.newBag.getBagPartFactory().createFetchTxt();
		this.newBag.putBagFile(this.fetch);
		this.originalBag.putBagFile(this.fetch);
		this.bagVerifyResult = this.originalBag.verifyValid(FailMode.FAIL_SLOW);
	}
	
	@Override
	public void visitPayload(BagFile bagFile) {
		if(resume){
			//Skip the file if the file is not missing or invalid. 
			if(! SimpleResultHelper.isMissingOrInvalid(bagVerifyResult, bagFile.getFilepath())){
				return;
			}
		}
		String url = baseUrl;
		if (includePayloadDirectory) {
			url += UrlHelper.encodeFilepath(bagFile.getFilepath());
		}
		else {
			url += UrlHelper.encodeFilepath(bagFile.getFilepath().substring(this.newBag.getBagConstants().getDataDirectory().length() + 1));
		}
		fetch.add(new FetchTxt.FilenameSizeUrl(bagFile.getFilepath(), bagFile.exists()?bagFile.getSize():null, url));
	}
	
	@Override
	public void visitTag(BagFile bagFile) {
		if(resume){
			//Skip the file if the file is not missing or invalid. 
			if(! SimpleResultHelper.isMissingOrInvalid(bagVerifyResult, bagFile.getFilepath())){
				return;
			}
		}
		if (includeTags) {
			String url = baseUrl + UrlHelper.encodeFilepath(bagFile.getFilepath());
			fetch.add(new FetchTxt.FilenameSizeUrl(bagFile.getFilepath(), bagFile.exists()?bagFile.getSize():null, url));
		}
		if (! includeTags || leaveTags) {
			this.newBag.putBagFile(bagFile);
		}
	}		
}
