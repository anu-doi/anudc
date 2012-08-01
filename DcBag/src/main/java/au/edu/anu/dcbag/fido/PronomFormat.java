package au.edu.anu.dcbag.fido;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PronomFormat
{
	private static final Logger LOGGER = LoggerFactory.getLogger(Thread.currentThread().getClass());

	public enum MatchStatus
	{
		OK, KO;
	}

	private final String sourceStr;
	private final MatchStatus matchStatus;
	private final long timeToParse;
	private final String puid;
	private final String formatName;
	private final String sigName;
	private final long fileSize;
	private final String fileName;
	private final String mimeType;
	private final String matchType;

	public PronomFormat(String fidoStr)
	{
		if (fidoStr == null || fidoStr.equals(""))
		{
			sourceStr = "";
			matchStatus = MatchStatus.KO;
			timeToParse = 0;
			puid = "";
			formatName = "";
			sigName = "";
			fileSize = 0;
			fileName = "";
			mimeType = "";
			matchType = "";
			
			LOGGER.warn("Fido String is null. Assigning default properties.");
		}
		else
		{
			this.sourceStr = fidoStr;
			// Split using commas as delimiters except for commas surrounded by double quotes.
			String[] tokens = sourceStr.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");

			matchStatus = MatchStatus.valueOf(tokens[0].trim());
			timeToParse = Long.parseLong(tokens[1]);
			puid = tokens[2];
			formatName = stripQuotes(tokens[3]);
			sigName = stripQuotes(tokens[4]);
			fileSize = Long.parseLong(tokens[5]);
			fileName = stripQuotes(tokens[6]);
			mimeType = stripQuotes(tokens[7]);
			matchType = stripQuotes(tokens[8]);
		}
	}

	public MatchStatus getMatchStatus()
	{
		return matchStatus;
	}

	public long getTimeToParse()
	{
		return timeToParse;
	}

	public String getPuid()
	{
		return puid;
	}

	public String getFormatName()
	{
		return formatName;
	}

	public String getSigName()
	{
		return sigName;
	}

	public long getFileSize()
	{
		return fileSize;
	}

	public String getFileName()
	{
		return fileName;
	}

	public String getMimeType()
	{
		return mimeType;
	}

	public String getMatchType()
	{
		return matchType;
	}

	private String stripQuotes(String str)
	{
		StringBuilder sb = new StringBuilder(str);

		if (str == null || str.equals(""))
			return str;

		if (str.charAt(str.length() - 1) == '"')
			sb.deleteCharAt(str.length() - 1);

		if (str.charAt(0) == '"')
			sb.deleteCharAt(0);

		return sb.toString();
	}

	@Override
	public String toString()
	{
		return this.sourceStr;
	}
}
