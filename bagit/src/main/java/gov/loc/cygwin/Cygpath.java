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

package gov.loc.cygwin;

import static java.text.MessageFormat.format;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.OS;
import org.apache.commons.exec.PumpStreamHandler;

public class Cygpath
{
	// Private constructor to prevent instantiation
	private Cygpath() {}
	
	/**
	 * Returns a given path as a unix-style path by calling
	 * out to the <c>cygpath --unix</c> command line.  If the
	 * current environment is not a Windows machine, then the
	 * given path is returned unchanged.
	 * 
	 * @param path The path to be converted.
	 * @return The converted path.  If not on Windows, the same path given.
	 * @throws CygwinException If an error occurs during execution of the
	 * 		   Cygwin command.
	 * 
	 * @see OS#isFamilyWindows()
	 */
	public static String toUnix(String path) throws CygwinException
	{
		String finalPath;
		
		if (OS.isFamilyWindows())
		{
			ByteArrayOutputStream cygpathOut = new ByteArrayOutputStream();
			
			CommandLine cygPath = new CommandLine("cygpath");
			cygPath.addArgument("--unix");
			cygPath.addArgument(path);
			
			try
			{
				DefaultExecutor executor = new DefaultExecutor();
				executor.setStreamHandler(new PumpStreamHandler(cygpathOut));
				
				executor.execute(cygPath);
				finalPath = cygpathOut.toString().trim();
			}
			catch (ExecuteException e)
			{
				int exitValue = e.getExitValue();
				throw new CygwinException(format("Error when executing \"{0}\" (exit value {2}: {1}", cygPath, cygpathOut.toString().trim(), exitValue), e);
			}
			catch (IOException e)
			{
				throw new CygwinException(format("Error when executing \"{0}\": {1}", cygPath, cygpathOut.toString().trim()), e);
			}
		}
		else
		{
			finalPath = path;
		}
		
		return finalPath;
	}
}
