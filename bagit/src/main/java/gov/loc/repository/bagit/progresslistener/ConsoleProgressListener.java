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

package gov.loc.repository.bagit.progresslistener;

import gov.loc.repository.bagit.ProgressListener;
import java.io.Console;
import java.util.concurrent.atomic.AtomicBoolean;

public class ConsoleProgressListener extends Object implements ProgressListener
{
	private Console console = System.console();
	private long nextUpdate = System.currentTimeMillis();
	private int lastLineLength = 0;
	private AtomicBoolean updating = new AtomicBoolean(false);
	
	@Override
	public void reportProgress(String activity, Object item, Long count, Long total)
	{
		if (console != null)
		{
			long now = System.currentTimeMillis();
			long next = this.nextUpdate;
			
			if (now >= next)
			{
				String msg = ProgressListenerHelper.format(activity, item, count, total);

				// We use an atomic boolean here so that we don't have to lock
				// every single time.  This keeps contention down on this
				// bottleneck.
				if (this.updating.compareAndSet(false, true))
				{
					try
					{
						int lastLength = this.lastLineLength;

						this.backup(lastLength);
						this.console.format(msg);
						
						if (msg.length() < lastLength)
						{
							int spacesNeeded = lastLength - msg.length();
							this.spaces(spacesNeeded);
							this.backup(spacesNeeded);
						}
						
						this.console.flush();
						
						this.lastLineLength = msg.length();
						this.nextUpdate = now + 1000;
					}
					finally
					{
						this.updating.set(false);
					}
				}
			}
		}
	}
	
	private void backup(int length)
	{
		for (int i = 0; i < length; i++)
		{
			this.console.format("\b");
		}
	}
	
	private void spaces(int length)
	{
		for (int i = 0; i < length; i++)
		{
			this.console.format(" ");
		}
	}
}
