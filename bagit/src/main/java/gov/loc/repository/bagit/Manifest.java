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

package gov.loc.repository.bagit;

import java.io.InputStream;
import java.util.Map;

public interface Manifest extends Map<String,String>, BagFile {
	
	enum Algorithm {
		MD5 ("md5", "MD5"), SHA1 ("sha1", "SHA-1"), SHA256 ("sha256", "SHA-256"), SHA512 ("sha512", "SHA-512");
		
		public String bagItAlgorithm;
		public String javaSecurityAlgorithm;
		
		Algorithm(String bagItAlgorithm, String javaSecurityAlgorithm) {
			this.bagItAlgorithm = bagItAlgorithm;
			this.javaSecurityAlgorithm = javaSecurityAlgorithm;
		}
		
		public static Algorithm valueOfBagItAlgorithm(String bagItAlgorithm) throws IllegalArgumentException {
			for(Algorithm algorithm : Algorithm.values()) {
				if (bagItAlgorithm.equals(algorithm.bagItAlgorithm)) {
					return algorithm;
				}
			}
			throw new IllegalArgumentException();
		}
		
		public static Algorithm valueOfJavaSecurityAlgorithm(String javaSecurityAlgorithm) throws IllegalArgumentException {
			for(Algorithm algorithm : Algorithm.values()) {
				if (javaSecurityAlgorithm.equals(algorithm.javaSecurityAlgorithm)) {
					return algorithm;
				}
			}
			throw new IllegalArgumentException();
		}
		
	}

	
	public boolean isPayloadManifest();
	
	public boolean isTagManifest();
		
	public Algorithm getAlgorithm();
	
	public InputStream originalInputStream();
	
	public String getNonDefaultManifestSeparator();
	
	public void setNonDefaultManifestSeparator(String manifestSeparator);
	
	
}
