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

package au.edu.anu.datacommons.storage;

import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.transformer.Completer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Rahul Khanna
 *
 */
public class DcNewCompleter implements Completer {
	private static final Logger LOGGER = LoggerFactory.getLogger(DcNewCompleter.class);
	
	static ExecutorService execSvc = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
	
	private List<Future<?>> futures = new ArrayList<Future<?>>();
	
	private List<String> limitAddUpdatePayloadFilepaths = null;
	private List<String> limitDeletePayloadFilepaths = null;

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

	
	@Override
	public Bag complete(Bag bag) {
		
		return bag;
	}

}
