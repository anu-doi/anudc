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

package gov.loc.repository.bagit.verify;

public interface FailModeSupporting {
	public enum FailMode { 
		/*
		 * Fail on first error.
		 */
		FAIL_FAST,
		/*
		 * Fail at end.
		 */
		FAIL_SLOW,
		/*
		 * Fail after each step of verification.
		 * A step is a set of like verification operations.
		 * For example, check that all payload files are in at least one manifest.
		 */
		FAIL_STEP,
		/*
		 * Fail after each stage of verification.
		 * A stage is a set of logically grouped verification operations.
		 * For example, when validating a bag, all of the operations to verify
		 * that a bag is complete is a stage.
		 * 
		 */
		FAIL_STAGE };
	
	void setFailMode(FailMode failMode);
	FailMode getFailMode();
}
