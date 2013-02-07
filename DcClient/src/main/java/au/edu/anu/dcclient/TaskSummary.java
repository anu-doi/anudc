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
