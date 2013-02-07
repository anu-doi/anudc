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

package gov.loc.repository.bagit.transfer.dest;

import static java.text.MessageFormat.*;

import gov.loc.repository.bagit.BagFile;
import gov.loc.repository.bagit.impl.FileBagFile;
import gov.loc.repository.bagit.transfer.BagTransferException;
import gov.loc.repository.bagit.transfer.FetchedFileDestination;
import gov.loc.repository.bagit.transfer.FetchedFileDestinationFactory;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FileSystemFileDestination implements FetchedFileDestinationFactory
{
	private static final Log log = LogFactory.getLog(FileSystemFileDestination.class);
	
    public FileSystemFileDestination()
    {
        this.destinationRoot = new File(".");
    }
    
    public FileSystemFileDestination(File root)
    {
        this.destinationRoot = root;
    }
    
    public File getDestinationRoot()
    {
        return this.destinationRoot;
    }
    
    public void setDestinationRoot(File root)
    {
        this.destinationRoot = root;
    }
    
    @Override
    public FetchedFileDestination createDestination(String path, Long size) throws BagTransferException
    {
        return new Instance(path, new File(this.destinationRoot, path));
    }
    
    private File destinationRoot;
    
    private static class Instance implements FetchedFileDestination
    {
        public Instance(String bagPath, File file)
        {
            this.file = file;
            this.bagPath = bagPath;
        }
        
        @Override
        public String getFilepath()
        {
            return this.bagPath;
        }

    	@Override
    	public boolean getSupportsDirectAccess()
    	{
    		return true;
    	}
    	
    	@Override
    	public String getDirectAccessPath()
    	{
    		return this.file.getAbsolutePath();
    	}
    	
        @Override
        public OutputStream openOutputStream(boolean append) throws BagTransferException
        {
            try
            {
                // TODO Ensure that the file path requested is not above the root.
                
                // Create the parent directories, if need be.
            	this.createParentDirectories();
                
                return new BufferedOutputStream(new FileOutputStream(this.file, append));
            }
            catch (FileNotFoundException e)
            {
                throw new BagTransferException(e);
            }
        }
        
        @Override
        public BagFile commit() throws BagTransferException
        {
            return new FileBagFile(this.bagPath, this.file);
        }
        
        @Override
        public void abandon()
        {
        	if (this.file.exists())
        	{
        		if (!this.file.delete())
        		{
        			this.file.deleteOnExit();
        		}
        	}
        }
        
        private void createParentDirectories()
        {
            // Create the parent directories, if need be.
            if (!this.file.getParentFile().exists())
            {
            	log.trace(format("Parent directory of destination file does not exist.  Making directories: {0}", this.file.getParentFile().getAbsolutePath()));
                
            	if (!this.file.getParentFile().mkdirs())
            	{
            		log.warn(format("Unable to create directories: {0}", this.file.getParentFile().getAbsolutePath()));
            	}
            }
        }

        private File file;
        private String bagPath;
    }
}
