package au.edu.anu.dcclient;

import java.io.PrintStream;
import java.text.MessageFormat;
import java.util.LinkedHashMap;

public class TaskSummary extends LinkedHashMap<String, String>
{
	private static final long serialVersionUID = 1L;
	
	private PrintStream dispStream;
	
	public TaskSummary()
	{
		dispStream = System.out;
	}
	
	public TaskSummary(PrintStream out)
	{
		dispStream = out;
	}
	
	public void display()
	{
		dispHeader();
		for (String key : this.keySet())
		{
			dispStream.println(MessageFormat.format("{0}: {1}", key, this.get(key)));
		}
	}
	
	private void dispHeader()
	{
		dispStream.println();
		dispStream.println("---------------------");
		dispStream.println("DcClient Task Summary");
		dispStream.println("---------------------");
		dispStream.println();
	}
}
