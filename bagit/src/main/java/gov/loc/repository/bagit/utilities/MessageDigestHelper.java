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

package gov.loc.repository.bagit.utilities;

import java.security.MessageDigest;
import java.text.MessageFormat;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import gov.loc.repository.bagit.Manifest.Algorithm;

public class MessageDigestHelper {

	private static final Log log = LogFactory.getLog(MessageDigestHelper.class);	
    private static final int BUFFERSIZE = 65536;
    
    public static String generateFixity(File file, Algorithm algorithm) {
    	try {
    		log.debug("Generating fixity for " + file.toString());
    		return generateFixity(new FileInputStream(file), algorithm);
    	}
    	catch(Exception ex) {
    		throw new RuntimeException(ex);
    	}
    }
    
	public static String generateFixity(InputStream in, Algorithm algorithm) {
		
    	try
		{
			MessageDigest md = MessageDigest.getInstance(algorithm.javaSecurityAlgorithm);
			byte[] dataBytes = new byte[BUFFERSIZE];
			int nread = in.read(dataBytes);
			while (nread > 0)
			{
				md.update(dataBytes, 0, nread);
			    nread = in.read(dataBytes);
			}
			return new String(Hex.encodeHex(md.digest()));
			
		}
		catch(Exception ex)
		{
			throw new RuntimeException(ex);
		} finally {
			IOUtils.closeQuietly(in);
		}
    
	}	

    public static boolean fixityMatches(InputStream in, Algorithm algorithm,
    		String fixity) {
    	if (fixity == null) {
    		return false;
    	}
    	String generatedFixity = generateFixity(in, algorithm);
    	log.debug(MessageFormat.format("Generated fixity is {0}.  Check fixity is {1}.", generatedFixity, fixity));
    	if (generatedFixity.equalsIgnoreCase(fixity))
    	{
    		return true;
    	}
    	return false;
    }
    
}
