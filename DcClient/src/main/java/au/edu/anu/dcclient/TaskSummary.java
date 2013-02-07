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

package au.edu.anu.dcclient;

import java.io.PrintStream;
import java.text.MessageFormat;
import java.util.LinkedHashMap;

/**
 * Extends a LinkedHashMap so summary information can be added as a key-value pairs. Using LinkedHashMap as it has predictable iteration order.
 */
public class TaskSummary extends LinkedHashMap<String, String>
{
	private static final long serialVersionUID = 1L;
	
	private final PrintStream dispStream;

	/**
	 * Creates a task summary with System.out (StdOut) as the target PrintStream.
	 */
	public TaskSummary()
	{
		this(System.out);
	}
	
	/**
	 * Creates a task summary with a specified PrintStream.
	 * 
	 * @param out
	 *            target PrintStream to which Task Summary will be written
	 */
	public TaskSummary(PrintStream out)
	{
		dispStream = out;
	}
	
	/**
	 * Outputs the task summary to the PrintStream.
	 */
	public void display()
	{
		dispHeader();
		for (String key : this.keySet())
			dispStream.println(MessageFormat.format("{0}: {1}", key, this.get(key)));
	}
	
	/**
	 * Writes the Task Summary header to PrintStream.
	 */
	private void dispHeader()
	{
		dispStream.println();
		dispStream.println("---------------------");
		dispStream.println("DcClient Task Summary");
		dispStream.println("---------------------");
		dispStream.println();
	}
}
