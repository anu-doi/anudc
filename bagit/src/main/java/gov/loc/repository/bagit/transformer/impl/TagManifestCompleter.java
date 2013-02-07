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

import java.util.List;

import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.BagFactory;
import gov.loc.repository.bagit.Manifest;
import gov.loc.repository.bagit.ManifestHelper;
import gov.loc.repository.bagit.Manifest.Algorithm;
import gov.loc.repository.bagit.transformer.Completer;

public class TagManifestCompleter implements Completer {

	private CompleterHelper helper;
	private BagFactory bagFactory;
	private Algorithm tagManifestAlgorithm = Algorithm.MD5;
	private String nonDefaultManifestSeparator = null;
	private List<String> limitUpdateFilepaths = null;
	private List<String> limitDeleteFilepaths = null;
	private List<String> limitAddFilepaths = null;
	private List<String> limitUpdateDirectories = null;
	private List<String> limitDeleteDirectories = null;
	private List<String> limitAddDirectories = null;
	
	//Not bothering with extending LongRunningOperation since this should be fast
	//Not bothering with configuration of threadcount
	
	public TagManifestCompleter(BagFactory bagFactory) {
		this.bagFactory = bagFactory;
		this.helper = new CompleterHelper();
	}
	
	public void setTagManifestAlgorithm(Algorithm tagManifestAlgorithm) {
		this.tagManifestAlgorithm = tagManifestAlgorithm;
	}
	
	/*
	 * Limit updates to the provided filepaths, i.e., only the manifest entries of the
	 * provided files will be updated.
	 */
	public void setLimitUpdateTagFilepaths(List<String> limitUpdateFiles) {
		this.limitUpdateFilepaths = limitUpdateFiles;
	}
	
	/*
	 * Limit deletes to the provided filepaths, i.e., only the manifest entries of the
	 * provided files will be removed. 
	 */
	public void setLimitDeleteTagFilepaths(List<String> limitDeleteFiles) {
		this.limitDeleteFilepaths = limitDeleteFiles;
	}
	
	/*
	 * Limit additions to the provided filepaths, i.e., only manifest entries for the
	 * provided files will be added.
	 */
	public void setLimitAddTagFilepaths(List<String> limitAddFiles) {
		this.limitAddFilepaths = limitAddFiles;
	}

	/*
	 * Limit updates to the files in and below the provided directories, i.e.,
	 * only the manifest entries of files in or below the provided directories will be updated.
	 */
	public void setLimitUpdateTagDirectories(List<String> limitUpdateDirectories) {
		this.limitUpdateDirectories = limitUpdateDirectories;
	}
	
	/*
	 * Limit deletes to the files in and below the provided directories, i.e.,
	 * only the manifest entries of files in or below the provided directories will be removed.
	 */
	public void setLimitDeleteTagDirectories(List<String> limitDeleteDirectories) {
		this.limitDeleteDirectories = limitDeleteDirectories;
	}
	
	/*
	 * Limit additions to the files in and below the provided directories, i.e.,
	 * only manifest entries for files in or below the provided directories will be added.
	 */
	public void setLimitAddTagDirectories(List<String> limitAddDirectories) {
		this.limitAddDirectories = limitAddDirectories;
	}

	
	@Override
	public Bag complete(Bag bag) {
		
		Bag newBag = this.bagFactory.createBag(bag);
		newBag.putBagFiles(bag.getPayload());
		newBag.putBagFiles(bag.getTags());

		//Delete anything that doesn't exist
		this.helper.cleanManifests(newBag, newBag.getTagManifests(), this.limitDeleteFilepaths, this.limitDeleteDirectories);
		
		//Regenerate the tag manifests
		for(Manifest manifest : newBag.getTagManifests()) {
			this.helper.regenerateManifest(newBag, manifest, true, this.limitUpdateFilepaths, this.limitUpdateDirectories);
		}
		//See if anything is missing
		this.helper.handleManifest(newBag, this.tagManifestAlgorithm, ManifestHelper.getTagManifestFilename(this.tagManifestAlgorithm, newBag.getBagConstants()), newBag.getTags(), this.nonDefaultManifestSeparator, this.limitAddFilepaths, this.limitAddDirectories);
		return newBag;
	}

	public String getNonDefaultManifestSeparator() {
		return this.nonDefaultManifestSeparator;
	}
	
	public void setNonDefaultManifestSeparator(String manifestSeparator) {
		this.nonDefaultManifestSeparator = manifestSeparator;
	}

}
