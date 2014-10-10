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

package au.edu.anu.datacommons.storage.messagedigest;

import static java.text.MessageFormat.format;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * @author Rahul Khanna
 *
 */
public class FileMessageDigests {

	public enum Algorithm {
		MD5("MD5"), SHA1("SHA-1"), SHA256("SHA-256"), SHA512("SHA-512");
		
		private String javaAlgorithmName;
		
		Algorithm(String javaAlgorithmName) {
			this.javaAlgorithmName = javaAlgorithmName;
		}
		
		public String getJavaAlgorithmName() {
			return this.javaAlgorithmName;
		}
		
		public static Algorithm lookupJavaAlgorithm(String alg) {
			for (Algorithm iAlgorithm : Algorithm.values()) {
				if (iAlgorithm.getJavaAlgorithmName().equals(alg)) {
					return iAlgorithm;
				}
			}
			throw new IllegalArgumentException(format("Unknown hash Message Digest Algorithm - {0}", alg));
		}
	}

	private Set<FileMessageDigest> messageDigests = new HashSet<>();
	
	public void addMessageDigest(Algorithm algorithm, byte[] messageDigest) {
		FileMessageDigest md = new FileMessageDigest(algorithm, messageDigest);
		messageDigests.add(md);
	}
	
	public boolean hasMessageDigest(Algorithm algorithm) {
		for (FileMessageDigest iDigest : messageDigests) {
			if (iDigest.getAlgorithm().equals(algorithm)) {
				return true;
			}
		}
		return false;
	}
	
	public FileMessageDigest getMessageDigest(Algorithm algorithm) {
		for (FileMessageDigest iDigest : messageDigests) {
			if (iDigest.getAlgorithm().equals(algorithm)) {
				return iDigest;
			}
		}
		return null;
	}
}
