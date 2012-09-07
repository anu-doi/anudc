package au.edu.anu.dcbag.fido;

import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.BagFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.dcbag.PronomFormatsTxt;

public class PronomFormat
{
	private static final Logger LOGGER = LoggerFactory.getLogger(Thread.currentThread().getClass());
	
	public enum MatchStatus
	{
		OK, KO;
	}

	private String sourceStr;
	private MatchStatus matchStatus;
	private long timeToParse;
	private String puid;
	private String formatName;
	private String sigName;
	private long fileSize;
	private String fileName;
	private String mimeType;
	private String matchType;

	public PronomFormat(String fidoStr)
	{
		parseFidoStr(fidoStr);
	}
	
	public PronomFormat(Bag bag, BagFile bf)
	{
		BagFile pronomFormatsTxt = bag.getBagFile(PronomFormatsTxt.FILEPATH);
		if (pronomFormatsTxt != null)
		{
			PronomFormatsTxt tagFile = new PronomFormatsTxt(pronomFormatsTxt.getFilepath(), pronomFormatsTxt, bag.getBagItTxt().getCharacterEncoding());
			String fidoStr = tagFile.get(bf.getFilepath());
			parseFidoStr(fidoStr);
		}
		else
			parseFidoStr("");
	}
	
	private void parseFidoStr(String fidoStr)
	{
		if (fidoStr == null || fidoStr.length() == 0)
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
