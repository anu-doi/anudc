package au.edu.anu.datacommons.collectionrequest;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PageMessages
{
	public enum MessageType
	{
		ERROR, WARNING, INFO, SUCCESS
	};

	private Set<String> errors = null;
	private Set<String> warnings = null;
	private Set<String> infos = null;
	private Set<String> successes = null;

	public void add(MessageType msgType, String message, Map<String, Object> model)
	{
		switch (msgType)
		{
		case ERROR:
			if (errors == null)
				errors = new HashSet<String>();
			errors.add(message);
			break;
			
		case WARNING:
			if (warnings == null)
				warnings = new HashSet<String>();
			warnings.add(message);
			break;
			
		case INFO:
			if (infos == null)
				infos = new HashSet<String>();
			infos.add(message);
			break;
			
		case SUCCESS:
			if (successes == null)
				successes = new HashSet<String>();
			successes.add(message);
			break;
			
		default:
			throw new IllegalArgumentException("Invalid MessageType.");
		}
		
		// Add this instance to the model if not added already.
		if (!model.containsValue(this))
			model.put("messages", this);
	}

	public Set<String> getErrors()
	{
		return errors;
	}

	public Set<String> getWarnings()
	{
		return warnings;
	}

	public Set<String> getInfos()
	{
		return infos;
	}

	public Set<String> getSuccesses()
	{
		return successes;
	}
	
	public void clear(MessageType msgType)
	{
		switch (msgType)
		{
		case ERROR:
			if (errors != null)
				errors.clear();
			break;
			
		case WARNING:
			if (warnings != null)
				warnings.clear();
			break;
			
		case INFO:
			if (infos != null)
				infos.clear();
			break;
			
		case SUCCESS:
			if (successes != null)
				successes.clear();
			break;

		default:
			throw new IllegalArgumentException("Invalid MessageType.");
		}
	}
	
	public void clear()
	{
		clear(MessageType.ERROR);
		clear(MessageType.WARNING);
		clear(MessageType.INFO);
		clear(MessageType.SUCCESS);
	}
}
